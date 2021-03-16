package sealab.burt.server.actions.observedbehavior;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class SelectOBScreenAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        return new MessageObj("Got it. Just to confirm, can you select the Chikii screen that is having the problem? Please hit the " +
                "“Done” button when you are done.");
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_SCREEN_SELECTED";
    }
}
