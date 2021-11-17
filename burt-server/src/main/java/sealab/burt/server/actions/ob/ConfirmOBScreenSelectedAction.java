package sealab.burt.server.actions.ob;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.MetricsRecorder;

import java.util.Collections;
import java.util.List;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.PROVIDE_EB;
import static sealab.burt.server.msgparsing.Intent.OB_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;

public @Slf4j
class ConfirmOBScreenSelectedAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

        //------------------------------------------

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        List<GraphState> matchedStates = result.getMatchedStates();

        //-------------------------------------
        Integer currentObScreenPosition = (Integer) state.get(CURRENT_OB_SCREEN_POSITION);

        if (msg.getMessages().isEmpty()) {
            return getDefaultMessage();
        }

        //------------------------------
        this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

        MessageObj message = msg.getFirstMessage();
        StringBuilder response = new StringBuilder();
        if (DONE.equals(message.getMessage())) {

            List<String> selectedValues = message.getSelectedValues();
            if (selectedValues == null || selectedValues.isEmpty())
                return getDefaultMessage();

            String optionId = selectedValues.get(0);

            int id;
            try {
                id = Integer.parseInt(optionId);
            } catch (NumberFormatException e) {
                return getDefaultMessage();
            }

            GraphState selectedState = matchedStates.get(id);

            setNextExpectedIntents(Collections.singletonList(Intent.EB_DESCRIPTION));
            startEBChecker(state);

            state.put(OB_SCREEN_SELECTED, true);

//            state.put(StateVariable.OB_STATE, selectedState);
            state.getStateUpdater().updateOBState(state, selectedState);

            //---------------------

         /*
            String selectedScreenDescription = GraphTransition.getWindowString(
                    selectedState.getScreen().getActivity(),
                    selectedState.getScreen().getWindow());
            response.append("Okay, you selected the screen \"")
                    .append(id + 1)
                    .append(". ")
                    .append(selectedScreenDescription)
                    .append("\"");*/
            response.append("Got it, please tell me how the app is <b>supposed to work</b>");

            state.remove(StateVariable.CURRENT_OB_SCREEN_POSITION);
            state.remove(CONFIRM_END_CONVERSATION_NEGATIVE);

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.OB_SCREENS, MetricsRecorder.YES);

            return createChatBotMessages(response.toString());

        } else if (NONE.equals(message.getMessage())) {

            state.remove(OB_SCREEN_SELECTED);

            //-----------------------------------
            //manage attempts

            boolean negativeEndConversationConfirmation = state.containsKey(CONFIRM_END_CONVERSATION_NEGATIVE);

            if (!negativeEndConversationConfirmation) {

                MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.OB_SCREENS, MetricsRecorder.NO);

                boolean nextAttempt = state.checkNextAttemptAndResetObScreens();

                log.debug("Current attempt (OB_SCREENS): " + state.getCurrentAttemptObScreens());

                if (!nextAttempt) {

                    startEBChecker(state);
                    this.setNextExpectedIntents(Collections.singletonList(Intent.EB_DESCRIPTION));

                    state.getStateUpdater().updateOBState(state, null);

                    return createChatBotMessages("All right, let's continue",
                            "Please tell me how the app is <b>supposed to work</b>"
                    );
                }

                state.increaseCurrentAttemptObScreens();
            }

            //---------------------------
            //increase ob screen position

            //increase the position of the next OB screen batch
            if (!negativeEndConversationConfirmation) {
                currentObScreenPosition += SelectOBScreenAction.MAX_OB_SCREENS_TO_SHOW;
            }
            state.put(StateVariable.CURRENT_OB_SCREEN_POSITION, currentObScreenPosition);

            //---------------------------
            //prepare options

            //get the options
            List<KeyValues> options = SelectOBScreenAction.getObScreenOptions(matchedStates, state,
                    currentObScreenPosition);

            state.remove(CONFIRM_END_CONVERSATION_NEGATIVE);

            if (options.isEmpty()) {

                //FIXME: would this lead to a infinite loop in the conversation?

                state.remove(StateVariable.CURRENT_OB_SCREEN_POSITION);
                this.setNextExpectedIntents(Collections.singletonList(OB_DESCRIPTION));

                return createChatBotMessages("All right. Then, your problem description does not seem " +
                                "to match any app screen",
                        "Please tell me the <b>incorrect behavior</b> one more time"
                );
            } else {

                //----------------------------------

                setNextExpectedIntents(Collections.singletonList(Intent.OB_SCREEN_SELECTED));

                //----------------------------------

                MessageObj messageObj = new MessageObj(
                        "Okay then, which of the following screens is <b>having or triggering the problem</b>?",
                        WidgetName.OBScreenSelector, false);

                return createChatBotMessages(
                        new ChatBotMessage(messageObj, options));
            }
        } else if (UPLOAD.equals(message.getMessage())) {
            startEBChecker(state);
            this.setNextExpectedIntents(Collections.singletonList(Intent.EB_DESCRIPTION));

            state.getStateUpdater().updateOBState(state, null);

            return createChatBotMessages("Your photo has been uploaded.",
                    "Please tell me how the app is <b>supposed to work</b>"
            );
        } else {
            state.remove(CONFIRM_END_CONVERSATION_NEGATIVE);
            return getDefaultMessage();
        }

    }


    private List<ChatBotMessage> getDefaultMessage() {
        this.nextExpectedIntents = Collections.singletonList(Intent.OB_SCREEN_SELECTED);

        return createChatBotMessages(
                "Sorry, the option you selected is incorrect",
                "Please <b>select one screen</b> from the list");
    }
}
