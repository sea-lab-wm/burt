package sealab.burt.server.actions.s2r.highquality;

import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.s2r.missing.SelectMissingS2RAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.S2R_QUALITY_RESULT;

public class ConfirmMatchedS2RAction extends ChatBotAction {

    public ConfirmMatchedS2RAction(Intent... nextIntents) {
        super(nextIntents);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        //get the quality result, we must exist in the state
        QualityFeedback qualityFeedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        S2RQualityAssessment highQualityAssessment = qualityFeedback.getQualityAssessments().stream()
                .filter(f -> f.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                .findFirst().orElse(null);

        if (highQualityAssessment == null)
            throw new RuntimeException("The high quality assessment is required");

        //----------------------------------------------

        AppStep matchedStep = highQualityAssessment.getMatchedSteps().get(0);

        List<KeyValues> optionList = SelectMissingS2RAction.getStepOptions(
                Collections.singletonList(matchedStep), state);

        ChatBotMessage optionMessage = new ChatBotMessage(
                new MessageObj("Ok, just to double check, is this the step that you are reporting?",
                        WidgetName.OneScreenYesNoButtons, false),
                optionList);

        state.put(StateVariable.S2R_MATCHED_CONFIRMATION, true);
        state.put(StateVariable.S2R_MATCHED_MSG, state.get(CURRENT_MESSAGE));

        return createChatBotMessages(optionMessage);
    }
}
