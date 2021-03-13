package sealab.burt.qualitychecker;

import static sealab.burt.qualitychecker.QualityResult.Result.MULTIPLE_MATCH;

public class OBChecker {

    public static QualityResult checkOb(String obDescription){
        return new QualityResult(MULTIPLE_MATCH);
    }
}
