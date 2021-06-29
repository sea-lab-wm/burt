package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.pattern.PatternMatcher;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NegativeAuxVerbPP extends NLActionPatternParser {

    final private static Set<String> ALLOWED_AUX_VERBS = JavaUtils.getSet("MD-can", "VB-do", "VB-be", "MD-would",
            "VB-have", "MD-will", "MD-could", "MD-may");

    public NegativeAuxVerbPP(boolean debugEnabled) {
        super(debugEnabled);
    }

    @Override
    public List<NLAction> processSentence(Sentence sentence) throws Exception {

        List<NLAction> actions = new ArrayList<NLAction>();

        SemanticGraph dependencies = sentence.getDependencies();

        // -------------------------------------
        // regular cases: the app does not show ...

        // find auxiliary verbs
        List<SemanticGraphEdge> auxRels2 = DependenciesUtils.findRelationsByTgtRelations(dependencies, "aux");

        for (SemanticGraphEdge auxRel : auxRels2) {

            // is it a verb?
            IndexedWord verbToken = auxRel.getSource();
            IndexedWord auxVerbToken = auxRel.getTarget();
            if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB") || !isAuxVerb(auxVerbToken)) {
                continue;
            }

            // is it negated?
            Pair<GrammaticalRelation, IndexedWord> negPair = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "neg");
            if (negPair == null) {
                continue;
            }

            if (isInConditionalClause(dependencies, verbToken)) {
                continue;
            }

            // avoid sentences: "do not run..." or "have not run" or "cannot
            // run"
            if (auxVerbToken.index() == 1 && (auxVerbToken.lemma().equals("do") || auxVerbToken.lemma().equals("have")
                    || auxVerbToken.lemma().equals("can") || auxVerbToken.lemma().equals("could"))) {
                continue;
            }

            // find the subject of the verb
            Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "nsubj", "nsubjpass");

            // no personal pronouns: I, we, and you
            if (subj != null) {
                IndexedWord subjToken = subj.second;
                if (isPersonalPronoun(subjToken)) {
                    continue;
                }
            }

            String subject = "app";
            if (subj != null) {
                subject = getObject(dependencies, subj.second);
            } else {
                Pair<GrammaticalRelation, IndexedWord> csubj = DependenciesUtils.getFirstChildByRelation(dependencies,
                        verbToken, "csubj");

                if (csubj != null) {
                    String[] rels = {"dobj", "nsubj"};
                    subject = getCompoundExpression(dependencies, csubj.second, rels, true, false);
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
                    getObject(dependencies, objToken), null, null, true, actType);
            setPrepositionalClause(dependencies, verbToken, objToken, action, null);
            setActionNegation(dependencies, verbToken, action);

            actions.add(action);

            // --------------------------------
            // add subordinate actions by subject

            if (subj != null) {
                List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies,
                        subj.second, "conj");

                for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
                    addSubordinatedActionsBySubject(actions, action, dependencies, pair.second);
                }
            }

        }

        // -------------------------------------
        // passive voice cases: "the user is not redirected"

        // find passive auxiliary verbs
        List<SemanticGraphEdge> passAuxRels = DependenciesUtils.findRelationsByTgtRelations(dependencies, "auxpass");

        for (SemanticGraphEdge passAuxRel : passAuxRels) {

            // is it a verb?
            IndexedWord verbToken = passAuxRel.getSource();
            IndexedWord auxVerbToken = passAuxRel.getTarget();
            if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB") || !isAuxVerb(auxVerbToken)) {
                continue;
            }

            // is it negated?
            Pair<GrammaticalRelation, IndexedWord> negPair = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "neg");
            if (negPair == null) {
                continue;
            }

            if (isInConditionalClause(dependencies, verbToken)) {
                continue;
            }

            // find the subject of the verb
            Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "nsubjpass");
            if (objRelation != null) {

                IndexedWord objToken = objRelation.second;

                // add action
                NLAction action = new NLAction(null, "app", getVerb(dependencies, verbToken),
                        getObject(dependencies, objToken), null, null, true, actType);
                setPrepositionalClause(dependencies, verbToken, objToken, action, null);
                setActionNegation(dependencies, verbToken, action);

                actions.add(action);
            }

        }
        // -------------------------------------

        // other cases: points are not possible

        // find auxiliary verbs
        List<SemanticGraphEdge> copVerb = DependenciesUtils.findRelationsByTgtRelations(dependencies, "cop");

        for (SemanticGraphEdge auxRel : copVerb) {

            // is it a verb?
            IndexedWord objToken = auxRel.getSource();
            IndexedWord auxVerbToken = auxRel.getTarget();
            if (!TextProcessor.checkGeneralPos(objToken.tag(), "NN") || !isAuxVerb(auxVerbToken)) {
                continue;
            }

            // is it negated?
            Pair<GrammaticalRelation, IndexedWord> negPair = DependenciesUtils.getFirstChildByRelation(dependencies,
                    objToken, "neg");
            if (negPair == null) {
                continue;
            }

            if (isInConditionalClause(dependencies, objToken)) {
                continue;
            }

            // avoid sentences: "do not run..." or "have not run" or "cannot
            // run"
            if (auxVerbToken.index() == 1 && (auxVerbToken.lemma().equals("do") || auxVerbToken.lemma().equals("have")
                    || auxVerbToken.lemma().equals("can") || auxVerbToken.lemma().equals("could"))) {
                continue;
            }

            // find the subject of the verb
            Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies,
                    objToken, "nsubj", "nsubjpass");

            // no personal proponuns: I, we, and you
            if (subj != null) {
                IndexedWord subjToken = subj.second;
                if (isPersonalPronoun(subjToken)) {
                    continue;
                }
            }

            String subject = "app";
            if (subj != null) {
                subject = getObject(dependencies, subj.second);
            }


            // add action
            NLAction action = new NLAction(null, subject, getVerb(dependencies, auxVerbToken),
                    getObjectNoNeg(dependencies, objToken), null, null, true, actType);
            setPrepositionalClause(dependencies, auxVerbToken, objToken, action, null);
            setActionNegation(dependencies, auxVerbToken, action);

            actions.add(action);

        }

        return actions;
    }

    private boolean isInConditionalClause(SemanticGraph dependencies, IndexedWord verbToken) {

        Pair<GrammaticalRelation, IndexedWord> condToken = DependenciesUtils.getFirstChildByRelationAndPos(dependencies,
                verbToken, JavaUtils.getSet("advmod"), JavaUtils.getSet("WRB"));

        if (condToken != null) {
            return true;
        }

        // find the conditional relations
        List<Pair<GrammaticalRelation, IndexedWord>> condTokens = DependenciesUtils.getChildrenByRelationPosAndLemma(
                dependencies, verbToken, JavaUtils.getSet("mark"), JavaUtils.getSet("IN"),
                PatternMatcher.CONDITIONAL_TERMS);

        if (!condTokens.isEmpty()) {
            return true;
        }

        return false;
    }

    private boolean isAuxVerb(IndexedWord verbToken) {
        String posLemma = TextProcessor.getGeneralPos(verbToken.tag()) + "-" + verbToken.lemma();
        return ALLOWED_AUX_VERBS.stream().anyMatch(av -> av.equals(posLemma));
    }

    private void addSubordinatedActionsBySubject(List<NLAction> actions, NLAction baseAction,
                                                 SemanticGraph dependencies, IndexedWord subject) {

        NLAction newAction = new NLAction(baseAction);
        newAction.setSubject(getObject(dependencies, subject));
        actions.add(newAction);

        List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies, subject,
                "conj");

        for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
            addSubordinatedActionsBySubject(actions, baseAction, dependencies, pair.second);
        }
    }

}
