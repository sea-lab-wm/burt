package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvidePredictedS2RAction extends ChatbotAction {

    public ProvidePredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        MessageObj messageObj = new MessageObj(" Ok, it seems the next steps that you performed are the following." +
                " Can you confirm which are correct? Please click the “done” button when you are done.",  "S2RScreenSelector");
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"));
        return new ChatbotMessage(messageObj, S2RScreens);

    }

}
