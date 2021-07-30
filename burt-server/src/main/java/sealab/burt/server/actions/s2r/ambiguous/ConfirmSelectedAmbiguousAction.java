package sealab.burt.server.actions.s2r.ambiguous;


import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.*;
public class ConfirmSelectedAmbiguousAction extends ChatBotAction {

    public ConfirmSelectedAmbiguousAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        return respondWithChoices(state, msg);
    }

    private List<ChatBotMessage> respondWithChoices(ConversationState state, UserResponse msg) {
        String response = "";
        //FIXME: this code is buggy, based on the last changes
       /* if (!msg.getMessages().isEmpty()) {
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
        }*/
        return createChatBotMessages(response);
    }

}
