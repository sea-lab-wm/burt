package sealab.burt.server.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data
@AllArgsConstructor
class OutputMessageObj {

    private String message;
    private String type;
    private String screenshotPath;

    public OutputMessageObj(){
    }

    public OutputMessageObj(String message) {
        this.message = message;
    }

    public OutputMessageObj(String message, String screenshotPath) {
        this.screenshotPath = screenshotPath;
        this.message = message;
    }


}
