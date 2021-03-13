package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

public class RephraseOBAction extends ChatbotAction {
    @Override
    public MessageObj execute() {
        return new MessageObj("I am sorry, I didn't quite get that. Can you please rephrase the incorrect behavior?");
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_DESCRIPTION";
    }
}
