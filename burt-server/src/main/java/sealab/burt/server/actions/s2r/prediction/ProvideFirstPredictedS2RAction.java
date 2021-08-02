package sealab.burt.server.actions.s2r.prediction;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.s2r.missing.SelectMissingS2RAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;

@Slf4j
public class ProvideFirstPredictedS2RAction extends ChatBotAction {

    public final static int MAX_NUMBER_OF_PATHS_TO_SHOW = 3;
    private final static int MAX_STEPS_TO_SHOW_IN_PATH = 5;

    public ProvideFirstPredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

        state.put(PREDICTING_S2R, true);

        //target state
        List<BugReportElement> obReportElements = (List<BugReportElement>) state.get(REPORT_OB);
        if (obReportElements == null) {
            return getNextStepMessage();
        }

        BugReportElement obReportElement = obReportElements.get(0);

        GraphState obState;
        if (obReportElement == null || obReportElement.getOriginalElement() == null) {
            return getNextStepMessage();
        }

        obState = (GraphState) obReportElement.getOriginalElement();

        //-----------------------------------------------

        //current state
        S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
        S2RPredictor predictor = new S2RPredictor(checker.getGraph());
        GraphState currentState = checker.getCurrentState();// this is the target state of the last step

        //-------------------

        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);
        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();

        //Check if maybe the last reported step, executed on the ob state and it is not a loop, then it is the real
        // last step, so we verify with the user

        if (lastStep != null) {//the last step can be null because of the max # of attempts functionality
            GraphTransition transition = lastStep.getTransition();
            if (transition != null) {
                GraphState sourceState = transition.getSourceState();
                if (sourceState.equals(obState) && !currentState.equals(obState)) {
                    return getLastStepMessage(state, bugReportElements.get(bugReportElements.size() - 1));
                }
            }
        }

        //-------------------

        List<AppStep> nonSelectedSteps = (List<AppStep>) state.get(StateVariable.NON_SELECTED_PREDICTED_S2R);

        List<List<AppStep>> pathsWithLoops;
        if (currentState.equals(obState)) { //the purpose of this branch is to predict loops

            log.debug("Predicting S2R (loops)");

            List<AppStep> stateLoops = new ArrayList<>();
            if (lastStep != null) { //the last step can be null because of the max # of attempts functionality
                if (DeviceUtils.isCloseApp(lastStep.getAction()))
                    return getNextStepMessage();

                stateLoops = predictor.getStateLoops(currentState, lastStep, nonSelectedSteps);

                if (stateLoops.isEmpty()) {
                    return getLastStepMessage(state, bugReportElements.get(bugReportElements.size() - 1));
                }
            }

            pathsWithLoops = Collections.singletonList(stateLoops);
        } else {// current state != ob state

            //get all the paths sorted according  to the scoring mechanism
            List<GraphPath<GraphState, GraphTransition>> predictedPaths = predictor.getAllRankedPaths(currentState,
                    obState);
            log.debug("Total number of predicted paths: " + predictedPaths.size());

            if (predictedPaths.isEmpty()) {
                return getNextStepMessage();
            }

            //----------------------------------------

            // the method getPathWithLoops() only returns a subset of the steps for each path
            pathsWithLoops = predictedPaths.stream()
                    .map(path -> predictor.getPathWithLoops(checker, path, state, currentState, nonSelectedSteps,
                            MAX_STEPS_TO_SHOW_IN_PATH))
                    .distinct()
                    .collect(Collectors.toList());

        }

        state.put(StateVariable.PREDICTED_S2R_PATHS_WITH_LOOPS, pathsWithLoops);
        state.put(PREDICTED_S2R_NUMBER_OF_PATHS, Math.min(MAX_NUMBER_OF_PATHS_TO_SHOW, pathsWithLoops.size()));

        int currentPath = 0;
        state.put(PREDICTED_S2R_CURRENT_PATH, currentPath);

        // get the first predicted path
        List<KeyValues> stepOptions = SelectMissingS2RAction.getStepOptions(pathsWithLoops.get(currentPath), state);

        if (stepOptions.isEmpty()) {
            return getNextStepMessage();
        }

        //----------------------------------------

        log.debug("Suggesting path #" + currentPath);

        setNextExpectedIntents(Collections.singletonList(Intent.S2R_PREDICTED_SELECTED));

        MessageObj messageObj = new MessageObj("<b>Input values</b> and <b>UI components</b> may be a little different from what you observed in the app.",
                WidgetName.S2RScreenSelector);
        return createChatBotMessages(
                "Okay, it seems <b>the next steps</b> that you performed might be the following.",
                "Can you select the ones you actually performed next?",
                "Remember that the screenshots below are <b>for reference only</b>.",
                new ChatBotMessage(messageObj, stepOptions, true));
    }

    private List<ChatBotMessage> getLastStepMessage(ConversationState state, BugReportElement lastStep) {
        setNextExpectedIntents(Collections.singletonList(Intent.NO_EXPECTED_INTENT));
        state.put(StateVariable.CONFIRM_LAST_STEP, true);
        return createChatBotMessages(String.format("Okay, is this the last step that you performed: \"%s\"?",
                lastStep.getStringElement())
        );
    }

    private List<ChatBotMessage> getNextStepMessage() {
        setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
        return createChatBotMessages("Okay, can you please provide the next step?");
    }

}
