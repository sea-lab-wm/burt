/**
 * Created by Kevin Moran on Mar 16, 2018
 */
package sealab.burt.qualitychecker.actionparser;

import edu.semeru.android.core.entity.model.fusion.Screen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.GraphPath;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KevinMoran
 */
public @Slf4j
class StepResolver {

    private NLActionS2RParser s2rParser;
    private int graphMaxDepthCheck;

    public StepResolver(NLActionS2RParser s2rParser, int graphMaxDepthCheck) {
        this.s2rParser = s2rParser;
        this.graphMaxDepthCheck = graphMaxDepthCheck;
    }

    private static boolean isValidStepOnComponent(Entry<AppGuiComponent, Double> foundComponentEntry, AppStep appStep) {

        final AppGuiComponent stepComponent = appStep.getComponent();
        final Integer event = appStep.getAction();

        if (foundComponentEntry == null || stepComponent == null) {
            return DeviceUtils.isChangeRotation(event) ||
                    DeviceUtils.isClickMenuButton(event);
        }

//        GraphTransition transition = appStep.getTransition();
        final AppGuiComponent foundComponent = foundComponentEntry.getKey();
        final AppGuiComponent parent = foundComponent.getParent();
        return stepComponent.equals(foundComponent) ||
                //these conditions are needed because CrashScope may execute the layouts,
                // which have the text of the child component
                (stepComponent.getType().endsWith("Layout") && parent != null && stepComponent.getType().equals
                        (parent.getType()) && stepComponent.getText().equals(foundComponent.getText()))

                //&& (foundComponent.getKey().getState() == null
                //|| (transition.getSourceState().equals(foundComponent.getKey().getState())))
                ;
    }

    private static void getCandidateGraphStates(AppGraph<GraphState, GraphTransition> executionGraph,
                                                LinkedHashMap<GraphState, Integer> stateCandidates,
                                                GraphState currentState,
                                                Integer currentDistance,
                                                int maxDistanceToCheck) {
        if (currentState == null) {
            return;
        }
        if (currentDistance > maxDistanceToCheck) {
            return;
        }

        if (stateCandidates.containsKey(currentState) || GraphState.END_STATE.equals(currentState))
            return;

        // If the node is not in the map then we add the state and the
        // distance from the current state on the graph
        stateCandidates.put(currentState, currentDistance);

//        if (executionGraph.containsVertex(currentState)) {
        Set<GraphTransition> outgoingEdges = executionGraph.outgoingEdgesOf(currentState);
        final Set<GraphState> nextStates = outgoingEdges.stream().map(GraphTransition::getTargetState)
                .collect(Collectors.toCollection(HashSet::new));
        nextStates.remove(currentState);

        for (GraphState state : nextStates) {
            getCandidateGraphStates(executionGraph, stateCandidates, state, currentDistance + 1, maxDistanceToCheck);
        }
//        }
    }

    /**
     * Returns a list of AppStep finding the shortest path from the
     * current step's state to a target state's component
     */
    public static List<AppStep> findShortestPath(AppGraph<GraphState, GraphTransition> appGraph,
                                                 AppStep currentStep, GraphState currentState) {

        List<AppStep> intermediateSteps = new LinkedList<>();

        //we wanna reach the state where the current step is executed!
        final GraphState stepTargetState = currentStep.getCurrentState();

        log.debug(String.format("Finding shortest path between %s and %s",
                currentState, stepTargetState));

        //FIXME: if it is the same state, are there loops than may be intermediate steps?
        if (stepTargetState.equals(currentState)) {
            return intermediateSteps;
        }

        //-------------------------------

        List<GraphPath<GraphState, GraphTransition>> shortestPaths = GraphUtils.findPaths(appGraph, currentState,
                stepTargetState, false, 1);

        if (shortestPaths == null || shortestPaths.isEmpty()) {
            return intermediateSteps;
        }

        log.debug("Found shortest path of size " + shortestPaths.get(0).getEdgeList().size());

        Stream<GraphTransition> pathTransitions = shortestPaths.get(0).getEdgeList().stream();
        intermediateSteps.addAll(pathTransitions
                .map(GraphTransition::getStep)
                .collect(Collectors.toList()));
        return intermediateSteps;

        //--------------------------------------




      /*  List<GraphPath<GraphState, GraphTransition>> shortestPaths = GraphUtils.findPaths(appGraph, currentState,
                stepTargetState, considerLoops, 1);
        if (shortestPaths != null && !shortestPaths.isEmpty()) {

            Stream<GraphTransition> pathTransitions = shortestPaths.get(0).getEdgeList().stream();

            //remove the matched step from the path
            final GraphTransition transition = matchedStep.getTransition();
            if (considerLoops && transition != null) {
                pathTransitions = pathTransitions.filter(t -> !t.equals(transition));
            }

            intermediateSteps.addAll(pathTransitions
                    .map(GraphTransition::getStep)
                    .collect(Collectors.toList()));
        }*/

    }

