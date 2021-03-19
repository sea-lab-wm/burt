package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.pattern.ob.NegativeTerms;
import seers.bugreppatterns.utils.SentenceUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NegativeVerbPP extends NLActionPatternParser {

	final public static Set<String> OTHER_VERBS = JavaUtils.getSet("close");

	public NegativeVerbPP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) throws Exception {

		SemanticGraph dependencies = sentence.getDependencies();

		IndexedWord verbToken = dependencies.getFirstRoot();

		List<NLAction> actions = processVerb(dependencies, verbToken);

		if (actions.isEmpty()) {
			actions = processCrash(dependencies, verbToken, sentence);

			List<SemanticGraphEdge> edges = DependenciesUtils.findRelationsByTgtRelations(dependencies, "acl:relcl");
			for (SemanticGraphEdge edge : edges) {
				if (!edge.getTarget().equals(verbToken)) {
					actions.addAll(processVerb(dependencies, edge.getTarget()));
				}
			}

			HashMap<String, List<Sentence>> quotes = sentence.getQuotes();
			if (actions.isEmpty() && sentence.getTokens().size() == 1 && quotes.size() == 1) {
				Collection<List<Sentence>> sentences = quotes.values();
				for (List<Sentence> sntcList : sentences) {
					for (Sentence sentence2 : sntcList) {
						SemanticGraph dependencies2 = sentence2.getDependencies();
						actions.addAll(processVerb(dependencies2, dependencies2.getFirstRoot()));
					}
				}
			}

			if (actions.isEmpty()) {
				List<IndexedWord> verbToks = getNegativeVerbTokens(dependencies);
				for (IndexedWord idxWord : verbToks) {
					actions.addAll(processVerb(dependencies, idxWord));
				}
			}

			if (actions.isEmpty()) {

				String text = TextProcessor.getStringFromLemmas(sentence);

				if (text.matches(".+ > crash")) {

					actions.add(new NLAction(null, "app", "crash", null, actType));
				}
			}
		}

		return actions;
	}

	private List<IndexedWord> getNegativeVerbTokens(SemanticGraph dependencies) {
		List<IndexedWord> vertexListSorted = dependencies.vertexListSorted();
		return vertexListSorted.stream().filter(v -> SentenceUtils.stringEqualsToAnyToken(NegativeTerms.VERBS, v.lemma()))
				.collect(Collectors.toList());
	}

	private List<NLAction> processCrash(SemanticGraph dependencies, IndexedWord verbToken, Sentence sentence) {

		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (TextProcessor.checkGeneralPos(verbToken.tag(), "NN") && verbToken.lemma().equals("crash")) {

			Pair<GrammaticalRelation, IndexedWord> subjRelation = DependenciesUtils
					.getFirstChildByRelation(dependencies, verbToken, "compound");

			if (subjRelation == null) {
				return actions;
			}
			String subject = null;
			subject = getSubjectFromNoun(dependencies, subjRelation.second);

			// build the action
			NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken, false), null, actType);

			actions.add(nlAction);
		} else {

			String text = TextProcessor.getStringFromTerms(sentence).toLowerCase();

			if (text.matches("^(the( \\w+)? )?build crashes.+")) {
				// build the action
				NLAction nlAction = new NLAction(null, "build", "crash", null, actType);

				actions.add(nlAction);
			} else {
				Pattern p = Pattern.compile("(\\w+) force close");
				Matcher matcher = p.matcher(text);
				if (matcher.find()) {

					NLAction nlAction = new NLAction(null, matcher.group(1), "force close", null, actType);

					actions.add(nlAction);
				}

			}
		}

		return actions;
	}

	private String getSubjectFromNoun(SemanticGraph dependencies, IndexedWord subjToken) {

		String subject = getObject(dependencies, subjToken);
		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		}
		return subject;
	}

	private List<NLAction> processVerb(SemanticGraph dependencies, IndexedWord verbToken) {
		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) {
			return actions;
		}

		if (!SentenceUtils.stringEqualsToAnyToken(NegativeTerms.VERBS, verbToken.lemma())
				&& !SentenceUtils.stringEqualsToAnyToken(OTHER_VERBS, verbToken.lemma())) {
			return actions;
		}

		// the object modified by the verb
		Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "dobj");

		IndexedWord objToken = null;
		if (objRelation != null) {
			objToken = objRelation.second;
		}

		String subject = getSubject(dependencies, verbToken);

		if (subject == null || subject.toLowerCase().equals("unable")) {
			return actions;
		}

		// build the action
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
				getObject(dependencies, objToken), actType);
		setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
		setActionNegation(dependencies, verbToken, nlAction);

		actions.add(nlAction);

		return actions;
	}

	private String getSubject(SemanticGraph dependencies, IndexedWord verbToken) {
		// find the subject of the verb
		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj");

		if (subj == null) {
			return null;
		}
		
		IndexedWord subjToken = subj.second;
		if (isPersonalPronoun(subjToken)) {
			return null;
		}

		String subject = getObject(dependencies, subjToken);
		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		}
		return subject;
	}

}
