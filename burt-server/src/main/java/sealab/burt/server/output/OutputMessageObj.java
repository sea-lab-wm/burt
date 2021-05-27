package sealab.burt.server.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import sealab.burt.server.conversation.KeyValue;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


import java.nio.file.Path;
import java.util.List;

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
