package sealab.burt.server.statecheckers;


import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;

public class DefaultActionStateChecker extends StateChecker {
    public DefaultActionStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConversationState state) {
        return getDefaultAction();
    }
}