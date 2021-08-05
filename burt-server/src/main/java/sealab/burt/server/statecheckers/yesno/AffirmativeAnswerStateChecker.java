package sealab.burt.server.statecheckers.yesno;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.output.MetricsRecorder;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.List;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class AffirmativeAnswerStateChecker extends StateChecker {

    public AffirmativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {
        ActionName nextAction = null;

        if(state.containsKey(CONFIRM_END_CONVERSATION)){
            state.remove(StateVariable.CONFIRM_END_CONVERSATION);
            state.remove(ACTION_NEGATIVE_END_CONVERSATION);
            nextAction = END_CONVERSATION_ACTION;
        } else if (state.containsKey(APP_ASKED)) {
            state.remove(APP_ASKED);
            nextAction = PROVIDE_OB;
        } else if (state.containsKey(OB_SCREEN_SELECTED)) {
            state.remove(OB_SCREEN_SELECTED);
            nextAction = PROVIDE_EB;

            state.getStateUpdater().updateOBState(state, (GraphState) state.get(OB_STATE));
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(COLLECTING_S2R);
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            // CHECK LAST STEP HERE
            nextAction = REPORT_SUMMARY;
        } else if (state.containsKey(EB_SCREEN_CONFIRMATION)) {
            state.remove(EB_SCREEN_CONFIRMATION);
            nextAction = PROVIDE_S2R_FIRST;
            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.EB_NO_MATCH, MetricsRecorder.YES);
            state.getStateUpdater().updateEBState(state, (GraphState) state.get(EB_STATE));
        } else if (state.containsKey(OB_MATCHED_CONFIRMATION)) {
            state.remove(OB_MATCHED_CONFIRMATION);
            nextAction = PROVIDE_EB;

            QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
            state.getStateUpdater().updateOBState(state, result.getMatchedStates().get(0));

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.OB_MATCHED, MetricsRecorder.YES);
        } else if (state.containsKey(S2R_MATCHED_CONFIRMATION)) {
            state.remove(S2R_MATCHED_CONFIRMATION);

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.S2R_MATCHED, MetricsRecorder.YES);

            state.resetCurrentAttemptS2RMatch();

            //---------------------
            //add the step to the set of S2R for the report

            //get the quality result, we must exist in the state
            QualityFeedback qFeedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

            //------------------
            //decide the next action

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.contains(S2RQualityCategory.LOW_Q_INCORRECT_INPUT))
                nextAction = SPECIFY_INPUT_S2R;
            else if (results.contains(S2RQualityCategory.MISSING)) {
                nextAction = SELECT_MISSING_S2R;
            } else {

                S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                        .filter(f -> f.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                        .findFirst().orElse(null);

                if (assessment == null)
                    throw new RuntimeException("The high quality assessment is required");

                UserResponse msg = (UserResponse) state.get(S2R_MATCHED_MSG);
                String message = msg.getMessages().get(0).getMessage();
                state.getStateUpdater().addStepAndUpdateGraphState(state, message, assessment);

                nextAction = PREDICT_FIRST_S2R_PATH;
            }

        } else if (!state.containsKey(PARTICIPANT_ASKED)) {
            nextAction = PROVIDE_PARTICIPANT_ID;
        }

        return nextAction;
    }
}
