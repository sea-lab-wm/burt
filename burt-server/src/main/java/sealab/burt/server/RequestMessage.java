package sealab.burt.server;

import lombok.Data;

import java.util.List;

public @Data class RequestMessage {

    String sessionId;
    List<MessageObj> messages;
}
