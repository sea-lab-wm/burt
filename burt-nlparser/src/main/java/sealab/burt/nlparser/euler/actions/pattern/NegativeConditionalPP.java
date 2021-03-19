package sealab.burt.nlparser.euler.actions.pattern;

import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.entity.Sentence;

import java.util.List;

public class NegativeConditionalPP extends NLActionPatternParser {


	private ConditionalPositivePP pp;

	public NegativeConditionalPP(boolean debugEnabled) {
		super(debugEnabled);
		pp = new ConditionalPositivePP(debugEnabled);
	}

	@Override
	public List<NLAction> processSentence(Sentence sentence) throws Exception {
		pp.setActType(actType);
		return pp.processSentence(sentence);
	}


}
