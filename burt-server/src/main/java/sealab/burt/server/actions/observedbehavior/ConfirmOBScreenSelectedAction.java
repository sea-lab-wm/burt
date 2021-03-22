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
        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                OBScreen = msg.getMessages().get(0).getSelectedValues().get(0);
                response = "you selected " + OBScreen + " , shall we continue?";
            }else{
                response = "So this screen is not right";
            }
        }
        return new ChatbotMessage(response);
    }
}
