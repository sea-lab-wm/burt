package sealab.burt.server.statecheckers;

import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.s2r.ProvidePredictedS2RAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;
import sealab.burt.server.msgparsing.Intent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;


public class S2RPredictionStateChecker extends StateChecker {

    public S2RPredictionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        MessageObj message = msg.getFirstMessage();

        if ("done".equals(message.getMessage())) {

            List<GraphPath<GraphState, GraphTransition>> Paths = (List<GraphPath<GraphState, GraphTransition>>) state.get(PREDICTED_S2R_LIST);
            GraphPath<GraphState, GraphTransition> path = Paths.get((int) state.get(PREDICTED_S2R_TRIES));

            // convert graph path to app steps
            List<AppStep> intermediateSteps = new LinkedList<>();
            ProvidePredictedS2RAction.convertGraphStateToAppStep(path, intermediateSteps);

            // get selected app steps
            List<String> selectedValues = message.getSelectedValues();
            List<AppStep> selectedSteps = intermediateSteps.stream()
                    .filter(step -> selectedValues.contains(step.getId().toString()))
                    .collect(Collectors.toList());

            //FIXME: need to consider this situation?
//            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
//                return getDefaultMessage(intermediateSteps, state);

            // add all selected app steps to state and update graph
            if (selectedSteps.size() == 1) {
                QualityStateUpdater.addPredictedStepAndUpdateGraphState(state, selectedSteps.get(0));
            } else {
                QualityStateUpdater.addStepsToState(state, selectedSteps.subList(0, selectedSteps.size() - 1));
                QualityStateUpdater.addPredictedStepAndUpdateGraphState(state, selectedSteps.get(selectedSteps.size() - 1));
            }

            state.remove(PREDICTED_S2R_TRIES);
            state.remove(PREDICTED_S2R_LIST);

            state.remove(PREDICTING_S2R);
            return ActionName.PREDICT_S2R;

        }else if ("none of above".equals(message.getMessage())){
            // check the number of tries to decide if we continue to provide next predicted path
            if ((int) state.get(PREDICTED_S2R_TRIES) < (int) state.get(PREDICTED_S2R_TRIES_LIMIT)) {
                return ActionName.PREDICT_S2R_2;
            }

        }
        return ActionName.PROVIDE_S2R;
    }
}
