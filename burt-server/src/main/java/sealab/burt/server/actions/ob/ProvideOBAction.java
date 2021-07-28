package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ProvideOBAction extends ChatBotAction {

    public ProvideOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(OB_CHECKER))
            state.put(OB_CHECKER, new OBChecker(appName, appVersion));
        Integer currentAttempt = (Integer) state.get(CURRENT_ATTEMPT_OB_MATCHED);

        if(currentAttempt == null)
            return createChatBotMessages(MessageFormat.format(
                "Okay, can you please tell me the incorrect behavior that you observed on {0}?", appName));
        else
            return createChatBotMessages(
                    "Okay, it seems I am having some difficulty recognizing the incorrect behavior.",
                    MessageFormat.format(
                    "Can you please clarify or rephrase the incorrect behavior?", appName));
    }

}
