package sealab.burt.server.actions.s2r.prediction;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.S2RCheckerUtils;
import sealab.burt.qualitychecker.actionmatcher.GraphUtils;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.output.BugReportElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.REPORT_S2R;

public @Slf4j
class S2RPredictor {


    public final static BiPredicate<AppStep, AppStep> matchByTransitionId = (step, nonStep) -> {
        GraphTransition stepTransition = step.getTransition();
        GraphTransition nonStepTransition = nonStep.getTransition();
        if (stepTransition == null || nonStepTransition == null) return false;
        return stepTransition.getId().equals(nonStepTransition.getId());
    };

    private final AppGraph<GraphState, GraphTransition> graph;

    public S2RPredictor(AppGraph<GraphState, GraphTransition> graph) {
        this.graph = graph;
    }

    /**
     * Yang Song
     * Get all possible paths for S2R prediction
     */
    public List<GraphPath<GraphState, GraphTransition>> getAllRankedPaths(GraphState sourceState,
                                                                          GraphState targetState) {

        log.debug(String.format("Getting the paths between %s and %s", sourceState, targetState));

        List<GraphPath<GraphState, GraphTransition>> paths = GraphUtils.findPaths(graph,
                sourceState, targetState, false, Integer.MAX_VALUE);
        sortPathsByScores(paths);
        return paths;

    }

    public List<GraphPath<GraphState, GraphTransition>> getFirstKDummyPaths(Integer k, GraphState sourceState,
                                                                            GraphState targetState) {

        Set<GraphTransition> outgoingEdges = graph.outgoingEdgesOf(sourceState);
        outgoingEdges = getNonLoops(outgoingEdges);
        List<GraphPath<GraphState, GraphTransition>> paths = new ArrayList<>();
        for (GraphTransition outgoingEdge : outgoingEdges) { //first level

            GraphState tgtState = outgoingEdge.getTargetState();

            Set<GraphTransition> outgoingEdges2 = graph.outgoingEdgesOf(tgtState);
            outgoingEdges2 = getNonLoops(outgoingEdges2);

            for (GraphTransition outgoingEdge2 : outgoingEdges2) { //second level
                GraphState tgtState2 = outgoingEdge2.getTargetState();

                Set<GraphTransition> outgoingEdges3 = graph.outgoingEdgesOf(tgtState2);
                outgoingEdges3 = getNonLoops(outgoingEdges3);

                for (GraphTransition outgoingEdge3 : outgoingEdges3) { //third level
                    List<GraphTransition> onePath = new ArrayList<>();
                    onePath.add(outgoingEdge);
                    onePath.add(outgoingEdge2);
                    onePath.add(outgoingEdge3);


                    GraphWalk<GraphState, GraphTransition> path = new GraphWalk<>(
                            graph, sourceState, targetState, onePath, 0.0);
                    paths.add(path);
                }
            }
        }

        paths.addAll(paths);

        sortPathsByScores(paths);

        return paths.subList(0, Math.min(k, paths.size()));

    }

    private Set<GraphTransition> getNonLoops(Set<GraphTransition> outgoingEdges) {
        return outgoingEdges.stream()
                .filter(e -> !e.getSourceState().equals(e.getTargetState()))
                .collect(Collectors.toSet());
    }

    private static void sortPathsByScores(List<GraphPath<GraphState, GraphTransition>> paths) {
        //sort by computed score in descending order
        paths.sort((p1, p2) -> Double.compare(computePathScore(p2), computePathScore(p1)));
    }

    private static double computePathScore(GraphPath<GraphState, GraphTransition> path) {
        List<GraphTransition> edgeList = path.getEdgeList();

        //compute the sum of all the weights
        double weightSum = 0;
        for (GraphTransition transition : edgeList) {
            weightSum += transition.getWeight();
        }
        double score = weightSum / edgeList.size(); //at this point, we compute the average of the weights
        score += 1.0 / edgeList.size(); //we sum the 1/(# of edges)

        return score;
    }

    public List<AppStep> getStateLoops(GraphState sourceState, AppStep lastStep, List<AppStep> nonSelectedSteps) {

        //get the loops of the current source state
        final Set<GraphTransition> stateLoops = graph
                .outgoingEdgesOf(sourceState).stream()
                .filter(tr -> tr.getTargetState().equals(sourceState))
                .collect(Collectors.toSet());

        //---------------------------

        List<AppGuiComponent> components = sourceState.getComponents();
        List<AppStep> pathSteps = new LinkedList<>();

        if (stateLoops.isEmpty()) {
            return pathSteps;
        }

        //get the enabled components
        //FIXME: should we consider all the components? this was a copy/paste from EULER
        List<AppGuiComponent> enabledComponents = null;
        if (components != null)
            enabledComponents = components.stream()
                    .filter(AppGuiComponent::getEnabled)
                    .collect(Collectors.toList());

        //filter the loops that operate on the enabled components and sort them based on their appearance in
        // the screen: top-down
        List<GraphTransition> sortedTransitions = S2RCheckerUtils.filterAndSortTransitions(stateLoops,
                enabledComponents);

        //----------------------------------

        List<GraphTransition> filteredTransitions = sortedTransitions;
        //filter out the transitions prior to the last step
        final int index = S2RCheckerUtils.indexOf.apply(sortedTransitions, lastStep);
        if (index != -1) {
            filteredTransitions = sortedTransitions.subList(index + 1, sortedTransitions.size());
        }

        //------------------------------------

        pathSteps = filteredTransitions.stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());

        pathSteps = S2RCheckerUtils.removeCheckedSteps(pathSteps, enabledComponents);

        //--------------------------------

        pathSteps = filterOutNonSelectedPreviousPredictedS2R(nonSelectedSteps, pathSteps);

        //----------------------------

        return pathSteps;
    }

    private List<AppStep> filterOutNonSelectedPreviousPredictedS2R(List<AppStep> nonSelectedSteps,
                                                                   List<AppStep> pathSteps) {
        //remove the predicted S2R that the user deemed not correct in the last prediction session
        if (nonSelectedSteps != null) {
            pathSteps = pathSteps.stream()
                    .filter(step -> nonSelectedSteps.stream()
                            .noneMatch(nonStep -> matchByTransitionId.test(step, nonStep)
                            ))
                    .collect(Collectors.toList());
        }
        return pathSteps;
    }

    public List<AppStep> getPathWithLoops(S2RChecker s2rchecker, GraphPath<GraphState, GraphTransition> path,
                                          ConversationState state, GraphState currentState,
                                          List<AppStep> nonSelectedSteps, int maxStepsToShowInPath) {
        // we convert the transitions to the steps
        List<AppStep> pathSteps = convertGraphTransitionsToAppStep(path);

        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);

        if (bugReportElements == null)
            throw new RuntimeException("The S2R bug report elements are required");

        // Add the state loops in order to the path
        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();
        List<AppStep> stepsWithLoops = new LinkedList<>();
        s2rchecker.addIntermediateStepsInShortestPath(null, lastStep, stepsWithLoops, pathSteps,
                currentState.getComponents());

        //-----------------

        stepsWithLoops = filterOutNonSelectedPreviousPredictedS2R(nonSelectedSteps, stepsWithLoops);

        //-----------------

        List<AppStep> stepSubSetWithLoops = stepsWithLoops.subList(0, Math.min(maxStepsToShowInPath,
                stepsWithLoops.size()));

        return stepSubSetWithLoops;
    }

    public static List<AppStep> convertGraphTransitionsToAppStep(GraphPath<GraphState, GraphTransition> path) {
        // get app steps
        return path.getEdgeList().stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());
    }
}
