package sealab.burt.qualitychecker;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class QualityResult {

    public enum Result{
        MATCH, MULTIPLE_MATCH, NO_MATCH, NO_S2R_INPUT, MISSING_STEPS, NO_PARSED
    };

    QualityResult(Result result){
        this.result = result;
    }

    private Result result;
    private String description;
    private String screenshotPath;
    private String qualityFeedback;


}
