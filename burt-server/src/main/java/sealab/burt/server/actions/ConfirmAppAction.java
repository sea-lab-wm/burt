package sealab.burt.server.actions;

import sealab.burt.server.MessageObj;

public class ConfirmAppAction extends ChatbotAction {
    @Override
    public MessageObj execute() {
        return new MessageObj("You selected Chikki, shall we continue?");
    }

}
