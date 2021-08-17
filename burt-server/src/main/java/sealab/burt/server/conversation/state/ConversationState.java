package sealab.burt.server.conversation.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.DoNothingAction;
import sealab.burt.server.actions.appselect.ConfirmAppAction;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.actions.eb.ClarifyEBAction;
import sealab.burt.server.actions.eb.ProvideEBAction;
import sealab.burt.server.actions.eb.ProvideEBNoParseAction;
import sealab.burt.server.actions.ob.*;
import sealab.burt.server.actions.others.ConfirmEndConversationAction;
import sealab.burt.server.actions.others.GenerateBugReportAction;
import sealab.burt.server.actions.others.ProvideParticipantIdAction;
import sealab.burt.server.actions.others.UnexpectedErrorAction;
import sealab.burt.server.actions.s2r.ConfirmLastStepAction;
import sealab.burt.server.actions.s2r.ProvideS2RAction;
import sealab.burt.server.actions.s2r.ProvideS2RFirstAction;
import sealab.burt.server.actions.s2r.ambiguous.ConfirmSelectedAmbiguousAction;
import sealab.burt.server.actions.s2r.ambiguous.DisambiguateS2RAction;
import sealab.burt.server.actions.s2r.highquality.ConfirmMatchedS2RAction;
import sealab.burt.server.actions.s2r.input.SpecifyInputS2RAction;
import sealab.burt.server.actions.s2r.input.SpecifyNextInputS2RAction;
import sealab.burt.server.actions.s2r.missing.ConfirmSelectedMissingAction;
import sealab.burt.server.actions.s2r.missing.SelectMissingS2RAction;
import sealab.burt.server.actions.s2r.otherquality.ProvideS2RNoMatchAction;
import sealab.burt.server.actions.s2r.otherquality.ProvideS2RNoParseAction;
import sealab.burt.server.actions.s2r.otherquality.RephraseS2RAction;
import sealab.burt.server.actions.s2r.prediction.AskForNewPredictionAction;
import sealab.burt.server.actions.s2r.prediction.IncorrectPredictedS2RSelectedAction;
import sealab.burt.server.actions.s2r.prediction.ProvideFirstPredictedS2RAction;
import sealab.burt.server.actions.s2r.prediction.ProvideNextPredictedS2RAction;
import sealab.burt.server.conversation.entity.ConversationResponse;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.msgparsing.Intent;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.*;

public @Slf4j
class ConversationState {

    private final ConcurrentHashMap<StateVariable, Object> stateVariables = new ConcurrentHashMap<>();
    private final AttemptManager attemptManager = new AttemptManager();
    private final QualityStateUpdater stateUpdater = new QualityStateUpdater();
    private List<MessageObj> frontEndMessageHistory;
    private List<Object> messageHistory = new ArrayList<>();

