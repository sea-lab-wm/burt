package sealab.burt.qualitychecker;

import static sealab.burt.qualitychecker.QualityResult.Result.MULTIPLE_MATCH;

public class OBChecker {

    private String app;

    public OBChecker(String app){
        this.app = app;
    }

    public QualityResult checkOb(String obDescription) {
        return new QualityResult(MULTIPLE_MATCH);
    }
}
