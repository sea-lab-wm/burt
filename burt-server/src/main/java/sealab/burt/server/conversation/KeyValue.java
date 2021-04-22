package sealab.burt.server.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class KeyValue {

    private String key;
    private String value;

    public KeyValue(){}
}
