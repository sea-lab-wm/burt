package sealab.burt.server.statecheckers;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.StateVariable.*;

public class NegativeAnswerStateChecker extends StateChecker {

    public NegativeAnswerStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        ActionName nextAction = null;

        if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            nextAction = PROVIDE_S2R;
            state.putIfAbsent(COLLECTING_S2R, true);
        } else if(state.containsKey(APP_ASKED)) {
            nextAction = SELECT_APP;
        } else if (state.containsKey(OB_SCREEN_SELECTED)){
            nextAction = UNEXPECTED_ERROR;
        } else if (state.containsKey(COLLECTING_EB)){
            nextAction = CLARIFY_EB;
        }
        return nextAction;


    }
}
