package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class RephraseS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("I didn’t quite get that. Can you please rephrase the step more accurately?");

    }

    public String nextExpectedIntent() {
        return "S2R_DESCRIPTION";
    }
}

