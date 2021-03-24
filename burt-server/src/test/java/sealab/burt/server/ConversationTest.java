package sealab.burt.server;

import org.junit.Before;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ConversationTest extends AbstractTest {

    private static final String END_POINT = "http://localhost:8081";

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @org.junit.Test
    public void regularConversationTest() throws Exception {
        MvcResult mvcResult1 = sendRequest();
        String sessionId = mvcResult1.getResponse().getContentAsString();

        System.out.println(sessionId);

        //----------------------------------

        UserMessage message = new UserMessage();
        message.setMessages(Collections.singletonList(new MessageObj("Hi")));
        message.setSessionId(sessionId);

        MvcResult mvcResult3 = sendRequest(message);
        String response3 = mvcResult3.getResponse().getContentAsString();

        ConversationResponse obj = mapFromJson(response3, ConversationResponse.class);

        System.out.println(obj);
        System.out.println(obj.getMessage().getMessageObj().getMessage());

        assertNotEquals(-1, obj.getCode());

        //----------------------------------

        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/end").param("sessionId",
                sessionId).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status2 = mvcResult2.getResponse().getStatus();
        assertEquals(200, status2);
        String response = mvcResult2.getResponse().getContentAsString();

        System.out.println(response);
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

    private MvcResult sendRequest() throws Exception {
        MvcResult mvcResult1 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/start")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult1.getResponse().getStatus();
        assertEquals(200, status);
        return mvcResult1;
    }
}
