package sealab.burt.server.actions.s2r;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.S2R_QUALITY_RESULT;

public
@Slf4j
class RephraseS2RAction extends ChatbotAction {

    public RephraseS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        StringBuilder message = new StringBuilder("Oops, it seems ");
        message.append(getFeedbackMessage(feedback));
        message.append(" Can you please rephrase the step more accurately?");
        return new ChatbotMessage(message.toString());

    }

    private String getFeedbackMessage(QualityFeedback feedback){

        S2RQualityAssessment assessment = feedback.getQualityAssessments().get(0);
        NLAction action = feedback.getAction();

        log.debug("Action: " + action);

        if (assessment.isObjsVocabMismatch() && assessment.isVerbVocabMismatch())
            return "the vocabulary of the step does not match the vocabulary of the app.";
        else if (assessment.isVerbVocabMismatch())
            return String.format("the terms \"%s\" do not match a valid action from the app.",
                    action.getAction());
        else {
            final String objs = getObjs(action);
            if (StringUtils.isEmpty(objs))
                return "some vocabulary of this step is missing.";
            else
                return String.format("the terms \"%s\" do not match a valid UI component from the app.", objs);
        }
    }

    private String getObjs(NLAction action) {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.isEmpty(action.getObject()) ? "" : action.getObject());
        builder.append(" ");
        builder.append(StringUtils.isEmpty(action.getPreposition()) ? "" : action.getPreposition());
        builder.append(" ");
        builder.append(StringUtils.isEmpty(action.getObject2()) ? "" : action.getObject2());
        return builder.toString().trim();
    }

}

