package sealab.burt.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.msgparsing.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public @Data
@AllArgsConstructor
class MessageObjectTest {

    public enum TestMessageType{
        REGULAR_RESPONSE, WITH_SELECTED_VALUES
    }

    private String message;
    private ActionName currentAction;
    private List<Intent> nextIntents;
    private TestMessageType type;
    private List<String> selectedValues;

    public MessageObjectTest(){
    }

    public MessageObjectTest(String message, ActionName currentAction, Intent... nextIntents){
        this.currentAction= currentAction;
        this.nextIntents = Arrays.asList(nextIntents);
        this.message= message;
        this.type= TestMessageType.REGULAR_RESPONSE;
    }

    public MessageObjectTest(String message, ActionName currentAction, Intent nextIntent, TestMessageType type){
        this.currentAction= currentAction;
        this.nextIntents = Collections.singletonList(nextIntent);
        this.message= message;
        this.type= type;
    }

    public MessageObjectTest(String message, ActionName currentAction, Intent nextIntent,
                             TestMessageType type, String... selectedValues){
        this.currentAction= currentAction;
        this.nextIntents = Collections.singletonList(nextIntent);
        this.message= message;
        this.type= type;
        this.selectedValues = Arrays.asList(selectedValues);
    }


    public MessageObjectTest(String message, ActionName currentAction, Intent nextIntent,
                             TestMessageType type, List<String> selectedValues){
        this.currentAction= currentAction;
        this.nextIntents = Collections.singletonList(nextIntent);
        this.message= message;
        this.type= type;
        this.selectedValues = selectedValues;
    }


    @Override
    public String toString() {
        return "msg{" +
                "text='" + message + '\'' +
                ", act=" + currentAction +
                ", nextInts='" + nextIntents + '\'' +
                ", type='" + type + '\'' +
                ", selVals=" + selectedValues +
                '}';
    }
}
