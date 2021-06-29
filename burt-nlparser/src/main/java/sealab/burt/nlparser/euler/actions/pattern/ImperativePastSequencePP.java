package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.pattern.ob.ProblemInPM;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.QuoteProcessor;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.*;

public class ImperativePastSequencePP extends NLActionPatternParser {

	Set<ImmutablePair<String, String>> clauseSeparators = JavaUtils.getPairSet("->");

	public ImperativePastSequencePP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) {

		// --------------------------------

		// List<Sentence> subsentences =
		// SentenceUtils.extractClausesBySeparatorPairs(sentence1,
		// clauseSeparators);

		// List<NLAction> allActions = new ArrayList<>();

		// for (Sentence sentence : subsentences) {
		SemanticGraph dependencies = sentence.getDependencies();

		IndexedWord verbToken = dependencies.getFirstRoot();

		verbToken = checkForTryVerb(dependencies, verbToken);

		List<NLAction> actions = getActions(dependencies, verbToken, sentence);

		if (actions.isEmpty()) {
			List<NLAction> actions2 = getActionsByPOS(sentence, 0);
			actions.addAll(actions2);

		}
		// allActions.addAll(actions);

		// }

		return actions;
	}

	private List<NLAction> getActionsByPOS(Sentence sentence, int index) {

		List<Token> tokens = sentence.getTokens().subList(index, sentence.getTokens().size());

		List<NLAction> actions = new ArrayList<>();

		int vIdx1 = getVerbIndex(tokens);
		if (vIdx1 == -1) {

			// check for labels in the first labelLength terms: find the token ":"
			final int labelLength = 5;
			int idx = -1;
			for (int i = 0; i < tokens.size() && i <= labelLength; i++) {

				Token token = tokens.get(i);
				if (token.getLemma().equals(":")) {
					idx = i;
				}
			}

			// if the ":" is found, check for the imperative tokens
			if (idx != -1) {
				if (idx + 2 < tokens.size()) {
					final List<Token> subTokens = tokens.subList(idx + 1, tokens.size());
					final boolean isImperative = SentenceUtils.isImperativeSentence(subTokens, false, false);

					if (isImperative) {

						final Token verbToken = subTokens.get(0);

						String object = subTokens.get(1).getWord();
						NLAction action = new NLAction(null, "user", verbToken.getLemma(), object, actType);

						actions.add(action);
					}

				}
			}

			return actions;
		}

		Token verbToken = tokens.get(vIdx1);

		if(isInvalidVerb(verbToken.getLemma())) return actions;

		SemanticGraph dependencies = sentence.getDependencies();

		// ----------------------------
		// special case for incorrectly pos-tagged verbs

	/*	if (isUndetectedVerb(verbToken)) {
			if (vIdx1 + 1 < tokens.size()) {
				Token prepositionToken = tokens.get(vIdx1 + 1);

				// case: verb + preposition
				// eg.: tap on button
				if (prepositionToken.getGeneralPos().equals("IN")) {
					NLAction action = new NLAction(null, "user", verbToken.getLemma(), null, actType);

					// find the object
					for (int i = vIdx1 + 2; i < tokens.size(); i++) {
						Token token = tokens.get(i);

						// find the next noun
						if (token.getGeneralPos().equals("NN")) {

							IndexedWord objToken = dependencies.getNodeByIndexSafe(index + i + 1);

							if (objToken != null) {

								List<IndexedWord> pars = DependenciesUtils.getParentsbyRelation(dependencies, objToken,
										"compound");
								String object2 = null;
								if (!pars.isEmpty()) {

									objToken = pars.get(0);

									object2 = getObject(dependencies, objToken);

								} else {
									object2 = getObject(dependencies, objToken);
								}
								object2 = object2.replace(verbToken.getWord() + " ", "").trim();

								action.setPreposition(prepositionToken.getLemma());
								action.setObject2(object2);
							}
							break;


						} else
							//case: tap on done
							if (SentenceUtils.lemmasContainToken(JavaUtils.getSet("in", "on"), prepositionToken) && token.getGeneralPos().equals("VB")) {
								action = new NLAction(null, "user",
										verbToken.getLemma() + " " + prepositionToken.getLemma(), token.getWord(),
										actType);
								break;
							}
					}

					actions.add(action);
					return actions;
				}
			}
		}*/

		// ----------------------------

		if (tokens.size() == 2 && vIdx1 + 1 < tokens.size()) {

			Token objToken = tokens.get(vIdx1 + 1);
			NLAction action = new NLAction(null, "user", verbToken.getLemma(), objToken.getWord(), actType);

			actions.add(action);
			return actions;
		}

		// ----------------------------

		IndexedWord vToken = dependencies.getNodeByIndexSafe(index + vIdx1 + 1);
		List<IndexedWord> subjs = DependenciesUtils.getParentsbyRelation(dependencies, vToken, "csubj");

		// if there is subject, then the sentence is not imperative
		if (subjs != null && !subjs.isEmpty()) {
			return actions;
		}

		// ----------------------------------

		for (int i = vIdx1 + 1; i < tokens.size(); i++) {
			Token token = tokens.get(i);

			// find the next noun
			if (token.getGeneralPos().equals("NN")) {

				IndexedWord objToken = dependencies.getNodeByIndexSafe(index + i + 1);
				if (objToken != null) {

					List<IndexedWord> pars = DependenciesUtils.getParentsbyRelation(dependencies, objToken, "compound");

					String object;
					if (!pars.isEmpty()) {
						objToken = pars.get(0);
						object = getObject(dependencies, objToken);
					} else {
						object = getObject(dependencies, objToken);
					}
					object = object.replace(verbToken.getWord() + " ", "").trim();

					NLAction action = new NLAction(null, "user", verbToken.getLemma(), object, actType);
					boolean prepSet = setPrepositionalClause(dependencies, null, objToken, action, null);
					if (!prepSet) {
						setPOSPrepositionalClause(dependencies, tokens, i + 1, action, objToken, index);
					}

					actions.add(action);
				}
				break;
			}
		}

		return actions;
	}

	private void setPOSPrepositionalClause(SemanticGraph dependencies, List<Token> tokens, int idx, NLAction action,
										   IndexedWord objToken, int index) {

		if (tokens.size() <= (idx + 1)) {
			return;
		}

		Token prepTok = tokens.get(idx);
		if (!SentenceUtils.lemmasContainToken(ProblemInPM.PREP_TERMS, prepTok)) {
			return;
		}

		IndexedWord prepIdxW = dependencies.getNodeByIndexSafe(index + idx + 1);

		List<IndexedWord> prepParentsByRel = DependenciesUtils.getParentsbyRelation(dependencies, prepIdxW, "case");
		if (prepParentsByRel == null || prepParentsByRel.isEmpty()) {
			return;
		}

		IndexedWord nounIdxW = prepParentsByRel.get(0);
		List<IndexedWord> nounParentsByRel = DependenciesUtils.getParentsbyRelation(dependencies, nounIdxW, "nmod");
		if (nounParentsByRel == null || nounParentsByRel.isEmpty()) {
			return;
		}


		String prep = prepTok.getLemma();
		String obj2 = getObject(dependencies, nounIdxW);

		action.setPreposition(prep);
		action.setObject2(obj2);
	}

	private int getVerbIndex(List<Token> tokens) {

		int idx = -1;
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			if (!QuoteProcessor.isQuoteToken(token.getWord()) && !isSpecialChar(token)) {
				idx = i;
				break;
			}
		}

		if (idx == -1) {
			return -1;
		}

		List<Token> subTokens = tokens.subList(idx, tokens.size());

		// ----------------------------------

		int idx2 = findVerbIndex(subTokens);

		if (idx2 == -1) {
			return -1;
		}

		return idx + idx2;
	}


	public static boolean isSpecialChar(Token token) {
		String word = token.getWord();
		return word.matches("[^A-Za-z0-9]") || token.getWord().equals("``") || token.getWord().equals("''")
				|| TextProcessor.isParenthesis(token.getWord())
				|| token.getWord().matches("[\\Q$&+,:;=?@#|'<>.^*()%!-][}{\\E]++");
	}

	private int findVerbIndex(List<Token> subTokens) {
		if (subTokens.size() < 2) {
			return -1;
		}

		Token verbToken = subTokens.get(0);
		Token secondToken = subTokens.get(1);

		// avoid "do not run..."
		if (verbToken.getLemma().equals("do") && secondToken.getLemma().equals("not")) {
			return -1;
		}

		// the first token is a verb in infinitive
		if ((verbToken.getPos().equals("VBD") || verbToken.getPos().equals("VBN"))) {

			// cases: try to change
			Token nextToken = subTokens.get(1);
			if (verbToken.getLemma().equals("try") && nextToken.getLemma().equals("to")) {
				if (subTokens.size() > 3) {
					if ((verbToken.getPos().equals("VB") || verbToken.getPos().equals("VBP"))) {
						return 2;
					}
				}
			}

			return 0;
		}

		// case: the sentence starts with an adverb/adjective and then with
		// a verb
		if (secondToken != null) {
			if ((verbToken.getPos().equals("RB") || verbToken.getPos().equals("JJ"))
					&& (secondToken.getPos().equals("VBD") || secondToken.getPos().equals("VBN")
			//		|| SentenceUtils.lemmasContainToken(SentenceUtils.AMBIGUOUS_POS_VERBS, secondToken)
			)
					&& subTokens.size() > 2) {
				return 1;
			}
		}

		// case: the first token is an undetected verb
		/*if (isUndetectedVerb(verbToken)) {
			return 0;
		}*/

		return -1;
	}

