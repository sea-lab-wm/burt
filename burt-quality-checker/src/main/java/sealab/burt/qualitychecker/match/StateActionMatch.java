package sealab.burt.qualitychecker.match;


import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.qualitychecker.graph.GraphState;

public class StateActionMatch extends ActionMatch {

	private GraphState state;

	public StateActionMatch(double score, NLAction action, GraphState state, boolean scoreNormalization) {
		super(score, action, scoreNormalization);
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GraphState getPossibleMatch() {
		return state;
	}

	@Override
	public boolean isTransitionMatch() {
		return false;
	}

	@Override
	public boolean isStateMatch() {
		return true;
	}

}
