package sealab.burt.server;

import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.conversation.KeyValues;

import java.util.*;

import static sealab.burt.server.MessageObjectTest.TestMessageType.WITH_SELECTED_VALUES;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.*;

public class ConversationTestData {

    enum FlowName {
        NO_OB_SCREENS_SELECTED, ISSUE36, GENERAL, PREDICTION, MATCHED_OB, MATCHED_OB_MAX_ATTEMPTS,
        OB_NO_MATCH_MAX_ATTEMPTS, EULER_IDEAL_MILEAGE_53, MATCHING_S2R, DUPLICATED_PREDICTED_PATH_MILEAGE,
        EULER_IDEAL_GNUCASH_616, GNUCASH_ISSUE60, EULER_IDEAL_GNUCASH_620, EULER_IDEAL_MILEAGE_53_2
    }

    public static HashMap<FlowName, List<MessageObjectTest>> conversationFlows = new HashMap<>() {{
        put(FlowName.GENERAL, getConversationFlowGeneral());
        put(FlowName.NO_OB_SCREENS_SELECTED, getConversationFlowNoOBScreensSelected());
        put(FlowName.ISSUE36, getConversationFlowIssue36());
        put(FlowName.PREDICTION, getConversationForPrediction());
        put(FlowName.MATCHING_S2R, getConversationFlowMatchingS2R());
        put(FlowName.MATCHED_OB, getConversationFlowMatchedOB());
        put(FlowName.MATCHED_OB_MAX_ATTEMPTS, getConversationFlowMatchedOBMaxAttempts());
        put(FlowName.OB_NO_MATCH_MAX_ATTEMPTS, getConversationFlowOBNoMatchMaxAttempts());
        put(FlowName.EULER_IDEAL_MILEAGE_53, getFlowEulerIdealMileage53());
        put(FlowName.DUPLICATED_PREDICTED_PATH_MILEAGE, getDuplicatedPredictedPathMileage());
        put(FlowName.EULER_IDEAL_MILEAGE_53_2, getFlowEulerIdealMileage53Second());
        put(FlowName.EULER_IDEAL_GNUCASH_616, getFlowEulerIdealGnuCash616());
        put(FlowName.EULER_IDEAL_GNUCASH_620, getFlowEulerIdealGnuCash620());
        put(FlowName.GNUCASH_ISSUE60, getFlowGnuCashIssue60());
    }};

    private static List<MessageObjectTest> getFlowEulerIdealGnuCash620() {
        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("GnuCash v. 2.1.3"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("When an account is edited, its color is lost", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("7")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("The color of the account should remain the same", PROVIDE_S2R_FIRST,
                    S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("tap on Edit account", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2", "3", "4", "5", "6")));
            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap on the orange color selector on the left side of the screen",
                    CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: there are predicted steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0")));
            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap the \"Save\" button at the top right of the screen",
                    CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION));


            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getFlowGnuCashIssue60() {
        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("GnuCash v. 2.1.3"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("crash when opening the settings", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("1")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("open the settings", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2", "3", "4", "5", "6")));
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};

    }

    private static List<MessageObjectTest> getFlowEulerIdealGnuCash616() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("GnuCash v. 2.1.3"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));


            //ChatBot: provide the OB
            add(new MessageObjectTest("Export to Google Drive silently fails", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("3")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("The transactions are deleted and the QIF file is in the appropriate directory" +
                    " in Google Drive",
                    CLARIFY_EB, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: the eb is general, is this the screen is having the problem?
            add(new MessageObjectTest("yes", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("Tap on the \"Menu\" button at the top left of the screen", CONFIRM_MATCHED_S2R
                    , AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2", "3", "4", "5")));
            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap on the \"Export\" Menu item", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("no", PROVIDE_S2R_NO_MATCH, S2R_DESCRIPTION));
       /*     //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));*/
            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap on \"Export To\"", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0")));
            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap on the \"Google Drive\" Dropdown menu option", CONFIRM_MATCHED_S2R,
                    AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0")));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0")));

            //ChatBot: give me the NEXT S2R
            add(new MessageObjectTest("Tap on the \"Export\" button", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_DESCRIPTION));
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};

    }

    private static List<MessageObjectTest> getDuplicatedPredictedPathMileage() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("the app crashed when I add a new vehicle",
                    SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("1")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I opened the app", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: Okay, it seems the next steps that you performed might be the following.
            //ChatBot: Can you confirm which ones you actually performed next?
            //ChatBot: Remember that the screenshots below are for reference only.
            //ChatBot: Please click the “done” button when you are done.
            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("i clicked the Vehicles", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));
           /* //ChatBot: give me the next S2R
            add(new MessageObjectTest("Go back", REPHRASE_S2R, S2R_DESCRIPTION));
*/
/*
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2", "3")));
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));*/
        }};

    }


    private static List<MessageObjectTest> getFlowEulerIdealMileage53() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("Fuel economy stats are not calculated when all fillups are \"not to the top\"",
                    SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: is this the OB screen?
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("5")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("\"Fuel economy\" stats are calculated", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I opened the app", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: Okay, it seems the next steps that you performed might be the following.
            //ChatBot: Can you confirm which ones you actually performed next?
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("1", "2", "3", "4")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES, Collections.singletonList("1")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.NONE, PREDICT_NEXT_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("Tap on the \"Fillup\" button", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("I saved another fillup", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("1")));
//                    Arrays.asList("1", "2", "3", "4")));
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("Tap on the \"Statistics\" button\"", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION));
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};

    }

