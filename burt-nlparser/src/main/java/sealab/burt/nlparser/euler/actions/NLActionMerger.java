package sealab.burt.nlparser.euler.actions;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NLActionMerger {

	private static final Logger LOGGER = LoggerFactory.getLogger(NLActionMerger.class);

	private final String systemName;

	public NLActionMerger(String systemName) {
		super();
		this.systemName = systemName;
	}

	public List<BugScenario> mergeActionsInScenarios(List<BugScenario> scenarios) {
		List<BugScenario> mergedScenarios = new ArrayList<>();
		scenarios.forEach(s -> {
			BugScenario mrgScn = mergeActions(s);
			mergedScenarios.add(mrgScn);
		});

		return mergedScenarios;
	}

	private BugScenario mergeActions(BugScenario scenario) {

		LinkedList<NLAction> mergedActions = new LinkedList<>(scenario.getActions());
		for (NLAction nlAction : scenario.getActions()) {
			mergeAction(mergedActions, nlAction);
		}

		return new BugScenario(scenario.getId(), mergedActions, true);
	}

	private void mergeAction(LinkedList<NLAction> mergedActions, NLAction nlAction) {
		int i = mergedActions.indexOf(nlAction);
		if (i == -1) {
			return;
		}

		NLAction actualAction = mergedActions.get(i);
		List<NLAction> actionsToRemove = new ArrayList<>();
		int positionToMove = -1;

		boolean isAppSubject1 = GeneralUtils.isAppSubject(nlAction, systemName);

		for (int j = 0; j < mergedActions.size(); j++) {
			NLAction currentAction = mergedActions.get(j);

			if (actualAction.equals(currentAction)) {
				continue;
			}

			if (!currentAction.getType().equals(actualAction.getType())) {
				continue;
			}

			boolean removeAction = false;
			boolean isAppSubject2 = GeneralUtils.isAppSubject(currentAction, systemName);
			if (!isAppSubject1 && isAppSubject2) {
				if (equalsIgnoreCaseNoSubject(actualAction, currentAction)) {
					removeAction = true;
				} else if (equalsIgnoreCaseNoSubjectPrepObject2(currentAction, actualAction)) {
					removeAction = true;
				}
			} else if (equalsIgnoreCaseNoPrepObject2(currentAction, actualAction)) {
				removeAction = true;
			} else if (containsObjectsIgnoreCase(actualAction, currentAction)) {
				removeAction = true;
			} else if (isAppSubject1 && isAppSubject2) {
				if (equalsIgnoreCaseNoSubject(actualAction, currentAction)) {
					removeAction = true;
				}
			}

			if (removeAction) {
				// move the actual action if the current action is
				// positioned after it
				if (positionToMove < j && i < j) {
					positionToMove = j;
				}
				actionsToRemove.add(currentAction);
			}

		}

		// move the actual action
		if (positionToMove != -1) {
			mergedActions.add(positionToMove, actualAction);
			mergedActions.remove(i);
		}

		if (!actionsToRemove.isEmpty()) {

			boolean removeAll = mergedActions.removeAll(actionsToRemove);
			if (!removeAll) {
				LOGGER.warn("Could not remove action when merging!");
			}
		}
	}

	private boolean containsObjectsIgnoreCase(NLAction thisAct, NLAction other) {

		if (thisAct.getSubject() == null) {
			if (other.getSubject() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getSubject(), other.getSubject()))
			return false;

		if (thisAct.getAction() == null) {
			if (other.getAction() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getAction(), other.getAction()))
			return false;

		if (thisAct.isActionNegated() == null) {
			if (other.isActionNegated() != null)
				return false;
		} else if (!thisAct.isActionNegated().equals(other.isActionNegated()))
			return false;

		if (thisAct.getPreposition() == null) {
			if (other.getPreposition() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getPreposition(), other.getPreposition()))
			return false;

		// -----------------------------

		boolean case1 = true;
		if (thisAct.getObject2() == null) {
			if (other.getObject2() != null)
				case1 = false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject2(), other.getObject2()))
			case1 = false;

		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				case1 = false;
		} else if (!containsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			case1 = false;

		// -----------------------------
		boolean case2 = true;

		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				case2 = false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			case2 = false;

		if (thisAct.getObject2() == null) {
			if (other.getObject2() != null)
				case2 = false;
		} else if (!containsLemmaIgnoreCase(thisAct.getObject2(), other.getObject2()))
			case2 = false;

		// ------------------
		boolean case3 = true;

		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				case3 = false;
		} else if (!containsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			case3 = false;

		if (thisAct.getObject2() == null) {
			if (other.getObject2() != null)
				case3 = false;
		} else if (!containsLemmaIgnoreCase(thisAct.getObject2(), other.getObject2()))
			case3 = false;

		// ------------------
		return case1 || case2 || case3;
	}

	private boolean containsLemmaIgnoreCase(String txt1, String txt2) {
		List<Sentence> stncs1 = TextProcessor.processText(txt1);

		if (txt2 == null) {
			return false;
		}

		List<Sentence> stncs2 = TextProcessor.processText(txt2);

		List<String> terms1 = new ArrayList<>();
		stncs1.stream()
				.forEach(s -> terms1.addAll(s.getTokens().stream().map(Token::getLemma).collect(Collectors.toList())));
		List<String> terms2 = new ArrayList<>();
		stncs2.stream()
				.forEach(s -> terms2.addAll(s.getTokens().stream().map(Token::getLemma).collect(Collectors.toList())));

		String join1 = StringUtils.join(terms1, ' ').toLowerCase();
		String join2 = StringUtils.join(terms2, ' ').toLowerCase();
		return join1.startsWith(join2 + " ") || join1.endsWith(" " + join2) || join2.startsWith(join1 + " ")
				|| join2.endsWith(" " + join1);
	}

	private boolean equalsIgnoreCaseNoSubjectPrepObject2(NLAction thisAct, NLAction other) {
		if (thisAct.getAction() == null) {
			if (other.getAction() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getAction(), other.getAction()))
			return false;
		if (thisAct.isActionNegated() == null) {
			if (other.isActionNegated() != null)
				return false;
		} else if (!thisAct.isActionNegated().equals(other.isActionNegated()))
			return false;
		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			return false;

		if (thisAct.getPreposition() == null && thisAct.getObject2() == null) {
			if (other.getPreposition() != null || other.getObject2() != null) {
				return true;
			}
		}

		return false;
	}

	private boolean equalsIgnoreCaseNoPrepObject2(NLAction thisAct, NLAction other) {
		if (thisAct.getSubject() == null) {
			if (other.getSubject() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getSubject(), other.getSubject()))
			return false;
		if (thisAct.getAction() == null) {
			if (other.getAction() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getAction(), other.getAction()))
			return false;
		if (thisAct.isActionNegated() == null) {
			if (other.isActionNegated() != null)
				return false;
		} else if (!thisAct.isActionNegated().equals(other.isActionNegated()))
			return false;
		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			return false;

		if (thisAct.getPreposition() == null && thisAct.getObject2() == null) {
			if (other.getPreposition() != null || other.getObject2() != null) {
				return true;
			}
		}

		return false;
	}

	private boolean equalsIgnoreCaseNoSubject(NLAction thisAct, NLAction other) {
		if (thisAct.getAction() == null) {
			if (other.getAction() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getAction(), other.getAction()))
			return false;
		if (thisAct.isActionNegated() == null) {
			if (other.isActionNegated() != null)
				return false;
		} else if (!thisAct.isActionNegated().equals(other.isActionNegated()))
			return false;
		if (thisAct.getObject() == null) {
			if (other.getObject() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject(), other.getObject()))
			return false;
		if (thisAct.getObject2() == null) {
			if (other.getObject2() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getObject2(), other.getObject2()))
			return false;
		if (thisAct.getPreposition() == null) {
			if (other.getPreposition() != null)
				return false;
		} else if (!equalsLemmaIgnoreCase(thisAct.getPreposition(), other.getPreposition()))
			return false;
		return true;
	}

	private boolean equalsLemmaIgnoreCase(String txt1, String txt2) {
		List<Sentence> stncs1 = TextProcessor.processText(txt1);

		if (txt2 == null) {
			return false;
		}

		List<Sentence> stncs2 = TextProcessor.processText(txt2);

		List<String> terms1 = new ArrayList<>();
		stncs1.stream()
				.forEach(s -> terms1.addAll(s.getTokens().stream().map(Token::getLemma).collect(Collectors.toList())));
		List<String> terms2 = new ArrayList<>();
		stncs2.stream()
				.forEach(s -> terms2.addAll(s.getTokens().stream().map(Token::getLemma).collect(Collectors.toList())));

		String join1 = StringUtils.join(terms1, ' ');
		String join2 = StringUtils.join(terms2, ' ');
		return join1.equalsIgnoreCase(join2);
	}

}
