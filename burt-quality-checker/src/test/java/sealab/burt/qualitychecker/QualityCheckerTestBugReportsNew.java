package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.opencsv.CSVWriter;

import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import seers.appcore.utils.JavaUtils;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledDescriptionSentence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class QualityCheckerTestBugReportsNew {

//    private final Path parsedBugReportsPath = Path.of("..", "data",
//            "euler_data", "4_s2r_in_bug_reports_oracle");
//    private final Path parsedBugReportsPath = Path.of("../..", "data",
//            "BugReportsS2R", "Bug2");
	private final Path parsedBugReportsPath =  Path.of("..", "data", "MarkedBugReports");
    private final Path resultsPath = Path.of("..", "data", "MatchedStates");




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

        // read ground truth state
        Path groundTruth =  Path.of("..", "data", "BuggyApplicationState.csv");
        HashMap<String, List<String>> mapBugToState = new HashMap<>();
        try {

            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(groundTruth)));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(";");
                String bugId = item[0];
                String states = item[4];
                if (states.contains("/")){

                    String clean_states = states.substring( 1, states.length() - 1);
                    List<String> stateList = List.of(clean_states.split("/"));

                    mapBugToState.put(bugId, stateList);
                }else{
                    List<String> bugList = new ArrayList<>();
                    bugList.add(states);
                    mapBugToState.put(bugId, bugList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<Triplet<String, String, String>> ALL_SYSTEMS = JavaUtils.getSet(
    	  		new Triplet<>("2", "familyfinance", "1.5.5-DEBUG"),
    	  		new Triplet<>("8", "trickytripper", "1.6.0"),
    	  		new Triplet<>("10","files", "1.0.0-beta.11"),
                new Triplet<>("18","calendula", "2.5.7"),
    	  		new Triplet<>("19","streetcomplete", "5.2"),
    	  		new Triplet<>("44","omninotes", "5.5.2"),
    	  		new Triplet<>("53","markor", "2.3.1"),
    	  		new Triplet<>("71","kiss", "3.13.5"),
    	  		new Triplet<>("117","openfoodfacts", "2.9.8"),
    	  		new Triplet<>("128","andotp", "0.7.1.1-dev"),
    	  		new Triplet<>("129","andotp", "0.7.0-dev"),
    	  		new Triplet<>("130","andotp", "0.6.3.1-dev"),
    	  		new Triplet<>("135","commons", "2.9.0-debug"),
    	  		new Triplet<>("191","anuto", "0.2-1"),
    	  		new Triplet<>("201","inaturalist", "1.5.1"),
    	  		new Triplet<>("206","gnucash", "2.1.3"),
    	  		new Triplet<>("209","gnucash", "2.2.0"),
    	  		new Triplet<>("256","gnucash", "2.1.4"),
    	  		new Triplet<>("1073","focus", "5.2"),
    	  		new Triplet<>("1096","inaturalist", "1.13.9"),
    	  		new Triplet<>("1146","gpstest", "3.8.0"),
    	  		new Triplet<>("1147","gpstest", "3.0.0"),
    	  		new Triplet<>("1151","gpstest", "3.0.1"),
    	  		new Triplet<>("1202","createpdf", "6.6.0"),
    	  		new Triplet<>("1205","createpdf", "8.5.7"),
    	  		new Triplet<>("1207","andotp", "0.4.0.1"),
    	  		new Triplet<>("1223","gnucash", "2.2.0"),
    	  		new Triplet<>("1224","gnucash", "2.1.3"),
    	  		new Triplet<>("1226","gnucash", "2.1.4"),
    	  		new Triplet<>("1299","fieldbook", "4.3.3"),
    	  		new Triplet<>("1399","phimpme", "1.4.0"),
    	  		new Triplet<>("1406","phimpme", "1.4.0"),
    	  		new Triplet<>("1430","fastnfitness", "0.19.0.1"),
    	  		new Triplet<>("1441","anglerslog", "1.2.5"),
    	  		new Triplet<>("1445","anglerslog", "1.3.1"),
    	  		new Triplet<>("1481","hex", "0.1.0")
    	  );
    	
//        List<Pair<String, String>> apps = new LinkedList<>() {
//            {
//                add(new ImmutablePair<>("gnucash-android", "2.1.3"));
//                add(new ImmutablePair<>("droidweight", "1.5.4"));
//                add(new ImmutablePair<>("android-mileage", "3.1.1"));
//            	  add(new ImmutablePair<>("familyfinance", "1.5.5-DEBUG")),
//            	  add(new ImmutablePair<>("familyfinance", "1.5.5-DEBUG"));
//            	  add(new ImmutablePair<>("trickytripper", "1.6.0"));
//            }
//        };
        
    	List<String[]> data = new ArrayList<>();
        List<String[]> dataS2R = new ArrayList<>();
        dataS2R.add(new String[] {"bugID", "appName","matchedStates", "rankedPredictedStatesS2R"});
        List<String[]> dataOB = new ArrayList<>();
        dataOB.add(new String[] {"bugID", "appName","matchedStates", "rankedPredictedStatesOB"});

        HashMap<String, List<Integer>> candidateStateMap = new HashMap<>();

        List<String[]> matchedStateMapData = new ArrayList<>();



        //------------------------------------
        //for (Pair<String, String> app : apps) {
        for (Triplet<String, String, String> app: ALL_SYSTEMS) {
            String appName = app.getValue1();
            String appVersion = app.getValue2();
            String bugID = app.getValue0();

            List<String> matchedStates = mapBugToState.get(bugID);
            log.debug("bugID: " + bugID);

            List<String> candidateStatesS2R = new ArrayList<>();
            List<String> candidateStatesOB = new ArrayList<>();


            try (Stream<Path> stream = Files.walk(Paths.get(String.valueOf(parsedBugReportsPath),"Bug" + bugID))) {
                List<Path> scenarioFiles = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().contains(appName + "#" + appVersion))
                        .collect(Collectors.toList());

                for (Path bugReportFile : scenarioFiles) {



                    log.debug("-------------------------------------------------------------");
                    log.debug("Processing: " + bugReportFile);

                    NewS2RChecker s2RChecker = new NewS2RChecker(appName, appVersion, bugID);

                    ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class,
                            bugReportFile.toFile());

                    LinkedList<String> allS2RSentences = getS2RSentences(bugReport);
                    QualityFeedback qualityResultS2R = s2RChecker.checkS2R(allS2RSentences);
                    List<S2RQualityAssessment> assessmentResults = qualityResultS2R.getAssessmentResults();

                    S2RQualityAssessment assessmentResult = assessmentResults.get(0);
                    if (assessmentResult.getCategory() != S2RQualityCategory.LOW_Q_VOCAB_MISMATCH) {


                        List<AppStep> matchedSteps = assessmentResult.getMatchedSteps();

                        for (AppStep step : matchedSteps) {
                            GraphTransition transition = step.getTransition();
                            GraphState targetState = transition.getTargetState(); // get target state of the matched step as the result
                            candidateStatesS2R.add(targetState.getUniqueHash().toString()); // with ranking


                        }
                        dataS2R.add(new String[] {bugID, appName, String.valueOf(matchedStates), String.valueOf(candidateStatesS2R)});
                    }



//                        log.debug("S2R quality results: " + assessmentResults.toString());


//                        if (Collections.singletonList(S2RQualityCategory.LOW_Q_NOT_PARSED).equals(assessmentResults))
//                            log.warn(S2RQualityCategory.LOW_Q_NOT_PARSED.toString());




                    /*
                    for (String s2rSentence : allS2RSentences) {

                        log.debug("S2R sentence: " + s2rSentence);

                        QualityFeedback qualityResult = s2RChecker.checkS2R(s2rSentence);

                        List<S2RQualityAssessment> assessmentResults = qualityResult.getAssessmentResults();

                        S2RQualityAssessment assessmentResult = assessmentResults.get(0);
                        if (assessmentResult.getCategory() == S2RQualityCategory.LOW_Q_VOCAB_MISMATCH){
                            continue;
                        }
                        List<AppStep> matchedSteps = assessmentResult.getMatchedSteps();
                        List<Integer> uniqueHashesS2R = new ArrayList<>();

                        for (AppStep step : matchedSteps) {
                            GraphTransition transition = step.getTransition();
                            GraphState targetState = transition.getTargetState();
                            if (matchedStates.contains(targetState.getUniqueHash().toString())) {
                                if (!matchedStateMap.containsKey(bugID)) {
                                    matchedStateMap.put(bugID, 1);
                                }
//                                }else{
//                                    matchedStateMap.put(bugID, matchedStateMap.get(bugID) + 1);
//                                }
                            }
                            uniqueHashesS2R.add(targetState.getUniqueHash());

                        }
                        dataS2R.add(new String[] {bugID, appName, s2rSentence, String.valueOf(uniqueHashesS2R)});


//                        log.debug("S2R quality results: " + assessmentResults.toString());


//                        if (Collections.singletonList(S2RQualityCategory.LOW_Q_NOT_PARSED).equals(assessmentResults))
//                            log.warn(S2RQualityCategory.LOW_Q_NOT_PARSED.toString());
                    }
                     */
//
//                    //---------------------------------------

                    NewOBChecker obChecker = new NewOBChecker(appName, appVersion);

                    LinkedList<String> allObSentences = getObSentences(bugReport);


                    log.debug("OB sentence: " + allObSentences);
                    QualityResult qualityResult = obChecker.checkOb(allObSentences, bugID);
                    log.debug("Matched States OB: " + qualityResult.getMatchedStates());
                    log.debug("OB quality results: " + qualityResult.getResult());
//                        List<Integer> uniqueHashes = new ArrayList<>();
                    for (GraphState state: qualityResult.getMatchedStates()){
                        candidateStatesOB.add(state.getUniqueHash().toString()); // with ranking
                    }
                    dataOB.add(new String[] {bugID, appName, String.valueOf(matchedStates), String.valueOf(candidateStatesOB)});



                }
            }
        }

//        for (Map.Entry<String,Integer> entry : matchedStateMap.entrySet()){
//            matchedStateMapData.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
//        }

        new File(String.valueOf(resultsPath)).mkdirs();
//        writeMathcedStates(resultsPath + File.separator + "matched_states.csv", data);
        writeMathcedStates(resultsPath + File.separator + "matched_states_s2r.csv", dataS2R);
        writeMathcedStates(resultsPath + File.separator + "matched_states_ob.csv", dataOB);

//        writeMathcedStates(resultsPath + File.separator + "matched_states_stat.csv", matchedStateMapData);

    }
 
    // https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
    public static void writeMathcedStates(String filePath, List<String[]> data)
    {
      
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
      
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);
      
            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
      
            // create a List which contains String array
            
            writer.writeAll(data);
      
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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