package sealab.burt.server.statecheckers;
import sealab.burt.qualitychecker.S2RLastStepChecker;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.MessageObj;

import java.util.concurrent.ConcurrentHashMap;

public class S2RDescriptionStateChecker extends StateChecker{
    private static final ConcurrentHashMap<String, String> nextActions= new ConcurrentHashMap<>(){{
        put(QualityResult.Result.IS_OK.name(), "PREDICT_S2R");
        put(QualityResult.Result.AMBIGUOUS.name(), "DISAMBIGUATE_S2R");
        put(QualityResult.Result.NOT_WRITTEN_PROPERLY.name(), "REPHRASE_S2R");
        put(QualityResult.Result.LACK_INPUT.name(), "SPECIFY_INPUT_S2R");
        put(QualityResult.Result.MISSING_STEPS.name(), "SELECT_MISSING_S2R");
    }};

    public S2RDescriptionStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        MessageObj message = (MessageObj) state.get("CURRENT_MESSAGE");
        // check if it is the last step
        QualityResult result = S2RChecker.checkS2R(message.getMessage());
        state.put("S2R_QUALITY_RESULT", result);
        if (result.getResult().name().equals("IS_OK")){
            if (S2RLastStepChecker.checkIfLastStep(message.getMessage())){
                return "CONFIRM_LAST_STEP";
            }
        }
        return nextActions.get(result.getResult().name());


    }
}
