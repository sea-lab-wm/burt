package sealab.burt.server.actions.appselect;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public class SelectAppAction extends ChatbotAction {

    public SelectAppAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        state.put(APP_ASKED, true);
        MessageObj messageObj = new MessageObj("To start, please select the app that is having the problem",
                "AppSelector");
        List<KeyValue> allApps = Arrays.asList(new KeyValue("Droid Weight v. 1.5.4","droidweight.webp"),
                new KeyValue("GnuCash v. 2.1.3","gnucash.png"),
                new KeyValue("Mileage v. 3.1.1","milage.webp")
                );
        return new ChatbotMessage(messageObj, allApps);

    }

}
