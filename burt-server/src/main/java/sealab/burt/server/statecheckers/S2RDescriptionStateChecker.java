package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.S2R_CHECKER;
import static sealab.burt.server.actions.ActionName.*;

public class S2RDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), PREDICT_S2R);
        put(QualityResult.Result.MULTIPLE_MATCH.name(), ActionName.DISAMBIGUATE_S2R);
        put(QualityResult.Result.NO_MATCH.name(), REPHRASE_S2R);
        put(QualityResult.Result.NO_S2R_INPUT.name(), SPECIFY_INPUT_S2R);
        put(QualityResult.Result.MISSING_STEPS.name(), SELECT_MISSING_S2R);
        put(QualityResult.Result.NO_PARSED.name(), PROVIDE_S2R_NO_PARSE);
    }};

    public S2RDescriptionStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {

        try {
            // check if it is last step
            UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
            String message = userMessage.getMessages().get(0).getMessage();
            String targetString = "last step";
            if (message.toLowerCase().contains(targetString.toLowerCase())){
                return ActionName.CONFIRM_LAST_STEP;
            }else {
                S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
                QualityResult result = checker.checkS2R(userMessage.getMessages().get(0).getMessage());
                return nextActions.get(result.getResult().name());
            }

        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return null;
        }

    }
}
