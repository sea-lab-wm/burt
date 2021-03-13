package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

public class ProvideOBAction extends ChatbotAction {
    @Override
    public MessageObj execute() {
        return new MessageObj("Ok, can you please tell me the incorrect behavior of Chikki that you observed?");
    }

    @Override
    public String nextExpectedIntent() {
        return "OB_DESCRIPTION";
    }
}
