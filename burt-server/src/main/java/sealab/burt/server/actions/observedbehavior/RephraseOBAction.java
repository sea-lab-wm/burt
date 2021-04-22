package sealab.burt.server.actions.observedbehavior;

import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class RephraseOBAction extends ChatbotAction {
    public RephraseOBAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("I am sorry, I didn't quite get that. Can you please rephrase the incorrect behavior?");
    }

}
