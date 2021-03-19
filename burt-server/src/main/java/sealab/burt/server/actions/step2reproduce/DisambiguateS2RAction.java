package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class DisambiguateS2RAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("DISAMBIGUATE_S2R", true);

        UserMessage userMessage = (UserMessage) state.get("CURRENT_MESSAGE");
        return new ChatbotMessage(MessageFormat.format("Okay, which of the following do you mean by \"{0}\"?",
                userMessage.getMessages().get(0).getMessage()));
        //Provide screenshots here
    }

    public String nextExpectedIntent() {
        return "S2R_AMBIGUOUS_SELECTED";
    }

}
