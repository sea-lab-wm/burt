package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data @AllArgsConstructor
class ChatbotMessage {
    MessageObj messageObj;
    List<String> paths;
    List<String> values;

    //path to screenshots...

    public ChatbotMessage(String message){
        this.messageObj = new MessageObj((message));
    }

    public ChatbotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }


}
