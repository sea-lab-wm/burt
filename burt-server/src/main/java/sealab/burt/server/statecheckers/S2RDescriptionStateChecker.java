package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserResponse;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class S2RDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(S2RQualityCategory.HIGH_QUALITY.name(), PROVIDE_S2R);
        put(S2RQualityCategory.LOW_Q_AMBIGUOUS.name(), ActionName.DISAMBIGUATE_S2R);
        put(S2RQualityCategory.LOW_Q_VOCAB_MISMATCH.name(), REPHRASE_S2R);
        put(S2RQualityCategory.LOW_Q_INCORRECT_INPUT.name(), SPECIFY_INPUT_S2R);
        put(S2RQualityCategory.MISSING.name(), SELECT_MISSING_S2R);
        put(S2RQualityCategory.LOW_Q_NOT_PARSED.name(), PROVIDE_S2R_NO_PARSE);
    }};

    public S2RDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {

        try {
            UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
            String message = userResponse.getFirstMessage().getMessage();

            //-------------------------------

            String targetString = "last step";
            if (message.toLowerCase().contains(targetString.toLowerCase())) {
                //ask for the first step, if there was no first step provided
                if (!state.containsKey(REPORT_S2R))
                    return PROVIDE_S2R_FIRST;
                else
                    return ActionName.CONFIRM_LAST_STEP;
            }

            //------------------------

            QualityFeedback qFeedback = runS2RQualityCheck(state);

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.isEmpty()) throw new RuntimeException("No quality assessment");

            if (results.size() > 1) {
                //FIXME: what if there is high quality result?
                if (results.contains(S2RQualityCategory.LOW_Q_INCORRECT_INPUT))
                    return nextActions.get(S2RQualityCategory.LOW_Q_INCORRECT_INPUT.name());
                else if (results.contains(S2RQualityCategory.MISSING)) {
                    if(results.contains(S2RQualityCategory.HIGH_QUALITY))
                        state.put(S2R_HQ_MISSING, message);
                    return nextActions.get(S2RQualityCategory.MISSING.name());
                } else
                    throw new RuntimeException("Unsupported quality assessment combination: " + results);
            }

            S2RQualityCategory assessmentCategory = results.get(0);

            if (results.contains(S2RQualityCategory.HIGH_QUALITY)) {
                S2RQualityAssessment assessment = qFeedback.getQualityAssessments().get(0);
                QualityStateUpdater.addStepAndUpdateGraphState(state, message, assessment);
            }

            return nextActions.get(assessmentCategory.name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }

    }

}
