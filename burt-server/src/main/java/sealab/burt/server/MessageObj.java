package sealab.burt.server;

import lombok.Data;

public @Data
class MessageObj {

    private String message;
    private String type;
    private double id;
    private String widget;
    private Boolean loading;

    public MessageObj(String message) {
        this.message = message;
    }

    public MessageObj(String message, String widget) {
        this.message = message;
        this.widget = widget;
    }

    public MessageObj(String message, String type, double id) {
        this.message = message;
        this.type = type;
        this.id = id;
    }
}
