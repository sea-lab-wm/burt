package sealab.burt.qualitychecker.actionparser;

import org.apache.commons.lang3.StringUtils;
import seers.textanalyzer.PreprocessingOptionsParser;
import seers.textanalyzer.entity.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
