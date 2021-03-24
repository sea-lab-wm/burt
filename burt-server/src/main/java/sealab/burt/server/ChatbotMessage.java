package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public @Data @AllArgsConstructor
class ChatbotMessage {
    private MessageObj messageObj;
    private List<KeyValue> values;

    ChatbotMessage(){
    }

    public ChatbotMessage(String message){
        this.messageObj = new MessageObj((message));
    }

    public ChatbotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }


}
