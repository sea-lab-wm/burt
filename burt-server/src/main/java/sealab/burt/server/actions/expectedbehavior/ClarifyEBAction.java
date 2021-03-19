package sealab.burt.server.actions.expectedbehavior;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClarifyEBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        // return new ChatbotMessage("Is this the screen that should work fine?");
        //provide EB screenshot here
        MessageObj messageObj = new MessageObj("Is this the screen that should work fine?",  "EBScreenSelector");
        List<KeyValue> EBScreen = Arrays.asList(new KeyValue("EBScreen","EBScreen.png"));
        return new ChatbotMessage(messageObj, EBScreen);
    }

    @Override
    public String nextExpectedIntent() {
        return "";
    }
}
