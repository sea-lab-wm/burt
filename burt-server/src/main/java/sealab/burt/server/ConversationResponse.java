package sealab.burt.server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.MessageObj;

public @Data
class ConversationResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationResponse.class);

    private ChatbotMessage message;
    private Integer code;
    private String nextIntent;
    private ActionName currentAction;


    public ConversationResponse(){}
    public ConversationResponse(ChatbotMessage message, Integer code) {
        this.message = message;
        this.code = code;
    }
    public ConversationResponse(ChatbotMessage message,String intent, ActionName action, Integer code) {
        this.message = message;
        this.nextIntent= intent;
        this.currentAction = action;
        this.code = code;
    }


    static ConversationResponse createResponse(String message, Integer code){
        ConversationResponse conversationResponse =
                new ConversationResponse(new ChatbotMessage(new MessageObj(message)), code);
        LOGGER.debug("Response created: " + conversationResponse);
        return conversationResponse;
    }

    static ConversationResponse createResponse(String message){
        ConversationResponse conversationResponse =
                new ConversationResponse(new ChatbotMessage(new MessageObj(message)), 0);
        LOGGER.debug("Response created: " + conversationResponse);
        return conversationResponse;
    }
}
