package sealab.burt.server.actions.s2r;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.*;

@Slf4j
public class ProvideFirstPredictedS2RAction extends ChatBotAction {

    public final static int MAX_NUMBER_OF_PATHS = 3;

    public ProvideFirstPredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    public static List<KeyValues> getPredictedStepOptions(S2RChecker s2rchecker, GraphPath<GraphState,
            GraphTransition> path, ConcurrentHashMap<StateVariable, Object> state, GraphState currentState) {
        List<AppStep> pathWithLoops = getPathWithLoops(s2rchecker, path, state, currentState);
        return SelectMissingS2RAction.getStepOptions(pathWithLoops, state);

    }

    public static List<AppStep> getPathWithLoops(S2RChecker s2rchecker, GraphPath<GraphState, GraphTransition> path,
                                             ConcurrentHashMap<StateVariable, Object> state, GraphState currentState) {
        // we convert the transitions to the steps
        List<AppStep> steps =  convertGraphTransitionsToAppStep(path);

        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);

        // Add the state loops in order to the path
        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();
        List<AppStep> currentResolvedSteps = new LinkedList<>();
        s2rchecker.executeIntermediateStepsInShortestPath(null, lastStep,
                currentResolvedSteps, steps, currentState.getComponents());

        //Get screenshots from the AppSteps
        //Show the first 5 steps of the path to the user

        List<AppStep> pathWithLoops = currentResolvedSteps.subList(0, Math.min(5, currentResolvedSteps.size()));

        //setting the id, for testing purposes
        for (int i = 0; i < pathWithLoops.size(); i++) {
            AppStep step = pathWithLoops.get(i);
            step.setId((long) i);
        }
        return pathWithLoops;
    }

    public static List<AppStep> convertGraphTransitionsToAppStep(GraphPath<GraphState, GraphTransition> path) {
        // get app steps
        return path.getEdgeList().stream()
                .map(GraphTransition::getStep)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        MessageObj messageObj = new MessageObj("Please click the “done” button when you are done.",
                "S2RScreenSelector");

        state.put(PREDICTING_S2R, true);

        //target state
        GraphState targetState = (GraphState) state.get(OB_STATE);

        //current state
        S2RChecker s2rchecker = (S2RChecker) state.get(S2R_CHECKER);
        GraphState currentState = s2rchecker.getCurrentState();

        // FIXME: HOW TO DO WHEN CURRENT STATE IS NULL
        if (currentState == null) {
            currentState = GraphState.START_STATE;
        }

        //FIXME:check target state equals to current state

        //get first k paths according to the score
        List<GraphPath<GraphState, GraphTransition>> predictedPaths = s2rchecker.getFirstKDummyPaths(
                MAX_NUMBER_OF_PATHS, targetState);

        log.debug("Total number of predicted paths: " + predictedPaths.size());

        // if there are less 3 paths
        state.put(PREDICTED_S2R_NUMBER_OF_PATHS, Math.min(MAX_NUMBER_OF_PATHS, predictedPaths.size()));

        if (predictedPaths.isEmpty()) {
            setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
            return createChatBotMessages("Okay, can you please provide next step?");
        }

        //----------------------------------------

        state.put(PREDICTED_S2R_PATHS, predictedPaths.subList(0, (int) state.get(PREDICTED_S2R_NUMBER_OF_PATHS)));
        state.put(PREDICTED_S2R_CURRENT_PATH, 0);

        List<KeyValues> stepOptions = getPredictedStepOptions(s2rchecker, predictedPaths.get(0), state, currentState);
        if (stepOptions.isEmpty()) {
            setNextExpectedIntents(Collections.singletonList(Intent.S2R_DESCRIPTION));
            return createChatBotMessages("Okay, can you please provide next step?");
        }

        log.debug("Suggesting path #" + state.get(PREDICTED_S2R_CURRENT_PATH));

        return createChatBotMessages(
                "Okay, it seems the next steps that you performed are the following.",
                "Can you confirm which ones are correct?",
                new ChatBotMessage(messageObj, stepOptions, true));
    }

}
