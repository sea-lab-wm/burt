package sealab.burt.server.actions.s2r;


import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.OutputMessageObj;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmSelectedAmbiguousAction extends ChatBotAction {

    public ConfirmSelectedAmbiguousAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        UserMessage msg = (UserMessage) state.get(CURRENT_MESSAGE);
        return respondWithChoices(state, msg);
    }

    private List<ChatBotMessage> respondWithChoices(ConcurrentHashMap<StateVariable, Object> state, UserMessage msg) {
        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getMessages().get(0).getMessage();
            if (confirmMessage.equals("done")) {
                 List<String> S2RScreens =  msg.getMessages().get(0).getSelectedValues();
                response = MessageFormat.format("Ok, you select {0}, what is the next step?",  S2RScreens.get(0));
                // add the selected step to report summary
                if(!state.containsKey(REPORT_S2R)){
                    List<OutputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new OutputMessageObj("screenshot description", "../../data/app_logos/" + S2RScreens.get(0)+ ".png"));
                    state.put(REPORT_S2R, outputMessageList);
                }else{
                    List<OutputMessageObj>  outputMessageList= (List<OutputMessageObj>) state.get(REPORT_S2R);
                    outputMessageList.add(new OutputMessageObj("screenshot description", "../../data/app_logos/" + S2RScreens.get(0)+ ".png"));
                }
            }else{

                // give other screens to let user choose?
                response = " Ok, what is the next step?";
                state.remove(StateVariable.DISAMBIGUATE_S2R);
                return createChatBotMessages(response);
            }
        }
        return createChatBotMessages(response);
    }

}
