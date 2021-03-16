package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class DisambiguateS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("CONVERSATION_STATE", "DISAMBIGUATE_S2R");
        return new ChatbotMessage("Ok, which of the following you mean by “I opened this game”?");
        //Provide screenshots here
    }
    public String nextExpectedIntent() {
        return "S2R_AMBIGUOUS_SELECTED";
    }

}
