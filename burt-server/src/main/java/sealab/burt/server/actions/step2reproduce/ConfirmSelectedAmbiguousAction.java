package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ConfirmSelectedAmbiguousAction extends ChatbotAction {

    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        return new MessageObj("you selected Option 2, shall we continue?");
    }
}
