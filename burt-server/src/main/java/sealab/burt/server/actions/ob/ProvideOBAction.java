package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.BurtConfigPaths;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvideOBAction extends ChatBotAction {

    public ProvideOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        state.put(COLLECTING_OB, true);
        if (!state.containsKey(OB_CHECKER))
            state.put(OB_CHECKER, new OBChecker(appName, appVersion));
        return createChatBotMessages(MessageFormat.format("Ok, can you please tell me the incorrect behavior that you " +
                "observed on {0}?", appName));
    }

}
