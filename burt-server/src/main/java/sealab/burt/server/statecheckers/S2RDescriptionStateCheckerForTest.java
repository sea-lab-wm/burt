package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.UserMessage;

import java.util.concurrent.ConcurrentHashMap;

public class S2RDescriptionStateCheckerForTest extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

//    private static final ConcurrentHashMap<String, String> nextActions = new ConcurrentHashMap<>() {{
//        put(QualityResult.Result.MATCH.name(), "PREDICT_S2R");
//        put(QualityResult.Result.MULTIPLE_MATCH.name(), "DISAMBIGUATE_S2R");
//        put(QualityResult.Result.NO_MATCH.name(), "REPHRASE_S2R");
//        put(QualityResult.Result.NO_S2R_INPUT.name(), "SPECIFY_INPUT_S2R");
//        put(QualityResult.Result.MISSING_STEPS.name(), "SELECT_MISSING_S2R");
//    }};

    public S2RDescriptionStateCheckerForTest(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {

        try {
            UserMessage userMessage = (UserMessage) state.get("CURRENT_MESSAGE");;
            return userMessage.getCurrentAction();
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return null;
        }

    }
}
