package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.qualitychecker.actionmatcher.ActionMatchingException;
import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.graph.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import sealab.burt.qualitychecker.similarity.EmbeddingSimilarityComputer;
public @Slf4j
class NewScreenResolver {

    private final int graphMaxDepthCheck;

    public NewScreenResolver(int graphMaxDepthCheck) {
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


    // new code
    public List<ImmutablePair<GraphState, Double>> resolveStateInAugmentedGraph(String obDescription,
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

        List<ImmutablePair<GraphState, Double>> matchedStates = new ArrayList<>();

//        for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
//            ImmutablePair<GraphState, Double> match = processCandidateState(obDescription, candidateEntry);
//            if (match != null) {
//                matchedStates.add(match);
//            }
//
//        }


        //list of all futures
        try {

            List<CompletableFuture<ImmutablePair<GraphState, Double>>> futures = new ArrayList<>();
            for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
                futures.add(CompletableFuture.supplyAsync(() ->
                {
                    try {
                        return processCandidateState(obDescription, candidateEntry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }, executor));
            }


            log.debug("Waiting for futures: " + futures.size());

            //wait until all futures finish, and then continue with the processing
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            //--------------------------------------------

            //aggregate results
            for (CompletableFuture<ImmutablePair<GraphState, Double>> future : futures) {
                ImmutablePair<GraphState, Double> match = future.get();
                if (match != null) {
                    matchedStates.add(match);
                }
            }

            return rankMatchedStates(matchedStates);

        } finally {
            executor.shutdown();
        }

    }


    private List<ImmutablePair<GraphState, Double>> rankMatchedStates(List<ImmutablePair<GraphState, Double>> matchedStates) {
        log.debug("Matched states (" + matchedStates.size() + "):" + matchedStates);


        //---------------------------------------------

        // sort based on the score
//        List<ImmutablePair<GraphState, Double>> stateScores = new ArrayList<>();

        // Give priority to components based on how far they are from the current
        // state, so the closer they are the higher the score
//        for (Map.Entry<GraphState, Double> stateEntry : matchedStates.entrySet()) {
//            final GraphState step = stateEntry.getKey();
//            final Double distance = stateEntry.getValue();
//
//            double score = 1d / (distance + 1);
//            stateScores.add(new ImmutablePair<>(step, score));
//        }
        matchedStates.sort((a, b) -> b.right.compareTo(a.right));

        return matchedStates;
    }

    // new code
    private ImmutablePair<GraphState, Double> processCandidateState(String ObDescription,
                                                                    Map.Entry<GraphState, Integer> candidateEntry) throws Exception {
        final GraphState candidateState = candidateEntry.getKey();
        final Integer distance = candidateEntry.getValue();

        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.debug("Checking candidate state/screen: " + candidateState.getUniqueHash());

        //-------------------------------------
        // Get the components of the current candidate screen

        List<AppGuiComponent> stateComponents = candidateState.getComponents();
        if (stateComponents == null)
            return null;

//        for (AppGuiComponent stateComponent : stateComponents){
//            log.debug("Checking phrases: " + stateComponent.getPhrases().toString());
//        }


        //filter out those components with phrases
        stateComponents = stateComponents.stream()
                .filter(c -> c.getPhrases() != null && !c.getPhrases().isEmpty())
                .collect(Collectors.toList());


        if (stateComponents.isEmpty())
            return null;

        List<String> phrases = new ArrayList<>();

        stateComponents.forEach(e -> phrases.addAll(e.getPhrases()));

        //-------------------------------------

            //FIXME: may need other device actions
            double score = determineComponentForOb(ObDescription,
                    phrases);
            if (score > 0.75){
                return new ImmutablePair<>(candidateState, score);
            }

        return null;

    }


    public double determineComponentForOb(String ObDescription, List<String> phrases)
            throws Exception {

        List<Double> scores =  EmbeddingSimilarityComputer.computeSimilarities(ObDescription, phrases);

        return Collections.max(scores);

    }




    private Map.Entry<AppGuiComponent, Double> findComponent(String ObDescription,
                                                                 List<AppGuiComponent> currentScreen){

        for (AppGuiComponent guiComponent: currentScreen){
            List<String> phrases = guiComponent.getPhrases();
            // match ObDescription with phrases
        }

        return null;
    }





}
