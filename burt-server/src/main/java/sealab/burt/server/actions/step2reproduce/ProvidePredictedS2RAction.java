package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ProvidePredictedS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage(" Ok, it seems the next steps that you performed are the following. Can you confirm which are correct? Please click the “done” button when you are done.");
        //provide screenshots here
    }

    @Override
    public String nextExpectedIntent() {
        return "S2R_PREDICTED_SELECTED";
    }
}
