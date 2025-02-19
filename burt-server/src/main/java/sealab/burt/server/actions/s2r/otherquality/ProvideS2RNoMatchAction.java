package sealab.burt.server.actions.s2r.otherquality;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideS2RNoMatchAction extends ChatBotAction {

    public ProvideS2RNoMatchAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages(" Ok, then I couldn't recognize this step",
                "Please <b>rephrase the step or provide a another one</b>"
        );
    }

}
