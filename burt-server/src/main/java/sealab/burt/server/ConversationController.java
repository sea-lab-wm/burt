package sealab.burt.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.actions.others.GenerateBugReportAction;
import sealab.burt.server.conversation.entity.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.msgparsing.MessageParser;
import sealab.burt.server.output.BugReportElement;
import sealab.burt.server.output.HTMLBugReportGenerator;
import sealab.burt.server.statecheckers.DefaultActionStateChecker;
import sealab.burt.server.statecheckers.StateChecker;
import sealab.burt.server.statecheckers.eb.EBDescriptionStateChecker;
import sealab.burt.server.statecheckers.ob.OBDescriptionStateChecker;
import sealab.burt.server.statecheckers.participant.ParticipantNameStateChecker;
import sealab.burt.server.statecheckers.s2r.*;
import sealab.burt.server.statecheckers.yesno.AffirmativeAnswerStateChecker;
import sealab.burt.server.statecheckers.yesno.NegativeAnswerStateChecker;
import seers.textanalyzer.TextProcessor;

import java.io.*;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.CONFIRM_END_CONVERSATION;
import static sealab.burt.server.msgparsing.Intent.EB_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.OB_DESCRIPTION;
import static sealab.burt.server.msgparsing.Intent.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@RestController
public
@Slf4j
class ConversationController {

    public final ConcurrentHashMap<Intent, StateChecker> stateCheckers = new ConcurrentHashMap<>() {{
//        put(GREETING, new DefaultActionStateChecker(PROVIDE_PARTICIPANT_ID));
        put(PARTICIPANT_PROVIDED, new ParticipantNameStateChecker());

        //--------------------
        put(APP_SELECTED, new DefaultActionStateChecker(CONFIRM_APP));
        put(AFFIRMATIVE_ANSWER, new AffirmativeAnswerStateChecker());
        put(NEGATIVE_ANSWER, new NegativeAnswerStateChecker());
        //--------OB---------------//
        put(OB_DESCRIPTION, new OBDescriptionStateChecker());
        put(Intent.OB_SCREEN_SELECTED, new DefaultActionStateChecker(CONFIRM_SELECTED_OB_SCREEN));
        //--------EB-------------//
        put(EB_DESCRIPTION, new EBDescriptionStateChecker());
        //--------S2R-----------//
        put(S2R_DESCRIPTION, new S2RDescriptionStateChecker());

//        put(S2R_PREDICTED_SELECTED, new NStateChecker(CONFIRM_PREDICTED_SELECTED_S2R_SCREENS));
        put(S2R_PREDICTED_SELECTED, new S2RPredictionStateChecker());
        put(NEW_PREDICTION_OR_TYPE_S2R, new NewPredictionOrTypeS2RStateChecker());

//        put(S2R_MISSING_SELECTED, new DefaultActionStateChecker(CONFIRM_SELECTED_MISSING_S2R));
        put(S2R_INPUT, new S2RInputStateChecker());

        put(S2R_AMBIGUOUS_SELECTED, new S2RDescriptionStateChecker());
//        put(S2R_AMBIGUOUS_SELECTED, new NStateChecker(CONFIRM_SELECTED_AMBIGUOUS_S2R));
        //--------Ending---------------//
        put(CONFIRM_END_CONVERSATION, new DefaultActionStateChecker(CONFIRM_END_CONVERSATION_ACTION));
        put(DELETE_LAST_STEP, new DeleteLastStepStateChecker());
    }};

