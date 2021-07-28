package sealab.burt.qualitychecker.actionmatcher;

import java.util.List;

public class ActionMatchingException extends Exception {

    private final MatchingResult result;
    private final List<Object> resultData;

    public ActionMatchingException(MatchingResult result) {
        this(result, null);
    }

    public ActionMatchingException(MatchingResult result, List<Object> resultData) {
        this.result = result;
        this.resultData = resultData;
    }


    public MatchingResult getResult() {
        return result;
    }

    public List<Object> getResultData() {
        return resultData;
    }

    @Override
    public String toString() {
        return "AME{" +
                "r=" + result +
                ", d='" + resultData + '\'' +
                '}';
    }
}
