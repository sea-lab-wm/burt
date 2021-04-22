package sealab.burt.server;

import java.util.ArrayList;
import java.util.List;

import static sealab.burt.server.MessageObjectTest.TestMessageType.WITH_SELECTED_VALUES;
import static sealab.burt.server.actions.ActionName.*;

public class ConversationExamples {

    public static List<List<MessageObjectTest>> getConversationExamples() {
        List<List<MessageObjectTest>> conversationFlowList = new ArrayList<>();
        conversationFlowList.add(getConversationFlow_1());
        conversationFlowList.add(getConversationFlow_2());
        return conversationFlowList;
//        conversationFlowList.add(getConversationFlow_3());
//        conversationFlowList.add(getConversationFlow_4());
//        conversationFlowList.add(getConversationFlow_5());

    }

    private static List<MessageObjectTest> getConversationFlow_1() {
        List<MessageObjectTest> conversationFlow = new ArrayList<>();
        conversationFlow.add(new MessageObjectTest("hi", SELECT_APP, "APP_SELECTED"));
        conversationFlow.add(new MessageObjectTest(null, CONFIRM_APP, "NO_EXPECTED_INTENT", WITH_SELECTED_VALUES,
                new ArrayList<>() {{
            add("GnuCash v. 2.1.3");
        }}));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_OB, "OB_DESCRIPTION"));
        conversationFlow.add(new MessageObjectTest("the app crashed", SELECT_OB_SCREEN, "OB_SCREEN_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, "NO_EXPECTED_INTENT",
                WITH_SELECTED_VALUES, new ArrayList<>() {{
            add("OB_SCREEN");
        }}));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_EB, "EB_DESCRIPTION"));
        conversationFlow.add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I opened the app", DISAMBIGUATE_S2R, "S2R_AMBIGUOUS_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("done",
                CONFIRM_SELECTED_AMBIGUOUS_S2R, "S2R_DESCRIPTION", WITH_SELECTED_VALUES, new ArrayList<>() {{
            add("S2R_SCREEN1");
        }}));
        conversationFlow.add(new MessageObjectTest("I click the menu button",
                PROVIDE_S2R, "S2R_DESCRIPTION"));
        conversationFlow.add(new MessageObjectTest("I create a new list", SPECIFY_INPUT_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I create a new item called listA", PROVIDE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I type some input ", REPHRASE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I type something in textbox", SELECT_MISSING_S2R, 
                "S2R_MISSING_SELECTED"));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, "S2R_DESCRIPTION", 
                WITH_SELECTED_VALUES, new ArrayList<>() {{
            add("S2R_SCREEN1");
            add("S2R_SCREEN2");
        }}));

//        conversationFlow.add(new MessageObjectTest());

        return conversationFlow;
    }

    private static List<MessageObjectTest> getConversationFlow_2() {
        List<MessageObjectTest> conversationFlow = new ArrayList<>();
        conversationFlow.add(new MessageObjectTest("hi", SELECT_APP, "APP_SELECTED"));
        conversationFlow.add(new MessageObjectTest(null, CONFIRM_APP, "NO_EXPECTED_INTENT", WITH_SELECTED_VALUES,
                new ArrayList<>() {{
            add("GnuCash v. 2.1.3");
        }}));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_OB, "OB_DESCRIPTION"));
        conversationFlow.add(new MessageObjectTest("the app crashed", SELECT_OB_SCREEN, "OB_SCREEN_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("none of above", CONFIRM_SELECTED_OB_SCREEN, "OB_SCREEN_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_OB_SCREEN, "NO_EXPECTED_INTENT",
                WITH_SELECTED_VALUES, new ArrayList<>() {{
            add("OB_SCREEN");
        }}));
        conversationFlow.add(new MessageObjectTest("yes", PROVIDE_EB, "EB_DESCRIPTION"));
        conversationFlow.add(new MessageObjectTest("the app should not crash", PROVIDE_S2R_FIRST, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I opened the app", DISAMBIGUATE_S2R, "S2R_AMBIGUOUS_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("none of above", CONFIRM_SELECTED_AMBIGUOUS_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I click the menu button", PROVIDE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I create a new list", SPECIFY_INPUT_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I create a new item called listA", PROVIDE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I type some input ", REPHRASE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I type some input in textbox ", PROVIDE_S2R, "S2R_DESCRIPTION"
                ));
        conversationFlow.add(new MessageObjectTest("I search something", SELECT_MISSING_S2R, "S2R_MISSING_SELECTED"
                ));
        conversationFlow.add(new MessageObjectTest("done", CONFIRM_SELECTED_MISSING_S2R, "S2R_DESCRIPTION", 
                WITH_SELECTED_VALUES, new ArrayList<>() {{
            add("S2R_SCREEN1");
            add("S2R_SCREEN2");
        }}));
        conversationFlow.add(new MessageObjectTest("I closed the application, and this is the last step",
                REPORT_SUMMARY, "NO_EXPECTED_INTENT"));
        conversationFlow.add(new MessageObjectTest("Thanks", ENDING, "NO_EXPECTED_INTENT"));

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
