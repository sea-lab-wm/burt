package sealab.burt.nlparser.euler.actions.nl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BugScenario {

	private Integer id;
	private LinkedList<NLAction> actions;
	private int seq = 1;

	public BugScenario(Integer id) {
		actions = new LinkedList<>();
		this.id = id;
	}

	public BugScenario(Integer id, LinkedList<NLAction> actions) {
		this(id, actions, false);
	}

	public BugScenario(Integer id, LinkedList<NLAction> actions, boolean reSequence) {
		this.id = id;
		if (actions != null && !actions.isEmpty()) {
			if (reSequence) {
				this.actions = new LinkedList<>();
				for (NLAction nlAction : actions) {
					addAction(nlAction);
				}
			} else {
				Optional<NLAction> max = actions.stream().max((a, b) -> a.getSequence().compareTo(b.getSequence()));
				this.seq = max.get().getSequence() + 1;
				this.actions = actions;
			}
		} else {
			this.actions = new LinkedList<>();
		}
	}

	public void addNotRepeatedAction(NLAction action) {
		if (!actions.contains(action)) {
			addAction(action);
		}
	}

	public void addAction(NLAction action) {
		action.setSequence(seq++);
		actions.add(action);
	}

	public LinkedList<NLAction> getActions() {
		return actions;
	}

	@Override
	public String toString() {
		return "BugScenario [id=" + id + ", actions=\n" + getStringActions() + "]\n";
	}

	private String getStringActions() {
		StringBuffer buffer = new StringBuffer();
		for (NLAction action : actions) {
			buffer.append("\t" + action.toString());
			buffer.append("\t\n");
		}
		if (buffer.length() > 0) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}
		return buffer.toString();
	}

	public void addActions(List<NLAction> actions2) {
		for (NLAction nlAction : actions2) {
			addAction(nlAction);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
