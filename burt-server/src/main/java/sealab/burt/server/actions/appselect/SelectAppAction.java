package sealab.burt.server.actions.appselect;

import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.concurrent.ConcurrentHashMap;

public class SelectAppAction extends ChatbotAction {

    @Override
    public MessageObj execute(ConcurrentHashMap<String, Object> state) {
        state.put("CONVERSATION_STATE", "APP_ASKED");
        return new MessageObj("Sure. To start, please select the app that is having the problem");
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
