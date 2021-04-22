package sealab.burt.server.actions.step2reproduce;

import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

public class RephraseS2RAction extends ChatbotAction {

    public RephraseS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        return new ChatbotMessage("I didn’t quite get that. Can you please rephrase the step more accurately?");

    }

}

