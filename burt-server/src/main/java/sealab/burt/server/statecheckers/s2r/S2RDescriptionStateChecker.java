package sealab.burt.server.statecheckers.s2r;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.statecheckers.ob.OBDescriptionStateChecker;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.qualitychecker.s2rquality.S2RQualityCategory.*;
import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class S2RDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(S2RQualityCategory.HIGH_QUALITY.name(), CONFIRM_MATCHED_S2R);
        put(S2RQualityCategory.LOW_Q_AMBIGUOUS.name(), ActionName.DISAMBIGUATE_S2R);
        put(S2RQualityCategory.LOW_Q_VOCAB_MISMATCH.name(), REPHRASE_S2R);
        put(S2RQualityCategory.LOW_Q_NOT_PARSED.name(), PROVIDE_S2R_NO_PARSE);
    }};

    public S2RDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {

        try {
            UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
            String message = userResponse.getFirstMessage().getMessage();

            //-------------------------------

            //Check for last step

            if (isLastStep(message)) {
                //ask for the first step, if there was no first step provided
                if (!state.containsKey(REPORT_S2R))
                    return PROVIDE_S2R_FIRST;
                else
                    return ActionName.CONFIRM_LAST_STEP;
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
            if(results.contains(S2RQualityCategory.HIGH_QUALITY)){


                S2RQualityAssessment assessment = qFeedback.getQualityAssessments().stream()
                        .filter(f -> f.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                        .findFirst().orElse(null);

                if (assessment == null)
                    throw new RuntimeException("The high quality assessment is required");

                Integer action = assessment.getMatchedSteps().get(0).getAction();
                if (DeviceUtils.isOpenApp(action) || DeviceUtils.isCloseApp(action)) {
                    QualityStateUpdater.addStepAndUpdateGraphState(state, message, assessment);

                    return PREDICT_FIRST_S2R_PATH;
                }else
                    return nextActions.get(S2RQualityCategory.HIGH_QUALITY.name());
            }

            S2RQualityCategory assessmentCategory = results.get(0);

            if(!Arrays.asList(LOW_Q_VOCAB_MISMATCH, LOW_Q_AMBIGUOUS, LOW_Q_NOT_PARSED)
                    .contains(assessmentCategory))
                throw new RuntimeException("Unsupported quality assessment combination: " + results);

            return nextActions.get(assessmentCategory.name());

        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }

    }

    public static boolean isLastStep(String message) {
        String targetString = "last step";
        return message.toLowerCase().contains(targetString.toLowerCase());
    }

}
