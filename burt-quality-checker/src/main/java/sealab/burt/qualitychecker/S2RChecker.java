package sealab.burt.qualitychecker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static sealab.burt.qualitychecker.QualityResult.Result.MISSING_STEPS;
import static sealab.burt.qualitychecker.QualityResult.Result.AMBIGUOUS;
import static sealab.burt.qualitychecker.QualityResult.Result.IS_OK;
import static sealab.burt.qualitychecker.QualityResult.Result.LACK_INPUT;

public class S2RChecker {
    public static QualityResult checkS2R(String S2RDescription){
        List<QualityResult.Result> choices =  new ArrayList<>();
        choices.add(MISSING_STEPS);
        choices.add(AMBIGUOUS);
        choices.add(IS_OK);
        choices.add(LACK_INPUT);
        int index = (int) (Math.random()* choices.size());

        return new QualityResult(choices.get(index));
    }
}
