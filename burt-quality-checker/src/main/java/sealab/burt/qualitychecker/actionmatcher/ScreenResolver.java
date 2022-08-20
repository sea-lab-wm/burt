package sealab.burt.qualitychecker.actionmatcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.graph.*;
import seers.appcore.csv.CSVHelper;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public @Slf4j
class ScreenResolver {

    private final NLActionS2RMatcher s2rParser;
    private final int graphMaxDepthCheck;

    public ScreenResolver(NLActionS2RMatcher s2rParser, int graphMaxDepthCheck) {
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
/*
        Map.Entry<GraphState, Integer> entry = stateCandidates.entrySet().stream()
                .filter(e -> e.getKey().getUniqueHash().equals(-683680957))
                .findFirst().get();

        stateCandidates = new LinkedHashMap<>();
        stateCandidates.put(entry.getKey(), entry.getValue());*/

        log.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);

        //-----------------------

        LinkedHashMap<GraphState, Integer> matchedStates = new LinkedHashMap<>();
        for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
            ImmutablePair<GraphState, Integer> result = processCandidateState(currNLAction, candidateEntry);
            if (result != null)
                matchedStates.put(result.left, result.right);
        }

        //--------------------------------------------

        return rankMatchedStates(matchedStates);

    }


    public List<ImmutablePair<GraphState, Double>> resolveStateInGraphConcurrent(NLAction currNLAction,
                                                                                 AppGraphInfo executionGraph,
                                                                                 GraphState currentState)
            throws Exception {

        // 1. Get all considered nodes that are in range of GRAPH_MAX_DEPTH_CHECK
        LinkedHashMap<GraphState, Integer> stateCandidates = new LinkedHashMap<>();
        getCandidateGraphStates(executionGraph.getGraph(), stateCandidates, currentState, 0, graphMaxDepthCheck);
        stateCandidates.remove(GraphState.START_STATE);

        log.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);


        int nThreads = 6;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        LinkedHashMap<GraphState, Integer> matchedStates = new LinkedHashMap<>();
        //list of all futures
        try {

            List<CompletableFuture<ImmutablePair<GraphState, Integer>>> futures = new ArrayList<>();
            for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
                futures.add(CompletableFuture.supplyAsync(() ->
                        processCandidateState(currNLAction, candidateEntry), executor));
            }


            log.debug("Waiting for futures: " + futures.size());

            //wait until all futures finish, and then continue with the processing
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            //--------------------------------------------

            //aggregate results
            for (CompletableFuture<ImmutablePair<GraphState, Integer>> future : futures) {
                ImmutablePair<GraphState, Integer> match = future.get();
                if (match != null) {
                    matchedStates.put(match.left, match.right);
                }
            }

            return rankMatchedStates(matchedStates);

        } finally {
            executor.shutdown();
        }

    }

    private List<ImmutablePair<GraphState, Double>> rankMatchedStates(LinkedHashMap<GraphState, Integer> matchedStates) {
        log.debug("Matched states (" + matchedStates.size() + "):" + matchedStates);

        //---------------------------------------------

        // sort based on the score
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

    private ImmutablePair<GraphState, Integer> processCandidateState(NLAction currNLAction,
                                                                     Map.Entry<GraphState, Integer> candidateEntry) {
        final GraphState candidateState = candidateEntry.getKey();
        final Integer distance = candidateEntry.getValue();

        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.debug("Checking candidate state/screen: " + candidateState.getUniqueHash());

        //-------------------------------------
        // Get the components of the current candidate screen

        List<AppGuiComponent> stateComponents = candidateState.getComponents();
        if (stateComponents == null)
            return null;

        //filter out those components associated with a step, which duplicate existing components
        stateComponents = stateComponents.stream()
                .filter(c -> c.getParent() != null || "NO_ID".equals(c.getIdXml()))
                .collect(Collectors.toList());

        //-------------------------------------
        // Determine the component
        LinkedHashMap<GraphState, Integer> matchedStates = new LinkedHashMap<>();
        try {
            //FIXME: may need other device actions
            Map.Entry<AppGuiComponent, Double> component = s2rParser.determineComponentForOb(currNLAction,
                    stateComponents, DeviceActions.CLICK, false);
            return new ImmutablePair<>(candidateState, distance);
        } catch (ActionMatchingException e) {
//                log.debug("Could not find the component in the candidate state/screen: "
//                        + candidateState.getUniqueHash() + " - " + e.getResult());
//                if (e.getResult().equals(ParsingResult.MULTIPLE_COMPONENTS_FOUND)) {
//                    log.debug(e.getResultData());
//                }
//                result.addCount(e);
        }
        return null;

    }

}
