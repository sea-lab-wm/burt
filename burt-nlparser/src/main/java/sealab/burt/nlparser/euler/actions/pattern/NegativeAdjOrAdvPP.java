package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.bugreppatterns.pattern.ob.NegativeTerms;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.*;

import static seers.bugreppatterns.pattern.ob.NegativeTerms.ADJECTIVES;

public class NegativeAdjOrAdvPP extends NLActionPatternParser {

    public NegativeAdjOrAdvPP(boolean debugEnabled) {
        super(debugEnabled);
    }

    @Override
    protected List<NLAction> processSentence(Sentence sentence) throws Exception {

        SemanticGraph dependencies = sentence.getDependencies();

        IndexedWord verbToken = dependencies.getFirstRoot();

        List<NLAction> nlActions = processVerb(dependencies, verbToken, sentence.getQuotes());

        List<Pair<GrammaticalRelation, IndexedWord>> pairs = DependenciesUtils.getChildRelations(dependencies,
                verbToken, "conj");

        for (Pair<GrammaticalRelation, IndexedWord> pair : pairs) {
            nlActions.addAll(processVerb(dependencies, pair.second, sentence.getQuotes()));
        }
        return nlActions;

    }

    private List<NLAction> processVerb(SemanticGraph dependencies, IndexedWord verbToken,
                                       HashMap<String, List<Sentence>> quoteMap) {

        List<NLAction> actions = new ArrayList<>();

        if (isPassiveVoice(dependencies, verbToken) || isPassiveVoice2(dependencies, verbToken)) {
            NLAction action = processPassiveVoiceSentence(dependencies, verbToken);
            if (action != null) return Collections.singletonList(action);
        }

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

    private boolean isPassiveVoice2(SemanticGraph dependencies, IndexedWord verbToken) {

        Pair<GrammaticalRelation, IndexedWord> auxRel = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "cop");
        if (auxRel != null && auxRel.second.lemma().equals("be")
                && (verbToken.tag().equals("VBN") || verbToken.tag().equals("VBD") || verbToken.tag().equals("JJ"))) {
            return true;
        }

        return false;
    }

    private NLAction processPassiveVoiceSentence(SemanticGraph dependencies, IndexedWord verbOrAdjToken) {

        if (!SentenceUtils.stringEqualsToAnyToken(NegativeTerms.VERBS, verbOrAdjToken.lemma())
                && !SentenceUtils.stringEqualsToAnyToken(ADJECTIVES, verbOrAdjToken.word())) {
            return null;
        }

        // find the subject of the verb
        Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbOrAdjToken, "nsubjpass", "nsubj");

        // no personal pronouns: I, we, and you
        if (subj != null) {
            IndexedWord subjToken = subj.second;
            if (isPersonalPronoun(subjToken)) {
                return null;
            }
        }

        String subject = "app";
        if (subj != null) {
            subject = getObject(dependencies, subj.second);
        }

        // find the object of the verb
        Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbOrAdjToken, "dobj");
        IndexedWord objToken = null;
        if (objRelation != null) {
            objToken = objRelation.second;
        }

		Pair<GrammaticalRelation, IndexedWord> auxPass = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbOrAdjToken, "auxpass", "cop");

        // add action
        NLAction action = new NLAction(null, subject, auxPass.second.word().toLowerCase(),
				verbOrAdjToken.word().toLowerCase(), null, null, actType);
        setPrepositionalClause(dependencies, verbOrAdjToken, objToken, action, null);
        setActionNegation(dependencies, verbOrAdjToken, action);

        return action;

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
