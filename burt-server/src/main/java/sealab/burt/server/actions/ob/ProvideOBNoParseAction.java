package sealab.burt.server.actions.ob;


import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideOBNoParseAction extends ChatBotAction {

    public ProvideOBNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        return createChatBotMessages("I am sorry, I didn't quite get that.",
                " Can you tell me the incorrect behavior one more time?"
        );
    }

}
