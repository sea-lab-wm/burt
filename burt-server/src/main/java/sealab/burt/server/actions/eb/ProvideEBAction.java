package sealab.burt.server.actions.eb;

import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvideEBAction extends ChatbotAction {

    public ProvideEBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        state.put(COLLECTING_EB, true);
        String appName = state.get(StateVariable.APP).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(EB_CHECKER)) state.put(EB_CHECKER, new EBChecker(appName, appVersion));
        return new ChatbotMessage("ok, can you please tell me how the app is supposed to work instead?");
    }
}
