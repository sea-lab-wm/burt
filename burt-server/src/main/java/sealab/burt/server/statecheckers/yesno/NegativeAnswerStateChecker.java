package sealab.burt.server.statecheckers.yesno;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.output.MetricsRecorder;
import sealab.burt.server.statecheckers.StateChecker;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class NegativeAnswerStateChecker extends StateChecker {

    public NegativeAnswerStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {
        ActionName nextAction = null;

        if (state.containsKey(CONFIRM_END_CONVERSATION)) {

            state.put(StateVariable.CONFIRM_END_CONVERSATION_NEGATIVE, true);

            //----------------------

            state.remove(StateVariable.CONFIRM_END_CONVERSATION);

            ActionName action = (ActionName) state.get(ACTION_NEGATIVE_END_CONVERSATION);
            UserResponse lastUserResponse = (UserResponse) state.get(MSG_NEGATIVE_END_CONVERSATION);
            if (lastUserResponse != null) state.put(CURRENT_MESSAGE, lastUserResponse);

            nextAction = action;

            state.remove(StateVariable.ACTION_NEGATIVE_END_CONVERSATION);
            state.remove(StateVariable.MSG_NEGATIVE_END_CONVERSATION);
        } else if (state.containsKey(StateVariable.CONFIRM_LAST_STEP)) {
            state.remove(StateVariable.CONFIRM_LAST_STEP);
            nextAction = PROVIDE_S2R;
            state.putIfAbsent(COLLECTING_S2R, true);
        }
        /*else if (state.containsKey(APP_ASKED)) {
            nextAction = SELECT_APP;
        }*/
/*        else if (state.containsKey(OB_SCREEN_SELECTED)) {
            //FIXME: should we start over after a "page" of options have already skipped by the user?
            //or should we show the last page?
            nextAction = SELECT_OB_SCREEN;
        }*/
        /*else if (state.containsKey(COLLECTING_EB)){
            nextAction = CLARIFY_EB;
        }*/
        else if (state.containsKey(EB_SCREEN_CONFIRMATION)) {
            nextAction = PROVIDE_EB;

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.EB_NO_MATCH, MetricsRecorder.NO);
        } else if (state.containsKey(OB_MATCHED_CONFIRMATION)) {
            state.remove(StateVariable.OB_MATCHED_CONFIRMATION);

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.OB_MATCHED, MetricsRecorder.NO);

            boolean newAttempt = state.checkNextAttemptAndResetObMatched();

            if (newAttempt) {
                nextAction = PROVIDE_OB;
            } else {
                nextAction = PROVIDE_EB;
                state.getStateUpdater().updateOBState(state, null);
            }

        } else if (state.containsKey(S2R_MATCHED_CONFIRMATION)) {
            state.remove(S2R_MATCHED_CONFIRMATION);
            nextAction = PROVIDE_S2R_NO_MATCH;

            MetricsRecorder.saveMatchRecord(state, MetricsRecorder.MetricsType.S2R_MATCHED, MetricsRecorder.NO);
        }
        return nextAction;


    }
}
