package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.util.LinkedList;
import java.util.stream.Collectors;

public @Slf4j
class OBCheckerTest {

    @Test
    void checkOb() throws Exception {

        String file = "../data/euler_data" +
                "/4_s2r_in_bug_reports_oracle/android-mileage#3.1.1_64.xml";
        ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, file);

//        System.out.println(bugReport);

        String appName = "Gnucash";
        String appVersion = "2.0.4";
        OBChecker checker = new OBChecker(appName, appVersion);

        LinkedList<String> allSentences =
                bugReport.getDescription().getAllSentences().stream()
                        .filter(s -> StringUtils.isNotBlank(s.getOb()))
                        .map(ShortLabeledDescriptionSentence::getValue)
                        .collect(Collectors.toCollection(LinkedList::new));

        if (StringUtils.isNotBlank(bugReport.getTitle().getOb())) {
            String title = bugReport.getTitle().getValue();
            allSentences.add(0, title);
        }

        for (String sentence : allSentences) {
            QualityResult result = checker.checkOb(sentence);
            log.debug(sentence);
            log.debug(result.toString());
        }

    }
}