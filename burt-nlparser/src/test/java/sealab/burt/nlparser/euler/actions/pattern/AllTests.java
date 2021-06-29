package sealab.burt.nlparser.euler.actions.pattern;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ActionsInfinitivePPTest.class, ConditionalObsBehaviorPPTest.class, ErrorNounPhrasePPTest.class,
		NegativeAuxVerbPPTest.class, LabeledListPPTest.class, VerbErrorPPTest.class, NegativeVerbPPTest.class,
		ImperativeSequencePPTest.class, OutputVerbPPTest.class, PassiveVoicePPTest.class, ActionsMultiPPTest.class,
		NegativeAdjOrAdvPPTest.class, ConditionalPositivePPTest.class, NegativeConditionalPPTest.class,
		GerundActionPPTest.class, ActionsSeparatorPPTest.class, ImperativePastSequencePPTest.class, ShouldPPTest.class })
public class AllTests {

}
