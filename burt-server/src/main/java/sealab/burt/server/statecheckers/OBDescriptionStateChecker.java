package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.MessageObj;

import java.util.concurrent.ConcurrentHashMap;

public class OBDescriptionStateChecker extends StateChecker {

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
        MessageObj message = (MessageObj) state.get("CURRENT_MESSAGE");
        OBChecker obChecker = (OBChecker) state.get("OB_CHECKER");
        QualityResult result = obChecker.checkOb(message.getMessage());
        state.put("OB_QUALITY_RESULT", result);
        return nextActions.get(result.getResult().name());
    }
}
