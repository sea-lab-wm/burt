package sealab.burt.qualitychecker;

import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.MULTIPLE_MATCH;
import static sealab.burt.qualitychecker.QualityResult.Result.NO_MATCH;

public class S2RChecker {
    private String app;

    public S2RChecker(String app) {
        this.app = app;
    }

    public QualityResult checkS2R(String S2RDescription) throws Exception {
        List<NLAction> nlActions = NLParser.parseText(app, S2RDescription);
        if (nlActions.isEmpty()) return new QualityResult(NO_MATCH);
        return matchActions(nlActions);
    }

    private QualityResult matchActions(List<NLAction> nlActions) {
        return new QualityResult(MULTIPLE_MATCH);
    }
}
