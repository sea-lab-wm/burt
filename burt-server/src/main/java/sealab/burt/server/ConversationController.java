package sealab.burt.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sealab.burt.server.actions.*;
import sealab.burt.server.actions.appselect.ConfirmAppAction;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.actions.eb.ClarifyEBAction;
import sealab.burt.server.actions.eb.ProvideEBAction;
import sealab.burt.server.actions.eb.ProvideEBNoParseAction;
import sealab.burt.server.actions.ob.*;
import sealab.burt.server.actions.s2r.*;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.ConversationResponse;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.msgparsing.MessageParser;
import sealab.burt.server.output.outputMessageObj;
import sealab.burt.server.statecheckers.*;
import seers.textanalyzer.TextProcessor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.CURRENT_MESSAGE;
import static sealab.burt.server.StateVariable.NEXT_INTENT;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.*;

@SpringBootApplication
@RestController
public class ConversationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationController.class);
    ConcurrentHashMap<String, List<MessageObj>> messages = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ConcurrentHashMap<StateVariable, Object>> conversationStates = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ActionName, ChatbotAction> actions = new ConcurrentHashMap<>(){
        {
            put(SELECT_APP, new SelectAppAction(APP_SELECTED));
            put(CONFIRM_APP, new ConfirmAppAction());

            //--------OB---------------//
            put(PROVIDE_OB, new ProvideOBAction(OB_DESCRIPTION));
            put(PROVIDE_OB_NO_PARSE, new ProvideOBNoParseAction(OB_DESCRIPTION));
            put(REPHRASE_OB, new RephraseOBAction(OB_DESCRIPTION));
            put(SELECT_OB_SCREEN, new SelectOBScreenAction(Intent.OB_SCREEN_SELECTED));
            put(CONFIRM_SELECTED_OB_SCREEN, new ConfirmOBScreenSelectedAction());

            //--------EB-------------//
            put(PROVIDE_EB, new ProvideEBAction(EB_DESCRIPTION));
            put(PROVIDE_EB_NO_PARSE, new ProvideEBNoParseAction(EB_DESCRIPTION));
            put(CLARIFY_EB, new ClarifyEBAction());

            //--------S2R-----------//
            put(PROVIDE_S2R_FIRST, new ProvideS2RFirstAction(S2R_DESCRIPTION));
            put(PREDICT_S2R, new ProvidePredictedS2RAction(S2R_PREDICTED_SELECTED));
            put(PROVIDE_S2R, new ProvideS2RAction(S2R_DESCRIPTION));
            put(PROVIDE_S2R_NO_PARSE, new ProvideS2RNoParseAction(S2R_DESCRIPTION));
            put(CONFIRM_PREDICTED_SELECTED_S2R_SCREENS, new ConfirmPredictedS2RScreensSelectedAction(S2R_DESCRIPTION));
            put(ActionName.DISAMBIGUATE_S2R, new DisambiguateS2RAction(S2R_AMBIGUOUS_SELECTED));
            put(REPHRASE_S2R, new RephraseS2RAction(S2R_DESCRIPTION));
            put(SPECIFY_INPUT_S2R, new SpecifyInputS2RAction(S2R_DESCRIPTION));
            put(SELECT_MISSING_S2R, new SelectMissingS2RAction(S2R_MISSING_SELECTED));
            put(CONFIRM_SELECTED_AMBIGUOUS_S2R, new ConfirmSelectedAmbiguousAction(S2R_DESCRIPTION));
            put(CONFIRM_SELECTED_MISSING_S2R, new ConfirmSelectedMissingAction(S2R_DESCRIPTION));
            put(ActionName.CONFIRM_LAST_STEP, new ConfirmLastStepAction());

            //--------OTHERS-----------//

            put(REPORT_SUMMARY, new ProvideReportSummary());
            put(UNEXPECTED_ERROR, new UnexpectedErrorAction());

            put(ENDING, new EndConversation());


        }
    };
    public static final ConcurrentHashMap<Intent, StateChecker> stateCheckers = new ConcurrentHashMap<>() {{
        put(GREETING, new NStateChecker(SELECT_APP));
        put(APP_SELECTED, new NStateChecker(CONFIRM_APP));
        put(AFFIRMATIVE_ANSWER, new AffirmativeAnswerStateChecker(null));
        put(NEGATIVE_ANSWER, new NegativeAnswerStateChecker(null));

        //--------OB---------------//
        put(OB_DESCRIPTION, new OBDescriptionStateChecker(null));
        put(Intent.OB_SCREEN_SELECTED, new NStateChecker(CONFIRM_SELECTED_OB_SCREEN));
        //--------EB-------------//
        put(EB_DESCRIPTION, new EBDescriptionStateChecker(null));
        //--------S2R-----------//
        put(S2R_DESCRIPTION, new S2RDescriptionStateChecker(null));
        put(S2R_PREDICTED_SELECTED, new NStateChecker(CONFIRM_PREDICTED_SELECTED_S2R_SCREENS));
        put(S2R_MISSING_SELECTED, new NStateChecker(CONFIRM_SELECTED_MISSING_S2R));
        put(S2R_AMBIGUOUS_SELECTED, new NStateChecker(CONFIRM_SELECTED_AMBIGUOUS_S2R));
        //--------Ending---------------//
        put(THANKS, new NStateChecker(ENDING));


    }};


    public static void main(String[] args) {
        //this call is required to load the stanford corenlp library since the start of the server:
        TextProcessor.processTextFullPipeline("start", false);
        SpringApplication.run(ConversationController.class, args);
    }


    @PostMapping("/processMessage")
    public ConversationResponse processMessage(@RequestBody UserMessage userResponse) {

        try {
            if (userResponse != null)
                LOGGER.debug("User response: " + userResponse);
            else {
                LOGGER.debug("User response is null");
                throw new RuntimeException("The message cannot be null");
            }

            String sessionId = userResponse.getSessionId();

            ConcurrentHashMap<StateVariable, Object> conversationState = conversationStates.get(sessionId);

            if (conversationState == null) {
                LOGGER.error("The session does not exist: " + sessionId);
                return ConversationResponse.createResponse("Thank you for using BURT", 100);
            }

            conversationState.put(CURRENT_MESSAGE, userResponse);

//        LOGGER.debug(MessageFormat.format("Past conversation state {0}"));
            Intent intent = MessageParser.getIntent(userResponse, conversationState);

            if (intent == null)
                return ConversationResponse.createResponse("Sorry, I did not get that!");

            LOGGER.debug("Identified intent: "+ intent);

            if(END_CONVERSATION.equals(intent)){
                endConversation(sessionId);
                return ConversationResponse.createResponse("Thank you for using BURT", 100);
            }


            StateChecker stateChecker = stateCheckers.get(intent);
            if (stateChecker == null)
                return ConversationResponse.createResponse("Sorry, I am not sure how to respond in this case");

            ActionName action = stateChecker.nextAction(conversationState);

            LOGGER.debug("Identified action name: "+ action);
            ChatbotAction nextAction = actions.get(action);
//        LOGGER.debug(conversationState.get("CONVERSATION_STATE").toString());

            if (nextAction == null)
                return ConversationResponse.createResponse("Sorry, I am not sure what to do in this case");

            LOGGER.debug("Identified action: "+ nextAction.getClass().getSimpleName());

            ChatbotMessage nextMessage = nextAction.execute(conversationState);
            Intent nextIntent = nextAction.nextExpectedIntent();
            conversationState.put(NEXT_INTENT, nextIntent);

            LOGGER.debug("Expected next intent: "+ nextIntent);

            return new ConversationResponse(nextMessage, nextIntent.toString(), action, 0);
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("There was an error processing the message: {0}", e.getMessage()), e);
            return  ConversationResponse.createResponse(e.getMessage(), -1);
        }

    }


    @PostMapping("/saveSingleMessage")
    public void saveSingleMessage(@RequestBody UserMessage req) {
        String msg = "Saving the messages in the server...";
        LOGGER.debug(msg);
        List<MessageObj> sessionMsgs = messages.getOrDefault(req.getSessionId(), new ArrayList<>());
        sessionMsgs.add(req.getMessages().get(0));
        messages.put(req.getSessionId(), sessionMsgs);
    }

    @PostMapping("/saveMessages")
    public void saveMessages(@RequestBody UserMessage req) {
        String msg = "Saving the messages in the server...";
        LOGGER.debug(msg);
        messages.put(req.getSessionId(), req.getMessages());
    }

    @PostMapping("/testResponse")
    public ConversationResponse testResponse(@RequestBody UserMessage req) {
        MessageObj responseMessageObj = req.getMessages().get(0);
        return ConversationResponse.createResponse(responseMessageObj.getMessage());

    }

    @PostMapping("/loadMessages")
    public List<MessageObj> loadMessages(@RequestBody UserMessage req) {
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
        ConcurrentHashMap<StateVariable, Object> state = new ConcurrentHashMap<>();
        state.put(StateVariable.SESSION_ID, sessionId);
        conversationStates.putIfAbsent(sessionId, state);
        return sessionId;
    }

    @PostMapping("/end")
    public String endConversation(@RequestParam(value = "sessionId") String sessionId) {
        Object obj = conversationStates.remove(sessionId);
        messages.remove(sessionId);
        return obj != null ? "true" : "false";
    }

}
