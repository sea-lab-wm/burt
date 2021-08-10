package sealab.burt.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sealab.burt.server.conversation.entity.ConversationResponse;
import sealab.burt.server.conversation.entity.ResponseCode;
import sealab.burt.server.conversation.state.ConversationState;

@Component
public class WebSocketComponent {
    @Autowired
    private SimpMessagingTemplate simpMessageSendingOperations;


    public void sendMessage(ConversationState state) {
        String sessionID = (String) state.get(StateVariable.SESSION_ID);

        simpMessageSendingOperations.convertAndSend("/stepsHistory/" + sessionID, "testteststestsete");
    }
}
