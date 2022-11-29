package sealab.burt.statematcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.javatuples.Triplet;

import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.statematcher.BugReportReader.BugReportType;
import sealab.burt.statematcher.BugReportReader.ElementIdentification;
import sealab.burt.statematcher.StateMatcher.MatcherType;

public @Slf4j class MainStateMatcher {

    public static void main(String[] args) throws Exception {
        log.info("Starting program");

        disableLogging();

        // ---------------------

        Path groundTruthFilePath = Path.of("..", "data", "BuggyApplicationState.csv");
        Path outputFilePath = Path.of("..", "matcher_evaluation", "retrieval_data.csv");

        // ---------------------

        // read/set list of bugs
        List<Triplet<String, String, String>> allBugs = getAllBugs();

        // read/set list of configurations
        List<Configuration> allConfigs = getAllConfigs();

        // read ground truth states
        HashMap<String, List<String>> bugGroundTruthStates = readGroundTruthStates(groundTruthFilePath);

        // ------------------------

        List<String[]> retrievalData = new ArrayList<>();
        retrievalData.add(new String[] {"config", "bug_id", "app_name_version","ground_truth_states", "retrieved_states", "num_states"});

        // for each bug
        for (Triplet bugInfo : allBugs) {

            String appName = bugInfo.getValue1().toString();
            String appVersion = bugInfo.getValue2().toString();
            String bugID = bugInfo.getValue0().toString();

            List<String> gtStates = bugGroundTruthStates.get(bugID);

            // for each configuration
            for (Configuration config : allConfigs) {
                
              try {
                  // run configuration to get retrieval results
                  RetrievalResults retrievalResults = config.run(bugInfo);
  
                  log.info(retrievalResults.toString());
  
                  // collect retrieval results
                  retrievalData.add(new String[] {config.toString(), bugID, String.format("%s#%s", appName, appVersion), gtStates.toString(), retrievalResults.getStates().toString(), retrievalResults.getNumberOfStates().toString()});
              } catch (Exception e) {
                log.error(String.format("Error for config and bug: %s - %s", config, bugID), e);
              }

            }

        }

        log.debug(String.format("Saving retrieval data on %s. # of records: $s",  outputFilePath.toFile().getAbsolutePath(), retrievalData.size()));

        //save retrieval data
        try(CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath.toFile()))) {
            writer.writeAll(retrievalData);
        }

    }

    private static HashMap<String, List<String>> readGroundTruthStates(Path groundTruth) throws Exception {
        HashMap<String, List<String>> mapBugToState = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(groundTruth)));) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(";");
                String bugId = item[0];
                String states = item[4];
                if (states.contains("/")) {

                    String clean_states = states.substring(1, states.length() - 1);
                    List<String> stateList = List.of(clean_states.split("/"));

                    mapBugToState.put(bugId, stateList);
                } else {
                    List<String> bugList = new ArrayList<>();
                    bugList.add(states);
                    mapBugToState.put(bugId, bugList);
                }
            }

            return mapBugToState;
        }
    }

    private static List<Configuration> getAllConfigs() {
        return Arrays.asList(
                new Configuration(ElementIdentification.MANUAL_EL,
                        BugReportType.ORIGINAL_BR,
                        MatcherType.PHRASES_SBERT_BURT));
    }

    private static List<Triplet<String, String, String>> getAllBugs() {
        return Arrays.asList(
                new Triplet<>("2", "familyfinance", "1.5.5-DEBUG")
                ,
                new Triplet<>("8", "trickytripper", "1.6.0"),
                new Triplet<>("10", "files", "1.0.0-beta.11"),
                new Triplet<>("18", "calendula", "2.5.7"),
                new Triplet<>("19", "streetcomplete", "5.2"),
                new Triplet<>("44", "omninotes", "5.5.2"),
                new Triplet<>("53", "markor", "2.3.1"),
                new Triplet<>("71", "kiss", "3.13.5"),
                new Triplet<>("117", "openfoodfacts", "2.9.8"),
                new Triplet<>("128", "andotp", "0.7.1.1-dev"),
                new Triplet<>("129", "andotp", "0.7.0-dev"),
                new Triplet<>("130", "andotp", "0.6.3.1-dev"),
                new Triplet<>("135", "commons", "2.9.0-debug"),
                new Triplet<>("191", "anuto", "0.2-1"),
                new Triplet<>("201", "inaturalist", "1.5.1"),
                new Triplet<>("206", "gnucash", "2.1.3"),
                new Triplet<>("209", "gnucash", "2.2.0"),
                new Triplet<>("256", "gnucash", "2.1.4"),
                new Triplet<>("1073", "focus", "5.2"),
                new Triplet<>("1096", "inaturalist", "1.13.9"),
                new Triplet<>("1146", "gpstest", "3.8.0"),
                new Triplet<>("1147", "gpstest", "3.0.0"),
                new Triplet<>("1151", "gpstest", "3.0.1"),
                new Triplet<>("1202", "createpdf", "6.6.0"),
                new Triplet<>("1205", "createpdf", "8.5.7"),
                new Triplet<>("1207", "andotp", "0.4.0.1"),
                new Triplet<>("1223", "gnucash", "2.2.0"),
                new Triplet<>("1224", "gnucash", "2.1.3"),
                new Triplet<>("1226", "gnucash", "2.1.4"),
                new Triplet<>("1299", "fieldbook", "4.3.3"),
                new Triplet<>("1399", "phimpme", "1.4.0"),
                new Triplet<>("1406", "phimpme", "1.4.0"),
                new Triplet<>("1430", "fastnfitness", "0.19.0.1"),
                new Triplet<>("1441", "anglerslog", "1.2.5"),
                new Triplet<>("1445", "anglerslog", "1.3.1"),
                new Triplet<>("1481", "hex", "0.1.0")
                );
    }

    private static void disableLogging() {
        Logger.getLogger("sealab.burt.qualitychecker.actionparser").setLevel(Level.OFF);
        Logger.getLogger("sealab.burt.qualitychecker.S2RChecker").setLevel(Level.OFF);
        Logger.getLogger("sealab.burt.qualitychecker.graph").setLevel(Level.OFF);
        Logger.getLogger("edu.stanford.nlp").setLevel(Level.OFF);
    }

}
