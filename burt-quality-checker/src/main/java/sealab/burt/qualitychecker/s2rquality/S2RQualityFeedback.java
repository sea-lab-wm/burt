package sealab.burt.qualitychecker.s2rquality;


import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Quality feedback for an S2R
 */
public class S2RQualityFeedback {

    //the action that represents the S2R
    private NLAction action;

    //an S2R can have more than one quality assessment
    private List<S2RQualityAssessment> qualityAssessments = new ArrayList<>();

    public S2RQualityFeedback() {
    }

    public S2RQualityFeedback(NLAction action) {
        this.action = action;
    }

    public void addFeedback(S2RQualityFeedback feedback){

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
}
