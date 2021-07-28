package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.*;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.statecheckers.QualityStateUpdater;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.S2R_MISSING_SELECTED;

public class ConfirmSelectedMissingAction extends ChatBotAction {

    public ConfirmSelectedMissingAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);

        @SuppressWarnings("unchecked") final List<AppStep> allMissingSteps = (List<AppStep>) state.get(S2R_ALL_MISSING);

        if (msg.getMessages().isEmpty()) {
            return getDefaultMessage(allMissingSteps, state);
        }

        //-------------------------------

        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);
        S2RQualityAssessment highQualityAssessment = feedback.getQualityAssessments().stream()
                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                .findFirst().orElse(null);
        String highQualityStepMessage = (String) state.get(S2R_HQ_MISSING);

        //------------------------------
        this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

        MessageObj message = msg.getFirstMessage();
        if ("done".equals(message.getMessage())) {

            List<String> selectedValues = message.getSelectedValues();

            List<AppStep> selectedSteps = selectedValues.stream()
                    .map(selectedValue -> allMissingSteps.get(Integer.parseInt(selectedValue)))
                    .collect(Collectors.toList());

        /*    List<AppStep> selectedSteps = allMissingSteps.stream()
                    .filter(step -> selectedValues.contains(step.getId().toString()))
                    .collect(Collectors.toList());*/

            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
                return getDefaultMessage(allMissingSteps, state);

            QualityStateUpdater.addStepsToState(state, selectedSteps);

            if (highQualityStepMessage != null)
                QualityStateUpdater.addStepAndUpdateGraphState(state, highQualityStepMessage, highQualityAssessment);

            //---------------------

            StringBuilder msg1 = new StringBuilder();
            msg1.append("Okay, you selected ")
                    .append(selectedSteps.size())
                    .append(" prior step(s).");

            StringBuilder msg2 = new StringBuilder();
            msg2.append("What step did you perform after the step \"")
                    .append(highQualityStepMessage)
                    .append("\"?");

            return createChatBotMessages(msg1.toString(), msg2.toString());

        } else if ("none of above".equals(message.getMessage())) {

            if (highQualityStepMessage != null)
                QualityStateUpdater.addStepAndUpdateGraphState(state, highQualityStepMessage, highQualityAssessment);

            return createChatBotMessages("Got it, what is the next step?");
        } else {
            return getDefaultMessage(allMissingSteps, state);
        }

    }

    private List<ChatBotMessage> getDefaultMessage(List<AppStep> allMissingSteps,
                                                   ConversationState state) {
        this.nextExpectedIntents = Collections.singletonList(S2R_MISSING_SELECTED);
        List<KeyValues> stepOptions = SelectMissingS2RAction.getStepOptions(allMissingSteps, state);

        MessageObj messageObj = new MessageObj(
                "From the following options, select the steps you performed before this step",
                WidgetName.S2RScreenSelector);

        return createChatBotMessages(
                "Sorry, the options you selected are incorrect.",
                new ChatBotMessage(messageObj, stepOptions, true));
    }


}
