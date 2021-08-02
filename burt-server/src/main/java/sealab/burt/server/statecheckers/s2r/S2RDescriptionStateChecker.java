package sealab.burt.server.statecheckers.s2r;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.*;
import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.REPORT_S2R;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class S2RDescriptionStateChecker extends StateChecker {

    private static final ConcurrentHashMap<S2RQualityCategory, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(HIGH_QUALITY, CONFIRM_MATCHED_S2R);
        put(LOW_Q_AMBIGUOUS, DISAMBIGUATE_S2R);
        put(LOW_Q_VOCAB_MISMATCH, REPHRASE_S2R);
        put(LOW_Q_NOT_PARSED, PROVIDE_S2R_NO_PARSE);
    }};

    public S2RDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {

        try {
            UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
            String currentMessage = userResponse.getFirstMessage().getMessage();

            //-------------------------------

            //Check for last step

            if (isLastStep(currentMessage)) {
                //ask for the first step, if there was no first step provided
                if (!state.containsKey(REPORT_S2R))
                    return PROVIDE_S2R_FIRST;
                else
                    return CONFIRM_LAST_STEP;
            }

            //------------------------

            //run quality feedback

            QualityFeedback qFeedback = runS2RQualityCheck(state);

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.isEmpty()) throw new RuntimeException("No quality assessment");

            //-------------------------------------------

            //Note: HIGH-QUALITY could be combined with all the other quality tags except for LOW_Q_VOCAB_MISMATCH,
            // LOW_Q_AMBIGUOUS, and LOW_Q_NOT_PARSED

            //if high quality -> confirm S2R
            if (results.contains(HIGH_QUALITY)) {

                //reset the current attempts
                state.resetCurrentAttemptS2RNotParsed();
                state.resetCurrentAttemptS2RNoMatch();
                state.resetCurrentAttemptS2RAmbiguous();
                state.resetCurrentAttemptS2RInput();

                //--------------------------

                S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                        .filter(f -> f.getCategory().equals(HIGH_QUALITY))
                        .findFirst().orElse(null);

                if (assessment == null)
                    throw new RuntimeException("The high quality assessment is required");

                Integer action = assessment.getMatchedSteps().get(0).getAction();
                if (DeviceUtils.isOpenApp(action) || DeviceUtils.isCloseApp(action)
                        || DeviceUtils.isClickBackButton(action)) {
                    state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, assessment);
                    return PREDICT_FIRST_S2R_PATH;
                } else {

                    state.initOrIncreaseCurrentAttemptS2RMatch();
                    boolean nextAttempt = state.checkNextAttemptAndResetS2RMatch();

                    if (!nextAttempt) {
                        state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, assessment);
                        return PREDICT_FIRST_S2R_PATH;
                    }

                    return nextActions.get(HIGH_QUALITY);
                }
            }

            //------------------------------------------------------

            S2RQualityCategory assessmentCategory = results.get(0);

            if (!Arrays.asList(LOW_Q_VOCAB_MISMATCH, LOW_Q_AMBIGUOUS, LOW_Q_NOT_PARSED)
                    .contains(assessmentCategory))
                throw new RuntimeException("Unsupported quality assessment combination: " + results);

            //------------------------------------------------------

            ActionName nextAction = nextActions.get(assessmentCategory);

            boolean nextAttempt = true;
            if (assessmentCategory.equals(LOW_Q_NOT_PARSED)) {
                state.initOrIncreaseCurrentAttemptS2RNotParsed();
                nextAttempt = state.checkNextAttemptAndResetS2RNotParsed();
            } else if (assessmentCategory.equals(LOW_Q_VOCAB_MISMATCH)) {
                state.initOrIncreaseCurrentAttemptS2RNoMatch();
                nextAttempt = state.checkNextAttemptAndResetS2RNoMatch();
            } else if (assessmentCategory.equals(LOW_Q_AMBIGUOUS)) {
                state.initOrIncreaseCurrentAttemptS2RAmbiguous();
                nextAttempt = state.checkNextAttemptAndResetS2RAmbiguous();
            }

            if (!nextAttempt) {
                nextAction = PROVIDE_S2R;
                state.getStateUpdater().addStepAndUpdateGraphState(state, currentMessage, null);
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
