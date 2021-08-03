package sealab.burt.nlparser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.NLParser;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReportTitle;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class NLParserTest {

    private static List<ImmutablePair<Path, ShortLabeledBugReport>> bugReportPairs;

    @BeforeAll
    static void setUp() throws Exception {
        bugReportPairs = readTestBugReports();
    }

    private static List<ImmutablePair<Path, ShortLabeledBugReport>> readTestBugReports() throws Exception {

        Collection<File> bugReportFiles = FileUtils.listFiles(
                new File("../data/euler_data/4_s2r_in_bug_reports_oracle/"),
                new String[]{"xml"}, true);

        return bugReportFiles.stream().map(f -> {
            try {
                return new ImmutablePair<Path, ShortLabeledBugReport>(f.toPath(),
                        XMLHelper.readXML(ShortLabeledBugReport.class, f));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

    }

    private static String getAppName(File f) {
        String fileName = f.getName();

        int i = fileName.indexOf("#");
        String projectName = fileName;
        if (i != 1) {
            projectName = fileName.substring(0, i);
        }

        return projectName;
    }

    @Test
    void parseOBFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getOb()),
                s -> StringUtils.isNotBlank(s.getOb()));
    }


    @Test
    void parseSentencesTest() throws Exception {

        List<String> allSentences = Arrays.asList(
//                "enter fill up",
//                "Maximum Fillup cost under statistics is displaying incorrectly"
//                "Links within Wikipedia don't work any more" //should not be parsed
//                "Tried hitting recalculate in the statistics page but it still comes up with the incorrect Maximum Cost"
//                "Actual:success dialog was shown without any text"
//                "After I use the small plus to add a new record this symbol is gone"
//                "The plus symbol is gone."
//                "Transactions are deleted (!)"
//                "No new file is anywhere in Google Drive" //should not be parsed
//                "The color is unset."
//                "4. Any further attempts to start the app cause crashes" // cannot be parse
//                "Footer is removed (balance and date)."
//                "Context Menus disappear on device rotation"

                //EB
//                "3. Account is created and account list is shown"
//                "got calculated \"Fuel economy\" stats",
//                    "I input the weight",
//                    "go back"
                    "save fillup"
                //"go back to the last screen"
//                "I entered 23 gallons"
//                ,
//                "Entered all the fillup data and goto History Tab",
//                "Add Service Interval",
//                "Force Close if I press Back in Preferences screen",
//                "Now again select \" Export view to csv\"",
//                "When I perform this sequence of events, the app does not respond",
//                "change orientation"
        );

        parseSentences(allSentences);
    }

    private void parseSentences(List<String> allSentences) throws Exception {
        Float numErrors = 0.0f;
        Integer totalNumSentences = allSentences.size();

        for (String sentence : allSentences) {

            log.debug("* Parsing sentence:");
            log.debug(sentence);

            List<NLAction> actions = NLParser.parseText(BurtConfigPaths.nlParsersBaseFolder, null, sentence);

            if (actions == null || actions.isEmpty()) {
                numErrors++;
                log.warn("NOT PARSED\t" + sentence);
            } else {
                log.debug("* Parsed actions:");
                log.debug(actions.toString());
            }
        }


        log.debug(numErrors.toString());
        log.debug(totalNumSentences.toString());
        assertEquals(0, numErrors / totalNumSentences);
    }

    @Test
    void parseS2RFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getSr()),
                s -> StringUtils.isNotBlank(s.getSr()));
    }

    @Test
    void parseEBFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getEb()),
                s -> StringUtils.isNotBlank(s.getEb()));
    }

    private void testSentenceParsing(List<ImmutablePair<Path, ShortLabeledBugReport>> bugReportPairs,
                                     Function<ShortLabeledDescriptionSentence, Boolean> filter1,
                                     Function<ShortLabeledBugReportTitle, Boolean> filter2) throws Exception {
        String baseFolder = Path.of("..", "burt-nlparser").toString();
        float numErrors = 0.0f;
        int totalNumSentences = 0;
        for (ImmutablePair<Path, ShortLabeledBugReport> brPair : bugReportPairs) {

            log.debug("-------------------------------------------------------------");
            log.debug("Processing: " + brPair.left);

            ShortLabeledBugReport bugReport = brPair.right;
            String appName = getAppName(brPair.left.toFile());

            LinkedList<String> allSentences =
                    bugReport.getDescription().getAllSentences().stream()
                            .filter(filter1::apply)
                            .map(ShortLabeledDescriptionSentence::getValue)
                            .collect(Collectors.toCollection(LinkedList::new));

            if (filter2.apply(bugReport.getTitle())) {
                String title = bugReport.getTitle().getValue();
                allSentences.add(0, title);
            }

//            log.debug(appName + allSentences);

            totalNumSentences += allSentences.size();

            for (String sentence : allSentences) {

                log.debug("* Parsing sentence:");
                log.debug(sentence);

                List<NLAction> actions = NLParser.parseText(baseFolder, appName, sentence);

                if (actions == null || actions.isEmpty()) {
                    numErrors++;
                    log.warn("NOT PARSED\t" + sentence);
                } else {
                    log.debug("* Parsed actions:");
                    log.debug(actions.toString());
                }
            }

        }

        log.debug(Float.toString(numErrors));
        log.debug(Integer.toString(totalNumSentences));
        assertEquals(0, numErrors / totalNumSentences);
    }
}