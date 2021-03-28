package sealab.burt.server;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public @Data
@AllArgsConstructor
class MessageObjectTest {
    private String message;
    private String currentAction;
    private String nextIntent;
    private String type;
    private List<String> selectedValues;
    public MessageObjectTest(){
    }
    public MessageObjectTest(String message, String currentAction, String nextIntent, String type){
        this.currentAction= currentAction;
        this.nextIntent= nextIntent;
        this.message= message;
        this.type= type;

    }

}
