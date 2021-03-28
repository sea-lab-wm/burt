package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SelectMissingS2RAction extends ChatbotAction {
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("SELECT_MISSING_S2R", true);
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"));
        MessageObj messageObj = new MessageObj("It seems that before that step you had to perform additional steps Please select, " +
                "from the following options the actions you performed before this step. Please click the “done” button when you are done");
        return new ChatbotMessage(messageObj, S2RScreens);

    }
    public String nextExpectedIntent() {
        return "S2R_MISSING_SELECTED";
    }

}
