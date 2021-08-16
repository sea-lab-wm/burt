package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

public class SimplePastPP extends NLActionPatternParser {

    public SimplePastPP(boolean debugEnabled) {
        super(debugEnabled);
    }

    @Override
    protected List<NLAction> processSentence(Sentence sentence) throws Exception {

        SemanticGraph dependencies = sentence.getDependencies();
        IndexedWord verbToken = dependencies.getFirstRoot();

        List<NLAction> actions = new ArrayList<>();
        Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
                "nsubj", "csubj");

        if (subj == null)
            return actions;

        if (!TextProcessor.checkGeneralPos(subj.second.tag(), "PRP"))
            return actions;

        //--------------------------------

        IndexedWord nonTryVerbToken = checkForTryVerb(dependencies, verbToken);

        boolean rootVerbIsTry = !verbToken.equals(nonTryVerbToken);
        if(rootVerbIsTry)
            verbToken = nonTryVerbToken;

        //--------------------------------

        // find the object of the verb
        Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "dobj", "nsubjpass");

        IndexedWord objToken = null;
        if (objRelation != null) {
            objToken = objRelation.second;
        }else{
            objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "ccomp", "nummod", "xcomp");
            if (objRelation != null) {
                objToken = objRelation.second;
            }
        }

        String subject = "user";

        NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken),
                getObject(dependencies, objToken), actType);
        setPrepositionalClause(dependencies, verbToken, objToken, action, null);

        actions.add(action);

        return actions;
    }
}
