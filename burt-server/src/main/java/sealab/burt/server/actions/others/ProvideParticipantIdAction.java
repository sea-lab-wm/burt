package sealab.burt.server.actions.others;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.PARTICIPANT_ASKED;
import static sealab.burt.server.StateVariable.PARTICIPANT_VALIDATED;

public class ProvideParticipantIdAction extends ChatBotAction {

    public ProvideParticipantIdAction(Intent nextIntent) {
        super(nextIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {
            return createChatBotMessages("Sorry, I couldn't identify the <b>Participant ID</b>, " +
                    "please provide it one more time");
    }
}
