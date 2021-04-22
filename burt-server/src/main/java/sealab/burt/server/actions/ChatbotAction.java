package sealab.burt.server.actions;

import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.msgparsing.Intent.NO_EXPECTED_INTENT;

public abstract class ChatbotAction {

    public Intent nextExpectedIntent;

    public ChatbotAction() {
        setNextExpectedIntent(NO_EXPECTED_INTENT);
    }

    public ChatbotAction(Intent nextExpectedIntent) {
        this.nextExpectedIntent = nextExpectedIntent;
    }

    public abstract ChatbotMessage execute(ConcurrentHashMap<String, Object> state);

    public final Intent nextExpectedIntent() {
        return nextExpectedIntent;
    }

    public final void setNextExpectedIntent(Intent nextExpectedIntent) {
        this.nextExpectedIntent = nextExpectedIntent;
    }
}
