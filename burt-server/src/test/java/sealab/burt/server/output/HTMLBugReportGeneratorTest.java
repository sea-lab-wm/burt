package sealab.burt.server.output;

import org.junit.jupiter.api.Test;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ConversationState;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

/**
 * @className: HTMLOutputGeneratorTest
 * @description: class description
 * @author: Yang Song
 * @date: 5/18/21
 **/
class HTMLBugReportGeneratorTest {

    @Test
    void generateOutput() throws Exception {
        File outputFolder = Paths.get("../data/generated_bug_reports", "test.html").toFile();
        ConversationState state = new ConversationState();
        state.put(APP_NAME, "Mileage");
        state.put(APP_VERSION, "3.1.1");
        List<BugReportElement> OB = new ArrayList<>();
        OB.add(new BugReportElement("the app crashed", null, "OBScreen.png"));
        state.put(REPORT_OB, OB);
        List<BugReportElement> EB = new ArrayList<>();
        EB.add(new BugReportElement("the app should not crash", null, "EBScreen.png"));
        state.put(REPORT_EB, EB);
        List<BugReportElement> S2R = new ArrayList<>();
        S2R.add(new BugReportElement("Open the app", null, "S2RScreen1.png"));
        S2R.add(new BugReportElement("This a step", null, "NO_SCREEN_AVAILABLE.png"));
        state.put(REPORT_S2R, S2R);
        new HTMLBugReportGenerator().generateOutput(outputFolder, state);
    }
}