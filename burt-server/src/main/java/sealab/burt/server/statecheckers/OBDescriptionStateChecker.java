package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.MessageObj;

import java.util.concurrent.ConcurrentHashMap;

public class OBDescriptionStateChecker extends StateChecker {

    private static ConcurrentHashMap<String, String> nextActions= new ConcurrentHashMap<>(){{
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
        QualityResult result = OBChecker.checkOb(message.getMessage());
        state.put("OB_QUALITY_RESULT", result);
        return nextActions.get(result.getResult().name());
    }
}
