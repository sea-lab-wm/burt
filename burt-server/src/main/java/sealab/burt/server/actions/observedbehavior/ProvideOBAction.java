package sealab.burt.server.actions.observedbehavior;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideOBAction extends ChatbotAction {

    public ProvideOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        String appName = state.get("APP").toString();
        String appVersion = state.get("APP_VERSION").toString();
        state.put("COLLECTING_OB", true);
        if (!state.containsKey("OB_CHECKER")) state.put("OB_CHECKER", new OBChecker(appName, appVersion));
        return new ChatbotMessage(MessageFormat.format("Ok, can you please tell me the incorrect behavior that you " +
                "observed in {0}?", appName));
    }

}
