package sealab.burt.server.actions.ob;


import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;

public class ProvideOBCrashInfo extends ChatBotAction {

    public ProvideOBCrashInfo(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("Please <b>describe what you attempted to do</b> when the app crashed");
    }

}