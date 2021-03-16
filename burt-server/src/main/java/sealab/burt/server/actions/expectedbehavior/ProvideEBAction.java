package sealab.burt.server.actions.expectedbehavior;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideEBAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        state.put("OB_COLLECTED", true);
        state.put("CONVERSATION_STATE", "COLLECTING_EB");
        return new MessageObj("ok, can you please tell me how the app is supposed to work instead?");
    }

    @Override
    public String nextExpectedIntent() {
        return "EB_DESCRIPTION";
    }
}
