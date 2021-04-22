package sealab.burt.server.actions.expectedbehavior;

import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClarifyEBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        //provide EB screenshot here
        MessageObj messageObj = new MessageObj("So, is this the screen that should work fine?", "EBScreenSelector");
        List<KeyValue> EBScreen = Arrays.asList(new KeyValue("EBScreen","EBScreen.png"));
        return new ChatbotMessage(messageObj, EBScreen);
    }

}
