package sealab.burt.qualitychecker;

import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.MULTIPLE_MATCH;
import static sealab.burt.qualitychecker.QualityResult.Result.NO_MATCH;

public class OBChecker {

    private String app;
//    private

    public OBChecker(String app) {
        this.app = app;
    }

    public QualityResult checkOb(String obDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(app, obDescription);
        if (nlActions.isEmpty()) return new QualityResult(NO_MATCH);
        return matchActions(nlActions);
    }

    private QualityResult matchActions(List<NLAction> nlActions) {
        return new QualityResult(MULTIPLE_MATCH);
    }
}
