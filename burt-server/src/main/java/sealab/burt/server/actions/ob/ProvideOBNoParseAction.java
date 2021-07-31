package sealab.burt.server.actions.ob;


import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideOBNoParseAction extends ChatBotAction {

    public ProvideOBNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("I am sorry, I couldn't recognize the language of your description.",
                " Can you tell me the incorrect behavior one more time?"
        );
    }

}
