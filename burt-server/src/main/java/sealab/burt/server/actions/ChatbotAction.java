package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

public abstract class ChatbotAction {
    public abstract MessageObj execute() ;

    public String nextExpectedIntent() {
        return "NO_EXPECTED_INTENT";
    }
}
