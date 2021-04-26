package sealab.burt.server.actions.ob;


import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideOBNoParseAction extends ChatbotAction {

    public ProvideOBNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        return new ChatbotMessage("I am sorry, I didn't quite get that. Can you tell me the " +
                "incorrect behavior one more time?");
    }

}
