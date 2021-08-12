package sealab.burt.server.conversation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data
@AllArgsConstructor
class MessageObj {

    //NOTE: don't rename the following fields are these are mapped to the GUI
    private String message;
    private String type;
    private double id;
    private String widget;
    private Boolean loading;
    private boolean disabled;
    private int index;

    //---------------

    private List<KeyValues> allValues;
    private List<String> selectedValues;

    public MessageObj(){
    }

    public MessageObj(String message) {
        this.message = message;
    }
    public MessageObj(Integer index) {
        this.index = index;
    }

    public MessageObj(String message, WidgetName widget) {
        this.message = message;
        this.widget = widget.toString();
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
    public MessageObj(String message, String type, double id, Boolean loading, List<String> selectedValues, boolean disabled) {
        this.message = message;
        this.type = type;
        this.id = id;
        this.loading = loading;
        this.selectedValues = selectedValues;
        this.disabled = disabled;
    }
}
