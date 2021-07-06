package sealab.burt.server.statecheckers;

import edu.semeru.android.core.entity.model.App;
import org.jgrapht.GraphPath;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.s2r.ProvideFirstPredictedS2RAction;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;


public class S2RPredictionStateChecker extends StateChecker {

    public S2RPredictionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        MessageObj message = msg.getFirstMessage();

        if ("done".equals(message.getMessage())) {

            List<GraphPath<GraphState, GraphTransition>> predictedPaths = (List<GraphPath<GraphState,
                    GraphTransition>>) state.get(PREDICTED_S2R_PATHS);
            GraphPath<GraphState, GraphTransition> path =
                    predictedPaths.get((int) state.get(PREDICTED_S2R_CURRENT_PATH));

            //current state
            S2RChecker s2rchecker = (S2RChecker) state.get(S2R_CHECKER);
            GraphState currentState = s2rchecker.getCurrentState();

            // convert graph path to app steps
            List<AppStep> steps = ProvideFirstPredictedS2RAction.getPathWithLoops(s2rchecker, path, state, currentState);

            // get selected app steps
            List<String> selectedValues = message.getSelectedValues();
            List<AppStep> selectedSteps = steps.stream()
                    .filter(step -> selectedValues.contains(step.getId().toString()))
                    .collect(Collectors.toList());

            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
                throw new RuntimeException("The selected steps and predicted steps do not match");



            // add all selected app steps to state and update graph
            if (selectedSteps.size() == 1) {
                QualityStateUpdater.addPredictedStepAndUpdateGraphState(state, selectedSteps.get(0));

            } else {
                QualityStateUpdater.addStepsToState(state, selectedSteps.subList(0, selectedSteps.size() - 1));
                QualityStateUpdater.addPredictedStepAndUpdateGraphState(state,
                        selectedSteps.get(selectedSteps.size() - 1));
            }

            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTED_S2R_PATHS);
            state.remove(PREDICTING_S2R);

            // check if it is the last step
            GraphState targetState = (GraphState) state.get(StateVariable.OB_STATE); // get OB state
            AppStep lastSelectedStep = selectedSteps.get(selectedSteps.size() -1);  // get current state
            //FIXME: target state is the current state of AppStep?
            GraphState lastSelectedState = lastSelectedStep.getCurrentState();
            if (targetState == lastSelectedState){
                return ActionName.CONFIRM_LAST_STEP;
            }
            return ActionName.PREDICT_FIRST_S2R;

        } else if ("none of above".equals(message.getMessage())) {
            // check the number of tries to decide if we continue to provide next predicted path
            if (isThereANextPath(state)) {
                return ActionName.PREDICT_NEXT_S2R;
            } else {

                state.remove(PREDICTED_S2R_CURRENT_PATH);
                state.remove(PREDICTED_S2R_PATHS);
                state.remove(PREDICTING_S2R);

                return ActionName.PROVIDE_S2R;
            }

        } else if (S2RDescriptionStateChecker.isLastStep(message.getMessage())) {
            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTED_S2R_PATHS);
            state.remove(PREDICTING_S2R);

            return ActionName.CONFIRM_LAST_STEP;
        }

        return null;
    }

    private boolean isThereANextPath(ConcurrentHashMap<StateVariable, Object> state) {
        return ((int) state.get(PREDICTED_S2R_CURRENT_PATH) + 1) < (int) state.get(PREDICTED_S2R_NUMBER_OF_PATHS);
    }
}
