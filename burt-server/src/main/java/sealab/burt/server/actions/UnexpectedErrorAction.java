package sealab.burt.server.actions;

import sealab.burt.server.conversation.ChatbotMessage;

import java.util.concurrent.ConcurrentHashMap;

public class UnexpectedErrorAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("I am sorry, there was an unexpected error in the server. Please try again or " +
                "restart the conversation.");
    }
}
