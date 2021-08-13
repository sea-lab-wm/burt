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
        return createChatBotMessages("Oops, your description doesn't seem to match the language " +
                        "of the app",
                "Can you please <b>rephrase the incorrect behavior</b> differently?");
    }

}
