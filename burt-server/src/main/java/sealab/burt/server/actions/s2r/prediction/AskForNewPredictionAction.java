package sealab.burt.server.actions.s2r.prediction;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

public class AskForNewPredictionAction extends sealab.burt.server.actions.ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        setNextExpectedIntents(Collections.singletonList(Intent.NEW_PREDICTION_OR_TYPE_S2R));

         MessageObj messageObj = new MessageObj( "If so, please click the button below, <b>otherwise write the next " +
                 "step</b>",
                WidgetName.S2RPredictionConfirmation);

        return createChatBotMessages(
                "Do you want me to keep <b>suggesting the next steps</b>?",
                new ChatBotMessage(messageObj));
    }
}
