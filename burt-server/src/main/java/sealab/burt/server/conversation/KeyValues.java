package sealab.burt.server.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor
class KeyValues {

    private String key;
    private String value1;
    private String value2;

    public KeyValues(){}

}
