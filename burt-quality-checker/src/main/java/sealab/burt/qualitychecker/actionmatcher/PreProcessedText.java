package sealab.burt.qualitychecker.actionmatcher;

import seers.textanalyzer.entity.Token;

import java.util.ArrayList;
import java.util.List;

public class PreProcessedText {

    String original;
    String lowerCased;
    List<Token> allTokens = new ArrayList<>();
    String lemmatized;
    List<Token> preprocessedTokens = new ArrayList<>();
    String preprocessed;
    String otherText;
    String componentType;

    @Override
    public String toString() {
        return "prepTxt{" +
                "p='" + preprocessed + '\'' +
                ", o='" + otherText + '\'' +
                ", cT='" + componentType + '\'' +
                '}';
    }

}
