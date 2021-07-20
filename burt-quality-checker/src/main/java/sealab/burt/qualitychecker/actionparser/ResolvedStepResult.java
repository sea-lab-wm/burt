package sealab.burt.qualitychecker.actionparser;

import sealab.burt.qualitychecker.graph.AppStep;

import java.util.*;
import java.util.stream.Collectors;

public class ResolvedStepResult {

    private AppStep step;
    private HashMap<ParsingResult, Integer> matchResultCounts = new HashMap<>();
    private HashMap<ParsingResult, Set<Object>> matchResultElements = new HashMap<>();

    public ResolvedStepResult() {
    }

    public ResolvedStepResult(AppStep step) {
        this.step = step;
    }

    public AppStep getStep() {
        return step;
    }

    public void setStep(AppStep step) {
        this.step = step;
    }

    public void addCount(ActionParsingException e) {
        if (matchResultCounts == null) matchResultCounts = new LinkedHashMap<>();
        if (e == null) return;
        final ParsingResult result = e.getResult();
        addCount(result);
        addElement(e);

    }

    private void addElement(ActionParsingException e) {
        final Set<Object> elements = matchResultElements.getOrDefault(e.getResult(), new LinkedHashSet<>());
        if (e.getResultData() != null)
            elements.add(e.getResultData());
        matchResultElements.put(e.getResult(), elements);
    }

    public void addCount(ParsingResult result) {
        if (result == null) return;
        final Integer count = matchResultCounts.getOrDefault(result, 0);
        matchResultCounts.put(result, count + 1);
    }


    public Set<Object> getElements(ParsingResult... parsingResults) {
        return Arrays.stream(parsingResults)
                .map(r -> matchResultElements.get(r))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public boolean isStepNotMatched() {
        return this.step == null;
    }

    public boolean isAnyParsingResultsPresent(ParsingResult... parsingResults) {
        for (ParsingResult parsingResult : parsingResults) {
            if (matchResultCounts.containsKey(parsingResult)) return true;
        }
        return false;
    }

    public boolean anyActionResultPresent() {
        return isAnyParsingResultsPresent(ParsingResult.ACTION_NOT_MAPPED,
                ParsingResult.AMBIGUOUS_ACTION,
                ParsingResult.ACTION_NOT_MATCHED, ParsingResult.UNKNOWN_ACTION);
    }

    public boolean anyObjsResultPresent() {
        return isAnyParsingResultsPresent(ParsingResult.EMPTY_OBJECTS, ParsingResult.COMPONENT_NOT_FOUND,
                ParsingResult.COMPONENT_NOT_SPECIFIED, ParsingResult.INCORRECT_COMPONENT_FOUND);
    }

    public boolean anyAmbiguousResultPresent() {
        return isAnyParsingResultsPresent(ParsingResult.AMBIGUOUS_ACTION, ParsingResult.MULTIPLE_COMPONENTS_FOUND);
    }

    public Set<Object> getAmbiguousComponents() {
        return getElements(ParsingResult.MULTIPLE_COMPONENTS_FOUND);
    }

    public Set<Object> getAmbiguousActions() {
        return getElements(ParsingResult.AMBIGUOUS_ACTION);
    }
}
