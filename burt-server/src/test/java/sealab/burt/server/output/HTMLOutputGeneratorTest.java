package sealab.burt.server.output;

import org.junit.jupiter.api.Test;
import sealab.burt.server.StateVariable;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static sealab.burt.server.StateVariable.*;

/**
 * @className: HTMLOutputGeneratorTest
 * @description: class description
 * @author: Yang Song
 * @date: 5/18/21
 **/
class HTMLOutputGeneratorTest {

    @Test
    void generateOutput() throws Exception {
        File outputFile=  Paths.get("../data/generated_bug_reports", "test.html").toFile();
        ConcurrentHashMap<StateVariable, Object> state = new ConcurrentHashMap<>();
        List<outputMessageObj> OB = new ArrayList<>();
        OB.add(new outputMessageObj("the app crashed", "./data/app_logos/OBScreen.png"));
        state.put(REPORT_OB, OB);
        List<outputMessageObj> EB = new ArrayList<>();
        EB.add(new outputMessageObj("the app should not crash", "./data/app_logos/EBScreen.png"));
        state.put(REPORT_EB, EB);
        List<outputMessageObj> S2R = new ArrayList<>();
        S2R.add(new outputMessageObj("the app should not crash", "./data/app_logos/S2RScreen1.png"));
        state.put(REPORT_S2R, S2R);
        new HTMLOutputGenerator().generateOutput(outputFile, state);
    }
}