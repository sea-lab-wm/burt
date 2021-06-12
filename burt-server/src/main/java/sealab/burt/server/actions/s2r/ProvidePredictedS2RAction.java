package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvidePredictedS2RAction extends ChatBotAction {

    public ProvidePredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        MessageObj messageObj = new MessageObj("Please click the “done” button when you are done.",
                "S2RScreenSelector");
        List<KeyValues> S2RScreens = Arrays.asList(
                new KeyValues("0", "S2RScreen1","S2RScreen1.png"),
                new KeyValues("1", "S2RScreen2","S2RScreen2.png"),
                new KeyValues("2", "S2RScreen2","S2RScreen2.png"));
        return createChatBotMessages("Ok, it seems the next steps that you performed are the following.",
                "Can you confirm which ones are correct?",
                new ChatBotMessage(messageObj, S2RScreens));

    }

}
