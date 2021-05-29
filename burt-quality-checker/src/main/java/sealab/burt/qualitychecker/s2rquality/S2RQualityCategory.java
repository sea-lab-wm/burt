package sealab.burt.qualitychecker.s2rquality;

public enum S2RQualityCategory {
    // high-quality
    HIGH_QUALITY("EM", "This S2R matches the following app interaction:"),

    // low-quality
    LOW_Q_NOT_PARSED("NP", "-"),
    LOW_Q_AMBIGUOUS("AS", "-"),
    LOW_Q_VOCAB_MISMATCH("VM", "-"),
    LOW_Q_INCORRECT_INPUT("IV", "This S2R's input value is missing or incorrect. An example of a valid value is:"),
//    LOW_Q_COMPOSITE("C-LQ", "The S2R can be decomposed into the following steps:"),

    // missing
    MISSING("MS", "There are app interactions that are missing in the bug report and should be executed before this " +
            "S2R:");

    private final String code;
    private final String description;

    private S2RQualityCategory(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
