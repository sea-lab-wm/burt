package sealab.burt.server.actions.expectedbehavior;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ClarifyEBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("Is this the screen that should work fine?");
        //provide EB screenshot here
    }

//    @Override
//    public String nextExpectedIntent() {
//        return "EB_DESCRIPTION";
//    }
}
