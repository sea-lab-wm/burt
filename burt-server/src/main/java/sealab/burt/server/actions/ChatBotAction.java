package sealab.burt.server.actions;

import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.msgparsing.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.APP_VERSION;
import static sealab.burt.server.StateVariable.EB_CHECKER;
import static sealab.burt.server.msgparsing.Intent.NO_EXPECTED_INTENT;

public abstract class ChatBotAction {

    public List<Intent> nextExpectedIntents;

    public ChatBotAction() {
        nextExpectedIntents = new ArrayList<>();
        addNextExpectedIntent(NO_EXPECTED_INTENT);

    }
//
//    public ChatBotAction(Intent nextExpectedIntent) {
//        nextExpectedIntents = new ArrayList<>();
//        addNextExpectedIntent(nextExpectedIntent);
//    }

    public ChatBotAction(Intent... nextExpectedIntents) {
        this.nextExpectedIntents = Arrays.asList(nextExpectedIntents);
    }

    public abstract List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception;

    public final List<Intent> nextExpectedIntents() {
        return nextExpectedIntents;
    }

    private void addNextExpectedIntent(Intent nextExpectedIntent) {
        this.nextExpectedIntents.add(nextExpectedIntent);
    }

    public final void setNextExpectedIntents(List<Intent> nextExpectedIntents) {
        this.nextExpectedIntents = nextExpectedIntents;
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

    protected void startEBChecker(ConcurrentHashMap<StateVariable, Object> state) {
        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(EB_CHECKER)) state.put(EB_CHECKER, new EBChecker(appName, appVersion));
    }
}
