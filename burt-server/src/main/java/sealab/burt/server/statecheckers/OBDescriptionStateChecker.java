package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.UserMessage;

import java.util.concurrent.ConcurrentHashMap;

public class OBDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, String> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), "PROVIDE_EB");
        put(QualityResult.Result.MULTIPLE_MATCH.name(), "SELECT_OB_SCREEN");
        put(QualityResult.Result.NO_MATCH.name(), "REPHRASE_OB");
    }};

    public OBDescriptionStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        try {
            UserMessage userMessage = (UserMessage) state.get("CURRENT_MESSAGE");
            OBChecker obChecker = (OBChecker) state.get("OB_CHECKER");
            QualityResult result = obChecker.checkOb(userMessage.getMessages().get(0).getMessage());
            state.put("OB_QUALITY_RESULT", result);
            return nextActions.get(result.getResult().name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return "UNEXPECTED_ERROR";
        }
    }
}
