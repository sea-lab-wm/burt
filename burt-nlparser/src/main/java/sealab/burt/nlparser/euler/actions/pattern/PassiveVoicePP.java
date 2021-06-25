package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.*;

public class PassiveVoicePP extends NLActionPatternParser {

	public PassiveVoicePP(boolean debugEnabled) {
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

				List<Pair<GrammaticalRelation, IndexedWord>> edges3 = DependenciesUtils.getChildRelations(dependencies,
						verbToken, "parataxis");
				for (Pair<GrammaticalRelation, IndexedWord> edge1 : edges3) {
					List<NLAction> actions3 = processVerb(dependencies, edge1.second);
					allActions.addAll(actions3);
				}
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

		if (!isPassiveVoice(dependencies, verbToken)) {
			return actions;
		}
		
		Pair<GrammaticalRelation, IndexedWord> objRel2 = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "nsubjpass");

		HashMap<String, List<String>> relations = new HashMap<>();
		relations.put("nmod", Arrays.asList("agent"));
		Pair<GrammaticalRelation, IndexedWord> author = DependenciesUtils.getFirstChildByRelationSpecific(dependencies,
				verbToken, relations);

		if (objRel2 != null) {

			IndexedWord objToken2 = objRel2.second;
			// build the action
			NLAction nlAction = new NLAction(null, "app", getVerb(dependencies, verbToken),
					getObject(dependencies, objToken2), actType);
			setPrepositionalClause(dependencies, verbToken, objToken2, nlAction, author);
			setActionNegation(dependencies, verbToken, nlAction);

			actions.add(nlAction);
		} else if (author != null) {
			Pair<GrammaticalRelation, IndexedWord> comp = DependenciesUtils.getFirstChildByRelation(dependencies,
					author.second, "compound");
			if (comp != null && comp.second.lemma().equals("default")) {

				IndexedWord objToken2 = author.second;
				// build the action
				String object = getObject(dependencies, objToken2);

				if (object != null) {
					object = object.replace("default", "").trim();
				}

				NLAction nlAction = new NLAction(null, "app", getVerb(dependencies, verbToken), object, actType);
				setPrepositionalClause(dependencies, verbToken, objToken2, nlAction, author);
				setActionNegation(dependencies, verbToken, nlAction);

				actions.add(nlAction);
			}
		}

		return actions;
	}

}
