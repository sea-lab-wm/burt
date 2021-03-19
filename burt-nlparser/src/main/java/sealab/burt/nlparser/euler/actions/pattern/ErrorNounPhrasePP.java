package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.utils.SentenceUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ErrorNounPhrasePP extends NLActionPatternParser {

	final public static Set<String> ERROR_NOUNS = JavaUtils.getSet("crash", "error", "errore", "exception", "fail",
			"failure", "fault", "glitch", "npe", "npes", "segfault", "segmentation");

	public ErrorNounPhrasePP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) {
		SemanticGraph dependencies = sentence.getDependencies();

		IndexedWord firstRoot = dependencies.getFirstRoot();

		// check for noun
		if (TextProcessor.checkGeneralPos(firstRoot.tag(), "NN")
				&& SentenceUtils.stringEqualsToAnyToken(ERROR_NOUNS, firstRoot.lemma())) {
			// build the action
			NLAction nlAction = new NLAction(null, "app", "crash", null, actType);
			return Arrays.asList(nlAction);
		}

		return null;
	}

}
