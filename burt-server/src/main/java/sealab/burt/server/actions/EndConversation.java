package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatBotMessage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EndConversation extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        return createChatBotMessages("Have a great day. Bye");
    }
}
