package sealab.burt.server.actions.s2r.otherquality;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideS2RNoParseAction extends ChatBotAction {

    public ProvideS2RNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("I am sorry, I am not able " +
                "to understand this step",
                " Can you please <b>rephrase and provide the step</b> one more time?");
    }

}
