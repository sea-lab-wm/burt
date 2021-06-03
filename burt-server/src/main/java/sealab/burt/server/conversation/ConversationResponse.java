package sealab.burt.server.conversation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

public @Data @Slf4j
class ConversationResponse {

    private List<ChatBotMessage> messages;
    private Integer code;
    private List<Intent> nextIntents;
    private ActionName currentAction;


    public ConversationResponse() {
    }

    public ConversationResponse(List<ChatBotMessage> messages, Integer code) {
        this.messages = messages;
        this.code = code;
    }

    public ConversationResponse(List<ChatBotMessage> messages, List<Intent> intents, ActionName action, Integer code) {
        this.messages = messages;
        this.nextIntents = intents;
        this.currentAction = action;
        this.code = code;
    }


    public static ConversationResponse createResponse(String message, Integer code) {
        ConversationResponse conversationResponse =
                new ConversationResponse(Collections.singletonList(new ChatBotMessage(new MessageObj(message))), code);
        log.debug("Response created: " + conversationResponse);
        return conversationResponse;
    }

    public static ConversationResponse createResponse(String message) {
     return createResponse(message, 0);
    }
}
