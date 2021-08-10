package sealab.burt.server.conversation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.output.BugReportElement;

import java.util.List;

public @Data
@AllArgsConstructor
class ChatBotMessage {

    private MessageObj messageObj;
    private List<KeyValues> values;
    private String intent;
    private String action;
    private boolean multiple;
    private String generatedReport;
    public ChatBotMessage() {
    }

    public ChatBotMessage(MessageObj messageObj, List<KeyValues> values) {
        this.messageObj = messageObj;
        this.values = values;
    }

    public ChatBotMessage(MessageObj messageObj, List<KeyValues> values, boolean multiple) {
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


//    public ChatBotMessage(MessageObj messageObj, List<BugReportElement> stepsHistory) {
//        this.messageObj = messageObj;
//        this.stepsHistory = stepsHistory;
//    }
}
