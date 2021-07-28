package sealab.burt.server.actions.s2r;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.s2r.prediction.S2RPredictor;
import sealab.burt.server.conversation.*;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;

import java.util.Collections;
import java.util.LinkedList;
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

    public static List<AppStep> getPathWithLoops(S2RChecker s2rchecker, GraphPath<GraphState, GraphTransition> path,
                                                 ConversationState state, GraphState currentState) {
        // we convert the transitions to the steps
        List<AppStep> steps = convertGraphTransitionsToAppStep(path);

        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);

        if(bugReportElements==null)
            throw new RuntimeException("The S2R bug report elements are required");

        // Add the state loops in order to the path
        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();
        List<AppStep> stepsWithLoops = new LinkedList<>();
        s2rchecker.addIntermediateStepsInShortestPath(null, lastStep, stepsWithLoops, steps,
                currentState.getComponents());

        //-----------------

        List<AppStep> pathWithLoops = stepsWithLoops.subList(0, Math.min(MAX_STEPS_TO_SHOW_IN_PATH,
                stepsWithLoops.size()));

        modifyIds(pathWithLoops);
        return pathWithLoops;
    }

    private static void modifyIds(List<AppStep> pathWithLoops) {
        //setting the id, for testing purposes
        for (int i = 0; i < pathWithLoops.size(); i++) {
            AppStep step = pathWithLoops.get(i);
            step.setId((long) i);
        }
    }

    public static List<AppStep> convertGraphTransitionsToAppStep(GraphPath<GraphState, GraphTransition> path) {
        // get app steps
        return path.getEdgeList().stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());
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

        GraphState targetState;
        if (obReportElement == null || obReportElement.getOriginalElement() == null) {
            return getNextStepMessage();
        }

        targetState = (GraphState) obReportElement.getOriginalElement();

        //-----------------------------------------------

        //current state
        S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
        S2RPredictor predictor = new S2RPredictor(checker.getGraph());
        GraphState currentState = checker.getCurrentState();

        List<List<AppStep>> pathsWithLoops;
        if (currentState.equals(targetState)) {

            log.debug("Predicting S2R (loops)");

            List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);
            AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();

            if(DeviceUtils.isCloseApp(lastStep.getAction()))
                return getNextStepMessage();

            List<AppStep> stateLoops = predictor.getStateLoops(currentState, lastStep);
            modifyIds(stateLoops);

            if (stateLoops.isEmpty()) {
                return getNextStepMessage();
            }

            pathsWithLoops = Collections.singletonList(stateLoops);
        } else {

            //get all the paths sorted according  to the scoring mechanism
            List<GraphPath<GraphState, GraphTransition>> predictedPaths = predictor.getAllRankedPaths(currentState,
                    targetState);
            log.debug("Total number of predicted paths: " + predictedPaths.size());

            if (predictedPaths.isEmpty()) {
                return getNextStepMessage();
            }

            //----------------------------------------

            // the method getPathWithLoops() only returns a subset of the steps for each path
            pathsWithLoops = predictedPaths.stream()
                    .map(path -> getPathWithLoops(checker, path, state, currentState))
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

        MessageObj messageObj = new MessageObj("Remember that the screenshots below are for reference only.",
                WidgetName.S2RScreenSelector);
        return createChatBotMessages(
                "Okay, it seems the next steps that you performed might be the following.",
                "Can you select the ones you actually performed next?",
                new ChatBotMessage(messageObj, stepOptions, true));
    }

    private List<ChatBotMessage> getNextStepMessage() {
        setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
        return createChatBotMessages("Okay, can you please provide the next step?");
    }

}
