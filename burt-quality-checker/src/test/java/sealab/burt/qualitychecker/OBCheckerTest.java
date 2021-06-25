package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.stream.Collectors;

public @Slf4j
class OBCheckerTest {

    @BeforeAll
    static void setUp() {
        disableLogging();
    }

    private static void disableLogging() {
        Logger.getLogger("sealab.burt.qualitychecker.actionparser").setLevel(Level.OFF);
        Logger.getLogger("sealab.burt.qualitychecker.OBChecker").setLevel(Level.OFF);
//        Logger.getLogger("sealab.burt.qualitychecker.graph").setLevel(Level.OFF);
        Logger.getLogger("edu.stanford.nlp").setLevel(Level.OFF);
    }


    @Test
    void testSingleOBChecking() throws Exception {
        String ob = "the app crashed when entering fillup";
        var app = new ImmutablePair<>("android-mileage", "3.1.1");

        var checker = new OBChecker(app.getKey(), app.getValue());

        QualityResult result = checker.checkOb(ob);

        log.debug(result.toString());

    }


    @Test
    void checkOb() throws Exception {

        String file = "../data/euler_data" +
                "/4_s2r_in_bug_reports_oracle/android-mileage#3.1.1_64.xml";
        ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, file);

        var app = new ImmutablePair<>("android-mileage", "3.1.1");

        OBChecker checker = new OBChecker(app.getLeft(), app.getRight());

        LinkedList<String> allObSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getOb()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getOb())) {
            String title = bugReport.getTitle().getValue();
            allObSentences.add(0, title);
        }

        for (String sentence : allObSentences) {
            log.debug(sentence);
            QualityResult result = checker.checkOb(sentence);
            log.debug(result.toString());

            for (GraphState matchedState : result.getMatchedStates()) {
                log.debug(matchedState.getScreenshotPath());
            }
        }

    }
}