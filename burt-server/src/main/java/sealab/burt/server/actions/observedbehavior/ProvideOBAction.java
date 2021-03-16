package sealab.burt.server.actions.observedbehavior;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideOBAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        return new MessageObj("Ok, can you please tell me the incorrect behavior of Chikki that you observed?");
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_DESCRIPTION";
    }
}
