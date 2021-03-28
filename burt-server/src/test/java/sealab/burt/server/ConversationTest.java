package sealab.burt.server;

import jdk.swing.interop.SwingInterOpUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
/**
 * test conversation flow
 * [Hi, send selected app, yes(confirm app selection), provide OB, done(select OB screenshots), provide EB, provide the first step, done(select predicted S2R screens), ]
 */
public class ConversationTest extends AbstractTest {

    private static final String END_POINT = "http://localhost:8081";
    private static final List<List<MessageObjectTest>> conversationFlowList = ConversationExamples.getConversationExamples();
    private static final List<MessageObjectTest> conversationFlow = conversationFlowList.get(1);
    private static String SESSION_ID;
    private static UserMessage MESSAGE;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @org.junit.Test
    public void test1() throws Exception {
        MvcResult mvcResult1 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/start")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult1.getResponse().getStatus();
        assertEquals(200, status);
        SESSION_ID = mvcResult1.getResponse().getContentAsString();
        MESSAGE = new UserMessage();;
        MESSAGE.setSessionId(SESSION_ID);
        for (MessageObjectTest messObj : conversationFlow) {
            ConversationResponse objAnswer = null;
            String message = messObj.getMessage();
            String currentAction = messObj.getCurrentAction();
            System.out.println(message);
            switch (messObj.getType()) {
                case "REGULAR_RESPONSE":
                    objAnswer = sendRegularRequest(message, currentAction);
                    break;
                case "WITH_SELECTED_VALUES":
                    List<String> selectedValues = messObj.getSelectedValues();
                    objAnswer = sendRegularRequestWithMultipleValues(message, selectedValues, currentAction);
                    break;
            }

            assert objAnswer != null;
            System.out.println(objAnswer.getMessage().getMessageObj().getMessage());
            assertNotEquals(-1, objAnswer.getCode());
            assertEquals(messObj.getCurrentAction(), objAnswer.getCurrentAction());
            assertEquals(messObj.getNextIntent(), objAnswer.getNextIntent());
        }

    }

    private ConversationResponse sendRegularRequest(String Message, String currentAction) throws Exception {
        MESSAGE.setMessages(Collections.singletonList(new MessageObj(Message)));
        MESSAGE.setCurrentAction(currentAction);
        MvcResult mvcResult= sendRequest(MESSAGE);
        String response = mvcResult.getResponse().getContentAsString();
        return mapFromJson(response, ConversationResponse.class);
    }

    private ConversationResponse sendRegularRequestWithMultipleValues(String Message, List<String> selectedValues, String currentAction) throws Exception {

        MESSAGE.setMessages(Collections.singletonList(new MessageObj(Message, selectedValues)));
        MESSAGE.setCurrentAction(currentAction);
        MvcResult mvcResult= sendRequest(MESSAGE);
        String response = mvcResult.getResponse().getContentAsString();
        return mapFromJson(response, ConversationResponse.class);
    }

    private MvcResult sendRequest(UserMessage message) throws Exception {
        MvcResult mvcResult3 =
                mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/processMessage").
                        content(mapToJson(message)).contentType(MediaType.APPLICATION_JSON_VALUE).
                        accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status3 = mvcResult3.getResponse().getStatus();
        assertEquals(200, status3);
        return mvcResult3;
    }

    @org.junit.Test
    @After
    public void test2() throws Exception{
        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/end").param("sessionId",
                SESSION_ID).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status2 = mvcResult2.getResponse().getStatus();
        assertEquals(200, status2);
        String response = mvcResult2.getResponse().getContentAsString();

        System.out.println(response);
    }

}
