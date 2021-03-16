package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.MessageObj;

import java.util.concurrent.ConcurrentHashMap;

public class EBDescriptionStateChecker extends StateChecker {

//    private static ConcurrentHashMap<String, String> nextActions= new ConcurrentHashMap<>(){{
//        put(QualityResult.Result.MATCH.name(), "PROVIDE_S2R_FIRST");
//        put(QualityResult.Result.NO_MATCH.name(), "CLARIFY_EB");
//    }};

    public EBDescriptionStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
//        MessageObj message = (MessageObj) state.get("CURRENT_MESSAGE");
//        QualityResult result = OBChecker.checkOb(message.getMessage());
//        state.put("OB_QUALITY_RESULT", result);
//        return nextActions.get(result.getResult().name());
        return "CLARIFY_EB";
    }

}