    public final ConcurrentHashMap<ActionName, ChatBotAction> actions = new ConcurrentHashMap<>() {
        {
            put(PROVIDE_PARTICIPANT_ID, new ProvideParticipantIdAction(PARTICIPANT_PROVIDED));

            //--------APP SELECTION---------------//

            put(SELECT_APP, new SelectAppAction(APP_SELECTED));
            put(CONFIRM_APP, new ConfirmAppAction());

            //--------OB---------------//

            put(PROVIDE_OB, new ProvideOBAction(OB_DESCRIPTION));

            //quality checking
            put(CONFIRM_MATCHED_OB, new ConfirmMatchedOBAction(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            put(PROVIDE_OB_NO_PARSE, new ProvideOBNoParseAction(OB_DESCRIPTION));
            put(REPHRASE_OB, new RephraseOBAction(OB_DESCRIPTION));
            put(SELECT_OB_SCREEN, new SelectOBScreenAction(Intent.OB_SCREEN_SELECTED));
            put(CONFIRM_SELECTED_OB_SCREEN, new ConfirmOBScreenSelectedAction());

            //--------EB-------------//

            put(PROVIDE_EB, new ProvideEBAction(EB_DESCRIPTION));

            //quality checking
            put(PROVIDE_EB_NO_PARSE, new ProvideEBNoParseAction(EB_DESCRIPTION));
            put(CLARIFY_EB, new ClarifyEBAction(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));

            //--------S2R-----------//

            //regular s2r prompt
//            put(PROVIDE_S2R_FIRST, new ProvideS2RFirstAction(S2R_DESCRIPTION));
            put(PROVIDE_S2R, new ProvideS2RAction(S2R_DESCRIPTION));

            //prediction
            put(PREDICT_FIRST_S2R_PATH, new ProvideFirstPredictedS2RAction(S2R_PREDICTED_SELECTED));
            put(PREDICT_NEXT_S2R_PATH, new ProvideNextPredictedS2RAction(S2R_PREDICTED_SELECTED));
            put(ASK_FOR_NEW_PREDICTION, new AskForNewPredictionAction());
            put(INCORRECT_PREDICTED_S2R, new IncorrectPredictedS2RSelectedAction());
//            put(CONFIRM_PREDICTED_SELECTED_S2R_SCREENS, new ConfirmPredictedS2RAction(S2R_DESCRIPTION));

            //quality checking
            put(CONFIRM_MATCHED_S2R, new ConfirmMatchedS2RAction(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            put(ActionName.PROVIDE_S2R_NO_MATCH, new ProvideS2RNoMatchAction(S2R_DESCRIPTION));
            put(PROVIDE_S2R_NO_PARSE, new ProvideS2RNoParseAction(S2R_DESCRIPTION));
            put(REPHRASE_S2R, new RephraseS2RAction(S2R_DESCRIPTION));
            put(SPECIFY_INPUT_S2R, new SpecifyInputS2RAction(S2R_INPUT));
            put(SPECIFY_NEXT_INPUT_S2R, new SpecifyNextInputS2RAction(S2R_INPUT));

            put(ActionName.DISAMBIGUATE_S2R, new DisambiguateS2RAction(S2R_AMBIGUOUS_SELECTED));
            put(CONFIRM_SELECTED_AMBIGUOUS_S2R, new ConfirmSelectedAmbiguousAction(S2R_DESCRIPTION));

            //quality checking: missing steps
            //put(SELECT_MISSING_S2R, new SelectMissingS2RAction(S2R_MISSING_SELECTED));
//            put(CONFIRM_SELECTED_MISSING_S2R, new ConfirmSelectedMissingAction(S2R_DESCRIPTION));

            //last step
            put(ActionName.CONFIRM_LAST_STEP, new ConfirmLastStepAction());

            //--------OTHERS-----------//

            put(CONFIRM_END_CONVERSATION_ACTION, new ConfirmEndConversationAction(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            put(REPORT_SUMMARY, new GenerateBugReportAction());
            put(UNEXPECTED_ERROR, new UnexpectedErrorAction());
            put(DO_NOTHING, new DoNothingAction());

        }
    };

    //-------------------------------

    public QualityStateUpdater getStateUpdater() {
        return stateUpdater;
    }

    public boolean containsKey(StateVariable var) {
        return stateVariables.containsKey(var);
    }

    public Object get(StateVariable variable) {
        return stateVariables.get(variable);
    }

    public Object put(StateVariable variable, Object value) {
        return stateVariables.put(variable, value);
    }

    public Object remove(StateVariable variable) {
        return stateVariables.remove(variable);
    }

    public Object putIfAbsent(StateVariable variable, Object value) {
        return stateVariables.putIfAbsent(variable, value);
    }

    public ChatBotAction getAction(ActionName action) {
        return actions.get(action);
    }

    @Override
    public String toString() {
        return "ConversationState{" +
                "vars=" + stateVariables +
                '}';
    }

    //-------------------------------------


    public void initOrIncreaseCurrentAttemptS2RGeneral() {
        attemptManager.initOrIncreaseCurrentAttemptS2RGeneral();
    }

    public boolean checkNextAttemptAndResetS2RGeneral() {
        return attemptManager.checkNextAttemptAndResetS2RGeneral();
    }

    public void resetCurrentAttemptS2RGeneral() {
        attemptManager.resetCurrentAttemptS2RGeneral();
    }

    public Integer getCurrentAttemptS2RGeneral() {
        return attemptManager.getCurrentAttemptS2RGeneral();
    }

    public Integer getMaxAttemptsS2RGeneral() {
        return attemptManager.getMaxAttemptsS2RGeneral();
    }
    //---------------------------------


    public Integer getMaxAttemptsObMatched() {
        return attemptManager.getMaxAttemptsObMatched();
    }


    public void initAttemptObMatched() {
        attemptManager.initAttemptObMatched();
    }

    public Integer getCurrentAttemptObMatched() {
        return attemptManager.getCurrentAttemptObMatched();
    }

    public boolean checkNextAttemptAndResetObMatched() {
        return attemptManager.checkNextAttemptAndResetObMatched();
    }

    public void initOrIncreaseCurrentAttemptObMatched() {
        attemptManager.initOrIncreaseCurrentAttemptObMatched();
    }

    public boolean isCurrentAttemptInitiatedObMatched() {
        return attemptManager.isCurrentAttemptInitiatedObMatched();
    }

    //---------------------------------


    public void initOrIncreaseCurrentAttemptObNoMatch() {
        attemptManager.initOrIncreaseCurrentAttemptObNoMatch();
    }

    public boolean checkNextAttemptAndResetObNoMatch() {
        return attemptManager.checkNextAttemptAndResetObNoMatch();
    }

    //---------------------------------

    public Integer getMaxAttemptObScreens() {
        return attemptManager.getMaxAttemptObScreens();
    }

    public void initOrIncreaseCurrentAttemptObScreens() {
        attemptManager.initOrIncreaseCurrentAttemptObScreens();
    }

    public boolean checkNextAttemptAndResetObScreens() {
        return attemptManager.checkNextAttemptAndResetObScreens();
    }

    public Integer getCurrentAttemptObScreens() {
        return attemptManager.getCurrentAttemptObScreens();
    }

    public void increaseCurrentAttemptObScreens() {
        attemptManager.increaseCurrentAttemptObScreens();
    }

    //---------------------------------------

    public void initOrIncreaseCurrentAttemptObNotParsed() {
        attemptManager.initOrIncreaseCurrentAttemptObNotParsed();
    }

    public boolean checkNextAttemptAndResetObNotParsed() {
        return attemptManager.checkNextAttemptAndResetObNotParsed();
    }

    //-----------------------------------------

    public void initOrIncreaseCurrentAttemptEbNoMatch() {
        attemptManager.initOrIncreaseCurrentAttemptEbNoMatch();
    }

    public boolean checkNextAttemptAndResetEbNoMatch() {
        return attemptManager.checkNextAttemptAndResetEbNoMatch();
    }

    //-----------------------------------------

    public void initOrIncreaseCurrentAttemptEbNotParsed() {
        attemptManager.initOrIncreaseCurrentAttemptEbNotParsed();
    }

    public boolean checkNextAttemptAndResetEbNotParsed() {
        return attemptManager.checkNextAttemptAndResetEbNotParsed();
    }

    //------------------------------

 /*   public void initOrIncreaseCurrentAttemptS2RNotParsed() {
        attemptManager.initOrIncreaseCurrentAttemptS2RNotParsed();
    }

    public boolean checkNextAttemptAndResetS2RNotParsed() {
        return attemptManager.checkNextAttemptAndResetS2RNotParsed();
    }

    public void resetCurrentAttemptS2RNotParsed() {
        attemptManager.resetCurrentAttemptS2RNotParsed();
    }*/

    //------------------------------

  /*  public void initOrIncreaseCurrentAttemptS2RNoMatch() {
        attemptManager.initOrIncreaseCurrentAttemptS2RNoMatch();
    }

    public boolean checkNextAttemptAndResetS2RNoMatch() {
        return attemptManager.checkNextAttemptAndResetS2RNoMatch();
    }

    public void resetCurrentAttemptS2RNoMatch() {
        attemptManager.resetCurrentAttemptS2RNoMatch();
    }*/

    //------------------------------

  /*  public void initOrIncreaseCurrentAttemptS2RAmbiguous() {
        attemptManager.initOrIncreaseCurrentAttemptS2RAmbiguous();
    }

    public boolean checkNextAttemptAndResetS2RAmbiguous() {
        return attemptManager.checkNextAttemptAndResetS2RAmbiguous();
    }

    public void resetCurrentAttemptS2RAmbiguous() {
        attemptManager.resetCurrentAttemptS2RAmbiguous();
    }*/

    //------------------------------

    /*public Integer getCurrentAttemptS2RMatched() {
        return attemptManager.getCurrentAttemptS2RMatched();
    }

    public Integer getMaxAttemptsS2RMatched() {
        return attemptManager.getMaxAttemptsS2RMatched();
    }

    public void initOrIncreaseCurrentAttemptS2RMatch() {
        attemptManager.initOrIncreaseCurrentAttemptS2RMatch();
    }

    public boolean checkNextAttemptAndResetS2RMatch() {
        return attemptManager.checkNextAttemptAndResetS2RMatch();
    }

    public void resetCurrentAttemptS2RMatch() {
        attemptManager.resetCurrentAttemptS2RMatch();
    }*/

    //-------------------------------------

  /*  public void initOrIncreaseCurrentAttemptS2RInput() {
        attemptManager.initOrIncreaseCurrentAttemptS2RInput();
    }

    public boolean checkNextAttemptAndResetS2RInput() {
        return attemptManager.checkNextAttemptAndResetS2RInput();
    }

    public void resetCurrentAttemptS2RInput() {
        attemptManager.resetCurrentAttemptS2RInput();
    }*/

    //----------------------------------

    public List<MessageObj> getFrontEndMessageHistory() {
        return frontEndMessageHistory;
    }

    public synchronized void setFrontEndMessageHistory(List<MessageObj> frontEndMessageHistory) {
        if (frontEndMessageHistory == null) return;
        if (this.frontEndMessageHistory == null) {
            this.frontEndMessageHistory = frontEndMessageHistory;
        } else if (frontEndMessageHistory.size() >= this.frontEndMessageHistory.size())
            this.frontEndMessageHistory = frontEndMessageHistory;
    }

    public void saveConversationMessages() {
        String sessionId = this.get(StateVariable.SESSION_ID).toString();
        try {

            boolean anyNotCreated = Stream.of(StateVariable.APP_NAME, StateVariable.APP_VERSION,
                            StateVariable.PARTICIPANT_ID)
                    .anyMatch(v -> this.get(v) == null);
            if (this.messageHistory == null || anyNotCreated)
                return;

            //--------------------------------

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String jsonContent = gson.toJson(this.messageHistory);

            //---------------------------------------------

            String appName = this.get(StateVariable.APP_NAME).toString();
            String appVersion = this.get(StateVariable.APP_VERSION).toString();
            String participant = this.get(StateVariable.PARTICIPANT_ID).toString();

            String reportName = String.join("-", "conversation_state",
                    participant, appName, appVersion, sessionId).replace(
                    " ", "_");
            String reportFileName = reportName + ".json";

            //-------------------------------------

            File outputFile = Paths.get(BurtConfigPaths.conversationDumpsPath, reportFileName).toFile();

            FileUtils.write(outputFile, jsonContent, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error dumping the conversation state for session: " + sessionId, e);
        }
    }

    public void addChatBotResponseToHistory(ConversationResponse conversationResponse) {
        messageHistory.add(conversationResponse);
    }

    public void addUserMessagesToHistory(List<MessageObj> messages) {
        messageHistory.addAll(messages);
    }

}
