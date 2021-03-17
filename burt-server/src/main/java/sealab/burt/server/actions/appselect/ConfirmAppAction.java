package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmAppAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        UserMessage msg = (UserMessage) state.get("CURRENT_MESSAGE");
        String app = "Dummy App";
        if (!msg.getSelectedValues().isEmpty())
            app = msg.getSelectedValues().get(0);
        state.put("APP", app);
        return new ChatbotMessage(MessageFormat.format("You selected {0}, shall we continue?", app));
    }

}
