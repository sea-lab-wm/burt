package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideEBNoParseAction extends ChatBotAction {

    public ProvideEBNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state){
        return createChatBotMessages("I am sorry, I didn't quite get that.",
                "Can you please tell me how the app is supposed to work one more time.");
    }
}
