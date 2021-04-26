package sealab.burt.qualitychecker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NLParserTest {

    private static List<ImmutablePair<String, ShortLabeledBugReport>> bugReportPairs;

    @BeforeAll
    static void setUp() throws Exception {
        bugReportPairs = readTestBugReports();
    }

    @Test
    void parseOBFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getOb()));
    }

    @Test
    void parseS2RFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getSr()));
    }

    @Test
    void parseEBFromBugReports() throws Exception {
        testSentenceParsing(bugReportPairs, s -> StringUtils.isNotBlank(s.getEb()));
    }

    private void testSentenceParsing(List<ImmutablePair<String, ShortLabeledBugReport>> bugReportPairs,
                                     Function<ShortLabeledDescriptionSentence, Boolean> filter) throws Exception {
        float  numErrors = 0;
        int totalNumSentences = 0;
        for (ImmutablePair<String, ShortLabeledBugReport> brPair : bugReportPairs) {

            ShortLabeledBugReport bugReport = brPair.right;
            String appName = brPair.left;

            LinkedList<String> allOBSentences =
                    bugReport.getDescription().getAllSentences().stream()
                            .filter(filter::apply)
                            .map(ShortLabeledDescriptionSentence::getValue)
                            .collect(Collectors.toCollection(LinkedList::new));

            if (StringUtils.isNotBlank(bugReport.getTitle().getOb())) {
                String title = bugReport.getTitle().getValue();
                allOBSentences.add(0, title);
            }

//            System.out.println(appName + allOBSentences);

            totalNumSentences += allOBSentences.size();

            for (String sentence : allOBSentences) {

                System.out.println("* Parsing sentence:");
                System.out.println(sentence);

                List<NLAction> actions = NLParser.parseText(appName, sentence);

                if(actions == null || actions.isEmpty())
                    numErrors++;
//                assertNotNull(actions);
//                assertFalse(actions.isEmpty());

                System.out.println("* Parsed actions:");
                System.out.println(actions);
            }

        }

        System.out.println(numErrors);
        System.out.println(totalNumSentences);
        assertEquals(0, numErrors/totalNumSentences);
    }

    private static List<ImmutablePair<String, ShortLabeledBugReport>> readTestBugReports() throws Exception {

        Collection<File> bugReportFiles = FileUtils.listFiles(
                new File("../data/euler_data/4_s2r_in_bug_reports_oracle/"),
                new String[]{"xml"}, true);

        return bugReportFiles.stream().map(f -> {
            try {
                return new ImmutablePair<String, ShortLabeledBugReport>(getAppName(f),
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
}