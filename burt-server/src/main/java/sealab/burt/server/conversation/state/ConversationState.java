package sealab.burt.server.conversation.state;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.appselect.ConfirmAppAction;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.actions.eb.ClarifyEBAction;
import sealab.burt.server.actions.eb.ProvideEBAction;
import sealab.burt.server.actions.eb.ProvideEBNoParseAction;
import sealab.burt.server.actions.ob.*;
import sealab.burt.server.actions.others.EndConversationAction;
import sealab.burt.server.actions.others.GenerateBugReportAction;
import sealab.burt.server.actions.others.ProvideParticipantIdAction;
import sealab.burt.server.actions.others.UnexpectedErrorAction;
import sealab.burt.server.actions.s2r.*;
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
import sealab.burt.server.actions.s2r.prediction.ProvideFirstPredictedS2RAction;
import sealab.burt.server.actions.s2r.prediction.ProvideNextPredictedS2RAction;
import sealab.burt.server.msgparsing.Intent;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.*;

public class ConversationState {

    private final ConcurrentHashMap<StateVariable, Object> stateVariables = new ConcurrentHashMap<>();
    private final AttemptManager attemptManager = new AttemptManager();

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
            put(PROVIDE_S2R_FIRST, new ProvideS2RFirstAction(S2R_DESCRIPTION));
            put(PROVIDE_S2R, new ProvideS2RAction(S2R_DESCRIPTION));

            //prediction
            put(PREDICT_FIRST_S2R_PATH, new ProvideFirstPredictedS2RAction(S2R_PREDICTED_SELECTED));
            put(PREDICT_NEXT_S2R_PATH, new ProvideNextPredictedS2RAction(S2R_PREDICTED_SELECTED));
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
            put(SELECT_MISSING_S2R, new SelectMissingS2RAction(S2R_MISSING_SELECTED));
            put(CONFIRM_SELECTED_MISSING_S2R, new ConfirmSelectedMissingAction(S2R_DESCRIPTION));

            //last step
            put(ActionName.CONFIRM_LAST_STEP, new ConfirmLastStepAction());

            //--------OTHERS-----------//

            put(REPORT_SUMMARY, new GenerateBugReportAction());
            put(UNEXPECTED_ERROR, new UnexpectedErrorAction());
            put(ActionName.END_CONVERSATION, new EndConversationAction());

        }
    };


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

    //---------------------------------

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
}
