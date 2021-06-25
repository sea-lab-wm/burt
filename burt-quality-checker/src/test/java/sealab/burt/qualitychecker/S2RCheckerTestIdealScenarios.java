package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.ActionType;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.DataReader;
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
class S2RCheckerTestIdealScenarios {

    private final Path perfectScenariosPath = Path.of("..", "data",
            "euler_data", "2_ideal_scenarios", "perfect_scenarios");

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
    void testSingleS2RChecking() throws Exception {
//        String s2r = "I entered 23 gallons";
        String s2r = "I closed the app";
        var app = new ImmutablePair<>("android-mileage", "3.1.1");

        S2RChecker checker = new S2RChecker(app.getKey(), app.getValue());

        QualityFeedback feedback = checker.checkS2R(s2r);

        log.debug(feedback.getAssessmentResults().toString());

    }

    @Test
    void testOneIdealScenario() throws Exception {
        String appName = "android-mileage";
        String appVersion = "3.1.1";
        boolean addPastTense = true;
        boolean includeSubject = true;
        boolean addPerfectTense = true;

        Path scenarioFile = Path.of("..", "data", "euler_data",
                "2_ideal_scenarios", "perfect_scenarios", "android-mileage#3.1.1_65.csv");

        parseScenario(appName, appVersion, addPastTense, includeSubject,
                scenarioFile, addPerfectTense, 5);
    }


    @Test
    void testAppsPast() throws Exception {
        boolean addPastTense = true;
        boolean includeSubject = true;
        boolean addPerfectTense = false;
        testApps(addPastTense, includeSubject, addPerfectTense);
    }

    @Test
    void testAppsPastPerfect() throws Exception {
        boolean addPastTense = true;
        boolean includeSubject = true;
        boolean addPerfectTense = true;
        testApps(addPastTense, includeSubject, addPerfectTense);
    }

    @Test
    void testAppsPresentPerfect() throws Exception {
        boolean addPastTense = false;
        boolean includeSubject = true;
        boolean addPerfectTense = true;
        testApps(addPastTense, includeSubject, addPerfectTense);
    }

    @Test
    void testAppsPresent() throws Exception {
        boolean addPastTense = false;
        boolean includeSubject = true;
        boolean addPerfectTense = false;
        testApps(addPastTense, includeSubject, addPerfectTense);
    }

    @Test
    void testAppsImperativePresent() throws Exception {
        boolean addPastTense = false;
        boolean includeSubject = false;
        boolean addPerfectTense = false;
        testApps(addPastTense, includeSubject, addPerfectTense);
    }

    private void testApps(boolean addPastTense, boolean includeSubject, boolean addPerfectTense) throws Exception {
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

            try (Stream<Path> stream = Files.walk(perfectScenariosPath)) {
                List<Path> scenarioFiles = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().contains(appName + "#" + appVersion))
                        .collect(Collectors.toList());

                for (Path scenarioFile : scenarioFiles) {

                    parseScenario(appName, appVersion, addPastTense, includeSubject,
                            scenarioFile, addPerfectTense, null);
                }
            }
        }
    }

    private void parseScenario(String appName, String appVersion,
                               boolean addPastTense, boolean includeSubject, Path scenarioFile, boolean addPerfectTense,
                               Integer sequence)
            throws Exception {

        log.debug("-------------------------------------------------------------");
        log.debug("Processing: " + scenarioFile);

        S2RChecker checker = new S2RChecker(appName, appVersion);

        List<BugScenario> bugScenarios = DataReader.readScenarios(scenarioFile.toString());

        List<NLAction> actions = bugScenarios.get(0).getActions().stream()
                .filter(nlAction -> nlAction.getType().equals(ActionType.SR))
                .collect(Collectors.toList());

        if (sequence != null) {
            actions = actions.stream()
                    .filter(nlAction -> nlAction.getSequence().equals(sequence))
                    .collect(Collectors.toList());
        }

        for (NLAction action : actions) {

            log.debug("Action: " + action);
            String s2r = UtilReporter.getActionString(action, false, false, includeSubject, addPastTense,
                    addPerfectTense);
            log.debug("S2R: " + s2r);
            QualityFeedback qualityResult = checker.checkS2R(s2r);

            List<S2RQualityCategory> assessmentResults = qualityResult.getAssessmentResults();
            log.debug(assessmentResults.toString());
            assertNotEquals(Collections.singletonList(S2RQualityCategory.LOW_Q_NOT_PARSED), assessmentResults);
        }
    }
}