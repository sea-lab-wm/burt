package sealab.burt.nlparser.euler.actions;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import seers.appcore.csv.CSVHelper;
import seers.appcore.threads.ThreadExecutor;
import seers.appcore.threads.processor.ThreadParameters;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.entity.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class MainNLActionParser {

	// private static String baseFolder =
	// "C:/Users/ojcch/Documents/Repositories/Git/Android-Bug-Report-Reproduction/Data/BugReports-Data/xml";
	// private static String baseFolder = "test_data/nlactions";
	private static String baseFolder = "C:/Users/ojcch/Documents/Repositories/Git/Android-Bug-Report-Reproduction/Evaluation/Data/Bugs_selected_sample";
	private static String[] projects = { "selected" };

	// private static String[] projects = { "selected_bugs" };
	// private static String[] projects = { "aarddict", "adsdroid", "AnagramSolver",
	// "android-mileage", "ATimeTracker",
	// "BMI_Calculator", "cardgamescores", "car-report", "document-viewer",
	// "droid-comic-viewer", "droidweight",
	// "eyeCam", "gnucash-android", "netmbuddy", "notepad_banderlabs", "Olam",
	// "openintents", "openintents2",
	// "schedule-campfahrplan" };
	// private static String[] projects = { "car-report" };
	// private static String[] projects = { "ATimeTracker", "AnagramSolver",
	// "BMI_Calculator", "Olam", "adsdroid",
	// "cardgamescores", "document-viewer", "droid-comic-viewer", "droidweight",
	// "eyeCam", "gnucash-android",
	// "netmbuddy", "notepad_banderlabs", "openintents" };

	private static final Logger LOGGER = LoggerFactory.getLogger(MainNLActionParser.class);

	public static void main(String[] args) throws Exception {

		for (String project : projects) {
			LOGGER.debug("Processing project: " + project);

			File inFolder = new File(
					baseFolder + File.separator + "data_" + project + File.separator + "/short_parsed");
			File outFolder = new File(
					baseFolder + File.separator + "data_" + project + File.separator + "/short_nlactions");

			parseBugReports(project, inFolder, outFolder);

			LOGGER.debug("Done project: " + project);

		}

	}

	private static void parseBugReports(String project, File inFolder, File outputFolder) throws Exception {

		Collection<File> files = FileUtils.listFiles(inFolder, new String[] { "xml" }, false);

		if (!outputFolder.exists()) {
			FileUtils.forceMkdir(outputFolder);
		}

		ThreadParameters params = new ThreadParameters();
		params.addParam("outFolder", outputFolder);
		params.addParam("project", project);
		ThreadExecutor.executePaginated(new ArrayList<>(files), BugFileProcessor.class, params);

	}

	public static class BugFileProcessor extends ThreadProcessor {

		private List<File> files;
		private File outFolder;
		private String project;

		public BugFileProcessor(ThreadParameters params) {
			super(params);
			files = params.getListParam(File.class, ThreadExecutor.ELEMENTS_PARAM);
			outFolder = params.getParam(File.class, "outFolder");
			this.project = params.getStringParam("project");
		}

		@Override
		public void executeJob() throws Exception {

			for (File file : files) {

				LOGGER.debug("Processing " + file.getName());

				try {

					NLActionParser parser = new HeuristicsNLActionParser();

					ParsedBugReport bugReport = XMLHelper.readXML(ParsedBugReport.class, file);
					List<BugScenario> scenarios = parser.parseActions(project, bugReport);

					String baseName = FilenameUtils.getBaseName(file.getName());

					// -------------------------------------------------------------

					processScenarios(outFolder, parser, scenarios, baseName);

				} catch (Exception e) {
					System.err.println("Error for: " + file);
					e.printStackTrace();
				}
			}
		}

		public static void processScenarios(File outFolder2, NLActionParser parser, List<BugScenario> scenarios,
				String baseName) throws IOException {
			Gson gson = new Gson();
			File txtFile = new File(outFolder2 + File.separator + baseName + "_stncs.txt");
			Set<Entry<Sentence, List<String>>> data = parser.getSentencesMatched().entrySet();
			Function<Entry<Sentence, List<String>>, List<String>> sentenceFunction = entry -> {
				StringBuffer buffer = new StringBuffer();
				buffer.append("* [" + entry.getKey().getId() + "] ");
				buffer.append(entry.getKey().getText());
				buffer.append(" -> ");
				buffer.append(entry.getValue());

				List<String> nextLine = Arrays.asList(buffer.toString());
				return nextLine;
			};
			CSVHelper.writeCsv(txtFile, null, data, null, sentenceFunction, ';');

			// -------------------------------------------------------------

			File csvFile = new File(outFolder2 + File.separator + baseName + ".csv");

			List<String> header = Arrays.asList("scenario", "sntc_type", "sequence", "subject", "negation", "action",
					"object", "preposition", "object2", "parsing_class", "original_stnc", "sntc_id", "trace");

			Function<BugScenario, List<List<String>>> scenarioFunction = bugScenario -> {

				List<NLAction> actions = bugScenario.getActions();
				if (actions == null) {
					return null;
				}

				List<List<String>> allLines = new ArrayList<>();
				for (NLAction action : actions) {

					List<String> nextLine = new ArrayList<>();
					nextLine.add(bugScenario.getId().toString());
					nextLine.add(action.getType().toString());
					nextLine.add(action.getSequence().toString());
					nextLine.add(action.getSubject());
					nextLine.add(action.isActionNegated() == null ? "" : (action.isActionNegated() ? "not" : ""));
					nextLine.add(action.getAction());
					nextLine.add(action.getObject());
					nextLine.add(action.getPreposition());
					nextLine.add(action.getObject2());
					nextLine.add(action.getParsingClass());
					nextLine.add(action.getOriginalSentence());
					nextLine.add(action.getSentenceId());

					StackTrace trace = action.getTrace();
					String json = "";
					if (trace != null) {
						json = gson.toJson(trace);
					}
					nextLine.add(json);

					allLines.add(nextLine);
				}

				return allLines;

			};
			CSVHelper.writeCsvMultiple(csvFile, header, scenarios, null, scenarioFunction, ';');
		}

	}

}