    public static List<DevServerCommand> getCommandsFromGraphSteps(List<AppStep> steps) {
        List<DevServerCommand> commandList = new ArrayList<>();
        for (AppStep step : steps) {
            // Create new component and add it to the list
            commandList.add(getCommandFromGraphStep(step));
        }
        return commandList;
    }

    private static DevServerCommand getCommandFromGraphStep(AppStep step) {
        Long componentId = step.getComponent() == null ? null : step.getComponent().getDbId();
        final Long screenId = step.getComponent() == null ? null : step.getComponent().getScreenId();
        return new DevServerCommand(componentId, step.getAction(), step.getText(), screenId);
    }

    /**
     * TODO: We might need to return multiple steps
     */
    public ResolvedStepResult resolveActionInGraph(NLAction currNLAction, AppGraphInfo executionGraph,
                                                   GraphState currentState) {

        log.debug("Finding action in graph >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Appl app = executionGraph.getApp();

        try {
            AppStep openAppStep = getOpenAppStep(currNLAction, app);
            if (openAppStep != null) return new ResolvedStepResult(openAppStep);

        } catch (ActionParsingException e) {
            //The open app step should not fail, if it does, then the action is not an open app step
        }


        try {
            AppStep closeAppStep = getCloseAppStep(currNLAction, app);
            if (closeAppStep != null) return new ResolvedStepResult(closeAppStep);
        } catch (ActionParsingException e) {
            //ok
        }

        // 1. Get all considered nodes that are in range of GRAPH_MAX_DEPTH_CHECK
        // TODO: check previously executed or seen states
        LinkedHashMap<GraphState, Integer> stateCandidates = new LinkedHashMap<>();
        getCandidateGraphStates(executionGraph.getGraph(), stateCandidates, currentState, 0, graphMaxDepthCheck);
        stateCandidates.remove(GraphState.START_STATE);

        log.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);

        final ResolvedStepResult result = new ResolvedStepResult();

        //-----------------------

        LinkedHashMap<AppStep, Integer> foundSteps = new LinkedHashMap<>();
        for (Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
            final GraphState candidateState = candidateEntry.getKey();
            final Integer distance = candidateEntry.getValue();

            log.debug("Checking candidate state/screen: " + candidateState.getUniqueHash());

            //-------------------------------------
            // Get the components of the current candidate screen

            List<AppGuiComponent> stateComponents = candidateState.getComponents();
            if (stateComponents == null)
                continue;

            //filter out those components associated with a step, which duplicate existing components
            stateComponents = stateComponents.stream()
                    .filter(c -> c.getParent() != null || "NO_ID".equals(c.getIdXml()))
                    .collect(Collectors.toList());

            //-------------------------------------

            // Determine event
            Integer event;
            try {
                event = s2rParser.determineEvent(currNLAction, app, stateComponents);
            } catch (ActionParsingException e) {
                log.debug("Could not determine the event in the candidate state/screen: "
                        + candidateState.getUniqueHash() + " - " + e.getResult());
                result.addCount(e);
                continue;
            }

            //-------------------------------------
            // Determine the candidate transitions that match the event

            Integer event2 = event;
            final boolean isInputEvent = DeviceUtils.isAnyInputType(event2);
            final Predicate<GraphTransition> filterPredicate = transition -> {
                final AppStep step = transition.getStep();
                final Integer stepEvent = step.getAction();
                return event2.equals(stepEvent) || (isInputEvent &&
                        DeviceUtils.isAnyInputType(stepEvent) == isInputEvent);
            };
            final List<GraphTransition> candidateTransitions = executionGraph.getGraph().
                    outgoingEdgesOf(candidateState).stream().filter(filterPredicate).collect(Collectors.toList());

            if (candidateTransitions.isEmpty()) {
                log.debug("No candidate transitions for candidate state/screen: "
                        + candidateState.getUniqueHash());
                //result.addCount(ParsingResult.ACTION_NOT_MATCHED);
                continue;
            }

            //-------------------------------------
            // Determine the component

            Entry<AppGuiComponent, Double> component;

            try {
                component = s2rParser.determineComponent(currNLAction, stateComponents, event, true);
            } catch (ActionParsingException e) {
                log.debug("Could not find the component in the candidate state/screen: "
                        + candidateState.getUniqueHash() + " - " + e.getResult());
                if (e.getResult().equals(ParsingResult.MULTIPLE_COMPONENTS_FOUND)) {
                    log.debug(e.getResultData());
                }
                result.addCount(e);
                continue;
            }

            //-------------------------------------
            // Determine the text

            String text;
            try {
                Long componentId = null;
                if (component != null) {
                    componentId = component.getKey().getDbId();
                }
                text = s2rParser.determineText(app, event, componentId, currNLAction);
                text = DeviceUtils.encodeText(text);
            } catch (ActionParsingException e) {
                log.debug("Could not determine the text for the candidate state/screen: "
                        + candidateState.getUniqueHash() + " - " + e.getResult());
                result.addCount(e);
                continue;
            }

            //-------------------------------------

            log.debug("Candidate transitions (" + candidateTransitions.size() + "):" + candidateTransitions);
            log.debug(String.format("Event identified: %s", event));
            log.debug(String.format("Component identified: %s", component));
            log.debug(String.format("Text identified: %s", text));

            // Get the step from the graph based on the <event, component> search above
            for (GraphTransition transition : candidateTransitions) {
                AppStep transitionStep = transition.getStep();

                if (!isValidStepOnComponent(component, transitionStep)) {
                    continue;
                }

                //build the step
                AppStep tempStep = new AppStep(event, transitionStep.getComponent(), text);
                tempStep.setId(transitionStep.getId());
                tempStep.setExecution(transitionStep.getExecution());
                tempStep.setException(transitionStep.getException());
                tempStep.setCurrentState(transition.getSourceState());
                tempStep.setTransition(transition);
                tempStep.setScreenshotFile(transitionStep.getScreenshotFile());
                tempStep.setSequence(transitionStep.getSequence());
                if (component != null) {
                    tempStep.setComponent(component.getKey());
                }

                //--------------------
                //add the step to the map of found steps

                final Integer currentDist = foundSteps.get(tempStep);
                if (currentDist == null) {
                    foundSteps.put(tempStep, distance);
                    log.debug(String.format("New candidate step found: %s", tempStep));

                } else if (currentDist > distance) {
                    //we want to have the nearest step
                    foundSteps.remove(tempStep);
                    foundSteps.put(tempStep, distance);
                    log.debug(String.format("Candidate step updated: %s", tempStep));
                }
            }

        }

        log.debug("Candidate steps (" + foundSteps.size() + "):" + foundSteps);

        // sort base on the score
        List<ImmutablePair<AppStep, Double>> stepScores = new ArrayList<>();

        // Give priority to components based on how far they are from the current
        // state, so the closer they are the higher the score
        for (Entry<AppStep, Integer> stepEntry : foundSteps.entrySet()) {
            final AppStep step = stepEntry.getKey();
            final Integer distance = stepEntry.getValue();

            double score = 1d / (distance + 1);
            stepScores.add(new ImmutablePair<>(step, score));
        }
        stepScores.sort((a, b) -> b.right.compareTo(a.right));

        // Return the first one
        if (!stepScores.isEmpty()) {
            result.setStep(stepScores.get(0).left);
        }

        return result;

    }

