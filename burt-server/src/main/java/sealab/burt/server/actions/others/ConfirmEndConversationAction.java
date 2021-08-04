package sealab.burt.server.actions.others;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ConfirmEndConversationAction extends ChatBotAction {

    public ConfirmEndConversationAction(Intent... nextExpectedIntents) {
        super(nextExpectedIntents);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        return createChatBotMessages("It seems that you haven't finished reporting the problem",
                "Are you sure you want to start a new conversation?");
    }
}
