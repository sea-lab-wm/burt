package sealab.burt.server.conversation.state;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.output.BugReportElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class QualityStateUpdater {

    private ConcurrentHashMap<GraphState, List<AppStep>> lastStepsToState = new ConcurrentHashMap<>();

    /**
     * add the S2R (missing steps but HQ) after adding the selected missing S2Rs
     */
    public void addStepAndUpdateGraphState(ConversationState state,
                                                  String stringStep,
                                                  S2RQualityAssessment assessment) {
        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);

        AppStep appStep = null;
        String screenshotFile;
        if (assessment != null) {
            appStep = assessment.getMatchedSteps().get(0);
            screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(appStep, state);
        } else {
            screenshotFile = ScreenshotPathUtils.getScreenshotPath(state, null, null);
        }

        if (stepElements == null) {
            stepElements = new ArrayList<>();
        }

        //--------------------
        S2RChecker s2rChecker = (S2RChecker) state.get(S2R_CHECKER);


        //if the step is to go back, then move to the state that led to the current state (by pulling the last step)
        if (appStep != null) {

            Integer action = appStep.getAction();
            boolean isClickBackButton = DeviceUtils.isClickBackButton(action);

            if (isClickBackButton && !stepElements.isEmpty() && s2rChecker != null) {

                GraphState currentState = s2rChecker.getCurrentState();
                if (currentState != null) {
                    List<AppStep> appSteps = lastStepsToState.get(currentState);

                    if (appSteps != null) {
                        AppStep lastStepToState = appSteps.get(appSteps.size() - 1);
                        if(lastStepToState.getTransition() != null){
                            GraphState sourceState = lastStepToState.getTransition().getSourceState();
                            s2rChecker.updateState(sourceState);

                            appSteps.remove(appSteps.size() - 1);

                            if(appSteps.isEmpty())
                                lastStepsToState.remove(currentState);
                        }
                    }

                    stepElements.add(new BugReportElement(stringStep, appStep, screenshotFile));
                    state.put(REPORT_S2R, stepElements);

                    return;
                }
            }
        }

        //--------------------

        stepElements.add(new BugReportElement(stringStep, appStep, screenshotFile));
        state.put(REPORT_S2R, stepElements);

        //---------------------

        updateStateBasedOnStep(appStep, s2rChecker);

    }

    private void updateStateBasedOnStep(AppStep appStep, S2RChecker s2rChecker) {
        if (appStep == null || s2rChecker == null) return;
        GraphTransition transition = appStep.getTransition();
        if (transition != null) {
            GraphState currentState = s2rChecker.getCurrentState();
            GraphState targetState = transition.getTargetState();

            //if we reach the end state, we set the current state to the start state.
            //essentially, we are jumping to the start state
            if(targetState.equals(GraphState.END_STATE)){
                log.debug("We reached the END STATE, setting the current state to be the START STATE");
                targetState = GraphState.START_STATE;
            }

            s2rChecker.updateState(targetState);
            if (!currentState.equals(targetState)) {
                List<AppStep> steps = lastStepsToState.get(targetState);
                if(steps == null) steps = new ArrayList<>();
                steps.add(appStep);
                lastStepsToState.put(targetState, steps);
            }
        }
    }

    /**
     * add steps as "report" steps and update the current state for each step
     */
    public void addStepsToState(ConversationState state,
                                       List<AppStep> selectedSteps) {

        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);
        if (stepElements == null)
            stepElements = new ArrayList<>();

        S2RChecker s2rChecker = (S2RChecker) state.get(S2R_CHECKER);
        for (AppStep appStep : selectedSteps) {

            String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(appStep, state);
            String nlStep = UtilReporter.getNLStep(appStep, false);
            BugReportElement element = new BugReportElement(nlStep, appStep, screenshotFile);
            stepElements.add(element);

            //---------------------------------

            updateStateBasedOnStep(appStep, s2rChecker);
        }

        state.put(REPORT_S2R, stepElements);
    }

    public static void updateOBState(ConversationState state, GraphState obState) {

        log.debug("Updating OB state to: " + obState);

        String screenshotFile = ScreenshotPathUtils.getScreenshotPathForGraphState(obState, state);
        state.put(REPORT_OB, Collections.singletonList(
                new BugReportElement((String) state.get(OB_DESCRIPTION), obState, screenshotFile)));
    }


    public static void updateEBState(ConversationState state, GraphState ebState) {

        log.debug("Updating EB state to: " + ebState);

        String screenshotFile = ScreenshotPathUtils.getScreenshotPathForGraphState(ebState, state);
        state.put(REPORT_EB, Collections.singletonList(
                new BugReportElement((String) state.get(EB_DESCRIPTION), ebState, screenshotFile)));
    }
}