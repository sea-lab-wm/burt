package sealab.burt.nlparser.euler.actions;

import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import seers.bugrepcompl.entity.codingparse.LabeledBugReport;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;
import seers.textanalyzer.entity.Sentence;

import java.util.HashMap;
import java.util.List;

public abstract class NLActionParser {

	// sentence --> [list of patterns that match the sentence]
	protected HashMap<Sentence, List<String>> sentencesMatched;

	public HashMap<Sentence, List<String>> getSentencesMatched() {
		return sentencesMatched;
	}

	public abstract List<BugScenario> parseActions(String systemName, ParsedBugReport bugReport) throws Exception;

	public List<BugScenario> parseActions(String systemName, LabeledBugReport bugReport) throws Exception {
		// not supported by default
		return null;
	}

}
