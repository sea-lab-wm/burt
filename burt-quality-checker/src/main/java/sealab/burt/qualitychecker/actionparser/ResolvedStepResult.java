package sealab.burt.qualitychecker.actionparser;

import sealab.burt.qualitychecker.graph.AppStep;

import java.util.*;
import java.util.stream.Collectors;

public class ResolvedStepResult {

    private AppStep step;
    private HashMap<MatchingResult, Integer> matchResultCounts = new HashMap<>();
    private HashMap<MatchingResult, Set<Object>> matchResultElements = new HashMap<>();

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
        final MatchingResult result = e.getResult();
        addCount(result);
        addElement(e);

    }

    private void addElement(ActionParsingException e) {
        final Set<Object> elements = matchResultElements.getOrDefault(e.getResult(), new LinkedHashSet<>());
        if (e.getResultData() != null)
            elements.add(e.getResultData());
        matchResultElements.put(e.getResult(), elements);
    }

    public void addCount(MatchingResult result) {
        if (result == null) return;
        final Integer count = matchResultCounts.getOrDefault(result, 0);
        matchResultCounts.put(result, count + 1);
    }


    public Set<Object> getElements(MatchingResult... matchingResults) {
        return Arrays.stream(matchingResults)
                .map(r -> matchResultElements.get(r))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public boolean isStepNotMatched() {
        return this.step == null;
    }

    public boolean isAnyMatchingResultsPresent(MatchingResult... matchingResults) {
        for (MatchingResult matchingResult : matchingResults) {
            if (matchResultCounts.containsKey(matchingResult)) return true;
        }
        return false;
    }

    public boolean anyActionResultPresent() {
        return isAnyMatchingResultsPresent(MatchingResult.ACTION_NOT_MAPPED,
                MatchingResult.AMBIGUOUS_ACTION,
                MatchingResult.ACTION_NOT_MATCHED, MatchingResult.UNKNOWN_ACTION);
    }

    public boolean anyObjsResultPresent() {
        return isAnyMatchingResultsPresent(MatchingResult.EMPTY_OBJECTS, MatchingResult.COMPONENT_NOT_FOUND,
                MatchingResult.COMPONENT_NOT_SPECIFIED, MatchingResult.INCORRECT_COMPONENT_FOUND);
    }

    public boolean anyAmbiguousResultPresent() {
        return isAnyMatchingResultsPresent(MatchingResult.AMBIGUOUS_ACTION, MatchingResult.MULTIPLE_COMPONENTS_FOUND);
    }

    public Set<Object> getAmbiguousComponents() {
        return getElements(MatchingResult.MULTIPLE_COMPONENTS_FOUND);
    }

    public Set<Object> getAmbiguousActions() {
        return getElements(MatchingResult.AMBIGUOUS_ACTION);
    }
}
