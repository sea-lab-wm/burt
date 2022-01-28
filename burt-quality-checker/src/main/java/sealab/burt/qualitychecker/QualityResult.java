package sealab.burt.qualitychecker;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.qualitychecker.graph.GraphState;

import java.util.List;

public @Data
@AllArgsConstructor
class QualityResult {

    private Result result;
    private List<GraphState> matchedStates;

    public QualityResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "QualityResult{" +
                "result=" + result +
                ", matchedStates=" + (matchedStates == null ? 0 : matchedStates.size()) +
                '}';
    }

    public enum Result {
        MATCH, MULTIPLE_MATCH, NO_MATCH, NOT_PARSED
    }
}
