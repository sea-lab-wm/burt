package sealab.burt.qualitychecker;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections.KeyValue;


import java.security.KeyStore;
import java.util.List;
import java.util.Map;

public @Data @AllArgsConstructor
class QualityResult {

    public enum Result{
        MATCH, MULTIPLE_MATCH, NO_MATCH, NO_S2R_INPUT, MISSING_STEPS, NO_PARSED
    }

    QualityResult(Result result){
        this.result = result;
    }

    private Result result;
    private String description;
    private String screenshotPath;
    private String qualityFeedback;
    private List<KeyValue> descriptionScreenshotPath;

    public QualityResult( Result result,  List<KeyValue> descriptionScreenshotPath){
        this.result = result;
        this.descriptionScreenshotPath = descriptionScreenshotPath;
    }
    public QualityResult(Result result, String description, String screenshotPath, String qualityFeedback){
        this.result = result;
        this.description = description;
        this.screenshotPath = screenshotPath;
        this.qualityFeedback = qualityFeedback;
    }
    public QualityResult(Result result, String description, String screenshotPath){
        this.result = result;
        this.description = description;
        this.screenshotPath = screenshotPath;

    }


}
