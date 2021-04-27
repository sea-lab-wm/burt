package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class DisambiguateS2RAction extends ChatbotAction {

    public DisambiguateS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {

        UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen3","S2RScreen3.png"));
        MessageObj messageObj = new MessageObj(MessageFormat.format("Okay, it seems ambiguous, which of the following do you mean by \"{0}\"?",
                userMessage.getMessages().get(0).getMessage()), "S2RScreenSelector" );
        return new ChatbotMessage(messageObj, S2RScreens, false);

    }


}
