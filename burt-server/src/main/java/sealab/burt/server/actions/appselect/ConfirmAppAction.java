package sealab.burt.server.actions.appselect;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ConfirmAppAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        return new MessageObj("You selected Chikki, shall we continue?");
    }

}
