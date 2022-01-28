package sealab.burt.qualitychecker;

import sealab.burt.qualitychecker.actionmatcher.ActionMatchingException;
import sealab.burt.qualitychecker.actionmatcher.MatchingResult;
import sealab.burt.qualitychecker.graph.AppStep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NewMatchingS2RResult {

    List<ActionMatchingException> matchingExceptions = new ArrayList<>();
    List<MatchingResult> matchingResults = new ArrayList<>();
    LinkedHashMap<AppStep, Integer> foundSteps = new LinkedHashMap<>();

    public void addCount(ActionMatchingException e) {
        matchingExceptions.add(e);
    }

    public void addCount(MatchingResult componentFound) {
        matchingResults.add(componentFound);
    }

    public void putFoundStep(AppStep tempStep, Integer distance) {
        foundSteps.put(tempStep, distance);
    }
}