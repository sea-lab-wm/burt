package sealab.burt.server.actions.eb;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideEBNoParseAction extends ChatbotAction {

    public ProvideEBNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        return new ChatbotMessage("I am sorry, I didn't quite get that. Can you please tell me how the app is " +
                "supposed to work one more time");
    }
}
