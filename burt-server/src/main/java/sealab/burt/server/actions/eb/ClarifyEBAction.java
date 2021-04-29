package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import static sealab.burt.server.StateVariable.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ClarifyEBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        //provide EB screenshot here
        MessageObj messageObj = new MessageObj("So, is this the screen that should work fine?", "EBScreenSelector");
        List<KeyValue> EBScreen = Arrays.asList(new KeyValue("EBScreen","EBScreen.png"));
        state.put(EB_SCREEN, Paths.get("../../data/app_logos/EBScreen.png"));
        return new ChatbotMessage(messageObj, EBScreen);
    }

}
