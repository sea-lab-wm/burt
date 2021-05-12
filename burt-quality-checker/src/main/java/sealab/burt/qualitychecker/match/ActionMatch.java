package sealab.burt.qualitychecker.match;

import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;

public abstract class ActionMatch {

	private double score;
	private NLAction action;

	private boolean requiresScoreNormalization;

	public ActionMatch(double score, NLAction action, boolean requiresScoreNormalization) {
		super();
		this.score = score;
		this.action = action;
		this.setRequiresScoreNormalization(requiresScoreNormalization);
	}

	public abstract <T> T getPossibleMatch();

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public NLAction getAction() {
		return action;
	}

	public void setAction(NLAction action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "[sc=" + getScoreStr() + ", mt=" + getPossibleMatch() + "]";
	}

	private String getScoreStr() {
		return GeneralUtils.format(score);
	}

	public abstract boolean isTransitionMatch();

	public abstract boolean isStateMatch();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((getPossibleMatch() == null) ? 0 : getPossibleMatch().hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionMatch other = (ActionMatch) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (getPossibleMatch() == null) {
			if (other.getPossibleMatch() != null)
				return false;
		} else if (!getPossibleMatch().equals(other.getPossibleMatch()))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		return true;
	}

	public boolean isRequiresScoreNormalization() {
		return requiresScoreNormalization;
	}

	public void setRequiresScoreNormalization(boolean requiresScoreNormalization) {
		this.requiresScoreNormalization = requiresScoreNormalization;
	}

}
