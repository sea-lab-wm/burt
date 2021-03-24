package sealab.burt.server;

import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ConversationTest extends AbstractTest {

    private static final String END_POINT = "http://localhost:8081";
    private static String APP;
    private static String OB_SCREEN;
    private static UserMessage MESSAGE;
    private static String SESSION_ID;


    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @org.junit.Test
     public void testA() throws Exception {
        MvcResult mvcResult1 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/start")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult1.getResponse().getStatus();
        assertEquals(200, status);

        SESSION_ID = mvcResult1.getResponse().getContentAsString();
        MESSAGE = new UserMessage();
        MESSAGE.setSessionId(SESSION_ID);
    }
    @org.junit.Test
    @DisplayName("send greeting message")
    public void testB() throws Exception {
        System.out.println(MESSAGE);
        MESSAGE.setMessages(Collections.singletonList(new MessageObj("Hi")));
        MvcResult mvcResult3 = sendRequest(MESSAGE);
        String response3 = mvcResult3.getResponse().getContentAsString();

        ConversationResponse obj = mapFromJson(response3, ConversationResponse.class);
        APP = obj.getMessage().getValues().get(0).getKey();

        assertNotEquals(-1, obj.getCode());
    }
    @org.junit.Test
    @DisplayName("send selected app")
    public void testC() throws Exception {

        List<String> selectedValuesSelectedApp = new ArrayList<>() {{ add(APP);}};
        MESSAGE.setMessages(Collections.singletonList(new MessageObj(null, selectedValuesSelectedApp)));
        MvcResult mvcResultSelectedApp = sendRequest(MESSAGE);
        String responseSelectedApp = mvcResultSelectedApp.getResponse().getContentAsString();
        ConversationResponse objSelectedApp = mapFromJson(responseSelectedApp, ConversationResponse.class);
        System.out.println(objSelectedApp.getMessage().getMessageObj().getMessage());

    }
    @org.junit.Test
    @DisplayName("confirm app selection")
    public void testD() throws Exception {
       int code = sendAffirmativeOrNegativeAnswer("yes");
       assertNotEquals(-1,code);
    }

    @org.junit.Test
    @DisplayName("provide OB")
    public void testE() throws Exception {
        MESSAGE.setMessages(Collections.singletonList(new MessageObj("the app crashed")));
        MvcResult mvcResultAnswerOB= sendRequest(MESSAGE);
        String responseAnswerOB = mvcResultAnswerOB.getResponse().getContentAsString();

        ConversationResponse objAnswerOB= mapFromJson(responseAnswerOB, ConversationResponse.class);
        System.out.println(objAnswerOB.getMessage().getMessageObj().getMessage());
        OB_SCREEN = objAnswerOB.getMessage().getValues().get(0).getKey();
        assertNotEquals(-1, objAnswerOB.getCode());
    }

    @org.junit.Test
    @DisplayName("select OB screen")
    public void testF() throws Exception {
        List<String> selectedValuesOBScreen = new ArrayList<>() {{ add(OB_SCREEN);}};
        MESSAGE.setMessages(Collections.singletonList(new MessageObj("done", selectedValuesOBScreen)));
        MvcResult mvcResultSelectedOBScreen= sendRequest(MESSAGE);
        String responseSelectedOBScreen = mvcResultSelectedOBScreen.getResponse().getContentAsString();

        ConversationResponse objSelectedOBScreen = mapFromJson(responseSelectedOBScreen, ConversationResponse.class);
        System.out.println(objSelectedOBScreen.getMessage().getMessageObj().getMessage());

        assertNotEquals(-1, objSelectedOBScreen.getCode());

    }
    @org.junit.Test
    @DisplayName("confirm OB screen selection")
    public void testG() throws Exception {
        int code = sendAffirmativeOrNegativeAnswer("yes");
        assertNotEquals(-1,code);
    }

    //Disabled does not work
    @Disabled("Do not run this")
    @org.junit.Test
    @DisplayName("confirm OB screen selection")
    public void testH() throws Exception {
        int code = sendAffirmativeOrNegativeAnswer("no");
        assertNotEquals(-1,code);
    }

    @org.junit.Test
    @DisplayName("provide EB")
    public void testI() throws Exception {
        int code = answerWithoutScreens("the app should not crash");
        assertNotEquals(-1, code);
    }

    @Disabled
    @org.junit.Test
    @DisplayName("confirm EB screen")
    public void testJ() throws Exception {
        int code = sendAffirmativeOrNegativeAnswer("yes");
        assertNotEquals(-1,code);
    }

    @org.junit.Test
    @DisplayName("provide the first step")
    public void testK() throws Exception {
        int code = answerWithoutScreens("the first step is that i open the app");
        assertNotEquals(-1, code);
    }

    @org.junit.Test
    public void testL() throws Exception{
        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/end").param("sessionId",
                SESSION_ID).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

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

    private int sendAffirmativeOrNegativeAnswer(String ans)throws Exception {
        MESSAGE.setMessages(Collections.singletonList(new MessageObj(ans)));
        MvcResult mvcResultAffirmativeAnswer= sendRequest(MESSAGE);
        String responseAffirmativeAnswer= mvcResultAffirmativeAnswer.getResponse().getContentAsString();

        ConversationResponse objAffirmativeAnswer = mapFromJson(responseAffirmativeAnswer, ConversationResponse.class);
        System.out.println(objAffirmativeAnswer.getMessage().getMessageObj().getMessage());
        return objAffirmativeAnswer.getCode();

    }
    private int answerWithoutScreens(String description) throws Exception {
        MESSAGE.setMessages(Collections.singletonList(new MessageObj(description)));
        System.out.println(MESSAGE);
        MvcResult mvcResult= sendRequest(MESSAGE);
        String response = mvcResult.getResponse().getContentAsString();

        ConversationResponse objAnswer= mapFromJson(response, ConversationResponse.class);
        System.out.println(objAnswer.getMessage().getMessageObj().getMessage());
        return objAnswer.getCode();

    }

}
