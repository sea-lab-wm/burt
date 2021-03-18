package sealab.burt.server.actions.observedbehavior;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideOBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        String app = state.get("APP").toString();
        if (!state.containsKey("OB_CHECKER")) state.put("OB_CHECKER", new OBChecker(app));
        return new ChatbotMessage(MessageFormat.format("Ok, can you please tell me the incorrect behavior that you " +
                "observed in {0}?", app));
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_DESCRIPTION";
    }
}
