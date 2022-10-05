package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.actionmatcher.MatchingResult;
import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.actionmatcher.ResolvedStepResult;
import sealab.burt.qualitychecker.actionmatcher.StepResolver;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import seers.appcore.utils.JavaUtils;

import java.util.*;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.HIGH_QUALITY;

public @Slf4j
class S2RChecker {

    private static final int GRAPH_MAX_DEPTH_CHECK = Integer.MAX_VALUE;
    private final String appName;
    private final String appVersion;
    private final StepResolver resolver;
    private final NLActionS2RMatcher s2rMatcher;
    private final String parsersBaseFolder;
    private GraphState currentState;
    private AppGraphInfo executionGraph;
    private final HashMap<Integer, Integer> statesExecuted = new HashMap<>();

    public S2RChecker(String appName, String appVersion, String bugID) throws Exception {
//        String bugId = "";
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = BurtConfigPaths.nlParsersBaseFolder;

        s2rMatcher = new NLActionS2RMatcher(BurtConfigPaths.qualityCheckerResourcesPath, true, appName);
        resolver = new StepResolver(s2rMatcher, GRAPH_MAX_DEPTH_CHECK);

        readGraph(bugID);
        this.currentState = GraphState.START_STATE;
    }

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

    private QualityFeedback matchActions(List<NLAction> nlActions, Integer currentStateId) throws Exception {

        if (this.currentState == null)
            this.currentState = GraphState.START_STATE;

        if (currentStateId != null) {
            updateState(currentStateId);
        }

        log.debug("Current state: " + this.currentState);

        //FIXME: focus on the 1st action for now
        NLAction nlAction = nlActions.get(0);

        log.debug("All actions: " + nlActions);
        log.debug("Matching S2R action: " + nlAction);

        QualityFeedback qualityFeedback = new QualityFeedback();
        qualityFeedback.setAction(nlAction);


        resolveNLAction(nlAction, currentState, qualityFeedback);

        return qualityFeedback;
    }

    private void readGraph(String bugId) throws Exception {
        if (BurtConfigPaths.crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion, bugId);
        else
            executionGraph = JSONGraphReader.getGraph(appName, appVersion, bugId);
    }

