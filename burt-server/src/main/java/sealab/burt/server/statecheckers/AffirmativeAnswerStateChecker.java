package sealab.burt.server.statecheckers;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.actions.ActionName.*;

import static sealab.burt.server.StateVariable.*;

public class AffirmativeAnswerStateChecker extends StateChecker {
    public AffirmativeAnswerStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        ActionName nextAction = null;

        if (state.containsKey(APP_ASKED)) {
            state.remove(APP_ASKED);
            nextAction = PROVIDE_OB;
        } else if (state.containsKey(COLLECTING_OB) || state.containsKey(OB_SCREEN_SELECTED)) {
            state.remove(COLLECTING_OB);
            state.remove(OB_SCREEN_SELECTED);
            nextAction = PROVIDE_EB;
        } else if (state.containsKey(COLLECTING_EB)) {
            state.remove(COLLECTING_EB);
            nextAction = PROVIDE_S2R_FIRST;
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(COLLECTING_S2R);
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            // CHECK LAST STEP HERE
            nextAction = REPORT_SUMMARY;
        }

        return nextAction;
    }
}
