package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import static sealab.burt.server.StateVariable.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ClarifyEBAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        //provide EB screenshot here
        MessageObj messageObj = new MessageObj("So, is this the screen that should work fine?", "EBScreenSelector");
//        String screenshotPath = (String) state.get(EB_SCREEN);
//        String description = (String) state.get(EB_DESCRIPTION);
        List<KeyValue> EBScreen = Arrays.asList(new KeyValue("EB_description", "EBScreen.png"));
        return new ChatbotMessage(messageObj, EBScreen);
    }

}
