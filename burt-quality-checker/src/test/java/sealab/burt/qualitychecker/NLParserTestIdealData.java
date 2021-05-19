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
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class NLParserTestIdealData {

    private final Path perfectScenariosPath = Path.of("..", "data",
            "euler_data", "2_ideal_scenarios", "perfect_scenarios");
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
                add(new ImmutablePair<>("gnucash-android", "2.1.3"));
                add(new ImmutablePair<>("droidweight", "1.5.4"));
                add(new ImmutablePair<>("android-mileage", "3.1.1"));
            }
        };
        String resourcesPath = "src/main/resources";
        String parsersBaseFolder = Path.of("..", "burt-nlparser").toString();

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

                    S2RChecker checker = new S2RChecker(appName, appVersion, resourcesPath, parsersBaseFolder);

                    ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, bugReportFile.toFile());

                    LinkedList<String> allSentences =
                            bugReport.getDescription().getAllSentences().stream()
                                    .filter(s -> StringUtils.isNotBlank(s.getSr()))
                                    .map(ShortLabeledDescriptionSentence::getValue)
                                    .collect(Collectors.toCollection(LinkedList::new));

                    if (StringUtils.isNotBlank(bugReport.getTitle().getSr())) {
                        String title = bugReport.getTitle().getValue();
                        allSentences.add(0, title);
                    }

                    for (String s2rSentence : allSentences) {

                        log.debug("Action: " + s2rSentence);
                        log.debug("S2R: " + s2rSentence);
                        QualityResult qualityResult = checker.checkS2R(s2rSentence);
                        log.debug(qualityResult.toString());
                        if(QualityResult.Result.NO_PARSED.equals(qualityResult.getResult()))
                            log.warn(QualityResult.Result.NO_PARSED.toString());
                    }
                }
            }
        }
        //-------------------

      /*  String file = "../data/euler_data" +
                "/4_s2r_in_bug_reports_oracle/android-mileage#3.1.1_64.xml";
        ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, file);

//        System.out.println(bugReport);

        String appName = "Mileage";
        String appVersion = "3.1.1";
        String resourcesPath = "src/main/resources";
        String parsersBaseFolder = Path.of("..", "burt-nlparser").toString();
        S2RChecker checker = new S2RChecker(appName, appVersion, resourcesPath, parsersBaseFolder);

        LinkedList<String> allSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getSr()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getOb())) {
            String title = bugReport.getTitle().getValue();
            allSentences.add(0, title);
        }

        for (String sentence : allSentences) {
            System.out.println(sentence);
            QualityResult qualityResult = checker.checkS2R(sentence);
            System.out.println(qualityResult);
        }*/
    }

    @Test
    void testIdealScenario() throws Exception {
        String appName = "android-mileage";
        String appVersion = "3.1.1";
        String resourcesPath = "src/main/resources";
        String parsersBaseFolder = Path.of("..", "burt-nlparser").toString();
        boolean addPastTense = true;
        boolean includeSubject = true;
        boolean addPerfectTense = true;

        Path scenarioFile = Path.of("..", "data", "euler_data",
                "2_ideal_scenarios", "perfect_scenarios", "android-mileage#3.1.1_65.csv");

        parseScenario(appName, appVersion, resourcesPath, parsersBaseFolder, addPastTense, includeSubject,
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
                add(new ImmutablePair<>("gnucash-android", "2.1.3"));
                add(new ImmutablePair<>("droidweight", "1.5.4"));
                add(new ImmutablePair<>("android-mileage", "3.1.1"));
            }
        };
        String resourcesPath = "src/main/resources";
        String parsersBaseFolder = Path.of("..", "burt-nlparser").toString();

        //------------------------------------

        for (Pair<String, String> app : apps) {
            String appName = app.getKey();
            String appVersion = app.getValue();

            try (Stream<Path> stream = Files.walk(perfectScenariosPath)) {
                List<Path> scenarioFiles = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().contains(appName + "#" + appVersion))
                        .collect(Collectors.toList());

                for (Path scenarioFile : scenarioFiles) {

                    parseScenario(appName, appVersion, resourcesPath, parsersBaseFolder, addPastTense, includeSubject,
                            scenarioFile, addPerfectTense, null);
                }
            }
        }
    }

    private void parseScenario(String appName, String appVersion, String resourcesPath, String parsersBaseFolder,
                               boolean addPastTense, boolean includeSubject, Path scenarioFile, boolean addPerfectTense,
                               Integer sequence)
            throws Exception {
        log.debug("-------------------------------------------------------------");
        log.debug("Processing: " + scenarioFile);

        S2RChecker checker = new S2RChecker(appName, appVersion, resourcesPath, parsersBaseFolder);

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
            QualityResult qualityResult = checker.checkS2R(s2r);
            log.debug(qualityResult.toString());
            assertNotEquals(QualityResult.Result.NO_PARSED, qualityResult.getResult());
        }
    }
}