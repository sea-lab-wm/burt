package sealab.burt.server.actions.ob;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SelectOBScreenAction extends ChatBotAction {

    public SelectOBScreenAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        MessageObj messageObj = new MessageObj(
                " Please hit the \"Done\" button after you have selected it.", "OBScreenSelector");
//        String screenshotPath = (String) state.get(OB_SCREEN);
//        String description  = (String) state.get(OB_DESCRIPTION);
        List<KeyValues> OBScreen = Arrays.asList(new KeyValues("0", "OB description", "OBScreen.png"));
//        List<KeyValue> OBScreen = Collections.singletonList(new KeyValue(description, screenshotPath));
        return createChatBotMessages(
                "Got it. Just to confirm, can you select the screen that is having the problem?",
                new ChatBotMessage(messageObj, OBScreen, true));

    }

}
