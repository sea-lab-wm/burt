package sealab.burt.server.actions.ob;


import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class RephraseOBAction extends ChatBotAction {

    public RephraseOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("It seems your description does not use a language that matches the one " +
                        "of the application",
                "Can you please rephrase the incorrect behavior differently or more specifically?");
    }

}
