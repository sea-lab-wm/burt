package sealab.burt.server.statecheckers.participant;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.statecheckers.StateChecker;

import static sealab.burt.server.StateVariable.*;

public class ParticipantNameStateChecker extends StateChecker {

    public ParticipantNameStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {

        //-----------------------------
        //parse the message
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        String message = userResponse.getFirstMessage().getMessage();

        state.remove(PARTICIPANT_ASKED);
        state.put(PARTICIPANT_NAME, message.toUpperCase());

        // Participant ID is hardcoded to P20
        state.put(PARTICIPANT_ID, "P20");
        return ActionName.SELECT_APP;
    }
}
