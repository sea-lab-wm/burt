package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.GraphPath;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.actionmatcher.*;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.similarity.EmbeddingSimilarityComputer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KevinMoran
 */
public @Slf4j
class NewStepResolver {

    private final int graphMaxDepthCheck;

    public NewStepResolver(int graphMaxDepthCheck) {
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


    }




    public List<ImmutablePair<AppStep, Double>>  resolveActionInGraphConcurrent(String S2RDescription, AppGraphInfo executionGraph,
                                               GraphState currentState) throws Exception {
        Appl app = executionGraph.getApp();

        List<ImmutablePair<AppStep, Double>> matchedAppSteps = new ArrayList<>();



        //-----------------------
        // 1. Get all considered nodes that are in range of GRAPH_MAX_DEPTH_CHECK
        // TODO: check previously executed or seen states

        // TODO: deal with go back

        List<String> gobackPhrases = new ArrayList<>(Arrays.asList("go back", "go back to previous screen", "click the back button",
                "tap the back button"));

        List<Double> scores = EmbeddingSimilarityComputer.computeSimilarities(S2RDescription, gobackPhrases);
        if (Collections.max(scores) > 0.7) {
            AppStep appStep;
            appStep = new AppStep(DeviceActions.BACK, null, app.getPackageName());
            appStep.setScreenshotFile(null); //FIXME: change the screenshot file
            appStep.setCurrentState(currentState);

            matchedAppSteps.add(new ImmutablePair<>(appStep, Collections.max(scores)));

            return matchedAppSteps;

        }



        LinkedHashMap<GraphState, Integer> candidateStates = new LinkedHashMap<>();

        getCandidateGraphStates(executionGraph.getGraph(), candidateStates, currentState, 0, graphMaxDepthCheck);
        candidateStates.remove(GraphState.START_STATE);

        log.debug("Candidate states (" + candidateStates.size() + "): " + candidateStates);


        //-----------------------
        //2. Get all steps from all the candidate states (including their distance)

        List<ImmutablePair<AppStep, Integer>> candidateSteps = new ArrayList<>();


        for (Map.Entry<GraphState, Integer> candidateEntry : candidateStates.entrySet()) {

            Set<GraphTransition> outgoingEdges = executionGraph.getGraph().outgoingEdgesOf(candidateEntry.getKey());


            outgoingEdges.forEach(c -> candidateSteps.add(new ImmutablePair<>(c.getStep(), candidateEntry.getValue())));

        }



        int nThreads = 6;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        //filter out those components with phrases
//        candidateSteps = candidateSteps.stream()
//                .filter(c -> c.getLeft().getPhrases() != null && !c.getLeft().getPhrases().isEmpty())
//                .collect(Collectors.toList());

        //list of all futures

        List<CompletableFuture<ImmutablePair<AppStep, Double>>> futures = new ArrayList<>();
        for (ImmutablePair<AppStep, Integer> candidateEntry : candidateSteps) {
            futures.add(CompletableFuture.supplyAsync(() ->
            {
                try {
                    return processCandidateTransition(S2RDescription, candidateEntry);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CompletionException(e);
                }
            }, executor));
        }


        log.debug("Waiting for futures: " + futures.size());

        //wait until all futures finish, and then continue with the processing

        try {
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            //--------------------------------------------

            //aggregate results
            for (CompletableFuture<ImmutablePair<AppStep, Double>> future : futures) {
                ImmutablePair<AppStep, Double> match = future.get();
                if (match != null) {
                    matchedAppSteps.add(match);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            log.debug(e.getMessage());


        }
        finally{
            executor.shutdown();
        }


        // rank the steps
        matchedAppSteps.sort((a, b) -> b.right.compareTo(a.right));
        if( matchedAppSteps.size() > 5){
            return matchedAppSteps.stream().limit(5).collect(Collectors.toList());
        }

        return matchedAppSteps;

    }

    private ImmutablePair<AppStep, Double> processCandidateTransition(String S2RDescription,  ImmutablePair<AppStep,Integer> candidateEntry) throws Exception {


        AppStep step = candidateEntry.getLeft();

        if (step.getPhrases() != null && !step.getPhrases().isEmpty()) {
            List<String> phrases = step.getPhrases();


            List<Double> scores = EmbeddingSimilarityComputer.computeSimilarities(S2RDescription, phrases);

            if (step.getTransition() != null) {
                log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>" + "\n" +
                        "Checking candidate step " + step.getTransition().getId() + "\n" +
                        "Checking candidate step phrases " + phrases.toString() + "\n" +
                        "Checking matched scores " + scores.toString());
            }

            if (Collections.max(scores) > 0.7) {


                return new ImmutablePair<>(step, Collections.max(scores) / (candidateEntry.getRight() + 1));

            } else {

                return null;
            }
        }else{
            return null;
        }
    }

}
