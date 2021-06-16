package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.OutputMessageObj;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;
import static sealab.burt.server.statecheckers.QualityStateUpdater.DEFAULT_SCREENSHOT;

public class ConfirmOBScreenSelectedAction extends ChatBotAction {


    private List<ChatBotMessage> getDefaultMessage(List<GraphState> matchedStates) {
        this.nextExpectedIntents = Collections.singletonList(Intent.OB_SCREEN_SELECTED);

        List<KeyValues> options = SelectOBScreenAction.getObScreenOptions(matchedStates);

        MessageObj messageObj = new MessageObj(
                "From the following options, select the steps you performed before this step", "OBScreenSelector");

        return createChatBotMessages(
                "Sorry, the options you selected are incorrect.",
                new ChatBotMessage(messageObj, options, true));
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

//------------------------------------------

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        List<GraphState> matchedStates = result.getMatchedStates();

        //-------------------------------------

        if (msg.getMessages().isEmpty()) {
            return getDefaultMessage(matchedStates);
        }


        //------------------------------
        this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

        MessageObj message = msg.getFirstMessage();
        StringBuilder response = new StringBuilder();
        if ("done".equals(message.getMessage())) {


            List<String> selectedValues = message.getSelectedValues();
            if (selectedValues == null || selectedValues.isEmpty())
                return getDefaultMessage(matchedStates);

            String optionId = selectedValues.get(0);

            int id;
            try {
                id = Integer.parseInt(optionId);
            } catch (NumberFormatException e) {
                return getDefaultMessage(matchedStates);
            }

            GraphState selectedState = matchedStates.get(id);

            setNextExpectedIntents(Collections.singletonList(Intent.NO_EXPECTED_INTENT));
            state.put(OB_SCREEN_SELECTED, true);

            String screenshotFile = selectedState.getScreenshotPath();
            if (screenshotFile == null)
                screenshotFile = DEFAULT_SCREENSHOT;

            state.put(REPORT_OB, Collections.singletonList(
                    new OutputMessageObj((String) state.get(OB_DESCRIPTION), screenshotFile)));

            //---------------------

            String selectedScreenDescription = GraphTransition.getWindowString(
                    selectedState.getScreen().getActivity(),
                    selectedState.getScreen().getWindow());

            response.append("Ok, you selected the screen \"");
            response.append(selectedScreenDescription);

            return createChatBotMessages(response.toString(), "Shall we continue?");

        } else if ("none of above".equals(message.getMessage())) {
            //FIXME: show the next batch of matches

            state.remove(OB_SCREEN_SELECTED);
            setNextExpectedIntents(Collections.singletonList(Intent.OB_SCREEN_SELECTED));
            MessageObj messageObj = new MessageObj("Then, is this screen that has the problem? Please hit the “Done” " +
                    "button after you have selected it.", "OBScreenSelector");
            List<KeyValues> OBScreen = Collections.singletonList(new KeyValues("0", "OBScreen", "OBScreen.png"));
            return createChatBotMessages(new ChatBotMessage(messageObj, OBScreen));
        } else {
            return getDefaultMessage(matchedStates);
        }

    }
}
