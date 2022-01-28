package sealab.burt.server.statecheckers.s2r;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.*;
import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.REPORT_S2R;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class NewS2RDescriptionStateChecker extends StateChecker {

    private static final ConcurrentHashMap<S2RQualityCategory, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(HIGH_QUALITY, CONFIRM_MATCHED_S2R);
        put(LOW_Q_AMBIGUOUS, DISAMBIGUATE_S2R);
        put(LOW_Q_VOCAB_MISMATCH, REPHRASE_S2R);
//        put(LOW_Q_NOT_PARSED, PROVIDE_S2R_NO_PARSE);
    }};

    public NewS2RDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception{

        try {
            UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
            String currentMessage = userResponse.getFirstMessage().getMessage();

            //-------------------------------

            //Check for last step

            if (isLastStep(currentMessage)) {
                //ask for the first step, if there was no first step provided
                if (!state.containsKey(REPORT_S2R)) {
                    state.put(StateVariable.COLLECTING_FIRST_S2R, true);
                    return PREDICT_FIRST_S2R_PATH;
                } else
                    return REPORT_SUMMARY;
            }

            //------------------------

            //run quality feedback

            QualityFeedback qFeedback = runS2RQualityCheck(state);

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.isEmpty()) throw new RuntimeException("No quality assessment");


            //-------------------------------------------

            ActionName nextAction = nextActions.get(results.get(0));

            if (results.get(0) == HIGH_QUALITY) {

                S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                        .filter(f -> f.getCategory().equals(HIGH_QUALITY))
                        .findFirst().orElse(null);

                if (assessment == null)
                    throw new RuntimeException("The high quality assessment is required");

                Integer action = assessment.getMatchedSteps().get(0).getAction();
                if (DeviceUtils.isOpenApp(action) || DeviceUtils.isCloseApp(action) || DeviceUtils.isClickBackButton(action)) {
                    state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, assessment);
                    return PREDICT_FIRST_S2R_PATH; // does not need to verify the screenshot
                } else {
                    state.initOrIncreaseCurrentAttemptS2RGeneral();
                }
            }


            if (results.get(0) == LOW_Q_AMBIGUOUS) {

                // FIXME: give user all steps once, right now
                List<AppStep> matchedSteps = qFeedback.getQualityAssessments().get(0).getMatchedSteps();

                state.initOrIncreaseCurrentAttemptS2RGeneral();
                boolean nextAttempt = state.checkNextAttemptAndResetS2RGeneral();

                if (!nextAttempt) {
                    state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, null); // ?
                    return PROVIDE_S2R;
                }
            }

            if (results.get(0) == LOW_Q_VOCAB_MISMATCH) {
               state.initOrIncreaseCurrentAttemptS2RGeneral();
               boolean nextAttempt = state.checkNextAttemptAndResetS2RGeneral();
               if (!nextAttempt) {

                   state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, null); // ?
                   return PROVIDE_S2R;
               }
            }

            return nextAction;



        } catch (Exception e) {
            log.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }

    }

    public static boolean isLastStep(String message) {
        String targetString = "last step";
        return message.toLowerCase().contains(targetString.toLowerCase());
    }

}
