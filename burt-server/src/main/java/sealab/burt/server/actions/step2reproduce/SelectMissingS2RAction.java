package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class SelectMissingS2RAction extends ChatbotAction {
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("SELECT_MISSING_S2R", true);
        return new ChatbotMessage(" It seems that before that step you had to perform additional steps. Please select, from the following options," +
                " the actions you performed before this step. Please click the “done” button when you are done");
        // provide screenshots to choose

    }
    public String nextExpectedIntent() {
        return "S2R_MISSING_SELECTED";
    }


}
