package sealab.burt.server.conversation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class KeyValues {

    private String key;
    private String value1;
    private String value2;

    public KeyValues(){}

}
