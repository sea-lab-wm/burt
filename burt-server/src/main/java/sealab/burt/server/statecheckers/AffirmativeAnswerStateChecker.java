package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class AffirmativeAnswerStateChecker extends StateChecker {
    public AffirmativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        ActionName nextAction = null;

        if (state.containsKey(APP_ASKED)) {
            state.remove(APP_ASKED);
            nextAction = PROVIDE_OB;
        } else if (state.containsKey(OB_SCREEN_SELECTED)) {
            state.remove(OB_SCREEN_SELECTED);
            nextAction = PROVIDE_EB;

            QualityStateUpdater.updateOBState(state, (GraphState) state.get(OB_STATE));
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(COLLECTING_S2R);
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            // CHECK LAST STEP HERE
            nextAction = REPORT_SUMMARY;
        } else if (state.containsKey(EB_SCREEN_CONFIRMATION)) {
            state.remove(EB_SCREEN_CONFIRMATION);
            nextAction = PROVIDE_S2R_FIRST;

            QualityStateUpdater.updateEBState(state, (GraphState) state.get(EB_STATE));
        } else if (!state.containsKey(PARTICIPANT_ASKED)) {
            nextAction = PROVIDE_PARTICIPANT_ID;
        }

        return nextAction;
    }
}
