package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

public class ConditionalPositivePP extends NLActionPatternParser {

	public ConditionalPositivePP(boolean debugEnabled) {
		super(debugEnabled);
	}

	@Override
	protected List<NLAction> processSentence(Sentence sentence) throws Exception {

		SemanticGraph dependencies = sentence.getDependencies();

		IndexedWord verbToken = dependencies.getFirstRoot();

		List<NLAction> actions = processVerb(dependencies, verbToken);

		return actions;

	}

	private List<NLAction> processVerb(SemanticGraph dependencies, IndexedWord verbToken) {

		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (!TextProcessor.checkGeneralPos(verbToken.tag(), "VB")) {

			if (verbToken.lemma().toLowerCase().equals("crash")) {
				actions = processCrash(dependencies, verbToken);
			}
			return actions;
		}

		// -----------------------------------------
		Pair<GrammaticalRelation, IndexedWord> markRel = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "mark");
		if (markRel != null) {
			return actions;
		}

		// -----------------------------------------

		Pair<GrammaticalRelation, IndexedWord> explRel = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "expl");
		if (explRel != null && verbToken.lemma().equals("be") && explRel.second.lemma().equals("there")) {
			actions = processExistential(dependencies, verbToken);
			return actions;
		}

		// -----------------------------------------

		if (isPassiveVoice(dependencies, verbToken)) {
			actions = processPassiveVoice(dependencies, verbToken);
			return actions;
		}

		// -----------------------------------------
		if (verbToken.lemma().equals("be")) {
			return actions;
		}

		// -----------------------------------------

		// the object modified by the verb
		Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "dobj");

		IndexedWord objToken = null;
		if (objRelation != null) {
			objToken = objRelation.second;
		} else {

			// preventive check
			if (verbToken.lemma().equals("get")) {
				return actions;
			}
		}

		// find the subject of the verb
		Pair<GrammaticalRelation, IndexedWord> subj = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
				"nsubj");

		if (subj == null) {
			return actions;
		}

		String subject = getSubject(dependencies, subj.second);

		if (subject == null) {
			return actions;
		}

		// build the action
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
				getObject(dependencies, objToken), actType);
		setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
		setActionNegation(dependencies, verbToken, nlAction);
		actions.add(nlAction);

		return actions;

	}

	private List<NLAction> processPassiveVoice(SemanticGraph dependencies, IndexedWord verbToken) {

		List<NLAction> actions = new ArrayList<>();

		Pair<GrammaticalRelation, IndexedWord> objRel2 = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "nsubjpass");
		if (objRel2 == null) {
			objRel2 = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken, "nsubj");
		}

		IndexedWord objToken = null;
		if (objRel2 != null) {
			if (objRel2.second.tag().equals("PRP")) {
				// the object modified by the verb
				Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils
						.getFirstChildByRelation(dependencies, verbToken, "dobj");
				if (objRelation != null) {
					objToken = objRelation.second;
				}
			} else {
				objToken = objRel2.second;
			}
		}

		// build the action
		String subject = "app";
		
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
				getObject(dependencies, objToken), actType);
		setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
		setActionNegation(dependencies, verbToken, nlAction);

		actions.add(nlAction);

		return actions;

	}

	private List<NLAction> processExistential(SemanticGraph dependencies, IndexedWord verbToken) {

		Pair<GrammaticalRelation, IndexedWord> subjRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "nsubj");

		List<NLAction> actions = new ArrayList<>();
		if (subjRelation == null) {
			return actions;
		}

		IndexedWord subToken = subjRelation.second;

		Pair<GrammaticalRelation, IndexedWord> refRel = DependenciesUtils.getFirstChildByRelation(dependencies,
				subToken, "ref");

		IndexedWord objToken = null;
		if (refRel != null) {

			Pair<GrammaticalRelation, IndexedWord> aclRel = DependenciesUtils.getFirstChildByRelation(dependencies,
					subToken, "acl:relcl");

			if (aclRel != null) {
				verbToken = aclRel.second;
			}

			// the object modified by the verb
			Pair<GrammaticalRelation, IndexedWord> objRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
					verbToken, "dobj");

			if (objRelation != null) {
				objToken = objRelation.second;
			}
		}

		// build the action
		String subject = getObject(dependencies, subToken);
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken),
				getObject(dependencies, objToken), actType);
		setPrepositionalClause(dependencies, verbToken, objToken, nlAction, null);
		setActionNegation(dependencies, verbToken, nlAction);

		actions.add(nlAction);

		return actions;
	}

	private List<NLAction> processCrash(SemanticGraph dependencies, IndexedWord verbToken) {

		List<NLAction> actions = new ArrayList<>();

		// check for the verb
		if (!TextProcessor.checkGeneralPos(verbToken.tag(), "NN") || !verbToken.lemma().toLowerCase().equals("crash")) {
			return actions;
		}

		Pair<GrammaticalRelation, IndexedWord> subjRelation = DependenciesUtils.getFirstChildByRelation(dependencies,
				verbToken, "compound");

		String subject = null;
		if (subjRelation != null) {
			subject = getSubjectFromNoun(dependencies, subjRelation.second);
		} else {
			List<IndexedWord> children = new ArrayList<>(dependencies.getChildren(verbToken));

			if (children.size() == 1 && TextProcessor.checkGeneralPos(children.get(0).tag(), "VB")) {
				subject = "app";
			} else {
				return actions;
			}
		}

		if (subject == null) {
			return actions;
		}

		// build the action
		NLAction nlAction = new NLAction(null, subject, getVerb(dependencies, verbToken), null, actType);
		actions.add(nlAction);

		return actions;
	}

	private String getSubjectFromNoun(SemanticGraph dependencies, IndexedWord subjToken) {

		if (isPersonalPronoun(subjToken)) {
			return null;
		}

		String subject = getObject(dependencies, subjToken);
		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		}
		return subject;
	}

	private String getSubject(SemanticGraph dependencies, IndexedWord subjToken) {

		String subject = "user";

		if (isPersonalPronoun(subjToken)) {
			return null;
		}

		if (TextProcessor.checkGeneralPos(subjToken.tag(), "PRP")) {
			if (subjToken.lemma().equals("it")) {
				subject = "app";
			}
		} else if (subjToken.tag().equals("WDT")) {
			subject = "app";
		} else {
			subject = getObject(dependencies, subjToken);
		}
		return subject;
	}

}
