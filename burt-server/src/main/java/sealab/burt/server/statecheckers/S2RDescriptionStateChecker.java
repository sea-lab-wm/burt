package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.output.OutputMessageObj;

import java.util.ArrayList;
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
            UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
            String message = userMessage.getMessages().get(0).getMessage();

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

            QualityFeedback qFeedback = runS2RChecker(state);

            List<S2RQualityCategory> results = qFeedback.getAssessmentResults();

            if (results.isEmpty()) throw new RuntimeException("No quality assessment");

            if (results.size() > 1) {
                //FIXME: what if there is high quality result?
                if(results.contains(S2RQualityCategory.MISSING))
                   return nextActions.get(S2RQualityCategory.MISSING.name());
                else
                    throw new RuntimeException("Unsupported quality assessment combination: " + results);
            }

            S2RQualityCategory assessmentCategory = results.get(0);

            String screenshotPath = "dummy.png";

            if (results.contains(S2RQualityCategory.HIGH_QUALITY)) {
                if (!state.containsKey(REPORT_S2R)) {
                    List<OutputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new OutputMessageObj(message, screenshotPath));
                    state.put(REPORT_S2R, outputMessageList);
                } else {
                    List<OutputMessageObj> outputMessage = (List<OutputMessageObj>) state.get(REPORT_S2R);
                    outputMessage.add(new OutputMessageObj(message, screenshotPath));
                }
            }

            return nextActions.get(assessmentCategory.name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }

    }

}
