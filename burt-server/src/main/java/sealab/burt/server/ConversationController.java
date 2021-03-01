package sealab.burt.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class ConversationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationController.class);
    ConcurrentHashMap<String, Object> conversations = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(ConversationController.class, args);
    }

    @RequestMapping("/")
    public String index() {
        String msg = "BURT is running...";
        LOGGER.debug(msg);
        return msg;
    }

    @GetMapping("/echo")
    public ConversationResponse echo() {
        LOGGER.debug("Echoing");
        return new ConversationResponse("BURT is running", 0);
    }

    @GetMapping("/status")
    public ConversationResponse isConversationStarted(@RequestParam(value = "id") String conversationId) {
        LOGGER.debug(MessageFormat.format("Checking conversation {0}", conversationId));
        return new ConversationResponse(String.valueOf(conversations.containsKey(conversationId)), 0);
    }

    @GetMapping("/start")
    public ConversationResponse startConversation() {
        String conversationId = UUID.randomUUID().toString();
        conversations.putIfAbsent(conversationId, new Conversation(conversationId));
        return new ConversationResponse(conversationId, 0);
    }

    @GetMapping("/end")
    public ConversationResponse endConversation(@RequestParam(value = "id") String conversationId) {
        Object obj = conversations.remove(conversationId);
        return new ConversationResponse(obj != null ? "true" : "false" , 0);
    }

}
