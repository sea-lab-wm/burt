package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationState;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.actions.ob.ConfirmMatchedOBAction.MAX_ATTEMPTS_OB_MATCHED;

public class NegativeAnswerStateChecker extends StateChecker {

    public NegativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {
        ActionName nextAction = null;

        if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            nextAction = PROVIDE_S2R;
            state.putIfAbsent(COLLECTING_S2R, true);
        } else if (state.containsKey(APP_ASKED)) {
            nextAction = SELECT_APP;
        } else if (state.containsKey(OB_SCREEN_SELECTED)) {
            nextAction = SELECT_OB_SCREEN;
        }
        /*else if (state.containsKey(COLLECTING_EB)){
            nextAction = CLARIFY_EB;
        }*/
        else if (state.containsKey(EB_SCREEN_CONFIRMATION)) {
            nextAction = PROVIDE_EB;
        } else if (state.containsKey(OB_MATCHED_CONFIRMATION)) {
            Integer currentAttempt = (Integer) state.get(CURRENT_ATTEMPT_OB_MATCHED);
            if (currentAttempt >= MAX_ATTEMPTS_OB_MATCHED) {
                state.remove(CURRENT_ATTEMPT_OB_MATCHED);
                nextAction = PROVIDE_EB;

                QualityStateUpdater.updateOBState(state, null);

            } else
                nextAction = PROVIDE_OB;
        } else if (state.containsKey(S2R_MATCHED_CONFIRMATION)) {
            state.remove(S2R_MATCHED_CONFIRMATION);
            nextAction = PROVIDE_S2R_NO_MATCH;
        }
        return nextAction;


    }
}
