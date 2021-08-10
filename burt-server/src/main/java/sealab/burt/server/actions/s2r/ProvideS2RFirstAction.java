package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.s2r.ProvideS2RAction.S2RFormatTip;

public class ProvideS2RFirstAction extends ChatBotAction {

    public ProvideS2RFirstAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
        state.put(COLLECTING_S2R, true);

        String appName = state.get(APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();

        if (!state.containsKey(S2R_CHECKER))
            state.put(S2R_CHECKER, new S2RChecker(appName, appVersion));

        List<ChatBotMessage> chatBotMessages;
        if (state.containsKey(StateVariable.ASKED_TO_WRITE_S2R)) {
            chatBotMessages = createChatBotMessages("Got it, can you please tell me the <b>first step</b> that you performed?");
        } else {
            state.put(StateVariable.ASKED_TO_WRITE_S2R, true);
            chatBotMessages = createChatBotMessages(
                    "Okay, now I need to know the steps that you performed and caused the problem",
                    "Can you please tell me the <b>first step</b> that you performed?", S2RFormatTip);
        }

        return chatBotMessages;
    }

}
