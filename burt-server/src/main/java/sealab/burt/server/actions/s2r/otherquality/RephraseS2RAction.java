package sealab.burt.server.actions.s2r.otherquality;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.S2R_QUALITY_RESULT;

public
@Slf4j
class RephraseS2RAction extends ChatBotAction {

    public RephraseS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        StringBuilder message = new StringBuilder("Oops, it seems ");
        message.append(getFeedbackMessage(feedback));
        return createChatBotMessages(message.toString(),
                " Can you please rephrase the step more accurately or provide a different one?");

    }

    private String getFeedbackMessage(QualityFeedback qFeedback){

        S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                .filter(f -> f.getCategory().equals(S2RQualityCategory.LOW_Q_VOCAB_MISMATCH))
                .findFirst().orElse(null);

        if (assessment == null)
            throw new RuntimeException("A vocabulary mismatch assessment is required");

        NLAction action = qFeedback.getAction();

        log.debug("Action: " + action);

        if (assessment.isObjsVocabMismatch() && assessment.isVerbVocabMismatch())
            return "the vocabulary of the step does not match the vocabulary of the app.";
        else if (assessment.isVerbVocabMismatch())
            return String.format("the terms \"%s\" do not match a valid action from the app.",
                    action.getAction());
        else if (assessment.isObjsVocabMismatch()){
            final String objs = getObjs(action);
            if (StringUtils.isEmpty(objs))
                return "some vocabulary in this step is missing or I wasn't able to identify it.";
            else
                return String.format("the terms \"%s\" do not match a valid UI component from the app.", objs);
        }else {
            return "I couldn't recognize the step.";
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

