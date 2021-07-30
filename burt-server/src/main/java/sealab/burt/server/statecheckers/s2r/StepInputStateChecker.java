package sealab.burt.server.statecheckers.s2r;

import sealab.burt.qualitychecker.actionmatcher.NLActionS2RMatcher;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class StepInputStateChecker extends StateChecker {

    public StepInputStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {


        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        MessageObj message = msg.getFirstMessage();
        String msgText = message.getMessage();

        final boolean isObjectLiteral = NLActionS2RMatcher.isLiteralValue(msgText)
                || NLActionS2RMatcher.getLiteralValue(msgText) != null;

        if(!isObjectLiteral){
            return ActionName.SPECIFY_NEXT_INPUT_S2R;
        } else {

            //get the quality result, we must exist in the state
            QualityFeedback qFeedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

            S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                    .filter(f -> f.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                    .findFirst().orElse(null);

            if (assessment == null)
                throw new RuntimeException("The high quality assessment is required");

            AppStep appStep = assessment.getMatchedSteps().get(0);
            appStep.setText(msgText);

            UserResponse highQualityMessage = (UserResponse) state.get(S2R_MATCHED_MSG);
            String hqMsgText = highQualityMessage.getMessages().get(0).getMessage();

            String hqMsgTextFinal = String.format("%s (value = %s)", hqMsgText, msgText);

            QualityStateUpdater.addStepAndUpdateGraphState(state, hqMsgTextFinal, assessment);

            return PREDICT_FIRST_S2R_PATH;
        }
    }
}
