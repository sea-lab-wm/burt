package sealab.burt.server.statecheckers.s2r;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.output.BugReportElement;

import java.util.List;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class DeleteLastStepStateChecker extends sealab.burt.server.statecheckers.StateChecker {

    public DeleteLastStepStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) throws Exception {

        List<BugReportElement> allSteps = (List<BugReportElement>) state.get(REPORT_S2R);

        // remove the corresponding step
        BugReportElement lastElement = allSteps.get(allSteps.size() - 1);
        BugReportElement secondLastElement = allSteps.get(allSteps.size() - 2);

        AppStep lastStep = (AppStep) lastElement.getOriginalElement();
        AppStep secondLastStep = (AppStep) secondLastElement.getOriginalElement();

        //------------------------------------------------

        if (secondLastStep != null && secondLastStep.getTransition() != null) {

            S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
            GraphState currentState = checker.getCurrentState();
            checker.updateState(secondLastStep.getTransition().getTargetState());

            state.getStateUpdater().removeLastStepToState(currentState, lastStep);
        }

        allSteps.remove(lastElement);
        state.put(REPORT_S2R, allSteps);

        state.put(DELETE_STEP_MSG, "Got it, the step was deleted from the reported steps");

        if (state.containsKey(PREDICTING_S2R)) {

            state.remove(PREDICTED_S2R_CURRENT_PATH);
            state.remove(PREDICTING_S2R);
            state.remove(PREDICTED_S2R_PATHS_WITH_LOOPS);
            state.remove(PREDICTED_S2R_NUMBER_OF_PATHS);
            state.remove(NON_SELECTED_PREDICTED_S2R);

            return ActionName.PREDICT_FIRST_S2R_PATH;
        }

        return ActionName.DO_NOTHING;

    }
}
