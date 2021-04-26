package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmLastStepAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        state.put(CONFIRM_LAST_STEP, true);
        return new ChatbotMessage("It seems this is the last step that you performed. Is this correct?");
    }
}
