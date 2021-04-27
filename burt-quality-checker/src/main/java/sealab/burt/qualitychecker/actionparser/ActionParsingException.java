package sealab.burt.qualitychecker.actionparser;

public class ActionParsingException extends Exception{

    private ParsingResult result;
    private String resultData;

    public ActionParsingException(ParsingResult result) {
        this(result, null);
    }

    public ActionParsingException(ParsingResult result, String resultData) {
        this.result = result;
        this.resultData = resultData;
    }

    public ParsingResult getResult() {
        return result;
    }

    public String getResultData() {
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
