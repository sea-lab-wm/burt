package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpecifyInputS2RAction extends ChatBotAction {

    public SpecifyInputS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("It seems that no specific input or value was provided.",
                "Can you please provide the input to make the step more accurate?");
    }

}
