package sealab.burt.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationResponse;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserMessage;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public @Slf4j
class ConversationTest2 extends AbstractTest {

    private static String sessionId;

    @Before
    public void setUp() {
        super.setUp();
    }

    @org.junit.Test
    public void test1() throws Exception {


        List<MessageObjectTest> conversationFlow = ConversationTestData.getConversationExamples().get(0);

        //start the conversation
        MvcResult mvcResult1 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/start")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult1.getResponse().getStatus();
        assertEquals(200, status);
        sessionId = mvcResult1.getResponse().getContentAsString();

        log.debug("Conversation started: " + sessionId);

        //send each message

        for (MessageObjectTest messObj : conversationFlow) {
            String message = messObj.getMessage();
            ActionName currentAction = messObj.getCurrentAction();

            log.debug("Sending message: " + messObj);
            ConversationResponse botResponse = null;
            switch (messObj.getType()) {
                case REGULAR_RESPONSE:
                    botResponse = sendRegularRequest(message, currentAction);
                    break;
                case WITH_SELECTED_VALUES:
                    List<String> selectedValues = messObj.getSelectedValues();
                    botResponse = sendRegularRequestWithMultipleValues(message, selectedValues, currentAction);
                    break;
            }

            log.debug("Received response: " + botResponse);

            assert botResponse != null;
//            System.out.println(botResponse.getMessage().getMessageObj().getMessage());
            assertNotEquals(-1, botResponse.getCode());
            assertEquals(messObj.getCurrentAction(), botResponse.getCurrentAction());
            assertEquals(messObj.getNextIntent(), botResponse.getNextIntent());
        }

    }

    private ConversationResponse sendRegularRequest(String Message, ActionName currentAction) throws Exception {
        UserMessage message = new UserMessage(sessionId, Collections.singletonList(new MessageObj(Message)));
        message.setCurrentAction(currentAction);

        MvcResult mvcResult = sendRequest(message);
        String response = mvcResult.getResponse().getContentAsString();

        return mapFromJson(response, ConversationResponse.class);
    }

    private ConversationResponse sendRegularRequestWithMultipleValues(String Message, List<String> selectedValues,
                                                                      ActionName currentAction) throws Exception {
        UserMessage message = new UserMessage(sessionId, Collections.singletonList(new MessageObj(Message,
                selectedValues)));
        message.setCurrentAction(currentAction);
        MvcResult mvcResult = sendRequest(message);
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

    @After
    public void endConversation() throws Exception {

        log.debug("Ending the conversation...");

        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders
                .post(END_POINT + "/end")
                .param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status2 = mvcResult2.getResponse().getStatus();
        assertEquals(200, status2);
        String response = mvcResult2.getResponse().getContentAsString();

        log.debug(response);
    }

}
