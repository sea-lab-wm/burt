package sealab.burt.nlparser.euler.actions.nl;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;
import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;


import java.util.ArrayList;

public class NLAction {

	@SerializedName("sq")
	private Integer sequence;
	@SerializedName("sub")
	private String subject;
	@SerializedName("act")
	private String action;
	@SerializedName("obj")
	private String object;
	@SerializedName("prep")
	private String preposition;
	@SerializedName("obj2")
	private String object2;
	@SerializedName("neg")
	private Boolean actionNegated;
	@SerializedName("ty")
	private ActionType type;

	private String parsingClass;
	private String originalSentence;
	private String sentenceId;

	// in the case of crashes
	private StackTrace trace;

	private Integer scenarioId;

	public NLAction(Integer sequence, String subject, String action, String object, Boolean actionNegated,
			ActionType type) {
		this(sequence, subject, action, object, null, null, actionNegated, type);
	}

	public NLAction(Integer sequence, String subject, String action, String object, ActionType type) {
		this(sequence, subject, action, object, null, null, null, type);
	}

	public NLAction(Integer sequence, String subject, String action, String object, String preposition, String object2,
			ActionType type) {
		this(sequence, subject, action, object, preposition, object2, null, type);
	}

	public NLAction(Integer sequence, String subject, String action, String object, String preposition, String object2,
			Boolean actionNegated, ActionType type) {
		super();
		this.sequence = sequence;
		this.subject = subject;
		this.action = action;
		this.object = object;
		this.preposition = preposition;
		this.object2 = object2;
		this.actionNegated = actionNegated;
		this.setType(type);
	}

	public NLAction(NLAction action2) {
		this(action2.sequence, action2.subject, action2.action, action2.object, action2.preposition, action2.object2,
				action2.actionNegated, action2.getType());
	}

	public Integer getSequence() {
		return sequence;
	}

	public String getSubject() {
		return subject;
	}

	public String getAction() {
		return action;
	}

	public String getObject() {
		return object;
	}

	public String getPreposition() {
		return preposition;
	}

	public String getObject2() {
		return object2;
	}

	public Boolean isActionNegated() {
		return actionNegated;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public void setPreposition(String preposition) {
		this.preposition = preposition;
	}

	public void setObject2(String object2) {
		this.object2 = object2;
	}

	public void setActionNegated(Boolean actionNegated) {
		this.actionNegated = actionNegated;
	}

	@Override
	public String toString() {
		return "(" + getType() + ")-[sq=" + sequence + ", sub=" + subject + ", neg="
				+ (actionNegated != null ? ((actionNegated ? "not" : "")) : null) + ", act=" + action + ", ob=" + object
				+ ", prep=" + preposition + ", obj2=" + object2 + "]-[" + parsingClass + "]-[" + sentenceId + "]";
	}

	public String toListString(boolean noUser, boolean selectAction) {

		ArrayList<String> strFields = new ArrayList<String>();
		if (noUser && !"user".equalsIgnoreCase(subject)) {
			strFields.add(subject);
		}

		if (selectAction) {
			strFields.add(action);
		}

		strFields.add(object);
		strFields.add(preposition);
		strFields.add(object2);

		return StringUtils.join(strFields, " ");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actionNegated == null) ? 0 : actionNegated.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((object2 == null) ? 0 : object2.hashCode());
		result = prime * result + ((preposition == null) ? 0 : preposition.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
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
		NLAction other = (NLAction) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actionNegated == null) {
			if (other.actionNegated != null)
				return false;
		} else if (!actionNegated.equals(other.actionNegated))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (object2 == null) {
			if (other.object2 != null)
				return false;
		} else if (!object2.equals(other.object2))
			return false;
		if (preposition == null) {
			if (other.preposition != null)
				return false;
		} else if (!preposition.equals(other.preposition))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (getType() != other.getType())
			return false;
		return true;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public String getParsingClass() {
		return parsingClass;
	}

	public void setParsingClass(String parsingClass) {
		this.parsingClass = parsingClass;
	}

	public String getOriginalSentence() {
		return originalSentence;
	}

	public void setOriginalSentence(String originalSentence) {
		this.originalSentence = originalSentence;
	}

	public Integer getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Integer scenarioId) {
		this.scenarioId = scenarioId;
	}

	public StackTrace getTrace() {
		return trace;
	}

	public void setTrace(StackTrace trace) {
		this.trace = trace;
	}

	public boolean isCrash() {

		boolean check = isOBAction() && (this.actionNegated == null || !this.actionNegated)

				&& ("crash".equalsIgnoreCase(action)
						|| (GeneralUtils.isAppSubject(this, "") && "fail".equalsIgnoreCase(action))
						|| "force close".equalsIgnoreCase(action)

						// -----------------------------------

						|| (("get".equalsIgnoreCase(action) || "cause".equalsIgnoreCase(action)
								|| "give".equalsIgnoreCase(action) || "throw".equalsIgnoreCase(action))
								&& (object != null && (object.toLowerCase().contains("error")
										|| object.toLowerCase().contains("exception")
										|| object.toLowerCase().contains("crash")))))

				// ---------------------------------

				|| ((preposition != null && preposition.equals("with")) && (object != null
						&& (object2.toLowerCase().contains("error") || object2.toLowerCase().contains("exception")
								|| object2.toLowerCase().contains("crash"))))

				// ---------------------------------

				|| ("close".equalsIgnoreCase(action) && (subject != null && subject.toLowerCase().contains("force")))

				// ----------------------------------
				|| ((subject != null && (subject.toLowerCase().contains("error")
						|| subject.toLowerCase().contains("exception") || subject.toLowerCase().contains("crash")))
						&& ("occur".equalsIgnoreCase(action) || "happen".equalsIgnoreCase(action)));

		return check;
	}

	public String getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(String sentenceId) {
		this.sentenceId = sentenceId;
	}

	public boolean isOBAction() {
		return ActionType.OB.equals(this.type);
	}

	public boolean isSRAction() {
		return ActionType.SR.equals(this.type);
	}

}
