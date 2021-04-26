package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
public class SelectMissingS2RAction extends ChatbotAction {

    public SelectMissingS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen3","S2RScreen3.png"));
        MessageObj messageObj = new MessageObj("It seems that before that step you had to perform additional steps Please select, " +
                "from the following options the actions you performed before this step. Please click the “done” button when you are done", "S2RScreenSelector");
        return new ChatbotMessage(messageObj, S2RScreens, true);

    }

}
