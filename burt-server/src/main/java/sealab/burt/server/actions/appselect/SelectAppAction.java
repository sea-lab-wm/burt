package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
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
        List<KeyValue> allApps = Arrays.asList(new KeyValue("Droid Weight","droidweight.webp"),
                new KeyValue("GnuCash","gnucash.png"),
                new KeyValue("Mileage","milage.webp")
                );
        return new ChatbotMessage(messageObj, allApps);
    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
