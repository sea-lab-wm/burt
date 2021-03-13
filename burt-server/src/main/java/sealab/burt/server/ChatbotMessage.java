package sealab.burt.server;

import lombok.Data;

public @Data
class ChatbotMessage {
    MessageObj messageObj;
    //path to screenshots...


    public ChatbotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }
}
