package sealab.burt.nlparser.euler.actions.pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.stanford.nlp.semgraph.SemanticGraph;
import net.quux00.simplecsv.CsvParser;
import net.quux00.simplecsv.CsvParserBuilder;
import net.quux00.simplecsv.CsvReader;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.ParserUtils;
import sealab.burt.nlparser.euler.actions.nl.ActionType;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.pattern.NLActionPatternParser;
import seers.bugreppatterns.utils.SentenceUtils;

import seers.textanalyzer.entity.Sentence;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import static org.junit.Assert.fail;

public class BasePPTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeuristicsNLActionParser.class);

	protected static final boolean debugEnabled = true;

	protected NLActionPatternParser parser;
	protected List<Pair<String, List<NLAction>>> positiveSentences;
	protected List<String> negativeSentences;

	private static HashMap<String, NLActionPatternParser> parsers;

	public BasePPTest() {
	}

	public BasePPTest(NLActionPatternParser parser) throws Exception {
		this.parser = parser;

		if (parsers == null) {
			parsers = HeuristicsNLActionParser.loadParsers(HeuristicsNLActionParser.DEFAULT_PARSERS_FILE);
		}

		Optional<Entry<String, NLActionPatternParser>> pair = parsers.entrySet().stream()
				.filter(p -> parser.getClass().getName().equals(p.getValue().getClass().getName())).findFirst();

		parser.setActType(ActionType.valueOf(pair.get().getKey().split("_")[1]));

		loadTestData(parser.getClass().getSimpleName());
	}

	protected void loadTestData(String fileName) {

		positiveSentences = new ArrayList<>();
		negativeSentences = new ArrayList<>();

		CsvParser csvParser = new CsvParserBuilder().multiLine(true).separator(';').build();
		try (CsvReader csvReader = new CsvReader(new InputStreamReader(
				new FileInputStream("test_data/nlactions/patterns/" + fileName + ".csv"), "Cp1252"), csvParser)) {

			List<List<String>> allLines = csvReader.readAll();

			Gson gson = new Gson();

			for (int idx = 0; idx < allLines.size(); idx++) {
				List<String> line = allLines.get(idx);

				try {
					String type = line.get(0);

					if (type.startsWith("//")) {
						continue;
					}

					if ("+".equals(type)) {
						List<NLAction> expVal = gson.fromJson(line.get(2), new TypeToken<List<NLAction>>() {
						}.getType());
						for (NLAction action : expVal) {
							action.setType(parser.getActType());
						}
						positiveSentences.add(new ImmutablePair<String, List<NLAction>>(line.get(1), expVal));
					} else {
						negativeSentences.add(line.get(1));
					}
				} catch (Exception e) {
					LOGGER.error("Error for line " + (idx + 1) + ": " + line, e);
					throw e;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error loding the test data for " + fileName, e);
		}
	}

	@Test
	public void testPositives() throws Exception {

		System.out.println();
		System.out.println("Testing parsers (positives): " + parser.getClass().getSimpleName());

		int numPasses = 0;
		for (Pair<String, List<NLAction>> sent : positiveSentences) {
			Sentence sentence = SentenceUtils.parseSentence("0", sent.getLeft());

			Sentence stncParsed = ParserUtils.parseSentenceWithDepsAndNoEnum(sentence);

			List<NLAction> actions = parser.parseSentence(stncParsed);

			List<NLAction> expActions = sent.getRight();

			if (actions != null && !actions.isEmpty() && expActions.equals(actions)) {
				numPasses++;
			} else {
				System.out.print("[FAILED]: ");
				System.out.print(sent.getLeft());
				System.out.println(" -> ");
				System.out.println("Exp: " + expActions);
				System.out.println("Act: " + actions);

				if (parser.debugEnabled) {
					SemanticGraph dependencies = stncParsed.getDependencies();
					System.out.println("Org: " + stncParsed.getText());
					System.out.println(dependencies);
				}

				System.out.println("--------");
			}

		}

		if (numPasses != positiveSentences.size()) {
			fail("Only " + numPasses + " out of " + positiveSentences.size() + " tests passed!");
		} else {
			System.out.println("Success: " + numPasses + " cases passed!");
		}
	}

	@Test
	public void testNegatives() throws Exception {

		System.out.println();
		System.out.println("Testing parser (negatives): " + parser.getClass().getSimpleName());

		int numPasses = 0;
		for (String sent : negativeSentences) {
			Sentence sentence = SentenceUtils.parseSentence("0", sent);

			Sentence stncParsed = ParserUtils.parseSentenceWithDepsAndNoEnum(sentence);

			List<NLAction> actions = parser.parseSentence(stncParsed);

			if (actions == null || actions.isEmpty()) {
				numPasses++;
			} else {

				System.out.print(sent);
				System.out.print(" -> ");
				System.out.println(actions);

				if (parser.debugEnabled) {
					SemanticGraph dependencies = stncParsed.getDependencies();
					System.out.println("Org: " + stncParsed.getText());
					System.out.println(dependencies);
				}

				System.out.println("--------");
			}
		}

		if (numPasses != negativeSentences.size()) {
			fail("Only " + numPasses + " out of " + negativeSentences.size() + " tests passed!");
		} else {
			System.out.println("Success: " + numPasses + " cases passed!");
		}
	}

}
