package sealab.burt.server.actions.eb;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideEBAction extends ChatBotAction {

    public ProvideEBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        startEBChecker(state);
        return createChatBotMessages("Okay, can you please tell me how the app is <b>supposed to work</b>?");
    }

}
