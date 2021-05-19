package sealab.burt.server.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.actions.ActionName;

import java.util.List;

public @Data
@AllArgsConstructor
class UserMessage {

    private String sessionId;
    private List<MessageObj> messages;
    private ActionName CurrentAction;

    public UserMessage(){}
    public UserMessage(String sessionId, List<MessageObj> messages){
        this.sessionId = sessionId;
        this.messages = messages;
    }

}
