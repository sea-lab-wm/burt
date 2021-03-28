package sealab.burt.server.actions.appselect;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.KeyValue;
import sealab.burt.server.MessageObj;
import sealab.burt.server.actions.ChatbotAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SelectAppAction extends ChatbotAction {

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.put("APP_ASKED", true);
        MessageObj messageObj = new MessageObj("To start, please select the app that is having the problem",
                "AppSelector");
        List<KeyValue> allApps = Arrays.asList(new KeyValue("Droid Weight v. 1.5.4","droidweight.webp"),
                new KeyValue("GnuCash v. 2.1.3","gnucash.png"),
                new KeyValue("Mileage v. 3.1.1","milage.webp")
                );
        return new ChatbotMessage(messageObj, allApps);

    }

    @Override
    public String nextExpectedIntent() {
        return "APP_SELECTED";
    }
}
