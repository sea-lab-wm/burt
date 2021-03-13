package sealab.burt.server;

import lombok.Data;

import java.util.List;

public @Data class RequestMessage {

    private String sessionId;
    private List<MessageObj> messages;
}
