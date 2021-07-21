package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.*;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.statecheckers.QualityStateUpdater;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.OB_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;

public class ConfirmOBScreenSelectedAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

        //------------------------------------------

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        List<GraphState> matchedStates = result.getMatchedStates();


        //-------------------------------------
        Integer currentObScreenPosition = (Integer) state.get(CURRENT_OB_SCREEN_POSITION);

        if (msg.getMessages().isEmpty()) {
            return getDefaultMessage(matchedStates, state, currentObScreenPosition);
        }

        //------------------------------
        this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

        MessageObj message = msg.getFirstMessage();
        StringBuilder response = new StringBuilder();
        if ("done".equals(message.getMessage())) {

            List<String> selectedValues = message.getSelectedValues();
            if (selectedValues == null || selectedValues.isEmpty())
                return getDefaultMessage(matchedStates, state, currentObScreenPosition);

            String optionId = selectedValues.get(0);

            int id;
            try {
                id = Integer.parseInt(optionId);
            } catch (NumberFormatException e) {
                return getDefaultMessage(matchedStates, state, currentObScreenPosition);
            }

            GraphState selectedState = matchedStates.get(id);

            setNextExpectedIntents(Collections.singletonList(Intent.NO_EXPECTED_INTENT));
            state.put(OB_SCREEN_SELECTED, true);

            state.put(StateVariable.OB_STATE, selectedState);

            //---------------------

            String selectedScreenDescription = GraphTransition.getWindowString(
                    selectedState.getScreen().getActivity(),
                    selectedState.getScreen().getWindow());

            response.append("Okay, you selected the screen \"")
                    .append(id + 1)
                    .append(". ")
                    .append(selectedScreenDescription)
                    .append("\"");

            state.remove(StateVariable.CURRENT_OB_SCREEN_POSITION);

            return createChatBotMessages(response.toString(), "Shall we continue?");

        } else if ("none of above".equals(message.getMessage())) {

            state.remove(OB_SCREEN_SELECTED);

            Integer currentAttempt = (Integer) state.get(StateVariable.CURRENT_ATTEMPT_OB_SCREENS);
            if (currentAttempt >= SelectOBScreenAction.MAX_OB_SCREEN_ATTEMPTS) {
                state.remove(CURRENT_ATTEMPT_OB_SCREENS);

                startEBChecker(state);
                this.setNextExpectedIntents(Collections.singletonList(Intent.EB_DESCRIPTION));

                QualityStateUpdater.updateOBState(state, null);

                return createChatBotMessages("All right. Let's continue.",
                        "Can you please tell me how the app is supposed to work instead?"
                );
            }

            //---------------------------

            //increase the position of the next OB screen batch
            currentObScreenPosition += SelectOBScreenAction.MAX_OB_SCREENS_TO_SHOW;
            state.put(StateVariable.CURRENT_OB_SCREEN_POSITION, currentObScreenPosition);

            //get the options
            List<KeyValues> options = SelectOBScreenAction.getObScreenOptions(matchedStates, state,
                    currentObScreenPosition);


            if (options.isEmpty()) {

                state.remove(StateVariable.CURRENT_OB_SCREEN_POSITION);
                this.setNextExpectedIntents(Collections.singletonList(OB_DESCRIPTION));

                return createChatBotMessages("All right. Then, your description of the problem does not seem to match" +
                                " any screen from the app.",
                        "Can you tell me the incorrect behavior one more time?"
                );
            } else {

                //----------------------------------

                setNextExpectedIntents(Collections.singletonList(Intent.OB_SCREEN_SELECTED));

                //----------------------------------

                MessageObj messageObj = new MessageObj(
                        " Please hit the \"Done\" button after you have selected it.", WidgetName.OBScreenSelector);

                return createChatBotMessages(
                        "Okay then, which of the following screens is having the problem?",
                        new ChatBotMessage(messageObj, options, false));
            }
        } else {
            return getDefaultMessage(matchedStates, state, currentObScreenPosition);
        }

    }


    private List<ChatBotMessage> getDefaultMessage(List<GraphState> matchedStates,
                                                   ConversationState state, int position) {
        this.nextExpectedIntents = Collections.singletonList(Intent.OB_SCREEN_SELECTED);

        List<KeyValues> options = SelectOBScreenAction.getObScreenOptions(matchedStates, state, position);

        MessageObj messageObj = new MessageObj(
                "From the list below, can you please select the screen that is having the problem", WidgetName.OBScreenSelector);

        return createChatBotMessages(
                "Sorry, the options you selected are incorrect.",
                new ChatBotMessage(messageObj, options, false));
    }
}
