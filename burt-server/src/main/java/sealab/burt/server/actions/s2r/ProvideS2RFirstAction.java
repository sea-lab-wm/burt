package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ProvideS2RFirstAction extends ChatbotAction {

    public ProvideS2RFirstAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        state.put(COLLECTING_S2R, true);
        state.remove(COLLECTING_EB);
        String appName = state.get(APP).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(S2R_CHECKER)) state.put(S2R_CHECKER, new S2RChecker(appName, appVersion));
        return new ChatbotMessage(" Okay. Now I need to know the steps that you performed and caused the problem. Can" +
                " you please tell me the first step that you performed?");

    }


}


