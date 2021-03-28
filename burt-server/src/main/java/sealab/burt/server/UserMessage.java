package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

public @Data
@AllArgsConstructor
class UserMessage {

    private String sessionId;
    private List<MessageObj> messages;
    private String CurrentAction;

    public UserMessage(){}
    public UserMessage(String sessionId, List<MessageObj> messages){
        this.sessionId = sessionId;
        this.messages = messages;
    }
}
