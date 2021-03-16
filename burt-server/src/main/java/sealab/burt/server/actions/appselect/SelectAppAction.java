package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class SelectAppAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("CONVERSATION_STATE", "APP_ASKED");
        MessageObj messageObj = new MessageObj("Sure. To start, please select the app that is having the problem", "OneScreenOption");
        ChatbotMessage message = new ChatbotMessage(messageObj, Arrays.asList("path/to/app1.png","path/to/app2.png",
                "path/to/app3.png"), Arrays.asList("app1", "app2", "app3"));
        return message;
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
