package seers.bugreppatterns.pattern.sr;

import seers.bugreppatterns.pattern.StepsToReproducePatternMatcher;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.List;

public class ImperativePastSequencePM extends StepsToReproducePatternMatcher {

    @Override
    public int matchSentence(Sentence sentence) throws Exception {

        List<Integer> condTerms = SentenceUtils.findLemmasInTokens(CONDITIONAL_TERMS, sentence.getTokens());
        if (!condTerms.isEmpty()) {
            return 0;
        }

        boolean question = SentenceUtils.isQuestion(sentence);
        if (question) {
            return 0;
        }

        List<Sentence> clauses = SentenceUtils.extractClauses(sentence, SentenceUtils.CLAUSE_SEPARATORS);

        // Needed to recognize the case in which there are no imperatives at all
        int imperativeSentences = 0;

        int i;
        for (i = 0; i < clauses.size(); i++) {
            Sentence clause = clauses.get(i);
            final List<Token> tokensNoBullet = LabeledListPM.getTokensNoBullet(clause);
            if (!SentenceUtils.isImperativeSentence(tokensNoBullet, false, false)) {
                // If the clause is not imperative there is only one case in
                // which it will be
                // accepted as part of this pattern: if it is at the beginning
                // and contains no verbs
                List<Token> tokens = clause.getTokens();
                if (i != 0 || tokens.stream().anyMatch(t -> t.getGeneralPos().equals("VB"))) {
                    break;
                }
            } else {
                imperativeSentences++;
            }
        }

        // If there aren't any imperative sentences at the beginning, there is
        // no match
        if (imperativeSentences == 0) {
            return 0;
        }
/*
        // If we find anything that is not observed behavior, but we accept if
        // it contains no verbs
        for (; i < clauses.size(); i++) {
            Sentence clause = clauses.get(i);
            final List<Token> vbTokens =
                    clause.getTokens().stream().filter(t -> t.getGeneralPos().equals("VB")).collect(Collectors.toList
                    ());
            if (!vbTokens.isEmpty()
                    &&
                    //hack because splits is misidentified as VB
                    vbTokens.stream().noneMatch(token -> token.getWord().equals("splits"))
                    &&
                    // Also accept sentences in future tense
                    clause.getTokens().stream().noneMatch(t -> t.getGeneralPos().equals("MD"))
                    && SentenceUtils.findObsBehaviorSentence(Collections.singletonList(clause)) == -1) {
                return 0;
            }
        }*/

        return 1;
    }

}
