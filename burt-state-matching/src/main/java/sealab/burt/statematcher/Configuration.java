package sealab.burt.statematcher;

import java.util.List;

import org.javatuples.Triplet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.statematcher.BugReportReader.BugReportType;
import sealab.burt.statematcher.BugReportReader.ElementIdentification;
import sealab.burt.statematcher.StateMatcher.MatcherType;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;

@Getter @AllArgsConstructor @Slf4j 
public class Configuration {

    ElementIdentification elIdent;
    BugReportType brType;
    MatcherType mrType;

    public RetrievalResults run(Triplet bugInfo) throws Exception {
        log.info(String.format("Running execution: %s for bug: %s", this.toString(),
                bugInfo.getValue0()));

        BugReportReader reader = new BugReportReader();
        ShortLabeledBugReport bugReport = reader.read(bugInfo, elIdent, brType);

        StateMatcher matcher = new StateMatcher();
        RetrievalResults retrievedStates = matcher.match(bugInfo, bugReport, mrType);

        return retrievedStates;
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s", elIdent, brType, mrType);
    }

}
