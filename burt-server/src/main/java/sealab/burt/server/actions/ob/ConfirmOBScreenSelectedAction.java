package sealab.burt.server.actions.ob;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.OutputMessageObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class ConfirmOBScreenSelectedAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {

        UserMessage msg = (UserMessage) state.get(CURRENT_MESSAGE);
        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                setNextExpectedIntent(Intent.NO_EXPECTED_INTENT);
                state.put(OB_SCREEN_SELECTED, true);
                String OBScreen =  msg.getMessages().get(0).getSelectedValues().get(0);
                response = "you selected " + OBScreen + " , shall we continue?";
                // add the selected screen to the report summary
//                Path OBScreenPath = Paths.get("../../data/app_logos/OBScreen.png");
                if(!state.containsKey(REPORT_OB)){
                    List<OutputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new OutputMessageObj((String)state.get(OB_DESCRIPTION),(String) state.get(OB_SCREEN)));
                    state.put(REPORT_OB, outputMessageList);
                }else{
                    List<OutputMessageObj> outputMessageList = (List<OutputMessageObj>) state.get(REPORT_OB);
                    outputMessageList.add(new OutputMessageObj((String)state.get(OB_DESCRIPTION),(String) state.get(OB_SCREEN)));
                }
            }else{
                state.remove(OB_SCREEN_SELECTED);
                setNextExpectedIntent(Intent.OB_SCREEN_SELECTED);
                MessageObj messageObj = new MessageObj("then, is this screen that has the problem? Please hit the “Done” button after you have selected it.",  "OBScreenSelector");
                List<KeyValue> OBScreen = Collections.singletonList(new KeyValue("OBScreen", "OBScreen.png"));
                return new ChatbotMessage(messageObj, OBScreen);
            }
        }
        return new ChatbotMessage(response);
    }
}
