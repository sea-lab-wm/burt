package sealab.burt.statematcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import org.javatuples.Triplet;

import lombok.extern.slf4j.Slf4j;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;

public @Slf4j class BugReportReader {

    enum ElementIdentification {
        BEE_EL, MANUAL_EL
    }

    enum BugReportType {
        ORIGINAL_BR, PERFECT_BR
    }

    private final Path parsedBugReportsPath = Path.of("..", "data", "MarkedBugReports");

    static LinkedHashMap<Triplet<ElementIdentification, BugReportType, String>, ShortLabeledBugReport> bugRepCache = new LinkedHashMap<>();

    public ShortLabeledBugReport read(Triplet bugInfo, ElementIdentification elIdent, BugReportType brType)
            throws Exception {

        ShortLabeledBugReport bugReport = bugRepCache
                .get(new Triplet<ElementIdentification, BugReportType, String>(null, null, null));
        if (bugReport != null)
            return bugReport;

        if (ElementIdentification.MANUAL_EL == elIdent && BugReportType.ORIGINAL_BR == brType) {
            Path bugFolder = Paths.get(parsedBugReportsPath.toString(), "Bug" + bugInfo.getValue0());
            Path bugReportFile = Files.list(bugFolder).findFirst().get();
            bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, bugReportFile.toFile());
            return bugReport;
        }
        throw new RuntimeException(String.format("Reader not supported: %s - %s", elIdent, brType));
    }
}
