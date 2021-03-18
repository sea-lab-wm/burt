package sealab.burt.server.actions.observedbehavior;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class SelectOBScreenAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {

        String app = state.get("APP").toString();
        return new ChatbotMessage(MessageFormat.format("Got it. Just to confirm, can you select the {0} screen that " +
                "is having the problem? Please hit the “Done” button after you have selected it.", app));
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_SCREEN_SELECTED";
    }
}
