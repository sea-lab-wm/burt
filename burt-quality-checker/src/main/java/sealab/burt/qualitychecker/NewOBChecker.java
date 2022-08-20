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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static sealab.burt.qualitychecker.QualityResult.Result.*;

public @Slf4j
class NewOBChecker {
	
	private static final Logger log = LoggerFactory.getLogger(NewOBChecker.class);
	
    public static void main(String[] args) throws Exception {
        //NewOBChecker obChecker = new NewOBChecker("androidtoken", "2.10");
    	NewOBChecker obChecker = new NewOBChecker("familyfinance", "1.5.5-DEBUG");
        obChecker.readGraph("2");
        //QualityResult result = obChecker.checkOb("got an error when I delete a token");
        QualityResult result = obChecker.checkOb("when the user changes the appearance of the report from \"view value\" to \"use percent value\" this application will crash and stop working", "2");
        log.debug(result.toString());
        //log.debug(result.getMatchedStates().get(0).getScreenshotPath());
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

    public QualityResult checkOb(String obDescription, String bugID) throws Exception {
        // we use the whole information to do the match

        return matchOB(obDescription, bugID);
    }


    private void readGraph(String bugID) throws Exception {
        if (BurtConfigPaths.crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion, bugID);
        else
            executionGraph = JSONGraphReader.getGraph(appName, appVersion, bugID);
    }


    private QualityResult matchOB(String obDescription, String bugID) throws Exception {
        readGraph(bugID);

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
