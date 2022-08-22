package sealab.burt.qualitychecker.s2rquality;


import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quality feedback for an S2R
 */
public class QualityFeedback {

    //the action that represents the S2R
    private NLAction action;

    //an S2R can have more than one quality assessment
    private List<S2RQualityAssessment> qualityAssessments = new ArrayList<>();

    public QualityFeedback() {
    }

    public QualityFeedback(NLAction action) {
        this.action = action;
    }

    public static QualityFeedback noParsedFeedback() {
        QualityFeedback feedback = new QualityFeedback();
        S2RQualityAssessment assessment = new S2RQualityAssessment(S2RQualityCategory.LOW_Q_NOT_PARSED);
        feedback.addQualityAssessment(assessment);
        return feedback;
    }

    public void addFeedback(QualityFeedback feedback){

        if (action == null)
            throw new RuntimeException("Cannot check the action because it is null!");

        if (!action.equals(feedback.action))
            throw new RuntimeException("The actions do not match!");

        //FIXME: we may have repeated types of qualityAssessments for the action
        this.qualityAssessments.addAll(feedback.qualityAssessments);
    }

    public void addQualityAssessment(S2RQualityAssessment assessment){
        this.qualityAssessments.add(assessment);
    }

    public NLAction getAction() {
        return action;
    }

    public List<S2RQualityAssessment> getQualityAssessments() {
        return qualityAssessments;
    }

    public void setAction(NLAction action) {
        this.action = action;
    }

    public void setQualityAssessments(List<S2RQualityAssessment> qualityAssessments) {
        this.qualityAssessments = qualityAssessments;
    }

    public List<S2RQualityCategory> getAssessmentCategory() {
        return this.qualityAssessments.stream()
                .map(S2RQualityAssessment::getCategory)
                .collect(Collectors.toList());
    }

    public List<S2RQualityAssessment> getAssessmentResults() {
        return this.qualityAssessments;
    }

}
