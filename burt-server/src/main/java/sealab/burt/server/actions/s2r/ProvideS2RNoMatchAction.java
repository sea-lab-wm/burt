package sealab.burt.server.actions.s2r;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideS2RNoMatchAction extends ChatBotAction {

    public ProvideS2RNoMatchAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages(" Okay, it seems I wasn't able to recognize the step you performed.",
                "Can you please rephrase the step or provide a another one?"
        );
    }

}
