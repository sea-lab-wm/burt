package sealab.burt.server.actions;

import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.APP_VERSION;
import static sealab.burt.server.StateVariable.EB_CHECKER;
import static sealab.burt.server.msgparsing.Intent.NO_EXPECTED_INTENT;

import org.springframework.web.multipart.MultipartFile;

public abstract class ChatBotAction {

    public final static String DONE = "done";
    public final static String NONE = "none of the above";
    public final static String UPLOAD = "upload image";

    public List<Intent> nextExpectedIntents;
    public MultipartFile image;

    public ChatBotAction() {
        nextExpectedIntents = new ArrayList<>();
        addNextExpectedIntent(NO_EXPECTED_INTENT);

    }

    public ChatBotAction(Intent... nextExpectedIntents) {
        this.nextExpectedIntents = Arrays.asList(nextExpectedIntents);
    }

    public abstract List<ChatBotMessage> execute(ConversationState state) throws Exception;

    public final List<Intent> nextExpectedIntents() {
        return nextExpectedIntents;
    }

    private void addNextExpectedIntent(Intent nextExpectedIntent) {
        this.nextExpectedIntents.add(nextExpectedIntent);
    }

    public final void setNextExpectedIntents(List<Intent> nextExpectedIntents) {
        this.nextExpectedIntents = nextExpectedIntents;
    }

    public void setImage(MultipartFile imageFile) {
        this.image = imageFile;
    }

    protected List<ChatBotMessage> createChatBotMessages(String... messages) {
        return Arrays.stream(messages).map(ChatBotMessage::new).collect(Collectors.toList());
    }

    protected List<ChatBotMessage> createChatBotMessages(Object... messages) {
        return Arrays.stream(messages).flatMap(msg -> {
            if (msg instanceof String)
                return Stream.of(new ChatBotMessage((String) msg));
            else if (msg instanceof ChatBotMessage)
                return Stream.of((ChatBotMessage) msg);
            else if (msg instanceof String[])
                return Arrays.stream((String[]) msg).map(ChatBotMessage::new);
            else if (msg instanceof ChatBotMessage[])
                return Arrays.stream((ChatBotMessage[]) msg);
            else if (msg == null)
                return null;
            throw new RuntimeException("Type not supported: " + msg.getClass().getSimpleName());
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected void startEBChecker(ConversationState state) {
        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        if (!state.containsKey(EB_CHECKER)) state.put(EB_CHECKER, new EBChecker(appName, appVersion));
    }
}
