package sealab.burt.server.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public @Data
@AllArgsConstructor
class ChatBotMessage {

    private MessageObj messageObj;
    private List<KeyValue> values;
    private String intent;
    private String action;
    private boolean multiple;
    private String generatedReport;

    public ChatBotMessage() {
    }

    public ChatBotMessage(MessageObj messageObj, List<KeyValue> values) {
        this.messageObj = messageObj;
        this.values = values;
    }

    public ChatBotMessage(MessageObj messageObj, List<KeyValue> values, boolean multiple) {
        this.messageObj = messageObj;
        this.values = values;
        this.multiple = multiple;

    }

    public ChatBotMessage(MessageObj messageObj, String generatedReport) {
        this.messageObj = messageObj;
        this.generatedReport = generatedReport;
    }


    public ChatBotMessage(String message) {
        this.messageObj = new MessageObj((message));
    }

    public ChatBotMessage(MessageObj messageObj) {
        this.messageObj = messageObj;
    }


}
