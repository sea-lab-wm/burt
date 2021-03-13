package sealab.burt.server;

import jdk.dynalink.linker.ConversionComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class ConversationController {

    static ConcurrentHashMap<String, String> intentTokens;
    static {
        intentTokens = new ConcurrentHashMap<>();

        addIntentTokens("GREETING", Arrays.asList("t1", "t2", "t3"));
        addIntentTokens("AFFIRMATIVE_ANSWER", Arrays.asList("t1", "t2", "t3"));
        //....
    }

    public static void addIntentTokens(String intent, List<String> tokens){
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public String getIntent(String msg){
        Set<Map.Entry<String, String>> entries = intentTokens.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if(msg.contains(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationController.class);
    ConcurrentHashMap<String, Object> conversations = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, List<MessageObj>> messages = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> conversationStates = new ConcurrentHashMap<>();
//    HashMap<String,
//    HashMap<String, ChatbotAction> actions =
//        {
//            SELECT_APP: new SelectAppAction(),
//            CONFIRM_APP: new ConfirmAppAction()
//        };
//    HashMap<String, StateChecker> intentsActions =
//        {
//            "GREETING": new NoStateChecker("SELECT_APP"),
//            "APP_SELECTED": new NoStateChecker("CONFIRM_APP"),
//            "AFFIRMATIVE_ANSWER": new AffirmativeAnswerStateChecker(null)
//        };
//

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ConversationController.class, args);
    }

//    @PostMapping("/processMessage")
//    void processMessage(@RequestBody RequestMessage req){
//        MessageObj responseMessageObj = req.messages.get(0);
//        String sessionId = req.sessionId;
//
//        intent = MessageParser.getIntent(msg);
//        intentAction = intentsActions.get(intent);
//        result = actions.get(intentAction.nextAction()).execute();
//        return result;
//    }


    @PostMapping("/saveSingleMessage")
    public void saveSingleMessage(@RequestBody RequestMessage req) {
        String msg = "Saving the messages in the server...";
        LOGGER.debug(msg);
        List<MessageObj> sessionMsgs = messages.getOrDefault(req.getSessionId(), new ArrayList<>());
        sessionMsgs.add(req.messages.get(0));
        messages.put(req.getSessionId(), sessionMsgs);
    }

    @PostMapping("/saveMessages")
    public void saveMessages(@RequestBody RequestMessage req) {
        String msg = "Saving the messages in the server...";
        LOGGER.debug(msg);
        messages.put(req.getSessionId(), req.getMessages());
    }

    @PostMapping("/testResponse")
    public ConversationResponse testResponse(@RequestBody RequestMessage req) {
        MessageObj responseMessageObj = req.messages.get(0);
        return ConversationResponse.createResponse(responseMessageObj.message);

    }

    @PostMapping("/loadMessages")
    public List<MessageObj> loadMessages(@RequestBody RequestMessage req) {
        String msg = "Returning the messages in the server...";
        LOGGER.debug(msg);
        return messages.get(req.getSessionId());
    }

    @PostMapping("/")
    public String index() {
        String msg = "BURT is running...";
        LOGGER.debug(msg);
        return msg;
    }

    @PostMapping("/echo")
    public ConversationResponse echo() {
        LOGGER.debug("Echoing");
        return ConversationResponse.createResponse("BURT is running");
    }

    @PostMapping("/start")
    public String startConversation() {
        String sessionId = UUID.randomUUID().toString();
        conversations.putIfAbsent(sessionId, new Conversation(sessionId));
        return sessionId;
    }

    @PostMapping("/end")
    public String endConversation(@RequestParam(value = "id") String conversationId) {
        Object obj = conversations.remove(conversationId);
        return obj != null ? "true" : "false";
    }

}
