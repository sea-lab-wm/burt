package sealab.burt.qualitychecker.match;


import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.graph.GraphTransition;

public class TransitionActionMatch extends ActionMatch {

	private GraphTransition transition;

	public TransitionActionMatch(double score, NLAction action, GraphTransition transition, boolean scoreNormalization) {
		super(score, action, scoreNormalization);
		this.transition = transition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GraphTransition getPossibleMatch() {
		return transition;
	}

	@Override
	public boolean isTransitionMatch() {
		return true;
	}

	@Override
	public boolean isStateMatch() {
		return false;
	}

}
