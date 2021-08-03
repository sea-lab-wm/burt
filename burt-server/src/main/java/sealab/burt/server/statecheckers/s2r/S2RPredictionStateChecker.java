package sealab.burt.server.statecheckers.s2r;

import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.statecheckers.StateChecker;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.*;


public class S2RPredictionStateChecker extends StateChecker {

    public S2RPredictionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {

        UserResponse msg = (UserResponse) state.get(CURRENT_MESSAGE);
        MessageObj message = msg.getFirstMessage();

        state.remove(StateVariable.NON_SELECTED_PREDICTED_S2R);

        if (ChatBotAction.DONE.equals(message.getMessage())) {

            // get current predicted path
            List<List<AppStep>> paths = (List<List<AppStep>>) state.get(PREDICTED_S2R_PATHS_WITH_LOOPS);
            List<AppStep> currentPath = paths.get((int) state.get(PREDICTED_S2R_CURRENT_PATH));

//             get selected app steps
            List<String> selectedValues = message.getSelectedValues();
            List<AppStep> selectedSteps = selectedValues.stream()
                    .map(selectedValue -> {
                        int index = Integer.parseInt(selectedValue);
                        if (index < currentPath.size())
                            return currentPath.get(index);
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (selectedSteps.isEmpty() || selectedValues.size() != selectedSteps.size())
                throw new RuntimeException("The selected steps and predicted steps do not match");

            //------------------------------

            List<AppStep> nonSelectedSteps = IntStream.range(0, currentPath.size())
                    .mapToObj(i -> {
                        if (selectedValues.contains(String.valueOf(i))) return null;
                        return currentPath.get(i);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            state.put(StateVariable.NON_SELECTED_PREDICTED_S2R, nonSelectedSteps);

            //------------------------------

            // add all selected app steps to state and update graph
            state.getStateUpdater().addStepsToState(state, selectedSteps);

            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTING_S2R);
            state.remove(PREDICTED_S2R_PATHS_WITH_LOOPS);
            state.remove(PREDICTED_S2R_NUMBER_OF_PATHS);

            return ActionName.PREDICT_FIRST_S2R_PATH;

        } else if (ChatBotAction.NONE.equals(message.getMessage())) {
            // check the number of tries to decide if we continue to provide next predicted path
            if (isThereANextPath(state)) {
                state.put(PREDICTED_S2R_CURRENT_PATH, (int) state.get(PREDICTED_S2R_CURRENT_PATH) + 1);
                return ActionName.PREDICT_NEXT_S2R_PATH;
            } else {

                state.remove(PREDICTED_S2R_CURRENT_PATH);
                state.remove(PREDICTING_S2R);
                state.remove(PREDICTED_S2R_PATHS_WITH_LOOPS);
                state.remove(PREDICTED_S2R_NUMBER_OF_PATHS);

                return ActionName.PROVIDE_S2R;
            }

        } else if (S2RDescriptionStateChecker.isLastStep(message.getMessage())) {
            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTING_S2R);
            state.remove(PREDICTED_S2R_NUMBER_OF_PATHS);
            state.remove(PREDICTED_S2R_PATHS_WITH_LOOPS);

            return ActionName.CONFIRM_LAST_STEP;
        } else {
            return ActionName.PREDICT_NEXT_S2R_PATH;
        }

    }

    private boolean isThereANextPath(ConversationState state) {
        return ((int) state.get(PREDICTED_S2R_CURRENT_PATH) + 1) < (int) state.get(PREDICTED_S2R_NUMBER_OF_PATHS);
    }
}
