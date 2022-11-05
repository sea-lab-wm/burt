package sealab.burt.statematcher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Triplet;
import org.junit.platform.commons.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.NewS2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

@Slf4j
public class StateMatcher {
    enum MatcherType{
        ORIGINAL_BURT, PHRASES_SBERT_BURT
    }

    public RetrievalResults match(Triplet bugInfo, ShortLabeledBugReport bugReport, MatcherType mrType) throws Exception {
        log.debug("BR: " + bugReport.getTitle().getValue());
        
        String appName = bugInfo.getValue1().toString();
        String appVersion = bugInfo.getValue2().toString();
        String bugID = bugInfo.getValue0().toString();

        NewS2RChecker s2RChecker = new NewS2RChecker(appName, appVersion, bugID);

        LinkedList<String> allS2RSentences = getS2RSentences(bugReport);
        QualityFeedback qualityResultS2R = s2RChecker.checkS2R(allS2RSentences);
        List<S2RQualityAssessment> assessmentResults = qualityResultS2R.getAssessmentResults();

        LinkedHashSet<String> candidateStatesS2R = new LinkedHashSet<>();
        S2RQualityAssessment assessmentResult = assessmentResults.get(0);
        if (assessmentResult.getCategory() != S2RQualityCategory.LOW_Q_VOCAB_MISMATCH) {

            List<AppStep> matchedSteps = assessmentResult.getMatchedSteps();

            for (AppStep step : matchedSteps) {
                GraphTransition transition = step.getTransition();
                GraphState targetState = transition.getTargetState(); 
                candidateStatesS2R.add(targetState.getUniqueHash().toString());
            }
        }

        int numOfScreens = s2RChecker.getGraph().vertexSet().size();

        return new RetrievalResults(new ArrayList<>(candidateStatesS2R), numOfScreens);
    }


    private LinkedList<String> getS2RSentences(ShortLabeledBugReport bugReport) {
        LinkedList<String> allS2RSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getSr()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getSr())) {
            String title = bugReport.getTitle().getValue();
            allS2RSentences.add(0, title);
        }
        return allS2RSentences;
    }

}
