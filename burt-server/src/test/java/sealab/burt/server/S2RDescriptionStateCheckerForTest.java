package sealab.burt.server;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.concurrent.ConcurrentHashMap;

public class S2RDescriptionStateCheckerForTest extends StateChecker {

    public S2RDescriptionStateCheckerForTest(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<String, Object> state) {
        UserMessage userMessage = (UserMessage) state.get("CURRENT_MESSAGE");
        return userMessage.getCurrentAction();
    }
}
