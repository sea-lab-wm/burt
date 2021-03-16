package sealab.burt.server.actions;

import sealab.burt.server.ChatbotMessage;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ChatbotAction {
    public abstract ChatbotMessage execute(ConcurrentHashMap<String, Object> state) ;

    public String nextExpectedIntent() {
        return "NO_EXPECTED_INTENT";
    }
}
