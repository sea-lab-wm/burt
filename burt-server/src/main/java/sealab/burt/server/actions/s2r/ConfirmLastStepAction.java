package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmLastStepAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        setNextExpectedIntent(Intent.NO_EXPECTED_INTENT);
        state.put(StateVariable.CONFIRM_LAST_STEP, true);
        return new ChatbotMessage("This is the last step that you performed. Is this correct?");
    }
}
