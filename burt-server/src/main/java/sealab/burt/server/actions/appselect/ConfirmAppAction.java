package sealab.burt.server.actions.appselect;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.UserMessage;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ConfirmAppAction extends ChatbotAction {

    public ConfirmAppAction() {
        super();
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        UserMessage msg = (UserMessage) state.get(CURRENT_MESSAGE);
        String appNameVersion = "Dummy App v. 2.1";
//
        if (!msg.getMessages().isEmpty()) {
            List<String> selectedValues = msg.getMessages().get(0).getSelectedValues();

            if(selectedValues == null || selectedValues.isEmpty())
                return  new ChatbotMessage("Sorry, I didn't get that. Please select the application from the list.");

            appNameVersion = selectedValues.get(0);
        }

        String[] tokens = appNameVersion.split("v\\.");
        state.put(APP, tokens[0].trim());
        state.put(APP_VERSION, tokens[1].trim());
        return new ChatbotMessage(MessageFormat.format("You selected {0}, shall we continue?", appNameVersion));

    }

}
