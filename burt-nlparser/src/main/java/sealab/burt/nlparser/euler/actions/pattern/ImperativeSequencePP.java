package sealab.burt.nlparser.euler.actions.pattern;

import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.entity.Sentence;

import java.util.List;

public class ImperativeSequencePP extends NLActionPatternParser {

	private ActionsInfinitivePP infActPP;

	public ImperativeSequencePP(boolean debugEnabled) {
		super(debugEnabled);
		infActPP = new ActionsInfinitivePP(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) throws Exception {
		infActPP.setActType(actType);
		return infActPP.processSentence(sentence);
	}

}
