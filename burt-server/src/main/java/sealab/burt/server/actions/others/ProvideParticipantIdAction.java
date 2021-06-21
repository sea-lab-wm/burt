package sealab.burt.server.actions.others;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.PARTICIPANT_ASKED;
import static sealab.burt.server.StateVariable.PARTICIPANT_VALIDATED;

public class ProvideParticipantIdAction extends ChatBotAction {

    public ProvideParticipantIdAction(Intent nextIntent) {
        super(nextIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        Boolean validParticipant = (Boolean) state.get(PARTICIPANT_VALIDATED);

        state.put(PARTICIPANT_ASKED, true);

        if (validParticipant == null)
            return createChatBotMessages("To start, " +
                    "please provide the Participant ID that was assigned to you (e.g., P3).");
        else
            return createChatBotMessages("Sorry, I couldn't identify the Participant ID, " +
                    "please provide it one more time (e.g., P3).");
    }
}
