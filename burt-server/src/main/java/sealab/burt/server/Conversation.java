package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data
class Conversation {

    private String id;

    public Conversation(String id) {
        this.id = id;
    }
}
