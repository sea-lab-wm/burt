package sealab.burt.server.actions.s2r;

import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.actionparser.GraphUtils;
import sealab.burt.qualitychecker.actionparser.StepResolver;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import static sealab.burt.server.StateVariable.*;
@Slf4j
public class ProvidePredictedS2RAction extends ChatBotAction {

    public ProvidePredictedS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }


    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {
        MessageObj messageObj = new MessageObj("Please click the “done” button when you are done.",
                "S2RScreenSelector");

        state.put(PREDICTING_S2R, true);
        state.put(PREDICTED_S2R_TRIES_LIMIT, 3);

        GraphState targetState = (GraphState) state.get(OB_STATE);

        //current state
        S2RChecker s2rchecker = (S2RChecker) state.get(S2R_CHECKER);
        GraphState currentState = s2rchecker.getCurrentState();

        // FIXME: HOW TO DO WHEN CURRENT STATE IS NULL
        if (currentState == null){
            currentState = GraphState.START_STATE;
        }

        // check target state equals to current state

        //get first k paths according to the score
        List<KeyValues> stepOptions;
        if (state.containsKey(PREDICTED_S2R_LIST) &&
                (int) state.get(PREDICTED_S2R_TRIES) < (int) state.get(PREDICTED_S2R_TRIES_LIMIT)) {
            GraphPath<GraphState, GraphTransition> path = ((List<GraphPath<GraphState, GraphTransition>>) state.get(PREDICTED_S2R_LIST)).get((int) state.get(PREDICTED_S2R_TRIES));

            // get screenshots
            stepOptions = getPredictedStepOptions(s2rchecker, path, state, currentState);
            if (stepOptions.size() == 0){
                return createChatBotMessages("Ok, can you provide next step 0?", new ChatBotMessage(messageObj));
            }
            String msg = "Option size 0";
            log.debug(msg + stepOptions.size());

            // increment the number of tries
            state.put(PREDICTED_S2R_TRIES, (int) state.get(PREDICTED_S2R_TRIES) + 1);


        } else {

            List<GraphPath<GraphState, GraphTransition>> Paths = s2rchecker.getFirstKPaths((Integer) state.get(PREDICTED_S2R_TRIES_LIMIT),
                    targetState);
            //FIXME: Paths could be null
            if (Paths.size() == 0) {
                return createChatBotMessages("Ok, can you provide next step 1?", new ChatBotMessage(messageObj));
            }else{
                state.put(PREDICTED_S2R_LIST, Paths);
                state.put(PREDICTED_S2R_TRIES, 0);

                //FIXME: stepOptions could be null
                stepOptions = getPredictedStepOptions(s2rchecker, Paths.get(0), state, currentState);
                String msg = "Option size";
                log.debug(msg + stepOptions.size());
                if (stepOptions.size() == 0){
                    return createChatBotMessages("Ok, can you provide next step 2?", new ChatBotMessage(messageObj));
                }
            }
        }
        return createChatBotMessages(
                "Ok, it seems the next steps that you performed are the following.",
                "Can you confirm which ones are correct?",
                new ChatBotMessage(messageObj, stepOptions, true));
    }


    private List<KeyValues> getPredictedStepOptions(S2RChecker s2rchecker, GraphPath<GraphState, GraphTransition> path,
                                           ConcurrentHashMap<StateVariable, Object> state, GraphState currentState ){
        // get app steps
        List<AppStep> intermediateSteps = new LinkedList<>();
        Stream<GraphTransition> pathTransitions = path.getEdgeList().stream();
        intermediateSteps.addAll(pathTransitions
                .map(GraphTransition::getStep)
                .collect(Collectors.toList()));

        // Add the state loops in order to the path
        List<BugReportElement> bugReportElements = (List<BugReportElement>) state.get(REPORT_S2R);

        AppStep lastStep = (AppStep) bugReportElements.get(bugReportElements.size() - 1).getOriginalElement();
        List<AppStep> currentResolvedSteps = new LinkedList<>();
        s2rchecker.executeIntermediateStepsInShortestPath(null, lastStep,
                currentResolvedSteps,
                intermediateSteps, currentState.getComponents());

        //Get screenshots from the AppSteps
        //Show the first 5 steps of the path to the user

        // add code to check if there are not 5 steps
        List<AppStep> recommendedSteps = currentResolvedSteps.subList(0, Math.min(5, currentResolvedSteps.size()));

        return SelectMissingS2RAction.getStepOptions(recommendedSteps, state);

    }




}
