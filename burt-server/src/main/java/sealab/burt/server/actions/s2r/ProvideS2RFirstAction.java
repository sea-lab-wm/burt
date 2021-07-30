package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.*;

public class ProvideS2RFirstAction extends ChatBotAction {

    public ProvideS2RFirstAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        state.put(COLLECTING_S2R, true);

        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();

        if (!state.containsKey(S2R_CHECKER))
            state.put(S2R_CHECKER, new S2RChecker(appName, appVersion));

        return createChatBotMessages(" Okay. Now I need to know the steps that you performed and caused the problem.",
                " Can you please tell me the first step that you performed?");
    }


}
