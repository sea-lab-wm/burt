package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmPredictedS2RScreensSelectedAction extends ChatbotAction {

    public ConfirmPredictedS2RScreensSelectedAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    static String nextIntent = "";
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        UserMessage msg = (UserMessage) state.get(CURRENT_MESSAGE);
        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                nextIntent = "";
                List<String> S2RScreens =  msg.getMessages().get(0).getSelectedValues();
                response = MessageFormat.format("Ok, you select {0} and {1}, what is the next step?",  S2RScreens.get(0), S2RScreens.get(1));
                // need to check the quality of selected steps? or just give the next predicted steps.
            }else{
                nextIntent = "none";
                response = " Ok, what is the next step?";
            }
        }
        return new ChatbotMessage(response);
    }
}
