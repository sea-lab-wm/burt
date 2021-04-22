package sealab.burt.server.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public @Data @AllArgsConstructor
class ChatbotMessage {
    private MessageObj messageObj;
    private List<KeyValue> values;
    private String intent;
    private String action;
    private boolean multiple;

    public ChatbotMessage(){}
    public ChatbotMessage(MessageObj messageObj,List<KeyValue> values ){
        this.messageObj= messageObj;
        this.values= values;
    }
    public ChatbotMessage(MessageObj messageObj,List<KeyValue> values, boolean multiple){
        this.messageObj= messageObj;
        this.values= values;
        this.multiple = multiple;
    }



    public ChatbotMessage(String message){
        this.messageObj = new MessageObj((message));
    }

    public ChatbotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }


}
