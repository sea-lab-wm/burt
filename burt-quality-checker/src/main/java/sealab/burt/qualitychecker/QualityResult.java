package sealab.burt.qualitychecker;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class QualityResult {

    public enum Result{
        MATCH, MULTIPLE_MATCH, NO_MATCH, IS_OK, AMBIGUOUS, NOT_WRITTEN_PROPERLY, LACK_INPUT, MISSING_STEPS
    };

    private Result result;


}
