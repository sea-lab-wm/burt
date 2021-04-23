package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatbotMessage;

import java.util.concurrent.ConcurrentHashMap;

public class EndConversation extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        // provide the summery of bug report
        String response =  "Have a great day. Bye";
        return new ChatbotMessage(response);
    }
}