    private AppStep getOpenAppStep(NLAction currNLAction, Appl app) throws ActionParsingException {
        Integer event = s2rParser.determineEvent(currNLAction, app, new ArrayList<>());
        final boolean isOpenApp = DeviceUtils.isOpenApp(event);
        if (isOpenApp) {
            AppStep appStep = new AppStep(event, null, app.getPackageName());
            appStep.setScreenshotFile(null); //FIXME: change the screenshot file for "open app"
            return appStep;
        }
        return null;
    }

    public AppStep getCloseAppStep(NLAction currNLAction, Appl app) throws ActionParsingException {
        Integer event = s2rParser.determineEvent(currNLAction, app, new ArrayList<>());
        final boolean isCloseEvent = DeviceUtils.isCloseApp(event);
        final boolean isAppWord = GeneralUtils.isAppWord(currNLAction.getObject(), app.getName(), app.getPackageName());
        if (isCloseEvent && isAppWord) {
            AppStep appStep = new AppStep(event, null, app.getPackageName());
            appStep.setScreenshotFile(null);
            return appStep;
        }
        return null;
    }

    /**
     * This method returns an AppStep with the information of the component and
     * just the action that was used to find the component
     */
    public ResolvedStepResult resolveActionInGraphBasedOnComponent(NLAction currNLAction, AppGraphInfo executionGraph,
                                                                   GraphState currentState, Screen currentScreen,
                                                                   AppStep lastStep) {

        log.debug("Revolving action based on component only >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Appl app = executionGraph.getApp();

        LinkedHashMap<AppStep, Double> candidateSteps = new LinkedHashMap<>();

        // 1. Get all candidate nodes that are in range of graphMaxDepthCheck
        LinkedHashMap<GraphState, Integer> stateCandidates = new LinkedHashMap<>();
        getCandidateGraphStates(executionGraph.getGraph(), stateCandidates, currentState, 0, graphMaxDepthCheck);
        stateCandidates.remove(GraphState.START_STATE);

        ResolvedStepResult result = new ResolvedStepResult();

        log.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);

        // 2. For each State get a StepAction
        for (Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
            GraphState candidateState = candidateEntry.getKey();

            log.debug("Checking candidate state/screen: " + candidateState.getUniqueHash());

            final Entry<AppStep, Double> stepEntry = resolveStepFromComponentInState(app, currNLAction,
                    candidateState, lastStep, false, result);

            if (stepEntry == null)
                continue;

            //--------------------
            //add the step to the map of candidate steps

            final AppStep tempStep = stepEntry.getKey();
            final Double stepScore = stepEntry.getValue();

            final Double currentScore = candidateSteps.get(tempStep);
            if (currentScore == null) {
                candidateSteps.put(tempStep, stepScore);
                log.debug(String.format("New candidate step build: %s", tempStep));
            }

        }

        //----------------------------------------------------

      /*  GraphState liveCurrentState = graphGenerator.getGraphState(currentScreen, -1);
        {
            log.debug("Checking current screen: " + liveCurrentState.getUniqueHash());

            final Entry<AppStep, Double> stepEntry = resolveStepFromComponentInState(app, currNLAction,
                    liveCurrentState, lastStep, true, result);

            if (stepEntry != null) {

                //--------------------
                //add the step to the map of candidate steps

                final AppStep tempStep = stepEntry.getKey();
                final Double stepScore = stepEntry.getValue();

                final Double currentScore = candidateSteps.get(tempStep);
                if (currentScore == null) {
                    candidateSteps.put(tempStep, stepScore);
                    log.debug(String.format("New candidate step build: %s", tempStep));
                }
            }
        }*/

        log.debug("Candidate steps (" + candidateSteps.size() + "):" + candidateSteps);

        //----------------------------------------------------

        //  3. sort the steps based on the score
        List<ImmutablePair<AppStep, Double>> stepScores = new ArrayList<>();

        // Give priority to components base on how far they are from the current
        // state, so the closer they are the higher the score
        for (Entry<AppStep, Double> stepEntry : candidateSteps.entrySet()) {
            final AppStep step = stepEntry.getKey();
            final Double matchingSimilarity = stepEntry.getValue();

            double candidateDistance = stateCandidates.get(step.getCurrentState());
            double score = matchingSimilarity / (candidateDistance + 1);
            stepScores.add(new ImmutablePair<>(step, score));
        }

        //sort in descending order
        stepScores.sort((step1, step2) -> step2.right.compareTo(step1.right));

        //-----------------------------------

        // Return the first one (i.e., the one with the greatest score
        if (!stepScores.isEmpty()) {
            result.setStep(stepScores.get(0).left);
        }

        return result;
    }

