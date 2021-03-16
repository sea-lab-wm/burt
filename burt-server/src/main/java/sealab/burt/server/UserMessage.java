package sealab.burt.server;

import lombok.Data;

import java.util.List;

public @Data class UserMessage {

    private String sessionId;
    private List<MessageObj> messages;
    private List<String> selectedValues;
}
