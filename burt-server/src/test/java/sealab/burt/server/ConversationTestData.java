package sealab.burt.server;

import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.appselect.SelectAppAction;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.msgparsing.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sealab.burt.server.MessageObjectTest.TestMessageType.WITH_SELECTED_VALUES;
import static sealab.burt.server.actions.ActionName.*;
import static sealab.burt.server.msgparsing.Intent.*;

public class ConversationTestData {


    public static List<List<MessageObjectTest>> getConversationExamples() {
        List<List<MessageObjectTest>> conversationFlowList = new ArrayList<>();
        conversationFlowList.add(getConversationFlow_1());
        conversationFlowList.add(getConversationFlow_2());
        conversationFlowList.add(getConversationFlow_3());
        return conversationFlowList;
//        conversationFlowList.add(getConversationFlow_4());
//        conversationFlowList.add(getConversationFlow_5());

    }

    private static List<MessageObjectTest> getConversationFlow_3() {
        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("what's up?", PROVIDE_PARTICIPANT_ID, PARTICIPANT_PROVIDED));
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
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("OB_SCREEN")));
            //ChatBot: Sorry, wrong option, please select the screen that is having the problem
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("no", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
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
            add(new MessageObjectTest("the app should run well", CLARIFY_EB,  AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: the eb is general, is this the screen is having the problem?
            add(new MessageObjectTest("yes", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me a S2R
            add(new MessageObjectTest("I clicked on \"edit vehicle types\"", PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlow_1() {

        KeyValues mileageOption = SelectAppAction.ALL_APPS.stream()
                .filter(entry -> entry.getValue1().equals("Mileage v. 3.1.1"))
                .findFirst().orElse(null);

        return new ArrayList<>() {{
            //ChatBot: hi this is burt
            add(new MessageObjectTest("what's up?", PROVIDE_PARTICIPANT_ID, PARTICIPANT_PROVIDED));
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
        /*    add(new MessageObjectTest("I don't know", PROVIDE_OB_NO_PARSE, OB_DESCRIPTION));
            //ChatBot: I couldn't parse that, pls provide the OB
            add(new MessageObjectTest("the app does not work", REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: I couldn't parse that, pls provide the OB
            add(new MessageObjectTest("the app crashed", REPHRASE_OB, OB_DESCRIPTION));
            //ChatBot: I couldn't match that, pls provide the OB*/
            add(new MessageObjectTest("the app crashed when entering fillup", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("OB_SCREEN")));
            //ChatBot: Sorry, wrong option, please select the screen that is having the problem
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: you selected X, correct?
            add(new MessageObjectTest("no", SELECT_OB_SCREEN, OB_SCREEN_SELECTED));
            //ChatBot: select the screen having the problem
            add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
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
            add(new MessageObjectTest("the app should run well", CLARIFY_EB,  AFFIRMATIVE_ANSWER, NEGATIVE_ANSWER));
            //ChatBot: the eb is general, is this the screen is having the problem?
            add(new MessageObjectTest("yes", PROVIDE_S2R_FIRST, S2R_DESCRIPTION));
            //ChatBot: give me a S2R
            add(new MessageObjectTest("I opened the app", PREDICT_FIRST_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("done", PREDICT_FIRST_S2R, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PREDICT_NEXT_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PREDICT_NEXT_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: give me a S2R
            add(new MessageObjectTest("bla bla", PROVIDE_S2R_NO_PARSE, S2R_DESCRIPTION));
            //ChatBot: I couldn't parse the msg, please rephrase it
            add(new MessageObjectTest("I ate that", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I entered that", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I entered 3 in the cost field", SELECT_MISSING_S2R, S2R_DESCRIPTION));
//            //ChatBot: there are missing steps, please select the ones are correct
//            add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, S2R_MISSING_SELECTED,
//                    WITH_SELECTED_VALUES,
//                    Arrays.asList("S2R_SCREEN1", "S2R_SCREEN2")));
//            //ChatBot: I didn't get that, please select the ones are correct
//            add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
//                    WITH_SELECTED_VALUES,
//                    Collections.singletonList("0"))); // "0" means the first step
//            //ChatBot: ok, you selected some steps, what is the next step?
            add(new MessageObjectTest("I created an entry", REPHRASE_S2R, S2R_DESCRIPTION));
            //ChatBot: I couldn't match the step to anything from the app, please rephrase it
            add(new MessageObjectTest("I set tank", DISAMBIGUATE_S2R, S2R_AMBIGUOUS_SELECTED));
            //ChatBot: this steps is ambiguous, please rephrase it
//            add(new MessageObjectTest("I tap \"tank was not filled to the top\"", CONFIRM_SELECTED_AMBIGUOUS_S2R,
//                    S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("I entered cost", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
//            add(new MessageObjectTest("I entered cost", DISAMBIGUATE_S2R, S2R_AMBIGUOUS_SELECTED));
            //ChatBot: this steps is ambiguous, please rephrase it
            add(new MessageObjectTest("I entered gallons", SPECIFY_INPUT_S2R, S2R_DESCRIPTION));
            //ChatBot: this steps has no input, please provide it
            add(new MessageObjectTest("I entered 23 gallons", PREDICT_FIRST_S2R, S2R_PREDICTED_SELECTED));
           /* //ChatBot: there are missing steps, please select the ones are correct
            add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, S2R_MISSING_SELECTED,
                    WITH_SELECTED_VALUES,
                    Arrays.asList("S2R_SCREEN1", "S2R_SCREEN2")));
            //ChatBot: I didn't get that, please select the ones are correct
            add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); // "0" means the first step*/
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("done", PREDICT_FIRST_S2R, S2R_PREDICTED_SELECTED,
                    WITH_SELECTED_VALUES,
                    Collections.singletonList("0"))); //"0" means the first option
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PREDICT_NEXT_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PREDICT_NEXT_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: please select the correct predicted S2Rs
            add(new MessageObjectTest("none of above", PROVIDE_S2R, S2R_DESCRIPTION));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("I closed the app", PREDICT_FIRST_S2R, S2R_PREDICTED_SELECTED));
            //ChatBot: ok, what is the next step?
            add(new MessageObjectTest("That was the last step", CONFIRM_LAST_STEP, NO_EXPECTED_INTENT));
            //ChatBot: is that the last step?
            add(new MessageObjectTest("yes", REPORT_SUMMARY, NO_EXPECTED_INTENT));
            //ChatBot: ok, this is the report
            add(new MessageObjectTest("Ok, bye", null, null));
        }};
    }

    private static List<MessageObjectTest> getConversationFlow_2() {
        List<MessageObjectTest> conversationFlow = new ArrayList<>();
        conversationFlow.add(new MessageObjectTest("hi", SELECT_APP, APP_SELECTED));
        conversationFlow.add(new MessageObjectTest(null, CONFIRM_APP,
                new ArrayList<Intent>() {{
                    add(AFFIRMATIVE_ANSWER);
                    add(NEGATIVE_ANSWER);
                }},
                WITH_SELECTED_VALUES,
                new ArrayList<>() {{
                    add("GnuCash v. 2.1.3");
                }}
        ));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_OB, OB_DESCRIPTION));
        conversationFlow.add(new MessageObjectTest("the app crashed", SELECT_OB_SCREEN, OB_SCREEN_SELECTED
        ));
        conversationFlow.add(new MessageObjectTest("none of above", CONFIRM_SELECTED_OB_SCREEN, OB_SCREEN_SELECTED
        ));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, NO_EXPECTED_INTENT,
                WITH_SELECTED_VALUES, "OB_SCREEN"));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_EB, EB_DESCRIPTION));
        conversationFlow.add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I opened the app", DISAMBIGUATE_S2R, S2R_AMBIGUOUS_SELECTED
        ));
        conversationFlow.add(new MessageObjectTest("none of above", CONFIRM_SELECTED_AMBIGUOUS_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I click the menu button", PROVIDE_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I create a new list", SPECIFY_INPUT_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I create a new item called listA", PROVIDE_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I type some input ", REPHRASE_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I type some input in textbox ", PROVIDE_S2R, S2R_DESCRIPTION
        ));
        conversationFlow.add(new MessageObjectTest("I search something", SELECT_MISSING_S2R, S2R_MISSING_SELECTED
        ));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, S2R_DESCRIPTION,
                WITH_SELECTED_VALUES,
                new ArrayList<String>() {{
                    add("S2R_SCREEN1");
                    add("S2R_SCREEN2");
                }}));
        conversationFlow.add(new MessageObjectTest("I closed the application, and this is the last step",
                REPORT_SUMMARY, NO_EXPECTED_INTENT));
        conversationFlow.add(new MessageObjectTest("Thanks", ActionName.END_CONVERSATION, NO_EXPECTED_INTENT));

        return conversationFlow;
    }
//    private List<MessageObjectTest> getConversationFlow_3(){
//
//    }
//    private List<MessageObjectTest> getConversationFlow_4() {
//
//    }
//    private List<MessageObjectTest> getConversationFlow_5() {
//
//    }

}
