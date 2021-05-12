package sealab.burt.nlparser.euler.actions;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.NLActionParser;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import seers.appcore.xml.XMLHelper;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;
import seers.bugrepcompl.entity.regularparse.ParsedBugReportDescription;
import seers.bugrepcompl.entity.regularparse.ParsedDescriptionSentence;
import seers.textanalyzer.entity.Sentence;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class HeuristicsNLActionParserTest {

	@Test
	public void testParseActions() throws Exception {

		Collection<File> xmlFiles = FileUtils.listFiles(new File("test_data/nlactions/test-bugs/"), null, false);
		NLActionParser parser = new HeuristicsNLActionParser();

		for (File file : xmlFiles) {
			System.out.println("Parsing " + file.getName());
			System.out.println();

			ParsedBugReport bugReport = XMLHelper.readXML(ParsedBugReport.class, file);

			ParsedBugReportDescription description = bugReport.getDescription();
			if (description != null) {

				List<ParsedDescriptionSentence> sentences = description.getAllSentences();

				for (ParsedDescriptionSentence sentence : sentences) {
					System.out.print("* [" + sentence.getId() + "] ");
					System.out.println(sentence.getValue());
				}
			}

			System.out.println();

			List<BugScenario> scenarios = parser.parseActions(null, bugReport);

			Set<Entry<Sentence, List<String>>> entrySet = parser.getSentencesMatched().entrySet();
			for (Entry<Sentence, List<String>> entry : entrySet) {
				System.out.print("* [" + entry.getKey().getId() + "] ");
				System.out.print(entry.getKey().getText());
				System.out.print(" -> ");
				System.out.println(entry.getValue());
			}

			System.out.println();
			System.out.println(scenarios);

			System.out.println();
			System.out.println("--------------------------");
		}

		// String filepath =
		// "test_data/nlactions/bugs/droid-comic-viewer_11.xml";
	}

}
