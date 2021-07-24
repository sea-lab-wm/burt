package sealab.burt.server.statecheckers;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.conversation.UserResponse;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class AffirmativeAnswerStateChecker extends StateChecker {

    ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(S2RQualityCategory.LOW_Q_INCORRECT_INPUT.name(), SPECIFY_INPUT_S2R);
        put(S2RQualityCategory.MISSING.name(), SELECT_MISSING_S2R);
    }};


    public AffirmativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {
        ActionName nextAction = null;

        if (state.containsKey(APP_ASKED)) {
            state.remove(APP_ASKED);
            nextAction = PROVIDE_OB;
        } else if (state.containsKey(OB_SCREEN_SELECTED)) {
            state.remove(OB_SCREEN_SELECTED);
            nextAction = PROVIDE_EB;

            QualityStateUpdater.updateOBState(state, (GraphState) state.get(OB_STATE));
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(COLLECTING_S2R);
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            // CHECK LAST STEP HERE
            nextAction = REPORT_SUMMARY;
        } else if (state.containsKey(EB_SCREEN_CONFIRMATION)) {
            state.remove(EB_SCREEN_CONFIRMATION);
            nextAction = PROVIDE_S2R_FIRST;

            QualityStateUpdater.updateEBState(state, (GraphState) state.get(EB_STATE));
        } else if (state.containsKey(OB_MATCHED_CONFIRMATION)) {
            state.remove(OB_MATCHED_CONFIRMATION);
            nextAction = PROVIDE_EB;

            QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
            QualityStateUpdater.updateOBState(state, result.getMatchedStates().get(0));

        } else if (state.containsKey(S2R_MATCHED_CONFIRMATION)) {
            state.remove(S2R_MATCHED_CONFIRMATION);

            //---------------------
            //add the step to the set of S2R for the report

            //get the quality result, we must exist in the state
            QualityFeedback qFeedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

            S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                    .filter(f -> f.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                    .findFirst().orElse(null);

            if (assessment == null)
                throw new RuntimeException("The high quality assessment is required");
            UserResponse msg = (UserResponse) state.get(S2R_MATCHED_MSG);
            String message = msg.getMessages().get(0).getMessage();
            QualityStateUpdater.addStepAndUpdateGraphState(state, message, assessment);

            //------------------
            //decide the next step

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.contains(S2RQualityCategory.LOW_Q_INCORRECT_INPUT))
                nextAction = nextActions.get(S2RQualityCategory.LOW_Q_INCORRECT_INPUT.name());
            else if (results.contains(S2RQualityCategory.MISSING)) {
                state.put(S2R_HQ_MISSING, message);
                nextAction = nextActions.get(S2RQualityCategory.MISSING.name());
            } else {
                nextAction = PREDICT_FIRST_S2R;
            }

        } else if (!state.containsKey(PARTICIPANT_ASKED)) {
            nextAction = PROVIDE_PARTICIPANT_ID;
        }

        return nextAction;
    }
}
