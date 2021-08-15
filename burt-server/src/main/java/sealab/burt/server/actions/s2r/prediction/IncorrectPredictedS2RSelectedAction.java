package sealab.burt.server.actions.s2r.prediction;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.NEXT_INTENTS;

public class IncorrectPredictedS2RSelectedAction extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        List<Intent> nextIntents = (List<Intent>) state.get(NEXT_INTENTS);
        setNextExpectedIntents(nextIntents);

        return createChatBotMessages(
                "Sorry, the option you selected is incorrect",
                "Please <b>select a valid option</b>");
    }
}
