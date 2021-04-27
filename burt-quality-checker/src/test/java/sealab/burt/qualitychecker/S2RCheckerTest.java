package sealab.burt.qualitychecker;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.DataReader;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class S2RCheckerTest {

    @Test
    void checkS2R() throws Exception{

        String file = "../data/euler_data" +
                "/4_s2r_in_bug_reports_oracle/android-mileage#3.1.1_64.xml";
        ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, file);

//        System.out.println(bugReport);

        String appName = "Mileage";
        String appVersion = "3.1.1";
        String resourcesPath ="src/main/resources";
        S2RChecker checker = new S2RChecker(appName, appVersion, resourcesPath);

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
        }
    }

    @Test
    void checkS2RIdealScenarios() throws Exception{

        String appName = "Mileage";
        String appVersion = "3.1.1";
        String resourcesPath ="src/main/resources";
        S2RChecker checker = new S2RChecker(appName, appVersion, resourcesPath);

        List<BugScenario> bugScenarios = DataReader.readScenarios("../data/euler_data" +
                "/2_ideal_scenarios/perfect_scenarios/android-mileage#3.1.1_64.csv");

        LinkedList<NLAction> actions = bugScenarios.get(0).getActions();
        for (NLAction action : actions) {
            System.out.println(action);
            QualityResult qualityResult = checker.checkS2R(action);
            System.out.println(qualityResult);
        }

    }
}