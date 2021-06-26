package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class QualityCheckerTestBugReports {

    private final Path parsedBugReportsPath = Path.of("..", "data",
            "euler_data", "4_s2r_in_bug_reports_oracle");

    @BeforeAll
    static void setUp() {
        disableLogging();
    }

    private static void disableLogging() {
        Logger.getLogger("sealab.burt.qualitychecker.actionparser").setLevel(Level.OFF);
        Logger.getLogger("sealab.burt.qualitychecker.S2RChecker").setLevel(Level.OFF);
        Logger.getLogger("sealab.burt.qualitychecker.graph").setLevel(Level.OFF);
        Logger.getLogger("edu.stanford.nlp").setLevel(Level.OFF);
    }

    @Test
    void testBugReports() throws Exception {

        List<Pair<String, String>> apps = new LinkedList<>() {
            {
//                add(new ImmutablePair<>("gnucash-android", "2.1.3"));
                add(new ImmutablePair<>("droidweight", "1.5.4"));
                add(new ImmutablePair<>("android-mileage", "3.1.1"));
            }
        };

        //------------------------------------

        for (Pair<String, String> app : apps) {
            String appName = app.getKey();
            String appVersion = app.getValue();

            try (Stream<Path> stream = Files.walk(parsedBugReportsPath)) {
                List<Path> scenarioFiles = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().contains(appName + "#" + appVersion))
                        .collect(Collectors.toList());

                for (Path bugReportFile : scenarioFiles) {

                    log.debug("-------------------------------------------------------------");
                    log.debug("Processing: " + bugReportFile);

                    S2RChecker s2RChecker = new S2RChecker(appName, appVersion);

                    ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class,
                            bugReportFile.toFile());

                    LinkedList<String> allS2RSentences = getS2RSentences(bugReport);

                    for (String s2rSentence : allS2RSentences) {

                        log.debug("S2R sentence: " + s2rSentence);
                        QualityFeedback qualityResult = s2RChecker.checkS2R(s2rSentence);

                        List<S2RQualityCategory> assessmentResults = qualityResult.getAssessmentResults();
                        log.debug("S2R quality results: " + assessmentResults.toString());

                        if (Collections.singletonList(S2RQualityCategory.LOW_Q_NOT_PARSED).equals(assessmentResults))
                            log.warn(S2RQualityCategory.LOW_Q_NOT_PARSED.toString());
                    }

                    //---------------------------------------

                    OBChecker obChecker = new OBChecker(appName, appVersion);

                    LinkedList<String> allObSentences = getObSentences(bugReport);

                    for (String obSentence : allObSentences) {

                        log.debug("OB sentence: " + obSentence);
                        QualityResult qualityResult = obChecker.checkOb(obSentence);

                        log.debug("OB quality results: " + qualityResult.getResult());
                    }

                }
            }
        }

    }

    private LinkedList<String> getObSentences(ShortLabeledBugReport bugReport) {
        LinkedList<String> allOBSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getOb()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getOb())) {
            String title = bugReport.getTitle().getValue();
            allOBSentences.add(0, title);
        }
        return allOBSentences;
    }

    private LinkedList<String> getS2RSentences(ShortLabeledBugReport bugReport) {
        LinkedList<String> allS2RSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getSr()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getSr())) {
            String title = bugReport.getTitle().getValue();
            allS2RSentences.add(0, title);
        }
        return allS2RSentences;
    }

}