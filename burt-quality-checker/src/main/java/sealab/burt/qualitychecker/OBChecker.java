package sealab.burt.qualitychecker;

import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.graph.AppGraphInfo;

import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.*;

public class OBChecker {

    private final String appName;
    private final String appVersion;

    public OBChecker(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    public QualityResult checkOb(String obDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(appName, obDescription);
        if (nlActions.isEmpty()) return new QualityResult(NO_PARSED);
        return matchActions(nlActions);
    }

    private QualityResult matchActions(List<NLAction> nlActions) throws Exception {
        AppGraphInfo graph = GraphReader.getGraph(appName, appVersion);
        //TODO: continue here
        return new QualityResult(MULTIPLE_MATCH);
    }
}
