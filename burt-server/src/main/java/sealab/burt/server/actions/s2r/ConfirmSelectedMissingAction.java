package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.outputMessageObj;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmSelectedMissingAction extends ChatbotAction {

    public ConfirmSelectedMissingAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        UserMessage msg = (UserMessage) state.get(CURRENT_MESSAGE);
        StringBuilder response = new StringBuilder();
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                List<String> S2RScreens =  msg.getMessages().get(0).getSelectedValues();
                response.append("Ok, you select ");
                List<Path> S2RScreenPathList = new ArrayList<>();

                for (String screen: S2RScreens){
                    S2RScreenPathList.add(Paths.get("D:/Projects/burt/data/app_logos/", screen, ".png"));
                    response.append(screen);
                    response.append(", ");
                }
                response.append("what is the next step?");
//                response = new StringBuilder(MessageFormat.format("Ok, you select {0} and {1}, what is the next step?", S2RScreens.get(0), S2RScreens.get(1)));

                // add the selected missing steps to report summary
                if(!state.containsKey(S2R_DESCRIPTION)){
                    List<outputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new outputMessageObj(null, S2RScreenPathList));
                    state.put(S2R_DESCRIPTION, outputMessageList);
                }else{
                    List<outputMessageObj>  outputMessageList= (List<outputMessageObj>) state.get(S2R_DESCRIPTION);
                    outputMessageList.add(new outputMessageObj(null, S2RScreenPathList));
                }

            }else{

                response.append(" Ok, what is the next step?");
            }
        }
        return new ChatbotMessage(response.toString());
    }


}
