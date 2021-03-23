package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvidePredictedS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        MessageObj messageObj = new MessageObj(" Ok, it seems the next steps that you performed are the following." +
                " Can you confirm which are correct? Please click the “done” button when you are done.",  "S2RScreenSelector");
        List<KeyValue> S2RScreen = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"));
        return new ChatbotMessage(messageObj, S2RScreen);

    }

    @Override
    public String nextExpectedIntent() {
        return "S2R_PREDICTED_SELECTED";
    }
}
