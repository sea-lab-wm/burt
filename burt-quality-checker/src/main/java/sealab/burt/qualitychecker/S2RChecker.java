package sealab.burt.qualitychecker;

import edu.semeru.android.core.dao.DynGuiComponentDao;
import edu.semeru.android.core.dao.exception.CRUDException;
import edu.semeru.android.core.entity.model.fusion.Screen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.actionparser.*;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.qualitychecker.graph.db.DBUtils;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.graph.db.Transform;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import seers.appcore.utils.JavaUtils;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.HIGH_QUALITY;

public @Slf4j
class S2RChecker {

    private static final int GRAPH_MAX_DEPTH_CHECK = 3;
    private static int textCounter = 1;

    //function that finds the index of stepToFind in the transition list
    private static BiFunction<List<GraphTransition>, AppStep, Integer> indexOf = (transitions, stepToFind)
            -> IntStream.range(0, transitions.size())
            .filter(j -> {
                final AppStep step = transitions.get(j).getStep();
                return stepToFind.equals(step);
            })
            .findFirst()
            .orElse(-1);

    private final String appName;
    private final String appVersion;
    private final StepResolver resolver;
    private final NLActionS2RParser s2rParser;
    private final String parsersBaseFolder;
    private final String crashScopeDataPath;
    private GraphState currentState;
    private AppGraphInfo executionGraph;
    private HashMap<Integer, Integer> statesExecuted = new HashMap<>();

    public S2RChecker(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = BurtConfigPaths.nlParsersBaseFolder;
        this.crashScopeDataPath = BurtConfigPaths.getCrashScopeDataPath();

        s2rParser = new NLActionS2RParser(null, BurtConfigPaths.qualityCheckerResourcesPath, false);
        resolver = new StepResolver(s2rParser, GRAPH_MAX_DEPTH_CHECK);
    }

   /* public S2RChecker(String appName, String appVersion, String resourcesPath, String parsersBaseFolder,
                      String crashScopeDataPath) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = parsersBaseFolder;
        this.crashScopeDataPath = crashScopeDataPath;

        s2rParser = new NLActionS2RParser(null, resourcesPath, false);
        resolver = new StepResolver(s2rParser, GRAPH_MAX_DEPTH_CHECK);
    }*/

    public QualityFeedback checkS2R(String S2RDescription) throws Exception {
        return checkS2R(S2RDescription, null);
    }

    public QualityFeedback checkS2R(String S2RDescription, Integer currentState) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(parsersBaseFolder, appName, S2RDescription);
        if (nlActions.isEmpty()) return QualityFeedback.noParsedFeedback();
        List<NLAction> s2rActions = nlActions.stream().filter(NLAction::isSRAction).collect(Collectors.toList());
        if (s2rActions.isEmpty()) return QualityFeedback.noParsedFeedback();
        return matchActions(nlActions, currentState);
    }

