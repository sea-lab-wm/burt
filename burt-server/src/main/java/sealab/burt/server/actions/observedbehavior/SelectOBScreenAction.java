package sealab.burt.server.actions.observedbehavior;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SelectOBScreenAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {

        String app = state.get("APP").toString();
        MessageObj messageObj = new MessageObj(MessageFormat.format("Got it. Just to confirm, can you select the {0} screen that is having the problem?" +
                " Please hit the “Done” button after you have selected it.", app) ,"OBScreenSelector");
        List<KeyValue> OBScreen = Arrays.asList(new KeyValue("OBScreen","OBScreen.png"));
        return new ChatbotMessage(messageObj, OBScreen);
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_SCREEN_SELECTED";
    }
}
