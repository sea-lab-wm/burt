package sealab.burt.server.statecheckers.ob;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.statecheckers.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.StateVariable.CURRENT_ATTEMPT_OB_MATCHED;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.actions.ob.ConfirmMatchedOBAction.MAX_ATTEMPTS_OB_MATCHED;

public @Slf4j
class OBDescriptionStateChecker extends StateChecker {

    public static final Integer MAX_ATTEMPTS_OB_NO_MATCH = 3;

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), CONFIRM_MATCHED_OB);
        put(QualityResult.Result.MULTIPLE_MATCH.name(), SELECT_OB_SCREEN);
        put(QualityResult.Result.NO_MATCH.name(), REPHRASE_OB);
        put(QualityResult.Result.NOT_PARSED.name(), PROVIDE_OB_NO_PARSE);
    }};

    public OBDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {
        QualityResult result = runOBQualityCheck(state);
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        state.put(OB_DESCRIPTION, userResponse.getFirstMessage().getMessage());

        ActionName nextAction = nextActions.get(result.getResult().name());

        //we ask for the rephrase only 3 times, otherwise we skip the OB
        if(result.getResult().equals(QualityResult.Result.NO_MATCH)){
            Integer currentAttempt = (Integer) state.putIfAbsent(CURRENT_ATTEMPT_OB_NO_MATCH, 1);
            if (currentAttempt != null) {
                state.put(CURRENT_ATTEMPT_OB_NO_MATCH, ++currentAttempt);
                if (currentAttempt >= MAX_ATTEMPTS_OB_NO_MATCH) {
                    state.remove(CURRENT_ATTEMPT_OB_NO_MATCH);
                    nextAction = PROVIDE_EB;
                    QualityStateUpdater.updateOBState(state, null);
                }

            }
        }

        return nextAction;
    }

}