    ConcurrentHashMap<String, ConversationState> conversationStates = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        //this call is required to load the stanford corenlp library since the start of the server
        //to avoid the long delay
        TextProcessor.processTextFullPipeline("start", false);
        SpringApplication.run(ConversationController.class, args);
    }


    @PostMapping(value = "/processMessage", consumes = "multipart/form-data")
    public ConversationResponse processMessage(@RequestPart UserResponse userResponse, @RequestPart(value="image", required=false) final MultipartFile image) {

        ConversationResponse response = getConversationResponse(userResponse, image);
        log.debug("ChatBot response: " + response.toString());
        return response;
    }

    private ConversationResponse getConversationResponse(UserResponse userResponse, MultipartFile image) {
        try {

            if (userResponse != null)
                log.debug("User response: " + userResponse);
            else {
                log.debug("User response is null");
                throw new RuntimeException("The message cannot be null");
            }

            String sessionId = userResponse.getSessionId();
            if (sessionId == null) {
                return ConversationResponse.createResponse("Thank you for using BURT. " +
                                "Please reload the page and confirm the action to start a new conversation",
                        ResponseCode.END_CONVERSATION);
            }

            ConversationState conversationState = conversationStates.get(sessionId);
            ConversationResponse defaultResponse = getDefaultResponse();

            if (conversationState == null) {
                log.error("The session does not exist: " + sessionId);
                return defaultResponse;
            }

            conversationState.put(CURRENT_MESSAGE, userResponse);

            //-----------------------------

            conversationState.addUserMessagesToHistory(userResponse.getMessages());

            //-----------------------------

            Intent intent = MessageParser.getIntent(userResponse, conversationState);
            if (intent == null) {
                ConversationResponse response = ConversationResponse.createResponse("Sorry, I did not get that. " +
                        "Please try one more time.");
                conversationState.addChatBotResponseToHistory(response);
                return response;
            }

            log.debug("Identified intent: " + intent);

            if (Intent.END_CONVERSATION.equals(intent)) {
                conversationState.addChatBotResponseToHistory(defaultResponse);
                endConversation(sessionId);
                return defaultResponse;
            }

            StateChecker stateChecker = stateCheckers.get(intent);
            if (stateChecker == null) {
                ConversationResponse response = ConversationResponse.createResponse("Sorry, I am not sure how to " +
                        "respond in this case");
                conversationState.addChatBotResponseToHistory(response);
                return response;
            }

            log.debug("Identified state checker: " + stateChecker);

            ActionName action = stateChecker.nextAction(conversationState);
            if (action == null)
                throw new RuntimeException("The state checker returned a null action. It cannot be null!");

            if (END_CONVERSATION_ACTION.equals(action)) {
                conversationState.addChatBotResponseToHistory(defaultResponse);
                endConversation(sessionId);
                return defaultResponse;
            }

            log.debug("Identified action name: " + action);
            ChatBotAction nextAction = conversationState.getAction(action);

            if (nextAction == null) {
                ConversationResponse response = ConversationResponse.createResponse("Sorry, I am not sure what to do " +
                        "in this case");
                conversationState.addChatBotResponseToHistory(response);
                return response;
            }

            log.debug("Identified action: " + nextAction.getClass().getSimpleName());

            // Need to provide the image to our processor class so it can be saved 
            // and updated in the Graph appropriately
            if (nextAction.getClass().getSimpleName().equals("ConfirmOBScreenSelectedAction")) {
                nextAction.setImage(image);
            }
            
            List<ChatBotMessage> nextMessages = nextAction.execute(conversationState);
            List<Intent> nextIntents = nextAction.nextExpectedIntents();
            conversationState.put(NEXT_INTENTS, nextIntents);

            conversationState.put(LAST_ACTION, action);
            conversationState.put(LAST_MESSAGE, userResponse);

            log.debug("Expected next intent: " + nextIntents);

//            log.debug("State: ");
//            log.debug(conversationState.toString());

            ConversationResponse conversationResponse = new ConversationResponse(nextMessages, nextIntents, action,
                    ResponseCode.SUCCESS);

            conversationState.addChatBotResponseToHistory(conversationResponse);

            return conversationResponse;
        } catch (Exception e) {
            log.error(MessageFormat.format("There was an error processing the message: {0}", e.getMessage()), e);
            return ConversationResponse.createResponse("I am sorry, there was an unexpected error. " +
                    "Please try again or contact the administrator.", ResponseCode.SUCCESS);
        }
    }

    private ConversationResponse getDefaultResponse() {
        return ConversationResponse.createResponse("You got it. " +
                "The conversation will automatically end in a few seconds.", ResponseCode.END_CONVERSATION);
    }

    @PostMapping("/updateStep")
    public boolean updateStep(@RequestBody UserResponse req) {
        String msg = "Updating step in the server...";
        log.debug(msg);

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return false;
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return false;
        }

        try {
            MessageObj firstMessage = req.getFirstMessage();
            String newStepDescription = firstMessage.getMessage();

            int stepIndex = Integer.parseInt(firstMessage.getSelectedValues().get(0));

            List<BugReportElement> allSteps = (List<BugReportElement>) state.get(REPORT_S2R);

            allSteps.get(stepIndex).setStringElement(newStepDescription);

            return true;
        } catch (Exception e) {
            log.error("Error updating the step: " + req, e);
            return false;
        }
    }

    @PostMapping(value = "/updateImage", consumes = "multipart/form-data")
    public boolean updateImage(@RequestPart UserResponse req, @RequestPart final MultipartFile image) {
        String msg = "Updating image in the server...";
        log.debug(msg);

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return false;
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return false;
        }

        try {
            MessageObj firstMessage = req.getFirstMessage();

            int stepIndex = Integer.parseInt(firstMessage.getSelectedValues().get(0));

            List<BugReportElement> allSteps = (List<BugReportElement>) state.get(REPORT_S2R);

            if (image != null) {
                log.debug("Downloading image to server");

                // Gets appropriate paths and creates a file location for the image to be saved
                Path dataPath = Paths.get("../data").toAbsolutePath();
                Path imagePath = Paths.get("../data/user_screenshots", UUID.randomUUID().toString() + ".png").toAbsolutePath();
                
                // Creates new file in location where the image is going to be saved
                File outputFile = new File(imagePath.toString());

                // Copys the file to it's new location                
                image.transferTo(outputFile);

                // Updates the screenshot path for the step
                allSteps.get(stepIndex).setScreenshotPath("\\"+dataPath.relativize(imagePath).toString());
                log.debug("Finished download and updated path");
            }

            return true;
        } catch (Exception e) {
            log.error("Error updating the step: " + req, e);
            return false;
        }
    }

    @PostMapping("/saveMessages")
    public void saveMessages(@RequestBody UserResponse req) {

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return;
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return;
        }

        List<MessageObj> messages = req.getMessages();
        int numMsgs = messages.size();
        String msg = "Saving the messages in the server: " + numMsgs;
        log.debug(msg);
        state.setFrontEndMessageHistory(messages);
    }

    @PostMapping("/testResponse")
    public ConversationResponse testResponse(@RequestBody UserResponse req) {
        MessageObj responseMessageObj = req.getMessages().get(0);
        return ConversationResponse.createResponse(responseMessageObj.getMessage());

    }

    @PostMapping("/loadMessages")
    public List<MessageObj> loadMessages(@RequestBody UserResponse req) {
        String msg = "Returning the messages in the server...";
        log.debug(msg);

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return null; //it should return null
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return null; //it should return null
        }

        return state.getFrontEndMessageHistory();
    }

    @PostMapping("/reportPreview")
    public ConversationResponse previewBugReport(@RequestBody UserResponse req) throws Exception {
        log.debug("Returning the bug report preview in the server...");

        String sessionId = req.getSessionId();

        if (sessionId == null) {
            return ConversationResponse.createResponse("The session is inactive. " +
                            "Please (re)start the conversation.",
                    ResponseCode.UNEXPECTED_ERROR);
        }

        ConversationState conversationState = conversationStates.get(sessionId);

        // check if state has APP
        if (conversationState == null || (conversationState.containsKey(StateVariable.APP_ASKED)) ||
                !conversationState.containsKey(StateVariable.APP_NAME) || !conversationState.containsKey(StateVariable.APP_VERSION) ||
                !conversationState.containsKey(StateVariable.PARTICIPANT_ID)) {
            return ConversationResponse.createResponse(
                    "There is no enough information to generate the report at this moment.",
                    ResponseCode.NO_INFO_FOR_REPORT);
        }

        File reportFile = GenerateBugReportAction.generateBugReport(conversationState);

        MessageObj messageObj = new MessageObj();
        return new ConversationResponse(Collections.singletonList(
                new ChatBotMessage(messageObj, reportFile.getName())), ResponseCode.SUCCESS);
    }

    @PostMapping("/stepsHistory")
    public ConversationResponse getStepsHistory(@RequestBody UserResponse req) throws Exception {
        log.debug("Returning the steps history in the server...");

        String sessionId = req.getSessionId();

        if (sessionId == null) {
            return ConversationResponse.createResponse("The session is inactive. " +
                            "Please (re)start the conversation.",
                    ResponseCode.UNEXPECTED_ERROR);
        }
        ConversationState conversationState = conversationStates.get(sessionId);
        if (conversationState == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return ConversationResponse.createResponse("");
        }

        List<BugReportElement> allSteps = (List<BugReportElement>) conversationState.get(REPORT_S2R);

        if (allSteps == null) {
            return ConversationResponse.createResponse("");
        }

        List<KeyValues> stepOptions = new ArrayList<>();
        for (int i = 0; i < allSteps.size(); i++) {
            BugReportElement element = allSteps.get(i);
            stepOptions.add(new KeyValues(String.valueOf(i),
                    element.getStringElement(),
                    HTMLBugReportGenerator.getLinkScreenshotPath(element.getScreenshotPath())
            ));
        }

        MessageObj messageObj = new MessageObj();
        return new ConversationResponse(Collections.singletonList(
                new ChatBotMessage(messageObj, stepOptions)), ResponseCode.SUCCESS);

    }

    @PostMapping("/storeTip")
    public void storeTip(@RequestBody UserResponse req) {
        log.debug("Storing the tips in the server...");

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return;
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return;
        }
        if (!state.containsKey(StateVariable.TIPS)) {
            List<String> tips = new ArrayList<>();
            tips.add(req.getTip());
            state.put(StateVariable.TIPS, tips);
        } else {
            List<String> tips = (List<String>) state.get(StateVariable.TIPS);
            tips.add(req.getTip());
        }

    }

    @PostMapping("/getTips")
    public ConversationResponse getTips(@RequestBody UserResponse req) {
        log.debug("Getting the tips from the server...");

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            return ConversationResponse.createResponse("The session is inactive. " +
                            "Please (re)start the conversation.",
                    ResponseCode.UNEXPECTED_ERROR);
        }
        ConversationState conversationState = conversationStates.get(sessionId);
        if (conversationState == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return ConversationResponse.createResponse("");
        }

        List<String> allTips = (List<String>) conversationState.get(StateVariable.TIPS);
        if (allTips == null) {
            return ConversationResponse.createResponse("");
        }
        List<KeyValues> tipsOptions = new ArrayList<>();

        for (int i = 0; i < allTips.size(); i++) {
            String tip = allTips.get(i);
            tipsOptions.add(new KeyValues(String.valueOf(i),
                    String.valueOf(i),
                    tip)
            );
        }
        MessageObj messageObj = new MessageObj();

        return new ConversationResponse(Collections.singletonList(
                new ChatBotMessage(messageObj, tipsOptions)), ResponseCode.SUCCESS);
    }

    @PostMapping("/")
    public String index() {
        String msg = "BURT is running...";
        log.debug(msg);
        return msg;
    }

    @PostMapping("/echo")
    public ConversationResponse echo() {
        log.debug("Echoing");
        return ConversationResponse.createResponse("BURT is running");
    }

    @PostMapping("/start")
    public String startConversation() {
        String sessionId = UUID.randomUUID().toString();
        log.debug("Starting a new conversation: " + sessionId);

        ConversationState state = new ConversationState();
        state.put(StateVariable.SESSION_ID, sessionId);
        conversationStates.putIfAbsent(sessionId, state);

        long startTime = System.currentTimeMillis();
        state.put(StateVariable.REPORTING_START_TIME, startTime);
        state.put(PARTICIPANT_ASKED, true);

        return sessionId;
    }

    @PostMapping(value = "/addApp", consumes = "multipart/form-data")
    public boolean updateImage(@RequestPart UserResponse req,
                               @RequestPart final MultipartFile image,
                               @RequestPart final MultipartFile crashScopeZip,
                               final MultipartFile traceReplayerZip ) {
        String msg = "Updating Data files in the server...";
        log.debug(msg);

        String sessionId = req.getSessionId();
        if (sessionId == null) {
            log.debug("No session ID provided");
            return false;
        }

        try {

            if (image != null) {
                log.debug("Downloading App Logos Data to server");
                // Gets appropriate paths and creates a file location for the App Logos Data to be saved
                Path zipPath = Paths.get("../data/app_logos").toAbsolutePath();
                // Creates new file in location where the app logos Data is going to be saved
                File outputFile = new File(zipPath + "/" + image.getOriginalFilename());
                // Copys the file to it's new location
                image.transferTo(outputFile);
            }

            if (traceReplayerZip != null) {
                log.debug("Downloading TraceReplayer Data to server");
                Path zipPath = Paths.get("../data/TraceReplayer-Data").toAbsolutePath();
                File outputFile = new File(zipPath + "/" + traceReplayerZip.getOriginalFilename());
                traceReplayerZip.transferTo(outputFile);
                String fileName = FilenameUtils.removeExtension(outputFile.getName());
                unzipFile(Path.of(outputFile.getPath()), fileName.split("-")[0], fileName.split("-")[1]);
                outputFile.delete();
            }


            if (crashScopeZip != null) {
                log.debug("Downloading CrashScope Data to server");
                Path zipPath = Paths.get("../data/CrashScope-Data").toAbsolutePath();
                File outputFile = new File(zipPath + "/" + crashScopeZip.getOriginalFilename());
                crashScopeZip.transferTo(outputFile);
                String fileName = FilenameUtils.removeExtension(outputFile.getName());
                unzipFile(Path.of(outputFile.getPath()), fileName.split("-")[0], fileName.split("-")[1]);
                outputFile.delete();
            }

            // Load Apps
            new SelectAppAction(APP_SELECTED);
            return true;
        } catch (Exception e) {
            log.error("Error updating the step: " + req, e);
            return false;
        }
    }

    public static void unzipFile(Path filePathToUnzip, String appName, String appVersion) {
        Path parentDir = filePathToUnzip.getParent();
        String fileName = appName + "-" +appVersion;
        Path targetDir = parentDir.resolve(fileName);

        //Open the file
        try (ZipFile zip = new ZipFile(filePathToUnzip.toFile())) {

            FileSystem fileSystem = FileSystems.getDefault();
            Enumeration<? extends ZipEntry> entries = zip.entries();

            //We will unzip files in this folder
            if (!targetDir.toFile().isDirectory()
                    && !targetDir.toFile().mkdirs()) {
                throw new IOException("failed to create directory " + targetDir);
            }

            //Iterate over entries
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                File f = new File(targetDir.resolve(Path.of(entry.getName())).toString());

                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                }

                //Else create the file
                else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }

                    try(InputStream in = zip.getInputStream(entry)) {
                        if (!f.exists()) {
                            Files.copy(in, f.toPath());
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    @PostMapping("/end")
    public int endConversation(@RequestBody UserResponse req) {
        String sessionId = req.getSessionId();
        return endConversation(sessionId);
    }

    private int endConversation(String sessionId) {
        if (sessionId == null) {
            log.debug("No session ID provided");
            return ResponseCode.SUCCESS.getValue();
        }

        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            log.debug("No conversation state associated to: " + sessionId);
            return ResponseCode.SUCCESS.getValue();
        }
        state.saveConversationMessages();
        try {
            GenerateBugReportAction.generateBugReport(state);
        } catch (Exception e) {
            log.error("Could not generate the bug report", e);
        }
        Object obj = conversationStates.remove(sessionId);
        return obj != null ? ResponseCode.SUCCESS.getValue() : ResponseCode.UNEXPECTED_ERROR.getValue();
    }

}
