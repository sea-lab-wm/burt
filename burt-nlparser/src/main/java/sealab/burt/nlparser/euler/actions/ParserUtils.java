package sealab.burt.nlparser.euler.actions;

import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

	public static Sentence parseSentenceWithDepsAndNoEnum(Sentence sentence) {

		String text = sentence.getText();

		Pattern p = Pattern.compile("(?i)(\\w+) force closes");
		Matcher matcher = p.matcher(text);
		text = matcher.replaceFirst("$1 fails");

		p = Pattern.compile("(?i)(force close)");
		matcher = p.matcher(text);
		text = matcher.replaceFirst("crash");

		// System.out.println(text);

		// ---------------------------------
		
		// remove enumeration

		p = Pattern.compile("(?i)^(\\d+ )");
		matcher = p.matcher(text);
		text = matcher.replaceFirst("");

		// ---------------------------------

		List<Sentence> sentencesParsed = TextProcessor.processTextFullPipelineAndQuotes(text, true);
		Sentence stncParsed = sentencesParsed.get(0);
		stncParsed = removeEnumerations(sentencesParsed, stncParsed);
		stncParsed.setId(sentence.getId());

		return stncParsed;
	}

	private static Sentence removeEnumerations(List<Sentence> sentencesParsed, Sentence stncParsed) {
		// crop the sentence "1. bla bla" --> "bla bla"
		List<Token> tokens = stncParsed.getTokens();
		if (tokens.size() == 2 && sentencesParsed.size() > 1) {
			if ((tokens.get(0).getGeneralPos().equals("CD") || tokens.get(0).getGeneralPos().equals("LS"))
					&& tokens.get(1).getLemma().equals(".")) {
				stncParsed = sentencesParsed.get(1);
			} else if (tokens.get(0).getLemma().matches("\\d+\\w") && tokens.get(1).getLemma().equals(".")) {
				stncParsed = sentencesParsed.get(1);
			}
		}
		return stncParsed;
	}

}
