package sealab.burt.server.actions.observedbehavior;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class ConfirmOBScreenSelectedAction extends ChatbotAction {
    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        return new MessageObj("you selected Screen 2, shall we continue?");
    }

}
