package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

public class ConfirmLastStepAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        setNextExpectedIntents(Collections.singletonList(Intent.NO_EXPECTED_INTENT));
        state.put(StateVariable.CONFIRM_LAST_STEP, true);
        return createChatBotMessages("It seems this is the last step that you performed",
                "Is this correct?");
    }
}
