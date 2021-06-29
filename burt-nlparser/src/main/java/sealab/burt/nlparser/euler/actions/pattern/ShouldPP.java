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
import java.util.stream.Stream;

public class ShouldPP extends NLActionPatternParser {

    public ShouldPP(boolean debugEnabled) {
        super(debugEnabled);
    }

    @Override
    public List<NLAction> processSentence(Sentence sentence) throws Exception {

        SemanticGraph dependencies = sentence.getDependencies();

        IndexedWord verbToken = dependencies.getFirstRoot();

        List<NLAction> actions = new ArrayList<>();

        Pair<GrammaticalRelation, IndexedWord> aux = DependenciesUtils.
                getFirstChildByRelation(dependencies, verbToken, "aux");

        if (aux == null) {
            Pair<GrammaticalRelation, IndexedWord> parataxis = DependenciesUtils.
                    getFirstChildByRelation(dependencies, verbToken, "parataxis");
            if (parataxis == null) {

                Pair<GrammaticalRelation, IndexedWord> dep = DependenciesUtils.
                        getFirstChildByRelation(dependencies, verbToken, "dep");

                if (dep == null) {
                    return actions;
                } else {

                    verbToken = dep.second;

                    aux = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken, "aux");
                    if (aux == null) {
                        return actions;
                    }
                }
            } else {

                verbToken = parataxis.second;

                aux = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken, "aux");
                if (aux == null) {
                    return actions;
                }
            }

        }

        //-------------------------------------------

        Pair<GrammaticalRelation, IndexedWord> finalAux = aux;
        if (Stream.of("should", "shall").noneMatch(l -> l.equalsIgnoreCase(finalAux.second.lemma())))
            return actions;

        if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) {
            List<Pair<GrammaticalRelation, IndexedWord>> parents = dependencies.parentPairs(aux.second);
            for (Pair<GrammaticalRelation, IndexedWord> parent : parents) {
                if (TextProcessor.checkGeneralPos(parent.second.tag(), "VB")) {
                    verbToken = parent.second;
                }
            }
        }

        if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) return actions;


        // find the subject of the verb
        Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "nsubj", "nsubjpass");

        // no personal pronouns: I, we, and you
        if (subj != null) {
            IndexedWord subjToken = subj.second;
            if (isPersonalPronoun(subjToken)) {
                return actions;
            }
        }

        String subject = "app";
        if (subj != null) {
            if (subj.second.lemma().equalsIgnoreCase("it")) {
                subject = "app";
            }else {
                subject = getObject(dependencies, subj.second);
            }
        }

        // find the object of the verb
        Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "dobj");
        IndexedWord objToken = null;
        if (objRelation != null) {
            objToken = objRelation.second;
        }

        // add action
        NLAction action = new NLAction(null, subject, getVerb(dependencies, verbToken),
                getObject(dependencies, objToken), actType);
        setPrepositionalClause(dependencies, verbToken, objToken, action, null);
        setActionNegation(dependencies, verbToken, action);

        actions.add(action);


        return actions;
    }

}
