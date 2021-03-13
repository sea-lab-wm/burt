package sealab.burt.qualitychecker;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class QualityResult {

    public enum Result{ MATCH, MULTIPLE_MATCH, NO_MATCH };

    private Result result;


}
