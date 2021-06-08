package sealab.burt.server.actions.appselect;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValue;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.APP_ASKED;
import static sealab.burt.server.StateVariable.PARTICIPANT_ID;

public class SelectAppAction extends ChatBotAction {

    public SelectAppAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state){
        state.put(APP_ASKED, true);
        String participant = state.get(PARTICIPANT_ID).toString();
        MessageObj messageObj = new MessageObj(participant + ", please select the app that is having the problem",
                "AppSelector");
        List<KeyValue> allApps = Arrays.asList(new KeyValue("Droid Weight v. 1.5.4", "droidweight.webp"),
                new KeyValue("GnuCash v. 2.1.3", "gnucash.png"),
                new KeyValue("Mileage v. 3.1.1", "milage.webp")
        );
        return createChatBotMessages(
                new ChatBotMessage(messageObj, allApps, false)
        );

    }

}
