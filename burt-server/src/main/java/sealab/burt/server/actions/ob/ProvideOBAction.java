package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.NewOBChecker;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.List;

import static sealab.burt.server.StateVariable.*;

public class ProvideOBAction extends ChatBotAction {

    public ProvideOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(OB_CHECKER))
            state.put(OB_CHECKER, new NewOBChecker(appName, appVersion));

        boolean initiated = state.isCurrentAttemptInitiatedObMatched();

        if (!initiated)
            return createChatBotMessages(MessageFormat.format(
                    "Okay, please tell me the <b>incorrect behavior</b> that you observed on {0}", appName));
        else
            return createChatBotMessages(
                    "Okay, I am having some difficulty recognizing the incorrect behavior",
                    MessageFormat.format(
                            "Please <b>clarify or rephrase</b> the incorrect behavior", appName));
    }

}
