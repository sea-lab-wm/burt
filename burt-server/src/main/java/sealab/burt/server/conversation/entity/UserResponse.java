package sealab.burt.server.conversation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.actions.ActionName;

import java.util.List;

public @Data
@AllArgsConstructor
class UserResponse {

    private String sessionId;
    private List<MessageObj> messages;
    private ActionName currentAction;

    public UserResponse(){}
    public UserResponse(String sessionId){
        this.sessionId = sessionId;
    }

    public UserResponse(String sessionId, List<MessageObj> messages){
        this.sessionId = sessionId;
        this.messages = messages;
    }

    public MessageObj getFirstMessage() {
        if(getMessages() == null || getMessages().isEmpty())
            throw new RuntimeException("There are no messages in this response");
        return getMessages().get(0);
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                ", currentAction=" + currentAction +
                ", messages=" + messages +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