/*	private boolean isUndetectedVerb(Token verbToken) {
		return isUndetectedVerbByLemma(verbToken.getLemma());
	}

	private boolean isUndetectedVerbByLemma(String verbLemma) {
		return SentenceUtils.stringEqualsToAnyToken(SentenceUtils.UNDETECTED_VERBS, verbLemma)
				|| SentenceUtils.stringEqualsToAnyToken(SentenceUtils.AMBIGUOUS_POS_VERBS, verbLemma);
	}*/

	private List<NLAction> getActions(SemanticGraph dependencies, IndexedWord verbToken, Sentence sentence) {

		List<NLAction> actions = new ArrayList<>();


		// is it a valid verb?
		if ((!verbToken.tag().equals("VBD") && !verbToken.tag().equals("VBN"))) {

			List<Pair<GrammaticalRelation, IndexedWord>> relations = DependenciesUtils.getChildRelations(dependencies,
					verbToken, "parataxis");

			for (Pair<GrammaticalRelation, IndexedWord> rel : relations) {
				IndexedWord verbTok = rel.second;

				List<NLAction> actions2 = getActions(dependencies, verbTok, sentence);
				addAllActions(actions, actions2);
			}

			return actions;
		}

		if(isInvalidVerb(verbToken.lemma())) return actions;

		// ----------------------------------------------

		// the object modified by the verb
		Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "dobj", "xcomp");

		IndexedWord objToken = null;
		if (objRelation != null) {
			objToken = objRelation.second;
		} else {
			Pair<GrammaticalRelation, IndexedWord> depRel = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "dep");
			if(depRel!=null){
				objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
						depRel.second, "dobj", "xcomp");
				if (objRelation != null) {
					objToken = objRelation.second;
				}
			}
		}

		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj", "csubj");

		// if there is subject, then the sentence is not imperative
		if (subj != null) {
			return actions;
		}

		// avoid negated imperative sentences
		Pair<GrammaticalRelation, IndexedWord> negatedRelation = findNegatedRelation(dependencies, verbToken);
		if (negatedRelation != null) {
			return actions;
		}

		String subject = "user";
		String object = getObject(dependencies, objToken);

		if (object == null) {

			//case: click on done
			if (verbToken.lemma().equals("click")
					|| verbToken.lemma().equals("tap")) {

				objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
						verbToken, "dep", "advcl");

				if (objRelation != null) {
					IndexedWord objTok2 = objRelation.second;
					object = objTok2.word();
				} else {
					Map<String, List<String>> rels = new HashMap<>();
					rels.put("acl", Arrays.asList("on", "in"));

					objRelation = DependenciesUtils.getFirstChildByRelationSpecific(dependencies,
							verbToken, rels);

					if (objRelation != null) {
						IndexedWord objTok2 = objRelation.second;
						object = objTok2.word();
					}
				}
			}


		}

		// build the action
		NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken), object, actType);
		setPrepositionalClause(dependencies, verbToken, objToken, action, null);

		actions.add(action);

		// --------------------------------
		// recursive calls, by conjunctions

		List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies,
				verbToken, "conj");

		for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
			List<NLAction> actions2 = getActions(dependencies, pair.second, sentence);
			addAllActions(actions, actions2);
		}

		//--------------------------------
		// recursive calls, by conjunctions when deps/pos parsing is inaccurate

		if (pairs.isEmpty()) {

			if (action.getObject2() != null) {

				Pair<GrammaticalRelation, IndexedWord> objClause = getNounModifier(dependencies, verbToken, objToken,
						null);

				if (objClause != null) {

					pairs = DependenciesUtils.getChildRelations(dependencies,
							objClause.second, "conj");

					for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {

						IndexedWord verbToken2 = pair.second;
						List<NLAction> actions2 = getActions(dependencies, verbToken2, sentence);
						addAllActions(actions, actions2);

						if (actions2 == null || actions2.isEmpty()) {
							List<NLAction> actionsByPOS = getActionsByPOS(sentence, verbToken2.index() - 1);
							addAllActions(actions, actionsByPOS);

						}
					}

				}
			}
		}

		return actions;
	}

	private boolean isInvalidVerb(String lemma) {
		return lemma.equals("expect");
	}

	private void addAllActions(List<NLAction> actions, List<NLAction> actions2) {
		for (NLAction action : actions2) {
			if (!actions.contains(action)) {
				actions.add(action);
			}
		}
	}


}
