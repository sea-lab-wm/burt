package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import seers.bugreppatterns.pattern.ob.OutputVerbPM;
import seers.bugreppatterns.utils.SentenceUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OutputVerbPP extends NLActionPatternParser {

	public OutputVerbPP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) throws Exception {

		SemanticGraph dependencies = sentence.getDependencies();

		// IndexedWord verbToken = dependencies.getFirstRoot();

		List<NLAction> allActions = new ArrayList<>();
		Collection<IndexedWord> roots = dependencies.getRoots();
		for (IndexedWord verbToken : roots) {
			List<NLAction> actions = processVerb(dependencies, verbToken);
			allActions.addAll(actions);

			if (actions.isEmpty()) {
				List<Pair<GrammaticalRelation, IndexedWord>> edges = DependenciesUtils.getChildRelations(dependencies,
						verbToken, "ccomp");
				for (Pair<GrammaticalRelation, IndexedWord> edge : edges) {
					List<NLAction> actions2 = processVerb(dependencies, edge.second);
					allActions.addAll(actions2);
					
				}
				
				List<Pair<GrammaticalRelation, IndexedWord>> edges2 = DependenciesUtils.getChildRelations(dependencies,
						verbToken, "dep");
				for (Pair<GrammaticalRelation, IndexedWord> edge1 : edges2) {
					List<NLAction> actions3 = processVerb(dependencies, edge1.second);
					allActions.addAll(actions3);
				}
			}
			
			List<Pair<GrammaticalRelation, IndexedWord>> edges2 = DependenciesUtils.getChildRelations(dependencies,
					verbToken, "conj");
			for (Pair<GrammaticalRelation, IndexedWord> edge1 : edges2) {
				List<NLAction> actions3 = processVerb(dependencies, edge1.second);
				allActions.addAll(actions3);
			}
		}

		return allActions;
	}

	private List<NLAction> processVerb(SemanticGraph dependencies, IndexedWord verbToken) {
		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) {
			return actions;
		}

		if (!SentenceUtils.stringEqualsToAnyToken(OutputVerbPM.OUTPUT_VERBS, verbToken.lemma())) {
			return actions;
		}

		String subject = getSubject(dependencies, verbToken);

		if (subject == null) {
			Pair<GrammaticalRelation, IndexedWord> objRel2 = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "nsubjpass");
			if (objRel2 != null) {

				IndexedWord objToken2 = objRel2.second;
				// build the action
				NLAction nlAction = new NLAction(null, "app", getVerb(dependencies, verbToken),
						getObject(dependencies, objToken2), actType);
				setPrepositionalClause(dependencies, verbToken, objToken2, nlAction, null);
				setActionNegation(dependencies, verbToken, nlAction);

				actions.add(nlAction);
			}

		} else {

			// the object modified by the verb
			Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "dobj");

			IndexedWord objToken = null;
			if (objRelation != null) {
				objToken = objRelation.second;
			} else {
				objRelation = findCCDObj(dependencies, verbToken);
				if (objRelation != null) {
					objToken = objRelation.second;
				}
			}

			// build the action
			NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
					getObject(dependencies, objToken), actType);
			setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
			setActionNegation(dependencies, verbToken, nlAction);

			actions.add(nlAction);
		}

		return actions;
	}

	private Pair<GrammaticalRelation, IndexedWord> findCCDObj(SemanticGraph dependencies, IndexedWord verbToken) {

		Pair<GrammaticalRelation, IndexedWord> ccVerb = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "ccomp");
		Pair<GrammaticalRelation, IndexedWord> quotes = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "punct");

		if (ccVerb != null && quotes != null && quotes.second.lemma().equals("``")) {
			return DependenciesUtils.getFirstChildByRelation(dependencies, ccVerb.second, "dobj");
		}
		return null;
	}

	private String getSubject(SemanticGraph dependencies, IndexedWord verbToken) {
		// find the subject of the verb
		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj");

		if (subj == null) {
			return null;
		}

		IndexedWord subjToken = subj.second;
		String subject = getObject(dependencies, subjToken);
		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		}
		return subject;
	}
}
