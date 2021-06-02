package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.msgparsing.Intent.NO_EXPECTED_INTENT;

public abstract class ChatBotAction {

    public Intent nextExpectedIntent;

    public ChatBotAction() {
        setNextExpectedIntent(NO_EXPECTED_INTENT);
    }

    public ChatBotAction(Intent nextExpectedIntent) {
        this.nextExpectedIntent = nextExpectedIntent;
    }

    public abstract List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception;

    public final Intent nextExpectedIntent() {
        return nextExpectedIntent;
    }

    public final void setNextExpectedIntent(Intent nextExpectedIntent) {
        this.nextExpectedIntent = nextExpectedIntent;
    }

    protected List<ChatBotMessage> createChatBotMessages(String... messages) {
        return Arrays.stream(messages).map(ChatBotMessage::new).collect(Collectors.toList());
    }

    protected List<ChatBotMessage> createChatBotMessages(Object... messages) {
        return Arrays.stream(messages).map(msg ->{
            if(msg instanceof String)
                return new ChatBotMessage((String) msg);
            else if(msg instanceof ChatBotMessage)
                return (ChatBotMessage) msg;
            throw new RuntimeException("Type not supported: " + msg.getClass().getSimpleName());
        }).collect(Collectors.toList());
    }
}
