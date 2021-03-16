package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ConfirmLastStepAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        state.put("CONVERSATION_STATE", "CONFIRM_LAST_STEP");
        return new MessageObj("It seems this is the last step that you performed. Is this correct?");
    }
}
