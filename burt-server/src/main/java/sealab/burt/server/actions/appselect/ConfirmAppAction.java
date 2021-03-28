package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmAppAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        UserMessage msg = (UserMessage) state.get("CURRENT_MESSAGE");
        String appNameVersion = "Dummy App v. 2.1";
//
        if (!msg.getMessages().isEmpty())
            appNameVersion = msg.getMessages().get(0).getSelectedValues().get(0);
        String[] tokens = appNameVersion.split("v\\.");
        state.put("APP", tokens[0].trim());
        state.put("APP_VERSION", tokens[1].trim());
        return new ChatbotMessage(MessageFormat.format("You selected {0}, shall we continue?", appNameVersion));

    }

}
