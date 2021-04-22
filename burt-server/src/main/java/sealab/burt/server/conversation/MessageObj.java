package sealab.burt.server.conversation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data
@AllArgsConstructor
class MessageObj {

    private String message;
    private String type;
    private double id;
    private String widget;
    private Boolean loading;
    private List<KeyValue> allValues;
    private List<String> selectedValues;

    public MessageObj(){
    }

    public MessageObj(String message) {
        this.message = message;
    }

    public MessageObj(String message, String widget) {
        this.message = message;
        this.widget = widget;
    }

    public MessageObj(String message, String type, double id) {
        this.message = message;
        this.type = type;
        this.id = id;
    }
    public MessageObj(String message, List<String> selectedValues){
        this.message = message;
        this.selectedValues = selectedValues;
    }

    public MessageObj(String message, String type, double id, Boolean loading, List<String> selectedValues) {
        this.message = message;
        this.type = type;
        this.id = id;
        this.loading = loading;
        this.selectedValues = selectedValues;
    }
}
