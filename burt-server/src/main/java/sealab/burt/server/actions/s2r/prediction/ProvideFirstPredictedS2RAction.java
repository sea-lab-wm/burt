package sealab.burt.server.actions.s2r.prediction;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppGraph;
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

    public final static int MAX_NUMBER_OF_PATHS_TO_SHOW = 2;
    private final static int MAX_STEPS_TO_SHOW_IN_PATH = 5;

    public ProvideFirstPredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        state.put(COLLECTING_S2R, true);
        state.put(PREDICTING_S2R, true);

        if (!state.containsKey(S2R_CHECKER)) {
            String appName = state.get(APP_NAME).toString();
            String appVersion = state.get(APP_VERSION).toString();
            state.put(S2R_CHECKER, new S2RChecker(appName, appVersion));
        }
        S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);

        //----------------------------------------------
        boolean noStepsReportedYet = false;

        //not S2R reported yet
        if (state.containsKey(StateVariable.COLLECTING_FIRST_S2R)) {
            state.remove(StateVariable.COLLECTING_FIRST_S2R);
            noStepsReportedYet = true;

            //--------------------
            //Add the open app S2R

            AppGraph<GraphState, GraphTransition> graph = checker.getGraph();
            GraphTransition openAppEdge = graph.outgoingEdgesOf(GraphState.START_STATE)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (openAppEdge == null) throw new RuntimeException("There is no open \"app step\" in the graph");

            state.getStateUpdater().addStepsToState(state, Collections.singletonList(openAppEdge.getStep()));
        }

        //-----------------------------------------------

        //obtain the target state (i.e., the OB state if any
        List<BugReportElement> obReportElements = (List<BugReportElement>) state.get(REPORT_OB);
        //if no OB state, there is nothing to predict
        if (obReportElements == null) {
            if (noStepsReportedYet)
                return getFirstStepMessages(state);
            return getNextStepMessage();
        }

        BugReportElement obReportElement = obReportElements.get(0);

        GraphState obState;
        //if no OB state, there is nothing to predict
        if (obReportElement == null || obReportElement.getOriginalElement() == null) {
            if (noStepsReportedYet)
                return getFirstStepMessages(state);
            return getNextStepMessage();
        }

        obState = (GraphState) obReportElement.getOriginalElement();

        //-----------------------------------------------

        //current state
        S2RPredictor predictor = new S2RPredictor(checker.getGraph());
        GraphState currentState = checker.getCurrentState();// this is the target state of the last step

        //-------------------

        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);
        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();

        //Check if maybe the last reported step is executed on the OB state and it is not a loop, then it is the real
        // last step, so we verify with the user

        if (lastStep != null) {//the last step can be null because of the max # of attempts
            // functionality
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
            if (lastStep != null) { //the last step can be null because of the max # of attempts
                // functionality
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
                if (noStepsReportedYet)
                    return getFirstStepMessages(state);
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
            if (noStepsReportedYet)
                return getFirstStepMessages(state);
            return getNextStepMessage();
        }

        //----------------------------------------

        log.debug("Suggesting path #" + currentPath);

        setNextExpectedIntents(Collections.singletonList(Intent.S2R_PREDICTED_SELECTED));

        MessageObj messageObj = new MessageObj("Can you <b>select the steps</b> that you actually performed?",
                WidgetName.S2RScreenSelector);
        List<ChatBotMessage> chatBotMessages;


        //if there are no steps reported yet
        if (noStepsReportedYet) {
            chatBotMessages = createChatBotMessages(
                    "Okay, now I need the steps that you performed and caused the problem",
                    "<b>The next steps</b> that you performed <b>after you opened the app</b> might be" +
                            " the following",
                    new ChatBotMessage(messageObj, stepOptions, true));
        } else {
            String deleteStepMsg = (String) state.get(DELETE_STEP_MSG);
            chatBotMessages = createChatBotMessages(deleteStepMsg,
                    "Okay, it seems <b>the next steps</b> that you performed might be the following",
                    new ChatBotMessage(messageObj, stepOptions, true));
            state.remove(DELETE_STEP_MSG);
        }
        return chatBotMessages;
    }

    private List<ChatBotMessage> getFirstStepMessages(ConversationState state) {
        setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
        if (state.containsKey(StateVariable.ASKED_TO_WRITE_S2R)) {
            return createChatBotMessages(
                    "Okay, can you tell me the <b>first step</b> that you performed after you opened the app?");
        } else {
            state.put(StateVariable.ASKED_TO_WRITE_S2R, true);
            return createChatBotMessages("Okay, now I need the steps that you performed and caused the problem",
                    "Can you tell me the <b>first step</b> that you performed after you opened the app?");
        }
    }

    private List<ChatBotMessage> getLastStepMessage(ConversationState state, BugReportElement lastStep) {
        setNextExpectedIntents(Collections.singletonList(Intent.NO_EXPECTED_INTENT));
        state.put(StateVariable.CONFIRM_LAST_STEP, true);

        ChatBotMessage confMsg = new ChatBotMessage(new MessageObj(String.format(
                "Okay, is this <b>the last step</b> that you performed: \"%s\"?",
                lastStep.getStringElement()),
                WidgetName.YesNoButtons));
        return createChatBotMessages(confMsg);
    }

    private List<ChatBotMessage> getNextStepMessage() {
        setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
        return createChatBotMessages("Okay, can you please provide the <b>next step</b> that you performed?");
    }

}
