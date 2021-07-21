package sealab.burt.qualitychecker.actionparser;

import java.util.List;

public class ActionParsingException extends Exception {

    private MatchingResult result;
    private List<Object> resultData;

    public ActionParsingException(MatchingResult result) {
        this(result, null);
    }

    public ActionParsingException(MatchingResult result, List<Object> resultData) {
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
        return "ape{" +
                "r=" + result +
                ", d='" + resultData + '\'' +
                '}';
    }
}
