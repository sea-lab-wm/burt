package sealab.burt.server.actions;

import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.DELETE_STEP_MSG;
import static sealab.burt.server.StateVariable.NEXT_INTENTS;

public class DoNothingAction extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        List<Intent> nextIntents  = (List<Intent>) state.get(NEXT_INTENTS);
        setNextExpectedIntents(nextIntents);
        String deleteStepMsg = (String) state.get(DELETE_STEP_MSG);
        return createChatBotMessages(deleteStepMsg);
    }
}
