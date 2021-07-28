package sealab.burt.server.conversation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.List;

public @Data
@Slf4j
class ConversationResponse {

    private List<ChatBotMessage> messages;
    private Integer code;
    private List<Intent> nextIntents;
    private ActionName currentAction;

    public ConversationResponse() {
    }

    public ConversationResponse(List<ChatBotMessage> messages,ResponseCode code) {
        this.messages = messages;
        this.code = code.getValue();
    }

    public ConversationResponse(List<ChatBotMessage> messages, List<Intent> intents, ActionName action, ResponseCode code) {
        this.messages = messages;
        this.nextIntents = intents;
        this.currentAction = action;
        this.code = code.getValue();
    }

    public static ConversationResponse createResponse(String message, ResponseCode code) {
        return new ConversationResponse(Collections.singletonList(new ChatBotMessage(new MessageObj(message))), code);
    }

    public static ConversationResponse createResponse(String message) {
        return createResponse(message, ResponseCode.SUCCESS);
    }
}
