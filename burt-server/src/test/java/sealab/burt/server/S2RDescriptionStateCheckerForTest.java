package sealab.burt.server;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;

public class S2RDescriptionStateCheckerForTest extends StateChecker {

    public S2RDescriptionStateCheckerForTest(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConversationState state) {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        return userResponse.getCurrentAction();
    }
}
