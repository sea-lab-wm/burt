package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DisambiguateS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("DISAMBIGUATE_S2R", true);

        UserMessage userMessage = (UserMessage) state.get("CURRENT_MESSAGE");
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen3","S2RScreen3.png"));
        MessageObj messageObj = new MessageObj(MessageFormat.format("Okay, it seems ambiguous, which of the following do you mean by \"{0}\"?",
                userMessage.getMessages().get(0).getMessage()), "S2RScreenSelector" );
        return new ChatbotMessage(messageObj, S2RScreens, false);

    }

    public String nextExpectedIntent() {
        return "S2R_AMBIGUOUS_SELECTED";
    }

}
