package sealab.burt.nlparser.euler.actions.pattern;

import seers.bugreppatterns.pattern.sr.MenuNavigationPM;
import seers.bugreppatterns.utils.SentenceUtils;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

public class ActionsSeparatorPP extends NLActionPatternParser {

    private ActionsInfinitivePP infActPP;

    public ActionsSeparatorPP(boolean debugEnabled) {
        super(debugEnabled);
        infActPP = new ActionsInfinitivePP(debugEnabled);
    }

    @Override
    protected List<NLAction> processSentence(Sentence sentence) throws Exception {
        infActPP.setActType(actType);

        List<Sentence> clauses = SentenceUtils.extractClausesBySeparators(sentence, MenuNavigationPM.SEPARATORS);

        List<NLAction> actions = new ArrayList<>();

        for (Sentence clause : clauses) {

            String text = TextProcessor.getStringFromLemmas(clause);
            if ("menu".equals(text)) {
                NLAction action = new NLAction(null, "user", "click", "menu", actType);
                actions.add(action);
            } else if (clause.getTokens().size() == 1 && clause.getTokens().get(0).getGeneralPos().equals("NN")) {
                NLAction action = new NLAction(null, "user", "click", clause.getTokens().get(0).getWord(), actType);
                actions.add(action);
            } else {
                List<NLAction> clauseActions = infActPP.processSentence(clause);
                actions.addAll(clauseActions);
            }
        }

        return actions;
    }

}
