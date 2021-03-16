package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SelectAppAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("APP_ASKED", true);
        MessageObj messageObj = new MessageObj("Sure. To start, please select the app that is having the problem", "OneScreenOption");
        List<String> paths = Arrays.asList("path/to/app1.png", "path/to/app2.png",                 "path/to/app3.png");
        List<String> values = Arrays.asList("app1", "app2", "app3");
        ChatbotMessage message = new ChatbotMessage(messageObj, paths, values);
        return message;
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
