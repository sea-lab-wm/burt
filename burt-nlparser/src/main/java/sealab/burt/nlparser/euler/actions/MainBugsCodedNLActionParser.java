package sealab.burt.nlparser.euler.actions;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import seers.appcore.threads.ThreadExecutor;
import seers.appcore.threads.processor.ThreadParameters;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.JavaUtils;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.codingparse.LabeledBugReport;
import seers.bugrepcompl.entity.shortcodingparse.ShortLabeledBugReport;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainBugsCodedNLActionParser {

	/*private static final String BASE_FOLDER = "C:\\Users\\ojcch\\Documents\\Repositories\\Git\\Android-Bug-Report" +
			"-Reproduction\\Bug_collection\\data_collected\\";
	private static String inFoldPath = BASE_FOLDER + "extracted_annotated_bugs";
	private static String outFoldPath = BASE_FOLDER + "nlactions";*/
	private static String inFoldPath = "C:\\Users\\ojcch\\Documents\\Repositories\\Git\\Android-Bug-Report" +
			"-Reproduction\\EulerEvaluation\\Data\\3_identified_s2r_in_bug_reports_test";
	private static String outFoldPath = "C:\\Users\\ojcch\\Documents\\Repositories\\Git\\Android-Bug-Report" +
			"-Reproduction\\EulerEvaluation\\Data\\4_parsed_s2r2";

	private static Set<String> projects = JavaUtils.getSet();

	private static final Logger LOGGER = LoggerFactory.getLogger(MainBugsCodedNLActionParser.class);

	public static void main(String[] args) throws Exception {

		File inFolder = new File(inFoldPath);
		File outFolder = new File(outFoldPath);

		File[] files = inFolder.listFiles(pathname -> {

			if (projects.isEmpty()) {
				return true;
			}

			String fileName = pathname.getName();

			int i = fileName.indexOf("#");
			String projectName = fileName;
			if (i != 1) {
				projectName = fileName.substring(0, i);
			}

			return projects.contains(projectName);
		});

		LOGGER.debug("Processing " + files.length + " bug reports");

		ThreadParameters params = new ThreadParameters();
		params.addParam("outFolder", outFolder);
		ThreadExecutor.executePaginated(Arrays.asList(files), BugFileProcessor.class, params, 1, 10);

		LOGGER.debug("Done!");

	}

	public static class BugFileProcessor extends ThreadProcessor {

		private List<File> files;
		private File outFolder;

		public BugFileProcessor(ThreadParameters params) {
			super(params);
			files = params.getListParam(File.class, ThreadExecutor.ELEMENTS_PARAM);
			outFolder = params.getParam(File.class, "outFolder");
		}

		@Override
		public void executeJob() throws Exception {

			for (File file : files) {

				LOGGER.debug("Processing " + file.getName());

				try {

					NLActionParser parser = new HeuristicsNLActionParser();

					ShortLabeledBugReport bugReport = XMLHelper.readXML(ShortLabeledBugReport.class, file);

					// ------------

					String baseName = FilenameUtils.getBaseName(file.getName());
					int i = baseName.lastIndexOf("_");
					String appNameVersion = baseName.substring(0, i);
					int j = appNameVersion.lastIndexOf("#");
					String project = appNameVersion.substring(0, j);
					// ---------------

					LabeledBugReport labeledBugReport = bugReport.toLabeledBugReport();

					List<BugScenario> scenarios = parser.parseActions(project, labeledBugReport);

					// -------------------------------------------------------------

					MainNLActionParser.BugFileProcessor.processScenarios(outFolder, parser, scenarios, baseName);

				} catch (Exception e) {
					System.err.println("Error for: " + file);
					e.printStackTrace();
				}
			}
		}

	}

}
