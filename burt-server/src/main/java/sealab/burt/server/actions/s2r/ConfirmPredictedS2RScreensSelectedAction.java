package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.ob.SelectOBScreenAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.msgparsing.Intent;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
public class ConfirmPredictedS2RScreensSelectedAction extends ChatBotAction {

    public ConfirmPredictedS2RScreensSelectedAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

        String response = "";
        if (!msg.getMessages().isEmpty()) {
            String confirmMessage = msg.getFirstMessage().getMessage();
            if (confirmMessage.equals("done")) {

                List<String> S2RScreens =  msg.getFirstMessage().getSelectedValues();
//                response = MessageFormat.format("Ok, you select {0} and {1}, what is the next step?",  S2RScreens.get(0), S2RScreens.get(1));
                response = MessageFormat.format("Ok, you selected {0}, what is the next step?",
                        S2RScreens.get(0));
                // need to check the quality of selected steps? or just give the next predicted steps.
            }else{
                response = " Ok, what is the next step?";
            }
        }
        return createChatBotMessages(response);
    }

    private List<ChatBotMessage> getDefaultMessage(List<GraphState> matchedStates,
            ConcurrentHashMap<StateVariable, Object> state) {
        this.nextExpectedIntents = Collections.singletonList(Intent.OB_SCREEN_SELECTED);

        List<KeyValues> options = SelectOBScreenAction.getObScreenOptions(matchedStates, state);

        MessageObj messageObj = new MessageObj(
                "From the following options, select the steps you performed before this step", "OBScreenSelector");

        return createChatBotMessages(
                "Sorry, the options you selected are incorrect.",
                new ChatBotMessage(messageObj, options, true));
    }
}
