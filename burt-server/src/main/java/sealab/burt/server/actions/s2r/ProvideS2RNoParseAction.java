package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class ProvideS2RNoParseAction extends ChatbotAction {

    public ProvideS2RNoParseAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        return new ChatbotMessage("I am sorry, it seems the language of the step is a little odd and I am not able " +
                "to understand it. Can you please rephrase the step and provide it one more time?");

    }

}
