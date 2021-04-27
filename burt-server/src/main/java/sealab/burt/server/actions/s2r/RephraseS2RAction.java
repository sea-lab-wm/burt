package sealab.burt.server.actions.s2r;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class RephraseS2RAction extends ChatbotAction {

    public RephraseS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        return new ChatbotMessage("Oops, I didn’t quite get that. Can you please rephrase the step more accurately?");

    }

}

