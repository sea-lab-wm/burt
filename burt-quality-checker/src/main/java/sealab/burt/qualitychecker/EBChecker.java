package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.NOT_PARSED;

public @Slf4j
class EBChecker {

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

        //FIXME: focus on the 1st action for now
        log.debug("All actions: " + nlActions);
        return new QualityResult(QualityResult.Result.MATCH);
    }
}
