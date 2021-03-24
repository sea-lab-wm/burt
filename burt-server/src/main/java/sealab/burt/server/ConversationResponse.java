package sealab.burt.server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public @Data
class ConversationResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationResponse.class);

    private ChatbotMessage message;
    private Integer code;

    ConversationResponse(){
    }


    public ConversationResponse(ChatbotMessage message, Integer code) {
        this.message = message;
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
