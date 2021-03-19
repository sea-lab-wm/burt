package sealab.burt.server.actions.observedbehavior;
import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmOBScreenSelectedAction extends ChatbotAction {
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("OB_SCREEN_SELECTED", true);
        UserMessage msg = (UserMessage) state.get("CURRENT_MESSAGE");
        String OBScreen = "Screen";
        if (!msg.getMessages().isEmpty())
            OBScreen = msg.getMessages().get(0).getSelectedValues().get(0);
        System.out.println(OBScreen);

        return new ChatbotMessage("you selected " + OBScreen + " , shall we continue?");
    }

}
