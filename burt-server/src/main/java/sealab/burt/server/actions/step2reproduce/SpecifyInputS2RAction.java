package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class SpecifyInputS2RAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("It seems you forget to specify input. Can you please provide the input to make the step more accurately?");
    }
    @Override
    public String nextExpectedIntent() {
        return "S2R_DESCRIPTION";
    }

}
