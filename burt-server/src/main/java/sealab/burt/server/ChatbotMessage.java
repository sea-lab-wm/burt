package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public @Data @AllArgsConstructor
class ChatbotMessage {
    private MessageObj messageObj;
    private List<KeyValue> values;
    private String intent;
    private String action;

    public ChatbotMessage(){}
    public ChatbotMessage(MessageObj messageObj,List<KeyValue> values ){
        this.messageObj= messageObj;
        this.values= values;
    }



    public ChatbotMessage(String message){
        this.messageObj = new MessageObj((message));
    }

    public ChatbotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }


}
