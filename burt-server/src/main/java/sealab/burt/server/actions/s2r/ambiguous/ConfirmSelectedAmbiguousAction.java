package sealab.burt.server.actions.s2r.ambiguous;


import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.s2r.missing.SelectMissingS2RAction;
import sealab.burt.server.conversation.entity.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.MetricsRecorder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.S2R_MISSING_SELECTED;

public class ConfirmSelectedAmbiguousAction extends ChatBotAction {

    public ConfirmSelectedAmbiguousAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        UserResponse currentMessage = (UserResponse) state.get(CURRENT_MESSAGE);

        @SuppressWarnings("unchecked") final List<AppStep> allAmbiguousSteps = (List<AppStep>) state.get(S2R_ALL_AMBIGUOUS);


        return respondWithChoices(state, currentMessage);

//        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);
//
//        S2RQualityAssessment AmbiguousAssessment = feedback.getQualityAssessments().stream()
//                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.LOW_Q_AMBIGUOUS))
//                .findFirst().orElse(null);
//
//        if (AmbiguousAssessment == null)
//        throw new RuntimeException("The ambiguous assessment is required");
//
//
//
//        this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION); //FIXME
//
//        MessageObj message = currentMessage.getFirstMessage();
//
//        if (DONE.equals(message.getMessage())) {
//
//            List<String> selectedValues = message.getSelectedValues();
//
//            List<AppStep> selectedSteps = selectedValues.stream()
//                    .map(selectedValue -> allAmbiguousSteps.get(Integer.parseInt(selectedValue)))
//                    .collect(Collectors.toList());
//
//            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
//                return getDefaultMessage(allAmbiguousSteps, state);
//
//            state.getStateUpdater().addStepsToState(state, selectedSteps);
//
//
//
//            // FIXME, here we ask the user the next step, rather than predicting
//            return createChatBotMessages("Got it, what is the step that you performed next?");
//
//        }
//        else if (NONE.equals(message.getMessage())) {
//
//            boolean negativeEndConversationConfirmation = state.containsKey(CONFIRM_END_CONVERSATION_NEGATIVE);
//            if (!negativeEndConversationConfirmation) {
//                state.getStateUpdater().addStepAndUpdateGraphState(state, highQualityStepMessage,
//                        highQualityAssessment);
//
//                MetricsRecorder.saveRecommendationRecord(state, MetricsRecorder.MetricsType.S2R_MISSING,
//                        allMissingSteps.size(), 0);
//            }
//        }



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

    private List<ChatBotMessage> getDefaultMessage(List<AppStep> allMissingSteps,
            ConversationState state) {
        this.nextExpectedIntents = Collections.singletonList(S2R_MISSING_SELECTED);
        List<KeyValues> stepOptions = SelectMissingS2RAction.getStepOptions(allMissingSteps, state);

        MessageObj messageObj = new MessageObj(
                "From the following options, please select the <b>steps that you performed before this step</b>",
                WidgetName.S2RScreenSelector, true);

        return createChatBotMessages(
                "Sorry, the options you selected are incorrect",
                new ChatBotMessage(messageObj, stepOptions));
    }


}
