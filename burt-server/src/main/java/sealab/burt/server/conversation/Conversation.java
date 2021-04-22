package sealab.burt.server.conversation;

import lombok.Data;

public @Data
class Conversation {

    private String id;

    public Conversation(String id) {
        this.id = id;
    }
}
