package sealab.burt.qualitychecker;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.BurtConfigPaths;

import sealab.burt.qualitychecker.graph.*;

import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;

import java.util.*;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.*;

public @Slf4j
class NewS2RChecker {

    private static final int GRAPH_MAX_DEPTH_CHECK = Integer.MAX_VALUE;
    private final String appName;
    private final String appVersion;
    private final NewStepResolver resolver;

    private GraphState currentState;
    private AppGraphInfo executionGraph;
    private final HashMap<Integer, Integer> statesExecuted = new HashMap<>();



    public NewS2RChecker(String appName, String appVersion) throws Exception {
        this.appName = appName;
        this.appVersion = appVersion;

        resolver = new NewStepResolver(GRAPH_MAX_DEPTH_CHECK);

        readGraph();
        this.currentState = GraphState.START_STATE;
    }


    private void readGraph() throws Exception {
        if (BurtConfigPaths.crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion);
        else
            executionGraph = JSONGraphReader.getGraph(appName, appVersion);
    }


    public QualityFeedback checkS2R(String S2RDescription) throws Exception {
        return checkS2R(S2RDescription, null);
    }


    public QualityFeedback checkS2R(String S2RDescription, Integer currentStateId) throws Exception {

        if (this.currentState == null)
            this.currentState = GraphState.START_STATE;

        if (currentStateId != null) {
            updateState(currentStateId);
        }

        log.debug("Current state: " + this.currentState);


        QualityFeedback qualityFeedback = new QualityFeedback();

        resolveS2R(S2RDescription, currentState, qualityFeedback);


        return qualityFeedback;
    }



    private void resolveS2R(String S2RDescription, GraphState currentState, QualityFeedback s2rQA) throws Exception {


        // First try to match with a Step in the graph
        List<ImmutablePair<AppStep, Double>> result = resolver.resolveActionInGraphConcurrent(S2RDescription, executionGraph, currentState);

        if (result == null || result.isEmpty()){
            s2rQA.addQualityAssessment(new S2RQualityAssessment(LOW_Q_VOCAB_MISMATCH));
            log.debug("Could not match the step" );
        }
        else
            {
            if (result.size() == 1){
                AppStep step = result.get(0).getLeft();
                S2RQualityAssessment qualityAssessment = new S2RQualityAssessment(HIGH_QUALITY);
                qualityAssessment.addMatchedStep(step);
                s2rQA.addQualityAssessment(qualityAssessment);

            }else {
                S2RQualityAssessment qualityAssessment = new S2RQualityAssessment(LOW_Q_AMBIGUOUS);
                result.forEach(s -> qualityAssessment.addMatchedStep(s.getLeft()));
                s2rQA.addQualityAssessment(qualityAssessment);

            }

        }
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
