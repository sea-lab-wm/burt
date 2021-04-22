package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.actions.ActionName;

import java.util.List;

public @Data
@AllArgsConstructor
class MessageObjectTest {

    public enum TestMessageType{
        REGULAR_RESPONSE, WITH_SELECTED_VALUES
    }

    private String message;
    private ActionName currentAction;
    private String nextIntent;
    private TestMessageType type;
    private List<String> selectedValues;

    public MessageObjectTest(){
    }

    public MessageObjectTest(String message, ActionName currentAction, String nextIntent){
        this.currentAction= currentAction;
        this.nextIntent= nextIntent;
        this.message= message;
        this.type= TestMessageType.REGULAR_RESPONSE;
    }

    public MessageObjectTest(String message, ActionName currentAction, String nextIntent, TestMessageType type){
        this.currentAction= currentAction;
        this.nextIntent= nextIntent;
        this.message= message;
        this.type= type;
    }


    @Override
    public String toString() {
        return "msg{" +
                "text='" + message + '\'' +
                ", act=" + currentAction +
                ", nextInt='" + nextIntent + '\'' +
                ", type='" + type + '\'' +
                ", selVals=" + selectedValues +
                '}';
    }
}
