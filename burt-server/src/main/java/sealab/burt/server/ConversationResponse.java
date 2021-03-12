package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data
class ConversationResponse {

    private MessageObj message;
    private Integer code;


    public ConversationResponse(MessageObj message, Integer code) {
        this.message = message;
        this.code = code;
    }

    static ConversationResponse createResponse(String message){
        return new ConversationResponse(new MessageObj(message), 0);
    }
}
