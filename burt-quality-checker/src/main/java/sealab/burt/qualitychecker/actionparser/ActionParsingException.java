package sealab.burt.qualitychecker.actionparser;

import java.util.List;

public class ActionParsingException extends Exception {

    private ParsingResult result;
    private List<Object> resultData;

    public ActionParsingException(ParsingResult result) {
        this(result, null);
    }

    public ActionParsingException(ParsingResult result, List<Object> resultData) {
        this.result = result;
        this.resultData = resultData;
    }


    public ParsingResult getResult() {
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
