package sealab.burt.server.actions.ob;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class SelectOBScreenAction extends ChatbotAction {

    public SelectOBScreenAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {

        String app = state.get(APP).toString();
        MessageObj messageObj = new MessageObj(MessageFormat.format("Got it. Just to confirm, can you select the {0} screen that is having the problem?" +
                " Please hit the \"Done\" button after you have selected it.", app) ,"OBScreenSelector");
        List<KeyValue> OBScreen = Arrays.asList(new KeyValue("OBScreen","OBScreen.png"));
        return new ChatbotMessage(messageObj, OBScreen, true);
    }

}
