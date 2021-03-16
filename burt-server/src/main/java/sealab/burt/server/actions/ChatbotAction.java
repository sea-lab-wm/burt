package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ChatbotAction {
    public abstract MessageObj execute(ConcurrentHashMap<String, Object> state) ;

    public String nextExpectedIntent() {
        return "NO_EXPECTED_INTENT";
    }
}
