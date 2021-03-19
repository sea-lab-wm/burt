package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import seers.bugreppatterns.pattern.ob.NegativeTerms;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NegativeAdjOrAdvPP extends NLActionPatternParser {

	public NegativeAdjOrAdvPP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	protected List<NLAction> processSentence(Sentence sentence) throws Exception {

		SemanticGraph dependencies = sentence.getDependencies();

		IndexedWord verbToken = dependencies.getFirstRoot();

		return processVerb(dependencies, verbToken, sentence.getQuotes());

	}

	private List<NLAction> processVerb(SemanticGraph dependencies, IndexedWord verbToken,
			HashMap<String, List<Sentence>> quoteMap) {

		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) {
			return actions;
		}

		// the object modified by the verb
		Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "dobj");

		IndexedWord objToken = null;
		boolean isObjNegAdj = false;
		Set<String> adjectives = NegativeTerms.ADJECTIVES;
		if (objRelation != null) {
			objToken = objRelation.second;
			isObjNegAdj = checkSetOfLemmas(quoteMap, objToken, adjectives);
		}

		String subject = getSubject(dependencies, verbToken);

		if (subject == null) {
			return actions;
		}

		// build the action
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
				getObject(dependencies, objToken), actType);
		setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
		setActionNegation(dependencies, verbToken, nlAction);

		if (!isObjNegAdj) {
			String obj2 = nlAction.getObject2();
			if (checkStringWithSetOfLemmas(quoteMap, adjectives, obj2)) {
				actions.add(nlAction);
			}
		} else {
			actions.add(nlAction);
		}

		return actions;

	}

	private String getSubject(SemanticGraph dependencies, IndexedWord verbToken) {

		// find the subject of the verb
		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj");

		if (subj == null) {
			return "app";
		}

		IndexedWord subjToken = subj.second;
		if (isPersonalPronoun(subjToken)) {
			return null;
		}

		String subject = "user";

		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		} else if (subjToken.tag().equals("WDT")) {
			subject = "app";
		} else {
			subject = getObject(dependencies, subjToken);
		}
		return subject;
	}

}