/*
    public QualityFeedback checkS2R(NLAction action) throws Exception {
        readGraph();

        if (currentState == null)
            currentState = GraphState.START_STATE;

        log.debug("Current state: " + currentState);

        return matchAction(action);
    }*/

    private QualityFeedback matchActions(List<NLAction> nlActions, Integer currentStateId) throws Exception {
        readGraph();

        if (this.currentState == null)
            this.currentState = GraphState.START_STATE;

        if (currentStateId != null) {
            updateState(currentStateId);
        }

        log.debug("Current state: " + this.currentState);

        //focus on the 1st action for now
        NLAction nlAction = nlActions.get(0);

        log.debug("Matching action: " + nlAction);

        return matchAction(nlAction);
    }

    private void readGraph() throws Exception {
        if (crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion);
        else
            executionGraph = JSONGraphReader.getGraph(crashScopeDataPath, appName, appVersion);
    }

    private QualityFeedback matchAction(NLAction nlAction) {
        QualityFeedback qualityFeedback = new QualityFeedback();
        qualityFeedback.setAction(nlAction);
        List<AppStep> currentResolvedSteps = new LinkedList<>();
        resolveNLAction(nlAction, currentResolvedSteps, currentState, null, null, null, qualityFeedback);

//
//        if (assessments.stream().anyMatch(a -> a.getCategory().equals(LOW_Q_AMBIGUOUS)))
//            return new QualityResult(MULTIPLE_MATCH);
//
//        if (assessments.stream().anyMatch(a -> a.getCategory().equals(LOW_Q_INCORRECT_INPUT)))
//            return new QualityResult(NO_S2R_INPUT);
//
//        if (assessments.stream().anyMatch(a -> a.getCategory().equals(LOW_Q_VOCAB_MISMATCH)))
//            return new QualityResult(NO_MATCH);
//
//        if (assessments.stream().anyMatch(a -> a.getCategory().equals(MISSING)))
//            return new QualityResult(MISSING_STEPS);
//
//        if (assessments.stream().anyMatch(a -> a.getCategory().equals(HIGH_QUALITY))) {
//            currentState = assessments.get(0).getMatchedSteps().get(0).getCurrentState();
//            log.debug("New current state: " + currentState);
//            return new QualityResult(MATCH);
//        }
//        throw new RuntimeException("Unknown quality assessment");
        return qualityFeedback;
    }

    private List<DevServerCommandResult> resolveNLAction(NLAction currNLAction, List<AppStep> currentResolvedSteps,
                                                         GraphState currentState, Screen currentScreen,
                                                         NLAction previousS2RNlAction,
                                                         AppStep lastStep, QualityFeedback s2rQA) {

        log.debug("Resolving action: " + currNLAction);

        // Holds the target matched step and any intermediate steps
        List<DevServerCommandResult> executionResults = new ArrayList<>();

        // First try to match with a Step in the graph
        ResolvedStepResult result = resolver.resolveActionInGraph(currNLAction, executionGraph, currentState);
        ResolvedStepResult result2;

        // Check to see if we were able to match with a step, if not we should try to
        // match with a component.
        if (result.isStepNotMatched()) {

            // This contains the component and the action as well
            result2 = resolver.resolveActionInGraphBasedOnComponent(currNLAction,
                    executionGraph, currentState, currentScreen, lastStep);

            // Get the steps to navigate to the screen where the GUI-component is displayed,
            // if it is not the one on the current screen
            if (result2.isStepNotMatched()) {

                final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result, result2);
                if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);
                else {
                    final S2RQualityAssessment assessment = buildVocabularyMismatchAssessment(result, result2);
                    s2rQA.addQualityAssessment(assessment);
                }
                return executionResults;
            } else {

               /* final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result, result2);
                if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);*/
                result = result2;
            }
        } else {
         /*   final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result, new ResolvedStepResult());
            if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);*/
        }

        AppStep matchedStep = result.getStep();
        log.debug("Step found: " + matchedStep);

        //-------------------------------------------------
        List<DevServerCommand> deviceCommands;

        try {

            //find and execute the intermediate steps
            executeIntermediateSteps(matchedStep, lastStep, currentState, executionResults, currentResolvedSteps);

        } catch (Exception e) {
            log.debug("Could not execute all the intermediate steps", e);
            log.debug("Trying to execute the matched step without all the intermediate steps");
        }

        //---------------------------------------------

        S2RQualityAssessment assessment1 = new S2RQualityAssessment(HIGH_QUALITY);
        S2RQualityAssessment assessment2 = null;
        assessment1.addMatchedStep(matchedStep);
        if (!currentResolvedSteps.isEmpty()) {
            //---------------------------------------------------
            assessment2 = new S2RQualityAssessment();
            assessment2.setQualityCategory(S2RQualityCategory.MISSING);
            assessment2.addInferredSteps(currentResolvedSteps);
        }
        s2rQA.addQualityAssessment(assessment1);
        if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);

        //-------------------------------------------------

        String text = getTextFromNLAction(currNLAction);
        matchedStep.setText(text);

        //-------------------------------------------------

        //execute the matched step
        try {
            List<AppStep> currentResolvedSteps2 = new ArrayList<>();

            //we need to execute additional commands for types
            final S2RQualityAssessment assessment3 = addAdditionalSteps(matchedStep, currentResolvedSteps2);
            if (assessment3 != null) s2rQA.addQualityAssessment(assessment3);

        /*    log.debug("Executing matched steps: " + currentResolvedSteps2);
            deviceCommands = StepResolver.getCommandsFromGraphSteps(currentResolvedSteps2);
            List<DevServerCommandResult> executionResults2 = new ArrayList<>();
            executeCommands(executionResults2, deviceCommands);

            if (matchedStep.getId() == null) {
                int i = 0;
                if (DeviceUtils.isAnyType(matchedStep.getAction())) {
                    i = 1;
                }
                final Step dbStep = getStepFromId(executionResults2.get(i).getStepId());
                matchedStep.setId(dbStep.getId());
                matchedStep.setSequence(dbStep.getSequenceStep());
                matchedStep.setScreenshotFile(dbStep.getScreenshot());
            }

            currentResolvedSteps.addAll(currentResolvedSteps2);
            executionResults.addAll(executionResults2);*/
        } catch (Exception e) {
            log.debug("Could not execute the matched steps", e);
            throw e;
        }

        //-------------------------------------------------

        return executionResults;

    }

    private String getTextFromNLAction(NLAction nlAction) {

        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String preposition = nlAction.getPreposition();

        String text = null;

        Map.Entry<AppGuiComponent, Double> componentFound = null;
        if (!StringUtils.isEmpty(object2)) {
            //case: type 'x' on 'y'
            if (JavaUtils.getSet("on", "in", "into", "for", "of", "as", "to", "with").contains(preposition)) {

                final boolean isObjectLiteral = s2rParser.isLiteralValue(object)
                        || s2rParser.getLiteralValue(object) != null;
                if (isObjectLiteral)
                    text = object;
            }

        } else if (!StringUtils.isEmpty(object)) {
            text = s2rParser.getLiteralValue(object);
        }
        return text;
    }


    private void executeIntermediateSteps(AppStep matchedStep, AppStep lastStep, GraphState currentState,
                                          List<DevServerCommandResult> executionResults,
                                          List<AppStep> currentResolvedSteps) {

        //no intermediate steps for open app
        if (DeviceUtils.isOpenApp(matchedStep.getAction())
                || DeviceUtils.isCloseApp(matchedStep.getAction())) {
            return;
        }

        //------------------------

        //find the shortest path!
        List<AppStep> shortestPath = StepResolver.findShortestPath(executionGraph.getGraph(),
                matchedStep, currentState);

        if (!shortestPath.isEmpty()) {

            executeIntermediateStepsInShortestPath(matchedStep, lastStep,
                    executionResults, currentResolvedSteps, shortestPath, currentState.getComponents());

        } else {

            if (matchedStep.getTransition() == null)
                return;


            final GraphState targetState = matchedStep.getCurrentState();
            if (!currentState.equals(targetState)) {
                log.debug("No intermediate steps!");
                return;
            }

            executeIntermediateStepsInCurrentScreen(matchedStep, currentState,
                    executionResults, currentResolvedSteps, currentState.getComponents());

        }

    }

    private void executeIntermediateStepsInCurrentScreen(AppStep matchedStep, GraphState currentState,
                                                         List<DevServerCommandResult> executionResults,
                                                         List<AppStep> currentResolvedSteps,
                                                         List<AppGuiComponent> components) {
        log.debug("Executing intermediate steps in the current state");

        final Set<GraphTransition> stateLoopTransitions = executionGraph.getGraph()
                .outgoingEdgesOf(currentState).stream()
                .filter(tr -> tr.getTargetState().equals(currentState))
                .collect(Collectors.toSet());

        if (stateLoopTransitions.isEmpty()) {
            return;
        }

        //------------------------------------------

        //the steps in the current screen were already executed!
        //FIXME: not sure this is correct
        if (statesExecuted.containsKey(currentState.getUniqueHash())) {
            return;
        }
        statesExecuted.putIfAbsent(currentState.getUniqueHash(), 1);

        //------------------------------------------

//        final Screen currentScreen = DeviceServerClient.fullDump(token);
        //FIXME: should we consider all the components? this is copy/paste from EULER
        List<AppGuiComponent> enabledComponents = null;
        if (components != null)
            enabledComponents = components.stream()
                    .filter(AppGuiComponent::getEnabled)
//                .map(c -> Transform.getGuiComponent(c, null))
                    .collect(Collectors.toList());

        List<GraphTransition> sortedTransitions = filterAndSortTransitions(stateLoopTransitions,
                enabledComponents);
        //----------------------------------

        final Integer index = indexOf.apply(sortedTransitions, matchedStep);
        List<GraphTransition> transitionsToExecute = sortedTransitions;
        if (index != -1) {
            //filter out the transitions after the step
            transitionsToExecute = sortedTransitions.subList(0, index);
        }

        //------------------------------------

        List<AppStep> stepsToExecute = transitionsToExecute.stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());

        stepsToExecute = removeCheckedSteps(stepsToExecute, enabledComponents);
        addAdditionalIntermediateSteps(stepsToExecute);

     /*   log.debug("Executing intermediate steps: " + stepsToExecute);
        List<DevServerCommand> deviceCommands = StepResolver.getCommandsFromGraphSteps(stepsToExecute);
        executeCommands(executionResults, deviceCommands);*/

        currentResolvedSteps.addAll(stepsToExecute);
    }

    private void executeIntermediateStepsInShortestPath(AppStep matchedStep, AppStep lastStep,
                                                        List<DevServerCommandResult> executionResults,
                                                        List<AppStep> currentResolvedSteps,
                                                        List<AppStep> shortestPath, List<AppGuiComponent> components) {

        log.debug("Adding intermediate steps in shortest path");
        //------------------------------

        for (int i = 0; i < shortestPath.size(); i++) {

            final AppStep currentStep = shortestPath.get(i);
            final GraphTransition currentTransition = currentStep.getTransition();
            final GraphState sourceState = currentTransition.getSourceState();


            //get the loops of the current source state
            final Set<GraphTransition> stateLoopTransitions = executionGraph.getGraph()
                    .outgoingEdgesOf(sourceState).stream()
                    .filter(tr -> tr.getTargetState().equals(sourceState))
                    .collect(Collectors.toSet());

            //---------------------------

            List<AppStep> stepsToExecute = new ArrayList<>();

            //for each loop
            if (!stateLoopTransitions.isEmpty()) {

                //get the enabled components
                //FIXME: should we consider all the components? this was a copy/past from EULER
                List<AppGuiComponent> enabledComponents = null;
                if (components != null)
                    enabledComponents = components.stream()
                            .filter(AppGuiComponent::getEnabled)
//                        .map(c -> Transform.getGuiComponent(c, null))
                            .collect(Collectors.toList());

                //filter the loops that operate on the enabled components and sort them based on their appearance in
                // the screen: top-down
                List<GraphTransition> sortedTransitions = filterAndSortTransitions(stateLoopTransitions,
                        enabledComponents);

                //----------------------------------

                List<GraphTransition> transitionsToExecute = sortedTransitions;
                if (i == 0 && lastStep != null) {
                    //filter out the transitions prior to the last step
                    final int index = indexOf.apply(sortedTransitions, lastStep);
                    if (index != -1) {
                        transitionsToExecute = sortedTransitions.subList(index + 1, sortedTransitions.size());
                    }
                } else if (i == shortestPath.size() - 1) {
                    //filter out the transitions after the matched step
                    final int index = indexOf.apply(sortedTransitions, matchedStep);
                    if (index != -1) {
                        transitionsToExecute = sortedTransitions.subList(0, index);
                    }
                }

                //------------------------------------

                stepsToExecute = transitionsToExecute.stream()
                        .map(GraphTransition::getStep)
                        .collect(Collectors.toList());

                stepsToExecute = removeCheckedSteps(stepsToExecute, enabledComponents);
            }
            stepsToExecute.add(currentStep);

            addAdditionalIntermediateSteps(stepsToExecute);

         /*   log.debug("Executing intermediate steps: " + stepsToExecute);
            List<DevServerCommand> deviceCommands = StepResolver.getCommandsFromGraphSteps(stepsToExecute);
            executeCommands(executionResults, deviceCommands);*/

            currentResolvedSteps.addAll(stepsToExecute);
        }
    }

    private void addAdditionalIntermediateSteps(List<AppStep> steps) {
        for (int i = 0; i < steps.size(); i++) {

            final AppStep appStep = steps.get(i);

            if (DeviceUtils.isAnyType(appStep.getAction())) {

                AppStep priorStep = null;
                if (i - 1 >= 0) {
                    priorStep = steps.get(i - 1);
                }
                AppStep nextStep = null;
                if (i + 1 < steps.size()) {
                    nextStep = steps.get(i + 1);
                }

                //----------------------------------

                final AppGuiComponent component = appStep.getComponent();

                final AppStep clickStep = new AppStep(DeviceActions.CLICK, component);
                //add one click before the type if there isn't one already
                if (!clickStep.equals(priorStep)) {
                    //add it
                    steps.add(i, clickStep);
                    //leave the index where the type is
                    i++;
                }
                //add one click after the type if there isn't one already
                if (!clickStep.equals(nextStep)) {
                    //add it after the type
                    steps.add(i + 1, clickStep);
                    //leave the index where the new click is
                    i++;
                }

                //--------------------------------

                //add the text in case it is empty
                if (StringUtils.isEmpty(appStep.getText())) {
                    String text = getTextForType();
                    appStep.setText(text);
                }
            }
        }
    }

    private List<AppStep> removeCheckedSteps(List<AppStep> stepsToExecute, List<AppGuiComponent> enabledComponents) {
        List<Integer> stepsToRemove = new ArrayList<>();

        //-------------------------------
        List<ImmutablePair<Integer, Integer>> stepGroups = getStepGroupsWithCheckComponent(stepsToExecute);

        if (stepGroups.isEmpty())
            return stepsToExecute;

        for (ImmutablePair<Integer, Integer> stepGroup : stepGroups) {
            Integer selected = getSelected(stepGroup, enabledComponents, stepsToExecute);
            if (selected != null) {
                IntStream.range(stepGroup.left, selected).forEach(stepsToRemove::add);
                IntStream.range(selected + 1, stepGroup.right).forEach(stepsToRemove::add);
            } else {
                IntStream.range(stepGroup.left + 1, stepGroup.right).forEach(stepsToRemove::add);
            }
        }

        //-------------------------------

        return IntStream.range(0, stepsToExecute.size())
                .filter(i -> !stepsToRemove.contains(i))
                .mapToObj(stepsToExecute::get)
                .collect(Collectors.toList());
    }

    private Integer getSelected(ImmutablePair<Integer, Integer> stepGroup,
                                List<AppGuiComponent> enabledComponents,
                                List<AppStep> stepsToExecute) {

        final List<AppStep> appSteps = stepsToExecute.subList(stepGroup.left, stepGroup.right);

        final List<AppGuiComponent> checkedComponents = enabledComponents.stream()
                .filter(AppGuiComponent::getChecked)
                .collect(Collectors.toList());

        final int idx = IntStream.range(0, appSteps.size())
                .filter(j -> {
                    final AppGuiComponent component = appSteps.get(j).getComponent();
                    return checkedComponents.contains(component);
                })
                .findFirst()
                .orElse(-1);

        if (idx == -1)
            return null;

        return idx + stepGroup.left;
    }

    private List<ImmutablePair<Integer, Integer>> getStepGroupsWithCheckComponent(List<AppStep> stepsToExecute) {
        List<ImmutablePair<Integer, Integer>> stepGroups = new ArrayList<>();

        int ini = -1;
        int end;
        for (int i = 0; i < stepsToExecute.size(); i++) {
            final AppStep appStep = stepsToExecute.get(i);
            final AppGuiComponent component = appStep.getComponent();

            if (s2rParser.isCheckedComponent(component.getType())) {
                if (ini == -1)
                    ini = i;
            } else {
                if (ini != -1) {
                    end = i;
                    stepGroups.add(new ImmutablePair<>(ini, end));
                    ini = -1;
                }
            }
        }

        if (ini != -1) {
            end = stepsToExecute.size();
            stepGroups.add(new ImmutablePair<>(ini, end));
        }

        return stepGroups;
    }

    private List<GraphTransition> filterAndSortTransitions(Set<GraphTransition> graphTransitions, List<AppGuiComponent>
            enabledComponents) {

        if (enabledComponents == null) return new ArrayList<>();

        final List<ImmutablePair<GraphTransition, Integer>> indexedComponents = graphTransitions.stream()
                .map(t -> new ImmutablePair<>(t,
                        enabledComponents.indexOf(t.getStep().getComponent())))
                .filter(tp -> tp.right != -1)
                .collect(Collectors.toList());

        return indexedComponents.stream()
                .sorted(Comparator.comparing(ImmutablePair::getRight))
                .map(ImmutablePair::getLeft)
                .collect(Collectors.toList());
    }

    private S2RQualityAssessment addAdditionalSteps(AppStep appStep, List<AppStep> currentResolvedSteps2) {
        if (DeviceUtils.isAnyType(appStep.getAction())) {

            final AppStep clickStep = new AppStep(DeviceActions.CLICK, appStep.getComponent());

            currentResolvedSteps2.add(clickStep);
            currentResolvedSteps2.add(appStep);
            currentResolvedSteps2.add(clickStep);

            //--------------------------------

            //add the text in case it is empty
            if (StringUtils.isEmpty(appStep.getText())) {
                String text = getTextForType();
                appStep.setText(text);


                S2RQualityAssessment assessment3 = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_INCORRECT_INPUT);
                assessment3.setInputValue(text);

                return assessment3;
            }
        } else {
            currentResolvedSteps2.add(appStep);
        }

        return null;
    }


    private String getTextForType() {


        return String.valueOf(textCounter++);
    }


    private S2RQualityAssessment buildAmbiguousAssessment(ResolvedStepResult result, ResolvedStepResult result2) {

        if (result.anyAmbiguousResultPresent() || result2.anyAmbiguousResultPresent()) {

            final S2RQualityAssessment assessment = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_AMBIGUOUS);
            Set<String> ambiguousElements = result.getAmbiguousElements();
            final Set<String> ambiguousElements2 = result2.getAmbiguousElements();
            ambiguousElements.addAll(ambiguousElements2);

            assessment.setAmbiguousCases(new ArrayList<>(ambiguousElements));

            //------------------------------------
            final Set<String> ambiguousComponents = result.getAmbiguousComponents();
            ambiguousComponents.addAll(result2.getAmbiguousComponents());
            for (String ambiguousComponent : ambiguousComponents) {
                final int i = ambiguousComponent.indexOf("[");
                final String[] elements = ambiguousComponent.substring(i + 1, ambiguousComponent.length() - 1)
                        .split(",");

                final List<String> strComponents = Arrays.asList(elements);
                int limit = 5;
                if (strComponents.size() < limit) {
                    limit = strComponents.size();
                }
                final List<String> firstComponents = strComponents.subList(0, limit);

                List<AppGuiComponent> components = getComponents(firstComponents);
                assessment.setAmbiguousComponents(components);
            }

            //-----------------------------

            final Set<String> ambiguousActions = result.getAmbiguousActions();
            ambiguousActions.addAll(result2.getAmbiguousActions());
            for (String ambiguousAction : ambiguousActions) {
                final int i = ambiguousAction.indexOf("[");
                final String[] elements = ambiguousAction.substring(i + 1, ambiguousAction.length() - 1)
                        .split(",");

                assessment.setAmbiguousActions(translateActions(Arrays.asList(elements)));
            }

            return assessment;
        }

        return null;
    }


    private List<String> translateActions(List<String> actions) {
        return actions.stream()
                .map(String::toLowerCase)
                .map(s -> s.replace("_", " "))
                .collect(Collectors.toList());
    }

    private List<AppGuiComponent> getComponents(List<String> firstComponents) {
        EntityManager em = DBUtils.createEntityManager();
        try {
            DynGuiComponentDao dao = new DynGuiComponentDao();
            return firstComponents.stream().map(id -> {
                try {
                    return dao.getById(Long.valueOf(id.trim()), em);
                } catch (CRUDException e) {
                    log.error("Error", e);
                }
                return null;
            }).filter(Objects::nonNull)
                    .map(c -> Transform.getGuiComponent(c, null))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }

    }

    private S2RQualityAssessment buildVocabularyMismatchAssessment(ResolvedStepResult result,
                                                                   ResolvedStepResult result2) {
        final S2RQualityAssessment assessment = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_VOCAB_MISMATCH);

        if (result.anyActionResultPresent() || result2.anyActionResultPresent())
            assessment.setVerbVocabMismatch();

        if (result.anyObjsResultPresent() || result2.anyObjsResultPresent())
            assessment.setObjsVocabMismatch();
        return assessment;
    }

    public void updateState(Integer currentStateId) {
        this.currentState = executionGraph.getState(currentStateId);
    }

    public void updateState(GraphState state) {
        this.currentState = state;
    }
}
