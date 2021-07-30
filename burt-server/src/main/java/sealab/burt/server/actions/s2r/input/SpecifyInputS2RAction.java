package sealab.burt.server.actions.s2r.input;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

import static sealab.burt.server.StateVariable.S2R_MATCHED_MSG;

public class SpecifyInputS2RAction extends ChatBotAction {

    public SpecifyInputS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

//        UserResponse msg = (UserResponse) state.get(S2R_MATCHED_MSG);
//        String highQualityStepMessage = msg.getMessages().get(0).getMessage();

        return createChatBotMessages("It seems that no specific input or value was provided in the step.",
                "Can you please provide an input (enclosed in quotes, e.g., \"5\")?");
    }

}
