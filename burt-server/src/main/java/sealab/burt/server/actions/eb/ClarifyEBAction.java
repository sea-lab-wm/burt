package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ClarifyEBAction extends ChatBotAction {
    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        //provide EB screenshot here
        MessageObj messageObj = new MessageObj("Ok, is this the screen that should work fine?", "EBScreenSelector");
//        String screenshotPath = (String) state.get(EB_SCREEN);
//        String description = (String) state.get(EB_DESCRIPTION);
        List<KeyValues> EBScreen = Arrays.asList(new KeyValues("0", "EB_description", "EBScreen.png"));
        return createChatBotMessages(new ChatBotMessage(messageObj, EBScreen));
    }

}
