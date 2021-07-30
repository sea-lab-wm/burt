package sealab.burt.server.actions.others;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;

import java.util.List;

public class EndConversationAction extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("Have a great day. See you next time");
    }
}
