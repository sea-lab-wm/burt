package sealab.burt.server;

import lombok.Data;

public @Data
class MessageObj {
    String message;
    String type;
    double id;
    String widget;
    Boolean loading;

    public MessageObj(String message, String type, double id) {
        this.message = message;
        this.type = type;
        this.id = id;
    }
}
