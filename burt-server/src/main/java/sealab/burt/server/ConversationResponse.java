package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data
class ConversationResponse {

    private ChatbotMessage message;
    private Integer code;


    public ConversationResponse(ChatbotMessage message, Integer code) {
        this.message = message;
        this.code = code;
    }

    static ConversationResponse createResponse(String message){
        return new ConversationResponse(new ChatbotMessage(new MessageObj(message)), 0);
    }
}
