package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

public class GerundActionPP extends NLActionPatternParser {

	public GerundActionPP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	protected List<NLAction> processSentence(Sentence sentence) throws Exception {
		SemanticGraph dependencies = sentence.getDependencies();
		List<NLAction> actions = new ArrayList<>();
		
		List<SemanticGraphEdge> clSubjs = DependenciesUtils.findRelationsByTgtRelations(dependencies, "csubj");
		
		for (SemanticGraphEdge clSubj : clSubjs) {
			
			IndexedWord verbToken = clSubj.getTarget();
			if (verbToken.tag().equals("VBG")) {
				

				// the object modified by the verb
				Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
						verbToken, "dobj");
				
				IndexedWord objToken = null;
				if (objRelation!=null) {
					objToken = objRelation.second;
				}
				
				NLAction action = new NLAction(null, "user", getVerb(dependencies, verbToken), getObject(dependencies, objToken), actType);
				setPrepositionalClause(dependencies, verbToken, objToken, action, null);
				setActionNegation(dependencies, verbToken, action);
				
				actions.add(action);
				
			}
			
		}
		
		
		return actions;
	}

}