    private Entry<AppStep, Double> resolveStepFromComponentInState(Appl app, NLAction currNLAction,
                                                                   GraphState state, AppStep lastStep,
                                                                   boolean checkCurrentScreen,
                                                                   ResolvedStepResult result) {

        List<AppGuiComponent> stateComponents = state.getComponents();

        //------------------------------------
        //Determine the event, but if there's an exception, the event is taken as CLICK by default

        Integer event = DeviceActions.CLICK;
        try {
            event = s2rParser.determineEvent(currNLAction, app, stateComponents);
        } catch (ActionParsingException e) {
            log.debug("Could not determine the event in candidate state/screen: "
                    + state.getUniqueHash() + " - " + e.getResult());
            result.addCount(e);
            return null;
        }

        //-----------------------------------
        //Determine the component

        Entry<AppGuiComponent, Double> component = null;
        try {

            //first by avoiding checking focused components in the screen in the DEVICE
            // i.e., not in the current state/screen

            component = s2rParser.determineComponent(currNLAction, stateComponents,
                    event, true);
        } catch (ActionParsingException e) {

            if (checkCurrentScreen) {
                try {

                    //if we can't determine the component, try again but this time checking the DEVICE screen
                    //this is useful for types such as "type X", i.e., types where no component is specified

                    Entry<AppGuiComponent, Double> componentFocused = s2rParser.determineComponent(currNLAction,
                            stateComponents, event, false);

                    if (componentFocused != null)
                        component = componentFocused;

                    /*if (lastStep != null) {
                        final AppGuiComponent lastComponent = lastStep.getComponent();
                        if (lastComponent != null) {
                            if (lastComponent.equalsNoDimensions(componentFocused.getKey()))
                                component = componentFocused;
                            else
                                throw new ActionParsingException(e.getResult(), e.getMessage());
                        }
                    }*/
                } catch (ActionParsingException e1) {
                    result.addCount(e1);
                    log.debug("Could not find the component in candidate state/screen: "
                            + state.getUniqueHash() + " - " + e.getResult());
                    if (e.getResult().equals(ParsingResult.MULTIPLE_COMPONENTS_FOUND)) {
                        log.debug(e.getResultData());
                    }
                }
            } else {
                result.addCount(e);
                log.debug("Could not find the component in candidate state/screen: "
                        + state.getUniqueHash() + " - " + e.getResult());
                if (e.getResult().equals(ParsingResult.MULTIPLE_COMPONENTS_FOUND)) {
                    log.debug(e.getResultData());
                }
            }
        }

        //We could not determine the component, then there is nothing else to do
        if (component == null && !DeviceUtils.isSwipe(event) &&
                !DeviceUtils.isChangeRotation(event) &&
                !DeviceUtils.isClickMenuButton(event)) {
            return null;
        }

        //-----------------------------

       /* try {
            event = DeviceActions.CLICK;
            component = s2rParser.determineComponent(currNLAction, stateComponents,
                    event, true);
        } catch (ActionParsingException e) {
            try {
                event = DeviceActions.TYPE;
                component = s2rParser.determineComponent(currNLAction, stateComponents, event, true);
            } catch (ActionParsingException e1) {
                try {
                    event = DeviceActions.SWIPE;
                    component = s2rParser.determineComponent(currNLAction, stateComponents, event, true);
                } catch (ActionParsingException e2) {
                    return null;
                }
            }
        }*/

        //-------------------------------------------------------
        //At this point we have found a component, and resolved an event to be applied to it

        //-------------------------------------------------------
        // Determine the text

        String text = null;
        try {
            Long componentId = null;
            if (component != null)
                componentId = component.getKey().getDbId();
            text = s2rParser.determineText(app, event, componentId, currNLAction, null, false);
            text = DeviceUtils.encodeText(text);
        } catch (ActionParsingException e) {
            log.debug("Could not determine the text for the candidate state/screen: "
                    + state.getUniqueHash() + " - " + e.getResult());
        }

        //-------------------------------
        //build the step!

        AppStep step = new AppStep();
        step.setAction(event);
        step.setText(text);
        step.setCurrentState(state);
        //FIXME: this one wouldn't have an annotated component,
        // so setting the state screenshot may cause some confusion
        step.setScreenshotFile(state.getScreenshotPath());

        //---------------------------
        //Determine the score of this step (based on the similarity score of the component) and set the component to it

        Double compScore = 0d;

        if (component != null) {
            compScore = component.getValue();

            AppGuiComponent compTemp = component.getKey();
            step.setComponent(compTemp);
        } else {
            if (DeviceUtils.isSwipe(event) ||
                    DeviceUtils.isChangeRotation(event) ||
                    DeviceUtils.isClickMenuButton(event)) {
                compScore = 1d;
            }
        }

        return new AbstractMap.SimpleEntry(step, compScore);
    }

}
