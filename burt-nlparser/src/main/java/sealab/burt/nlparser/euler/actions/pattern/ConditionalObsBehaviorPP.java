package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.utils.SentenceUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConditionalObsBehaviorPP extends NLActionPatternParser {

	final static Set<String> EXCLUDED_VERBS = JavaUtils.getSet("have", "want", "feel", "deal", "look", "decide", "need",
			"help");

	public static final Set<String> CONDITIONAL_TERMS = JavaUtils.getSet("if", "upon", "when", "whenever", "whereas",
			"while", "after");

	public ConditionalObsBehaviorPP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) throws Exception {

		List<NLAction> actions = new ArrayList<>();

		SemanticGraph dependencies = sentence.getDependencies();

		// find the "when" relations
		List<SemanticGraphEdge> condAdvMods = DependenciesUtils.findRelationsByTgtRelationAndPos(dependencies, "advmod",
				"WRB");

		for (SemanticGraphEdge edge : condAdvMods) {
			IndexedWord verbToken = edge.getSource();

			// is there a verb being modified by the "when"?
			if (TextProcessor.checkGeneralPos(verbToken.tag(), "VB")
					&& !SentenceUtils.stringEqualsToAnyToken(EXCLUDED_VERBS, verbToken.lemma())) {
				boolean anyMatch = DependenciesUtils.checkForRelationsInPairs(dependencies.parentPairs(verbToken),
						"advcl", "acl:relcl");
				if (anyMatch) {

					verbToken = checkForTryVerb(dependencies, verbToken);

					// find the object of the verb
					Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils
							.getFirstChildByRelation(dependencies, verbToken, "dobj", "nsubjpass");
					IndexedWord objToken = null;
					if (objRelation != null) {
						objToken = objRelation.second;
					}
					String subject = getSubject(dependencies, verbToken);

					NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken),
							getObject(dependencies, objToken), actType);
					setPrepositionalClause(dependencies, verbToken, objToken, action, null);

					addActions(actions, action);

					// --------------------------------
					// recursive calls, by conjunctions

					List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils
							.getChildRelations(dependencies, verbToken, "conj");

					boolean obj = false;
					if (pairs.isEmpty()) {
						pairs = DependenciesUtils.getChildRelations(dependencies, objToken, "conj");
						obj = true;
					}

					for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {

						IndexedWord verbToken2 = pair.second;

						verbToken2 = checkForTryVerb(dependencies, verbToken2);

						// cases: "when I press 'Go to...' and type a number"
						if (TextProcessor.checkGeneralPos(verbToken2.tag(), "NN") && SentenceUtils
								.matchTermsByLemma(SentenceUtils.UNDETECTED_VERBS, verbToken2.lemma())) {

							if (obj) {
								Pair<GrammaticalRelation, IndexedWord> rel = DependenciesUtils
										.getFirstChildByRelation(dependencies, objToken, "dep");

								if (rel != null && TextProcessor.checkGeneralPos(rel.second.tag(), "NN")) {

									IndexedWord objToken2 = rel.second;

									NLAction action2 = new NLAction(null, subject, getVerb(dependencies, verbToken2),
											getObject(dependencies, objToken2), actType);
									setPrepositionalClause(dependencies, verbToken2, objToken2, action2, null);

									addActions(actions, action2);

									// ------------------------
									//
									// List<Pair<GrammaticalRelation,
									// IndexedWord>> pairs2 = DependenciesUtils
									// .getChildRelations(dependencies,
									// objToken2, "conj");
									//
									// for (Pair<GrammaticalRelation,
									// IndexedWord> pair2 : pairs2) {
									// setSubordinatedActions(actions,
									// dependencies, pair2.second);
									// }
								}

							}

						} else {
							setSubordinatedActions(actions, dependencies, pair.second);
						}
					}
				}
			} else if (TextProcessor.checkGeneralPos(verbToken.tag(), "NN")) {

				Pair<GrammaticalRelation, IndexedWord> verbRel = DependenciesUtils.getFirstChildByRelation(dependencies,
						verbToken, "cop");

				if (verbRel != null) {

					IndexedWord objToken = verbToken;
					verbToken = verbRel.second;

					String subject = getSubject(dependencies, objToken);

					String verb = getVerb(dependencies, verbToken);
					String object = getObject(dependencies, objToken);

					if (object != null) {
						NLAction action = new NLAction(null, subject, verb, object, actType);
						setPrepositionalClause(dependencies, verbToken, objToken, action, null);

						addActions(actions, action);
					}

					// --------------------------------
					// recursive calls, by conjunctions

					List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils
							.getChildRelations(dependencies, objToken, "conj");

					if (pairs.isEmpty()) {
						List<IndexedWord> pars = DependenciesUtils.getParentsbyRelation(dependencies, objToken,
								"advcl");
						if (!pars.isEmpty()) {
							IndexedWord indexedWord = pars.get(0);
							pairs = DependenciesUtils.getChildRelations(dependencies, indexedWord, "conj");
						}
					}

					for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
						IndexedWord idxW = pair.second;

						// cases: "max score is bla bla or is a bug number"
						if (TextProcessor.checkGeneralPos(idxW.tag(), "NN")) {

							NLAction action2 = new NLAction(null, subject, verb, getObject(dependencies, idxW),
									actType);
							setPrepositionalClause(dependencies, verbToken, idxW, action2, null);

							addActions(actions, action2);

						} else {
							setSubordinatedActions(actions, dependencies, pair.second);
						}
					}
				}

			}
		}

		// --------------------------------------------

		// find the conditional relations
		List<SemanticGraphEdge> condMarks = DependenciesUtils.findRelationsByTgtRelationPosAndLemmas(dependencies,
				"mark", "IN", CONDITIONAL_TERMS);

		for (SemanticGraphEdge edge : condMarks) {
			IndexedWord verbToken = edge.getSource();

			verbToken = checkForTryVerb(dependencies, verbToken);

			processVerbToken(actions, dependencies, verbToken, false);

			// check for subordinates
			List<IndexedWord> parentPairs = DependenciesUtils.getParentsbyRelation(dependencies, verbToken, "ccomp",
					"advcl");
			for (IndexedWord parentVerbToken : parentPairs) {

				Pair<GrammaticalRelation, IndexedWord> child = DependenciesUtils.getFirstChildByRelation(dependencies,
						parentVerbToken, "dobj");

				// avoid parsing the prefix of cases such as "The new build
				// crashes after searching anything"
				if (parentVerbToken.lemma().equals("build") && child != null && child.second.lemma().equals("crash")) {
					continue;
				}

				if ((!parentVerbToken.equals(dependencies.getFirstRoot()) || !parentVerbToken.tag().equals("VBG"))
						&& !isPassiveVoice(dependencies, parentVerbToken)) {


					parentVerbToken = checkForTryVerb(dependencies, parentVerbToken);

					processVerbToken(actions, dependencies, parentVerbToken, true);
				}

			}
		}

		if (actions.isEmpty()) {
			return null;
		}

		return actions;
	}

	private void addActions(List<NLAction> actions, NLAction action) {
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	private void processVerbToken(List<NLAction> actions, SemanticGraph dependencies, IndexedWord verbToken,
			boolean checkForSubject) {

        // is there a verb being modified by the conditional?
        if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")
                || SentenceUtils.stringEqualsToAnyToken(EXCLUDED_VERBS, verbToken.lemma())
        ) {
            if (!"safe".equals(verbToken.lemma()))
                return;
        }

		// avoid: but not if...
		List<IndexedWord> parents = DependenciesUtils.getParentsbyRelation(dependencies, verbToken, "dep");
		if (parents.stream().anyMatch(p -> p.lemma().equals("not") || p.lemma().equals("not"))) {
			return;
		}

		// passive voice
		if (isPassiveVoice(dependencies, verbToken)) {

			Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "nsubjpass");

			IndexedWord objToken = null;
			if (objRelation != null) {
				objToken = objRelation.second;
			}

			NLAction action = new NLAction(null, "user", getVerbPassive(dependencies, verbToken),
					getObject(dependencies, objToken), actType);

			setPrepositionalClause(dependencies, verbToken, objToken, action, null);

			addActions(actions, action);

		} else {

			// active voice
			if (checkForSubject) {
				Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies,
						verbToken, "nsubj");
				if (subj != null) {
					return;
				}
			}

			// is there a clausal complement?
			Pair<GrammaticalRelation, IndexedWord> clausCompl = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "xcomp");
			if (clausCompl != null && TextProcessor.checkGeneralPos(clausCompl.second.tag(), "VB")) {
				verbToken = clausCompl.second;
			}

			// find the object of the verb
			Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "dobj");

			IndexedWord objToken = null;
			if (objRelation != null) {
				objToken = objRelation.second;
			}

			String subject = getSubject(dependencies, verbToken);
			String object = getObject(dependencies, objToken);


			NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken),
					object, actType);

			setPrepositionalClause(dependencies, verbToken, objToken, action, null);

			//add the action if if it is not of this type " subject + be"
			if (!verbToken.lemma().equals("be") || object != null || action.getPreposition() != null) {
				addActions(actions, action);
			}

		}

		// --------------------------------
		// recursive calls, by conjunctions

		List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies,
				verbToken, "conj");

		for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
			setSubordinatedActions(actions, dependencies, pair.second);
		}
	}

	private void setSubordinatedActions(List<NLAction> actions, SemanticGraph dependencies, IndexedWord verbToken) {
		// is there a verb being modified by the "when"?
		if (TextProcessor.checkGeneralPos(verbToken.tag(), "VB")
				&& !SentenceUtils.stringEqualsToAnyToken(EXCLUDED_VERBS, verbToken.lemma())) {

			// find the object of the verb
			Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "dobj", "nsubjpass");

			IndexedWord objToken = null;
			if (objRelation != null) {
				objToken = objRelation.second;
			}

			String subject = getSubject(dependencies, verbToken);

			NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken),
					getObject(dependencies, objToken), actType);
			setPrepositionalClause(dependencies, verbToken, objToken, action, null);

			addActions(actions, action);

			// --------------------------------
			// recursive calls, by conjunctions

			List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies,
					verbToken, "conj");

			for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
				setSubordinatedActions(actions, dependencies, pair.second);
			}
		}
	}

	private String getSubject(SemanticGraph dependencies, IndexedWord verbToken) {
		// find the subject of the verb
		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj");

		String subject = "user";
		if (subj != null && !TextProcessor.checkGeneralPos(subj.second.tag(), "PRP")) {
			subject = getObject(dependencies, subj.second);
		}
		return subject;
	}

}
