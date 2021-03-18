package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideS2RFirstAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("COLLECTED_EB", true);
        state.put("COLLECTING_S2R", true);
        return new ChatbotMessage(" Okay. Now I need to know the steps that you performed and caused the problem. Can you please tell me the first step that you performed?");


    }

    @Override
    public String nextExpectedIntent() {
        return "S2R_DESCRIPTION";
    }
}
