package sealab.burt.server.actions.others;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EndConversationAction extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("Have a great day. See you next time");
    }
}
