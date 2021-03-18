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
            nextAction = "PROVIDE_OB";
        } else if (state.containsKey("COLLECTING_OB") || state.containsKey("OB_SCREEN_SELECTED")) {
            state.remove("COLLECTING_OB");
            state.remove("OB_SCREEN_SELECTED");
            nextAction = "PROVIDE_EB";
        } else if (state.containsKey("COLLECTING_EB")) {
            state.remove("COLLECTING_EB");
            nextAction = "PROVIDE_S2R_FIRST";
        } else if (state.containsKey("DISAMBIGUATE_S2R")) {
            state.remove("DISAMBIGUATE_S2R");
            nextAction = "PROVIDE_S2R";
        } else if (state.containsKey("CONFIRM_LAST_STEP")) {
            state.remove("CONFIRM_LAST_STEP");
            // CHECK LAST STEP HERE
            nextAction = "REPORT_SUMMARY";
        }

        return nextAction;
    }
}
