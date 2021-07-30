package sealab.burt.server.statecheckers.ob;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.OB_DESCRIPTION;
import static sealab.burt.server.actions.ActionName.*;

public @Slf4j
class OBDescriptionStateChecker extends StateChecker {

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
        if (result.getResult().equals(QualityResult.Result.NO_MATCH)) {

            state.initOrIncreaseCurrentAttemptObNoMatch();

            boolean nextAttempt = state.checkNextAttemptAndResetObNoMatch();

            if (!nextAttempt) {
                nextAction = PROVIDE_EB;
                QualityStateUpdater.updateOBState(state, null);
            }
        } else if (result.getResult().equals(QualityResult.Result.NOT_PARSED)) {

            state.initOrIncreaseCurrentAttemptObNotParsed();

            boolean nextAttempt = state.checkNextAttemptAndResetObNotParsed();

            if (!nextAttempt) {
                nextAction = PROVIDE_EB;
                QualityStateUpdater.updateOBState(state, null);
            }
        }

        return nextAction;
    }

}
