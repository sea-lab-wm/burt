package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.OBChecker;

import java.util.concurrent.ConcurrentHashMap;

public class AffirmativeAnswerStateChecker extends StateChecker {
    public AffirmativeAnswerStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        String nextAction = null;

        if (state.containsKey("APP_ASKED")) {
            //create the OB checker
            state.remove("APP_ASKED");
            state.put("OB_CHECKER", new OBChecker(state.get("APP").toString()));
            state.put("CONVERSATION_STATE", "COLLECTING_OB");
            nextAction = "PROVIDE_OB";
        }
        else if (state.containsKey("COLLECTING_OB")) {
            nextAction = "PROVIDE_EB";
            state.put("CONVERSATION_STATE", "COLLECTING_EB");
        }
        else if (state.containsKey("COLLECTING_EB")){
            nextAction = "PROVIDE_S2R_FIRST";
            state.put("CONVERSATION_STATE", "COLLECTING_S2R");
        }
        else if (state.containsKey("DISAMBIGUATE_S2R")) {
            nextAction = "PROVIDE_S2R";
        }
        else if (state.containsKey("CONFIRM_LAST_STEP")) {
            // CHECK LAST STEP HERE
            nextAction = "REPORT_SUMMARY";
            state.put("S2R_COLLECTED", true);
            state.putIfAbsent("CONVERSATION_STATE", "REPORTING_SUMMARY");
        }

        return nextAction;
    }
}