    private void resolveNLAction(NLAction currNLAction, GraphState currentState, QualityFeedback s2rQA) throws Exception {

        log.debug("Resolving action: " + currNLAction);

        // First try to match with a Step in the graph
        ResolvedStepResult result = resolver.resolveActionInGraphConcurrent(currNLAction, executionGraph, currentState);
//        ResolvedStepResult result2;

        // Check to see if we were able to match with a step, if not we should try to
        // match with a component.
        if (result.isStepNotMatched()) {

            log.debug("Could not match the action: " + currNLAction);
/*
            // This contains the component and the action as well
            //NOTE: we should not try to build a non-existing  against the current screen
           result2 = resolver.resolveActionInGraphBasedOnComponent(currNLAction,
                    executionGraph, currentState, currentScreen, lastStep);

            // Get the steps to navigate to the screen where the GUI-component is displayed,
            // if it is not the one on the current screen
            if (result2.isStepNotMatched()) {*/
//            result2 =  new ResolvedStepResult();

            final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result);
            if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);
            else {
                final S2RQualityAssessment assessment = buildVocabularyMismatchAssessment(result);
                s2rQA.addQualityAssessment(assessment);
            }
            return;
            /*  } else {

             *//* final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result, result2);
                if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);*//*
                result = result2;
            }*/
        } else {
         /*   final S2RQualityAssessment assessment2 = buildAmbiguousAssessment(result, new ResolvedStepResult());
            if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);*/
        }

        AppStep matchedStep = result.getStep();
        log.debug("Step found: " + matchedStep);

        //-------------------------------------------------


        List<AppStep> inferredSteps = new LinkedList<>();
        try {

            //find and execute the intermediate steps
            addIntermediateSteps(matchedStep, null, currentState, inferredSteps);

        } catch (Exception e) {
            log.debug("Could not execute all the intermediate steps", e);
            log.debug("Trying to execute the matched step without all the intermediate steps");
        }

        //---------------------------------------------

        S2RQualityAssessment assessment1 = new S2RQualityAssessment(HIGH_QUALITY);
        S2RQualityAssessment assessment2 = null;
        assessment1.addMatchedStep(matchedStep);
        if (!inferredSteps.isEmpty()) {
            //---------------------------------------------------
            assessment2 = new S2RQualityAssessment();
            assessment2.setQualityCategory(S2RQualityCategory.MISSING);
            assessment2.addInferredSteps(inferredSteps);
        }
        s2rQA.addQualityAssessment(assessment1);
        if (assessment2 != null) s2rQA.addQualityAssessment(assessment2);

        //-------------------------------------------------

        String text = getTextFromNLAction(currNLAction);
        matchedStep.setText(text);

        //-------------------------------------------------

        //execute the matched step
        try {

            //we need to execute additional commands for types
            final S2RQualityAssessment assessment3 = checkForIncorrectInputValue(matchedStep);
            if (assessment3 != null) s2rQA.addQualityAssessment(assessment3);

        } catch (Exception e) {
            log.debug("Could not execute the matched steps", e);
            throw e;
        }

        //-------------------------------------------------

    }

    private String getTextFromNLAction(NLAction nlAction) {

        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String preposition = nlAction.getPreposition();

        String text = null;

        if (!StringUtils.isEmpty(object2)) {
            //case: type 'x' on 'y'
            if (JavaUtils.getSet("on", "in", "into", "for", "of", "as", "to", "with").contains(preposition)) {

/*                final boolean isObjectLiteral = NLActionS2RMatcher.isLiteralValue(object)
                        || NLActionS2RMatcher.getLiteralValue(object) != null;
                if (isObjectLiteral)*/
                    text = object;
            }

        } else if (!StringUtils.isEmpty(object)) {
            text = NLActionS2RMatcher.getLiteralValue(object);
        }
        return text;
    }

    private void addIntermediateSteps(AppStep matchedStep, AppStep lastStep, GraphState currentState,
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

            addIntermediateStepsInShortestPath(matchedStep, lastStep, currentResolvedSteps, shortestPath,
                    currentState.getComponents());

        } else {

            if (matchedStep.getTransition() == null)
                return;


            final GraphState targetState = matchedStep.getCurrentState();
            if (!currentState.equals(targetState)) {
                log.debug("No intermediate steps!");
                return;
            }

            addIntermediateStepsInCurrentScreen(matchedStep, currentState,
                    currentResolvedSteps, currentState.getComponents());

        }

    }

    private void addIntermediateStepsInCurrentScreen(AppStep matchedStep, GraphState currentState,
                                                     List<AppStep> currentResolvedSteps,
                                                     List<AppGuiComponent> components) {
        log.debug("Adding intermediate steps in the current state");

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

        List<GraphTransition> sortedTransitions = S2RCheckerUtils.filterAndSortTransitions(stateLoopTransitions,
                enabledComponents);
        //----------------------------------

        final Integer index = S2RCheckerUtils.indexOf.apply(sortedTransitions, matchedStep);
        List<GraphTransition> transitionsToExecute = sortedTransitions;
        if (index != -1) {
            //filter out the transitions after the step
            transitionsToExecute = sortedTransitions.subList(0, index);
        }

        //------------------------------------

        List<AppStep> stepsToExecute = transitionsToExecute.stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());

        stepsToExecute = S2RCheckerUtils.removeCheckedSteps(stepsToExecute, enabledComponents);
        //addAdditionalIntermediateSteps(stepsToExecute);

     /*   log.debug("Executing intermediate steps: " + stepsToExecute);
        List<DevServerCommand> deviceCommands = StepResolver.getCommandsFromGraphSteps(stepsToExecute);
        executeCommands(executionResults, deviceCommands);*/

        currentResolvedSteps.addAll(stepsToExecute);
    }

    public void addIntermediateStepsInShortestPath(AppStep matchedStep, AppStep lastStep,
                                                   List<AppStep> currentResolvedSteps,
                                                   List<AppStep> shortestPath, List<AppGuiComponent> components) {

//        log.debug("Adding intermediate steps in shortest path");
        //------------------------------

        for (int i = 0; i < shortestPath.size(); i++) {

            final AppStep currentStep = shortestPath.get(i);
            final GraphTransition currentTransition = currentStep.getTransition();
            final GraphState sourceState = currentTransition.getSourceState();


            //get the loops of the current source state
            final Set<GraphTransition> stateLoopTransitions = executionGraph.getGraph()
                    .outgoingEdgesOf(sourceState).stream()
                    .filter(tr -> tr.getTargetState().equals(sourceState)
                    )
                    .collect(Collectors.toSet());

            //---------------------------

            List<AppStep> stepsToExecute = new ArrayList<>();

            //for each loop
            if (!stateLoopTransitions.isEmpty()) {

                //get the enabled components
                //FIXME: should we consider all the components? this was a copy/paste from EULER
                List<AppGuiComponent> enabledComponents = null;
                if (components != null)
                    enabledComponents = components.stream()
                            .filter(AppGuiComponent::getEnabled)
//                        .map(c -> Transform.getGuiComponent(c, null))
                            .collect(Collectors.toList());

                //filter the loops that operate on the enabled components and sort them based on their appearance in
                // the screen: top-down
                List<GraphTransition> sortedTransitions = S2RCheckerUtils.filterAndSortTransitions(stateLoopTransitions,
                        enabledComponents);

                //----------------------------------

                List<GraphTransition> transitionsToExecute = sortedTransitions;
                if (i == 0 && lastStep != null) {
                    //filter out the transitions prior to the last step
                    final int index = S2RCheckerUtils.indexOf.apply(sortedTransitions, lastStep);
                    if (index != -1) {
                        transitionsToExecute = sortedTransitions.subList(index + 1, sortedTransitions.size());
                    }
                } else if (i == shortestPath.size() - 1) {
                    //filter out the transitions after the matched step
                    if (matchedStep != null) {
                        final int index = S2RCheckerUtils.indexOf.apply(sortedTransitions, matchedStep);
                        if (index != -1) {
                            transitionsToExecute = sortedTransitions.subList(0, index);
                        }
                    }
                }

                //------------------------------------

                stepsToExecute = transitionsToExecute.stream()
                        .map(GraphTransition::getStep)
                        .collect(Collectors.toList());

                stepsToExecute = S2RCheckerUtils.removeCheckedSteps(stepsToExecute, enabledComponents);
            }
            stepsToExecute.add(currentStep);

            //addAdditionalIntermediateSteps(stepsToExecute);

         /*   log.debug("Executing intermediate steps: " + stepsToExecute);
            List<DevServerCommand> deviceCommands = StepResolver.getCommandsFromGraphSteps(stepsToExecute);
            executeCommands(executionResults, deviceCommands);*/

            currentResolvedSteps.addAll(stepsToExecute);
        }
    }


    private S2RQualityAssessment checkForIncorrectInputValue(AppStep appStep) {

        if (!DeviceUtils.isAnyType(appStep.getAction())) {
            return null;
        }

        //--------------------------------

        if (StringUtils.isEmpty(appStep.getText())) {
            return new S2RQualityAssessment(S2RQualityCategory.LOW_Q_INCORRECT_INPUT);
        }

        return null;
    }

    private S2RQualityAssessment buildAmbiguousAssessment(ResolvedStepResult result) {

        if (result.anyAmbiguousResultPresent()) {

            final S2RQualityAssessment assessment = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_AMBIGUOUS);

            //------------------------------------
            final Set<Object> ambiguousComponents = result.getAmbiguousComponents();
//            ambiguousComponents.addAll(result2.getAmbiguousComponents());
            List<AppGuiComponent> components =
                    (List<AppGuiComponent>) ambiguousComponents.stream()
                            .flatMap(c -> ((List) c).stream())
                            .collect(Collectors.toList());
            components = components.subList(0, Math.min(5, components.size()));
            assessment.setAmbiguousComponents(components);

            //-----------------------------

            final Set<Object> ambiguousActions = result.getAmbiguousActions();
//            ambiguousActions.addAll(result2.getAmbiguousActions());
            assessment.setAmbiguousActions(translateActions(ambiguousActions.stream().
                    map(Object::toString).collect(Collectors.toList())));

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

    private S2RQualityAssessment buildVocabularyMismatchAssessment(ResolvedStepResult result) {
        final S2RQualityAssessment assessment = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_VOCAB_MISMATCH);

        if (result.anyActionResultPresent())
            assessment.setVerbVocabMismatch();

        if ((result.anyObjsResultPresent() && !result.isAnyMatchingResultsPresent(MatchingResult.COMPONENT_FOUND)))
            assessment.setObjsVocabMismatch();

        return assessment;
    }

    private void updateState(Integer currentStateId) {
        updateState(executionGraph.getState(currentStateId));
    }

    public void updateState(GraphState state) {
        log.debug("Setting the current state: " + state);
        this.currentState = state;
    }

    public GraphState getCurrentState() {
        return this.currentState;
    }

    public AppGraph<GraphState, GraphTransition> getGraph() {
        return executionGraph.getGraph();
    }
}
