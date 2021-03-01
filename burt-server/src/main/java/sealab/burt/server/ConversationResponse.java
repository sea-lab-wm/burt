package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data
class ConversationResponse {

    private String message;
    private Integer code;


    public ConversationResponse(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
