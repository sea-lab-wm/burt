package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.actionparser.NLActionS2RParser;
import sealab.burt.qualitychecker.actionparser.ScreenResolver;
import sealab.burt.qualitychecker.graph.AppGraphInfo;
import sealab.burt.qualitychecker.graph.GraphState;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.qualitychecker.QualityResult.Result.*;

public @Slf4j
class OBChecker {

    private static final int GRAPH_MAX_DEPTH_CHECK = 30;

    private final String appName;
    private final String appVersion;
    private final NLActionS2RParser s2rParser;
    private final ScreenResolver resolver;
    private final String parsersBaseFolder;

    private GraphState currentState;
    private AppGraphInfo executionGraph;

    public OBChecker(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = BurtConfigPaths.nlParsersBaseFolder;

        s2rParser = new NLActionS2RParser(null, BurtConfigPaths.qualityCheckerResourcesPath, true);
        resolver = new ScreenResolver(s2rParser, GRAPH_MAX_DEPTH_CHECK);
    }

    /*public OBChecker(String appName, String appVersion, String parsersBaseFolder, String resourcesPath,
                     String crashScopeDataPath) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.parsersBaseFolder = parsersBaseFolder;
        this.crashScopeDataPath = crashScopeDataPath;


        s2rParser = new NLActionS2RParser(null, resourcesPath, true);
        resolver = new ScreenResolver(s2rParser, GRAPH_MAX_DEPTH_CHECK);
    }*/

    public QualityResult checkOb(String obDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(parsersBaseFolder, appName, obDescription);
        if (nlActions.isEmpty()) return new QualityResult(NOT_PARSED);

        // avoid actions such as "app not work"
        if(nlActions.stream().noneMatch(act -> act.isOBAction() && !HeuristicsNLActionParser.isNotWorkAction(act)))
            return new QualityResult(NO_MATCH, Collections.emptyList());

        if (nlActions.stream().noneMatch(NLAction::isOBAction)) return new QualityResult(NOT_PARSED);
        return matchActions(nlActions);
    }

    private void readGraph() throws Exception {
        if (BurtConfigPaths.crashScopeDataPath == null)
            executionGraph = DBGraphReader.getGraph(appName, appVersion);
        else
            executionGraph = JSONGraphReader.getGraph(appName, appVersion);
    }

    private QualityResult matchActions(List<NLAction> nlActions) throws Exception {
        readGraph();

        if (currentState == null)
            currentState = GraphState.START_STATE;

        log.debug("Current state: " + this.currentState);

        //FIXME: focus on the 1st action for now
        NLAction nlAction = nlActions.get(0);

        log.debug("All actions: " + nlActions);
        log.debug("Matching OB action: " + nlAction);

        List<ImmutablePair<GraphState, Double>> matchedStates =
                resolver.resolveStateInGraph(nlAction, executionGraph, currentState);

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
