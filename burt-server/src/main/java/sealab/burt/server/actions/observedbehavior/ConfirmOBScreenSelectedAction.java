package sealab.burt.server.actions.observedbehavior;
import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.UserMessage;
import sealab.burt.server.actions.ChatbotAction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmOBScreenSelectedAction extends ChatbotAction {
    static String nextIntent = "";
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {

        UserMessage msg = (UserMessage) state.get("CURRENT_MESSAGE");
        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                state.put("OB_SCREEN_SELECTED", true);
                nextIntent = "";
                String OBScreen =  msg.getMessages().get(0).getSelectedValues().get(0);
                response = "you selected " + OBScreen + " , shall we continue?";
            }else{
                nextIntent = "none";
                MessageObj messageObj = new MessageObj("then, is this screen that has the problem? Please hit the “Done” button after you have selected it.",  "OBScreenSelector");
                List<KeyValue> OBScreen = Arrays.asList(new KeyValue("OBScreen","OBScreen.png"));
                return new ChatbotMessage(messageObj, OBScreen);
            }
        }
        return new ChatbotMessage(response);
    }
    @Override
    public String nextExpectedIntent() {
        if (nextIntent.equals("none")) {
            return "OB_SCREEN_SELECTED";
        }
        return "NO_EXPECTED_INTENT";
    }
}
