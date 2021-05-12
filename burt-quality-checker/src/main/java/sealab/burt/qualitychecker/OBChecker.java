package sealab.burt.qualitychecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.graph.*;

import java.util.*;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.QualityResult.Result.MULTIPLE_MATCH;
import static sealab.burt.qualitychecker.QualityResult.Result.NO_PARSED;

public class OBChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OBChecker.class);

    private final String appName;
    private final String appVersion;

    private GraphState currentState;
    private String parsersBaseFolder;

    public OBChecker(String appName, String appVersion, String parsersBaseFolder) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = parsersBaseFolder;
    }

    public QualityResult checkOb(String obDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(parsersBaseFolder, appName, obDescription);
        if (nlActions.isEmpty()) return new QualityResult(NO_PARSED);
        return matchActions(nlActions);
    }

    private QualityResult matchActions(List<NLAction> nlActions) throws Exception {
        AppGraphInfo graph = GraphReader.getGraph(appName, appVersion);

        if (currentState == null)
            currentState = GraphState.START_STATE;


       /* LinkedHashMap<GraphState, Integer> stateCandidates = new LinkedHashMap<>();
        getCandidateGraphStates(graph.getGraph(), stateCandidates, currentState, 0, 10);

        LOGGER.debug("State candidates (" + stateCandidates.size() + "): " + stateCandidates);

        LinkedHashMap<GraphState, Integer> matchedStates = new LinkedHashMap<>();

        //iterate over each candidate
        for (Map.Entry<GraphState, Integer> candidateEntry : stateCandidates.entrySet()) {
            final GraphState candidateState = candidateEntry.getKey();
            final Integer distance = candidateEntry.getValue();

            LOGGER.debug("Checking candidate state/screen: " + candidateState.getUniqueHash());

            //-------------------------------------
            // Get the components of the current candidate screen

            List<AppGuiComponent> stateComponents = candidateState.getComponents();
            if (stateComponents == null)
                continue;

            //filter out those components associated with a step, which duplicate existing components
            stateComponents = stateComponents.stream()
                    .filter(c -> c.getParent() != null || "NO_ID".equals(c.getIdXml()))
                    .collect(Collectors.toList());


        }*/

        //TODO: continue here
        return new QualityResult(MULTIPLE_MATCH);
    }

    private void getCandidateGraphStates(AppGraph<GraphState, GraphTransition> executionGraph,
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
        final Set<GraphState> nextStates = outgoingEdges.stream()
                .map(GraphTransition::getTargetState)
                .collect(Collectors.toCollection(HashSet::new));
        nextStates.remove(currentState);

        for (GraphState state : nextStates) {
            getCandidateGraphStates(executionGraph, stateCandidates, state, currentDistance + 1, maxDistanceToCheck);
        }
//        }
    }
}
