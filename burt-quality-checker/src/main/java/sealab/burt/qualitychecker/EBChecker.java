package sealab.burt.qualitychecker;

import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.NOT_PARSED;

public class EBChecker {

    private final String app;
    private final String appVersion;
    private String parsersBaseFolder;

    public EBChecker(String app, String appVersion, String parsersBaseFolder) {
        this.app = app;
        this.appVersion = appVersion;
        this.parsersBaseFolder = parsersBaseFolder;
    }

    public QualityResult checkEb(String ebDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(parsersBaseFolder, app, ebDescription);
        if (nlActions.isEmpty()) return new QualityResult(NOT_PARSED);
        return matchActions(nlActions);
    }

    private QualityResult matchActions(List<NLAction> nlActions) throws Exception {
//        AppGraphInfo graph = DBGraphReader.getGraph(app, appVersion);
        //TODO: continue here
        return new QualityResult(QualityResult.Result.MATCH);
    }
}
