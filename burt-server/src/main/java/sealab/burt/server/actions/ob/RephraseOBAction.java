package sealab.burt.server.actions.ob;


import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;
public class RephraseOBAction extends ChatbotAction {

    public RephraseOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        return new ChatbotMessage("It seems the description you provided does not use a proper language. " +
                "Can you please rephrase the incorrect behavior?");
    }

}
