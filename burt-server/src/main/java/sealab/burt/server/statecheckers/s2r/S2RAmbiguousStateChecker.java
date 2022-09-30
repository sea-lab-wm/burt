package sealab.burt.server.statecheckers.s2r;

import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.statecheckers.StateChecker;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;

import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.conversation.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;

public class S2RAmbiguousStateChecker extends StateChecker {
    public S2RAmbiguousStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {
        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        S2RQualityAssessment AmbiguousAssessment = feedback.getQualityAssessments().stream()
                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.LOW_Q_AMBIGUOUS))
                .findFirst().orElse(null);

        if (AmbiguousAssessment == null)
        throw new RuntimeException("The ambiguous assessment is required");
        List<AppStep> AmbiguousSteps = (List<AppStep>) state.get(S2R_ALL_AMBIGUOUS);



        MessageObj message = msg.getFirstMessage();
        if (ChatBotAction.DONE.equals(message.getMessage())) {
            List<String> selectedValues = message.getSelectedValues();

            List<AppStep> selectedSteps = selectedValues.stream()
                    .map(selectedValue -> AmbiguousSteps.get(Integer.parseInt(selectedValue)))
                    .collect(Collectors.toList());

            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
                throw new RuntimeException("The selected steps and predicted steps do not match");

            // FIXME: did not collect non-selected steps
            // state.put(StateVariable.NON_SELECTED_PREDICTED_S2R, nonSelectedSteps);

            state.getStateUpdater().addStepsToState(state, selectedSteps);

            state.remove(S2R_ALL_AMBIGUOUS);
            return ActionName.PREDICT_FIRST_S2R_PATH; // ?

        }else if (ChatBotAction.NONE.equals(message.getMessage())) {

            String UserMessage = (String) state.get(S2R_AMBIGUOUS_MSG);
            state.getStateUpdater().addStepAndUpdateGraphState(state, UserMessage,
                    null);

            state.remove(S2R_AMBIGUOUS_MSG);
            return ActionName.PROVIDE_S2R;



        }

        return null;
    }
}
