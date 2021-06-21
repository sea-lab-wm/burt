package sealab.burt.server.output;

import lombok.AllArgsConstructor;
import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = true)
public @Data
@AllArgsConstructor
class BugReportElement {

    private String stringElement;
    private Object originalElement;
    private String screenshotPath;

}
