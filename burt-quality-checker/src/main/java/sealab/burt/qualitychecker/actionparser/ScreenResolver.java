package sealab.burt.qualitychecker.actionparser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.qualitychecker.graph.*;

import java.util.*;
import java.util.stream.Collectors;

public @Slf4j
class ScreenResolver {

    private NLActionS2RParser s2rParser;
    private int graphMaxDepthCheck;

    public ScreenResolver(NLActionS2RParser s2rParser, int graphMaxDepthCheck) {
        this.s2rParser = s2rParser;
        this.graphMaxDepthCheck = graphMaxDepthCheck;
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

    public List<ImmutablePair<GraphState, Double>> resolveStateInGraph(NLAction currNLAction,
                                                                       AppGraphInfo executionGraph,
                                                                       GraphState currentState) {

        // 1. Get all considered nodes that are in range of GRAPH_MAX_DEPTH_CHECK
        LinkedHashMap<GraphState, Integer> stateCandidates = new LinkedHashMap<>();
        getCandidateGraphStates(executionGraph.getGraph(), stateCandidates, currentState, 0, graphMaxDepthCheck);
        stateCandidates.remove(GraphState.START_STATE);

        log.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);

        //-----------------------

        LinkedHashMap<GraphState, Integer> matchedStates = new LinkedHashMap<>();
        for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
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
            // Determine the component

            try {
                //FIXME: may need other device actions
                Map.Entry<AppGuiComponent, Double> component = s2rParser.determineComponentForOb(currNLAction,
                        stateComponents, DeviceActions.CLICK, false);
                matchedStates.put(candidateState, distance);
            } catch (ActionParsingException e) {
//                log.debug("Could not find the component in the candidate state/screen: "
//                        + candidateState.getUniqueHash() + " - " + e.getResult());
//                if (e.getResult().equals(ParsingResult.MULTIPLE_COMPONENTS_FOUND)) {
//                    log.debug(e.getResultData());
//                }
//                result.addCount(e);
            }

        }

        //--------------------------------------------

        log.debug("Matched states (" + matchedStates.size() + "):" + matchedStates);

        if (matchedStates.isEmpty()) {

            for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
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


                Map.Entry<AppGuiComponent, Double> component = s2rParser.matchAnyComponent(currNLAction, stateComponents);
                if(component!=null)
                    matchedStates.put(candidateState, distance);

            }
        }

        //---------------------------------------------

        // sort base on the score
        List<ImmutablePair<GraphState, Double>> stateScores = new ArrayList<>();

        // Give priority to components based on how far they are from the current
        // state, so the closer they are the higher the score
        for (Map.Entry<GraphState, Integer> stateEntry : matchedStates.entrySet()) {
            final GraphState step = stateEntry.getKey();
            final Integer distance = stateEntry.getValue();

            double score = 1d / (distance + 1);
            stateScores.add(new ImmutablePair<>(step, score));
        }
        stateScores.sort((a, b) -> b.right.compareTo(a.right));

        return stateScores;

    }

}
