package sealab.burt.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sealab.burt.server.actions.ChatbotAction;
import sealab.burt.server.actions.ConfirmAppAction;
import sealab.burt.server.actions.ProvideOBAction;
import sealab.burt.server.actions.SelectAppAction;
import sealab.burt.server.statecheckers.AffirmativeAnswerStateChecker;
import sealab.burt.server.statecheckers.NoStateChecker;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class ConversationController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationController.class);
    ConcurrentHashMap<String, List<MessageObj>> messages = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> conversationStates = new ConcurrentHashMap<>();
    //    HashMap<String,
    ConcurrentHashMap<String, ChatbotAction> actions = new ConcurrentHashMap<>() {
        {
            put("SELECT_APP", new SelectAppAction());
            put("CONFIRM_APP", new ConfirmAppAction());
            put("PROVIDE_OB", new ProvideOBAction());
//            CONFIRM_APP: new ConfirmAppAction()
        }
    };
    ConcurrentHashMap<String, StateChecker> stateCheckers = new ConcurrentHashMap<>() {{
        put("GREETING", new NoStateChecker("SELECT_APP"));
        put("APP_SELECTED", new NoStateChecker("CONFIRM_APP"));
        put("AFFIRMATIVE_ANSWER", new AffirmativeAnswerStateChecker(null));
//                "GREETING": new NoStateChecker("SELECT_APP"),
//            "APP_SELECTED": new NoStateChecker("CONFIRM_APP"),
//            "AFFIRMATIVE_ANSWER": new AffirmativeAnswerStateChecker(null)
    }};


    public static void main(String[] args) {
        SpringApplication.run(ConversationController.class, args);
    }


    @PostMapping("/processMessage")
    public ConversationResponse processMessage(@RequestBody RequestMessage req) {
        MessageObj message = req.getMessages().get(0);
        String sessionId = req.getSessionId();
        ConcurrentHashMap<String, Object> state = conversationStates.get(sessionId);

        String intent = MessageParser.getIntent(message, state);
        if (intent == null)
            return ConversationResponse.createResponse("Sorry, I did not get that!");


        StateChecker stateChecker = stateCheckers.get(intent);
        if (stateChecker == null)
            return ConversationResponse.createResponse("Sorry, I do not know how to respond in these case");

        ChatbotAction nextAction = actions.get(stateChecker.nextAction(state));

        if (nextAction == null)
            return ConversationResponse.createResponse("Sorry, I do not know what to do  in these case");

        String nextIntent = nextAction.nextExpectedIntent();
        MessageObj nextMessage = nextAction.execute();

        state.put("NEXT_INTENT", nextIntent);

        return new ConversationResponse(new ChatbotMessage(nextMessage), 0);
    }


    @PostMapping("/saveSingleMessage")
    public void saveSingleMessage(@RequestBody RequestMessage req) {
        String msg = "Saving the messages in the server...";
        LOGGER.debug(msg);
        List<MessageObj> sessionMsgs = messages.getOrDefault(req.getSessionId(), new ArrayList<>());
        sessionMsgs.add(req.getMessages().get(0));
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
        MessageObj responseMessageObj = req.getMessages().get(0);
        return ConversationResponse.createResponse(responseMessageObj.getMessage());

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
        conversationStates.putIfAbsent(sessionId, new ConcurrentHashMap<>());
        return sessionId;
    }

    @PostMapping("/end")
    public String endConversation(@RequestParam(value = "id") String conversationId) {
        Object obj = conversationStates.remove(conversationId);
        return obj != null ? "true" : "false";
    }

}