    private static List<MessageObjectTest> getFlowEulerIdealMileage53Second() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("Fuel economy stats are not calculated when all fillups are \"not to the top\"",
                    SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("4")));
            //ChatBot: is this the OB screen?
            add(new MessageObjectTest(ChatBotAction.NONE, SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("5")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("\"Fuel economy\" stats are calculated", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I opened the app", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: Okay, it seems the next steps that you performed might be the following.
            //ChatBot: Can you confirm which ones you actually performed next?
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("1", "2", "3", "4")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES, Collections.singletonList("1")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.NONE, PREDICT_NEXT_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("Tap on the \"Fillup\" button", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));
 /*           //ChatBot: give me the next S2R
            add(new MessageObjectTest("Tap on \"Price per Gallons\" Edit Text\"", REPHRASE_S2R,
                    S2R_DESCRIPTION));*/
            //ChatBot: give me the next S2R
            add(new MessageObjectTest("Type 1.6 on \"Price per Gallons\"", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R,
                    S2R_MISSING_SELECTED));
            //ChatBot: please select the correct missing S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));

            //------------------------------------------------

            //ChatBot: give me the next S2R
            add(new MessageObjectTest("I entered 6 gallons", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "3")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
/*            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("2")));*/

            add(new MessageObjectTest(ChatBotAction.NONE, PREDICT_NEXT_S2R_PATH, S2R_PREDICTED_SELECTED));

            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));

            //-------------------------------------------

            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};

    }


    private static List<MessageObjectTest> getConversationFlowOBNoMatchMaxAttempts() {

        KeyValues DroidWeightOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Droid Weight v. 1.5.4"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: didn't get that, please provide the participant id
            add(new MessageObjectTest("P23", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(DroidWeightOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("I got some error when I tried to input comment in edit weight",
                    REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: there is no match, could you rephrase it?
            add(new MessageObjectTest("I got some error when I tried to input comment in edit weight",
                    REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: there is no match, could you rephrase it?
            add(new MessageObjectTest("I got some error when I tried to input comment in edit weight",
                    PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("i should not get some error", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: Can you please tell me the first step that you performed?
            add(new MessageObjectTest("I input the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I set the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input the weight and set current weight in kg", CONFIRM_MATCHED_S2R,
                    AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input \"55\" on the \"weight\"", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2")));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlowMatchedOBMaxAttempts() {

        KeyValues DroidWeightOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Droid Weight v. 1.5.4"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: didn't get that, please provide the participant id
            add(new MessageObjectTest("P23", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(DroidWeightOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("the app crashed when I tried to change date", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: is this the OB screen?
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_DESCRIPTION));
            //Chatbot: All right. Then, your description of the problem does not seem to match any screen from the app.
            //ChatBot: Can you tell me the incorrect behavior one more time?
            add(new MessageObjectTest("the app crashed when I tried to change date", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: Got it. From the list below, can you please select the screen that is showing the problem or that triggered the problem when you performed some action on it?
            //ChatBot: Please hit the ChatBotAction.DONE button after you have selected it.
            add(new MessageObjectTest(ChatBotAction.NONE,  CONFIRM_SELECTED_OB_SCREEN, OB_DESCRIPTION));
            //Chatbot: All right. Then, your description of the problem does not seem to match any screen from the app.
            //ChatBot: Can you tell me the incorrect behavior one more time?
            add(new MessageObjectTest("the app crashed when I tried to change date", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            ///ChatBot: Got it. From the list below, can you please select the screen that is showing the problem or that triggered the problem when you performed some action on it?
            //ChatBot: Please hit the ChatBotAction.DONE button after you have selected it.
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, EB_DESCRIPTION));
            //ChatBot: All right. Let's continue.
            //ChatBot: Can you please tell me how the app is supposed to work instead?
            add(new MessageObjectTest("i should not get some error", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: Can you please tell me the first step that you performed?
//            add(new MessageObjectTest("c", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
//            //ChatBot: is this the S2R you mean to report?
//            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I set the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input the weight and set current weight in kg", CONFIRM_MATCHED_S2R,
                    AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input 55 on the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2")));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlowMatchedOB() {

        KeyValues DroidWeightOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Droid Weight v. 1.5.4"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: didn't get that, please provide the participant id
            add(new MessageObjectTest("P23", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(DroidWeightOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("the app crashed when I tried to change date", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: Got it. From the list below, can you please select the screen that is having or triggering the problem?
            //ChatBot: Please hit the ChatBotAction.DONE button after you have selected it.
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("3")));
            //ChatBot: Okay, you selected the screen "4. Measure Edit"
            //ChatBot: Shall we continue?
//            add(new MessageObjectTest("the app crashed when I tried to change date", CONFIRM_MATCHED_OB,
//                    AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
//            //ChatBot: is this the OB screen?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("i should not get some error", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: Can you please tell me the first step that you performed?
            add(new MessageObjectTest("I input the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_INPUT));
            add(new MessageObjectTest("55", PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I set the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_INPUT));
            add(new MessageObjectTest("55", PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input the weight and set current weight in kg", CONFIRM_MATCHED_S2R,
                    AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: you didn't specify the input, please provide the step with input
            add(new MessageObjectTest("I input 55 on the weight", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "1", "2")));
        }};
    }

    private static List<MessageObjectTest> getConversationFlowMatchingS2R() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("crash when I added a new vehicle", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("2")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I clicked the vehicles tab", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: please select the correct missing S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I added a new vehicle", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0")));
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlowNoOBScreensSelected() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("the app crashed when I was entering a new vehicle", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: ok, no more options, so please rephrase the OB
//            add(new MessageObjectTest("the app crashed when I was adding a new vehicle", SELECT_OB_SCREEN,
//                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_DESCRIPTION));
            //ChatBot: ok, no more options, so please rephrase the OB
            add(new MessageObjectTest("the app crashed when I was adding a new vehicle", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest("bla bla", CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: wrong option, select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: wrong option, select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_DESCRIPTION));


            //ChatBot: select the screen having the problem
            add(new MessageObjectTest("the app crashed when I was adding a new vehicle", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, EB_DESCRIPTION));

            //ChatBot: ok, that was the last attempt, so please give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I added \"bla bla\" as a comment", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("I saved the fillup", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0")));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));

          /*  //ChatBot: ok, select the screen having the problem from the new batch
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: ok, select the screen having the problem from the new batch
            add(new MessageObjectTest(ChatBotAction.NONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: ok, select the screen having the problem from the new batch


            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("5")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I added \"bla bla\" as a comment", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("I saved the fillup", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "3")));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));*/
        }};
    }

    private static List<MessageObjectTest> getConversationFlowGeneral() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("no participant id", PROVIDE_PARTICIPANT_ID, PARTICIPANT_PROVIDED));
            //ChatBot: didn't get that, please provide the participant id
            add(new MessageObjectTest("P23", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    APP_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("Mileage v. 3.1.1")));
            //ChatBot: I didn't get that, select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("no", SELECT_APP, APP_SELECTED));
            //ChatBot: ok, select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("I don't know", PROVIDE_OB_NO_PARSE, OB_DESCRIPTION));
            //ChatBot: I couldn't parse that, pls provide the OB
            add(new MessageObjectTest("the app does not work", REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: I couldn't parse that, pls provide the OB
            add(new MessageObjectTest("the app crashed", REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: I couldn't match that, pls provide the OB
            add(new MessageObjectTest("the app crashed when entering fillup", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("OB_SCREEN")));
            //ChatBot: Sorry, wrong option, please select the screen that is having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("no", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("just don't know", PROVIDE_EB_NO_PARSE, EB_DESCRIPTION));
            //ChatBot: couldn't parse it, give me the EB
            add(new MessageObjectTest("the app should run well", CLARIFY_EB, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: the eb is general, is this the screen is having the problem?
            add(new MessageObjectTest("no", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: ok, give me the EB
            add(new MessageObjectTest("the app should run well", CLARIFY_EB, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: the eb is general, is this the screen is having the problem?
            add(new MessageObjectTest("yes", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me a S2R
            add(new MessageObjectTest("i opened the app", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("1", "2", "3")));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: Okay, can you please provide the next step?
            add(new MessageObjectTest("bla bla", PROVIDE_S2R_NO_PARSE, S2R_DESCRIPTION));
            //ChatBot: I couldn't parse the msg, please rephrase it
            add(new MessageObjectTest("I ate that", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I entered that", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I entered 3 in the cost field", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));


            //ChatBot: there are missing steps, please select the ones are correct
            //ChatBot: I didn't get that, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("I created an entry", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I set tank", DISAMBIGUATE_S2R, S2R_AMBIGUOUS_SELECTED));
            //ChatBot: this steps is ambiguous, please rephrase it
//            add(new MessageObjectTest("I tap \"tank was not filled to the top\"", CONFIRM_SELECTED_AMBIGUOUS_S2R,
//                    S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("I entered cost", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
//            add(new MessageObjectTest("I entered cost", DISAMBIGUATE_S2R, S2R_AMBIGUOUS_SELECTED));
            //ChatBot: this steps is ambiguous, please rephrase it
            add(new MessageObjectTest("I entered gallons", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: this steps has no input, please provide it
            add(new MessageObjectTest("I entered 23 gallons", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            add(new MessageObjectTest("yes", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
           /* //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_MISSING_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("S2R_SCREEN1", "S2R_SCREEN2")));
            //ChatBot: I didn't get that, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step*/
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest(ChatBotAction.NONE, PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("I closed the app", PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlowIssue36() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: what is you participant id?
            add(new MessageObjectTest("P5", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(mileageOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("crash when entering fillup", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("5"))); //"5" means the fifth option
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me the first S2R
            add(new MessageObjectTest("I added \"bla bla\" as a comment", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER,
                    NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step
            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("I saved the fillup", CONFIRM_MATCHED_S2R, AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: is this the S2R you mean to report?
            add(new MessageObjectTest("yes", SELECT_MISSING_S2R, S2R_MISSING_SELECTED));
            //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("0", "3")));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationForPrediction() {

        KeyValues DroidWeightOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Droid Weight v. 1.5.4"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            add(new MessageObjectTest("I'd like to report some problem", PROVIDE_PARTICIPANT_ID,
                    PARTICIPANT_PROVIDED));
            //ChatBot: didn't get that, please provide the participant id
            add(new MessageObjectTest("P23", SELECT_APP, APP_SELECTED));
            //ChatBot: select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    APP_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("Droid Weight v. 1.5.4")));
            //ChatBot: I didn't get that, select an app from the list
            add(new MessageObjectTest(null, CONFIRM_APP,
                    Arrays.asList(AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER),
                    WITH_SELECTED_VALUES,
                    Collections.singletonList(DroidWeightOption.getKey())));
            //ChatBot: is that the app you selected?
            add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
            //ChatBot: provide the OB
            add(new MessageObjectTest("I got some error when I tried to compute BMI", SELECT_OB_SCREEN,
                    OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest(ChatBotAction.DONE, CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("3")));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("ye", null, null));
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
            //ChatBot: give me the EB
            add(new MessageObjectTest("i should not get some error", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot:Okay. Now I need to know the steps that you performed and caused the problem.
            //ChatBot:Can you please tell me the first step that you performed?
            add(new MessageObjectTest("opened the app", PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED));
            //ChatBot: Okay, it seems the next steps that you performed might be the following.
            //ChatBot: Can you confirm which ones you actually performed next?
            //ChatBot: Please click the “done” button when you are done.
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("3", "4")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"
            add(new MessageObjectTest(ChatBotAction.DONE, PREDICT_FIRST_S2R_PATH, S2R_DESCRIPTION, WITH_SELECTED_VALUES,
                    Arrays.asList("1", "2")));
            //ChatBot: "Okay, it seems the next steps that you performed might be the following.",
            //ChatBot: "Can you confirm which ones you actually performed next?"

            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }
}
