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
        MessageObj messageObj = new MessageObj("To start, please select the app that is having the problem",
                "AppSelector");
        List<String> paths = Arrays.asList("droidweight.webp", "gnucash.png", "milage.webp");
        List<String> values = Arrays.asList("Droid Weight", "GnuCash", "Mileage");
        return new ChatbotMessage(messageObj, paths, values);
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
