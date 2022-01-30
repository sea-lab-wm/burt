package sealab.burt.qualitychecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.graph.AppGraphInfo;
import sealab.burt.qualitychecker.graph.GraphState;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.QualityResult.Result.*;

public @Slf4j
class NewOBChecker {

    public static void main(String[] args) throws Exception {
        NewOBChecker obChecker = new NewOBChecker("androidtoken", "2.10");
        obChecker.readGraph();
        QualityResult result = obChecker.checkOb("got an error when I delete a token");
        log.debug(result.toString());
        log.debug(result.getMatchedStates().get(0).getScreenshotPath());
    }

    private static final int GRAPH_MAX_DEPTH_CHECK = 30;

    private final String appName;
    private final String appVersion;

    private final NewScreenResolver resolver;


    private GraphState currentState;
    private AppGraphInfo executionGraph;

    public NewOBChecker(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;


        this.resolver = new NewScreenResolver(GRAPH_MAX_DEPTH_CHECK);
    }

    public QualityResult checkOb(String obDescription) throws Exception {
        // we use the whole information to do the match

        return matchOB(obDescription);
    }


    private void readGraph() throws Exception {
        if (BurtConfigPaths.crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion);
        else
            executionGraph = JSONGraphReader.getGraph(appName, appVersion);
    }


    private QualityResult matchOB(String obDescription) throws Exception {
        readGraph();

        if (currentState == null)
            currentState = GraphState.START_STATE;

        List<ImmutablePair<GraphState, Double>> matchedStates =
                resolver.resolveStateInAugmentedGraph(obDescription, executionGraph, currentState);

        if (matchedStates == null || matchedStates.isEmpty())
            return new QualityResult(NO_MATCH, Collections.emptyList());
        else if (matchedStates.size() == 1)
            return new QualityResult(MATCH, Collections.singletonList(matchedStates.get(0).getLeft()));
        else
            return new QualityResult(MULTIPLE_MATCH, matchedStates.stream()
                    .map(ImmutablePair::getLeft)
                    .collect(Collectors.toList()));


    }
}
