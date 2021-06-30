package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.output.BugReportElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;

public class QualityStateUpdater {

    /**
     * add the S2R (missing steps but HQ) after adding the selected missing S2Rs
     */
    public static void addStepAndUpdateGraphState(ConcurrentHashMap<StateVariable, Object> state,
                                                  String stringStep,
                                                  S2RQualityAssessment assessment) {
        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);
        AppStep appStep = assessment.getMatchedSteps().get(0);
        String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(appStep, state);
        if (!state.containsKey(REPORT_S2R)) {
            stepElements = new ArrayList<>(Collections.singletonList(
                    new BugReportElement(stringStep, appStep, screenshotFile)
            ));
        } else {
            stepElements.add(new BugReportElement(stringStep, appStep, screenshotFile));
        }
        state.put(REPORT_S2R, stepElements);

        //---------------------

        S2RChecker s2rChecker = (S2RChecker) state.get(S2R_CHECKER);
//        OBChecker obChecker = (OBChecker) state.get(OB_CHECKER);
//        EBChecker ebChecker = (EBChecker) state.get(EB_CHECKER);

        s2rChecker.updateState(appStep.getCurrentState());

    }

    /**
     * add the intermediate missing steps into state
     */
    public static void addStepsToState(ConcurrentHashMap<StateVariable, Object> state,
                                       List<AppStep> selectedSteps) {
        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);
        if (!state.containsKey(REPORT_S2R)) {
            stepElements = getOutputMessagesFromSteps(selectedSteps, state);
        } else {
            stepElements.addAll(getOutputMessagesFromSteps(selectedSteps, state));
        }
        state.put(REPORT_S2R, stepElements);
    }


    private static List<BugReportElement> getOutputMessagesFromSteps(List<AppStep> selectedSteps,
                                                                     ConcurrentHashMap<StateVariable, Object> state) {
        //we need to return a modifiable list
        return new ArrayList<>(selectedSteps.stream()
                .map(step -> {
                    String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(step, state);
                    return new BugReportElement(UtilReporter.getNLStep(step, false), step, screenshotFile);
                })
                .collect(Collectors.toList()));
    }


    public static void updateOBState(ConcurrentHashMap<StateVariable, Object> state, GraphState obState) {

        if (obState == null)
            throw new RuntimeException("The state cannot be null");

        String screenshotFile = ScreenshotPathUtils.getScreenshotPathForGraphState(obState, state);
        state.put(REPORT_OB, Collections.singletonList(
                new BugReportElement((String) state.get(OB_DESCRIPTION), obState, screenshotFile)));
    }


    public static void updateEBState(ConcurrentHashMap<StateVariable, Object> state, GraphState ebState) {
        if (ebState == null)
            throw new RuntimeException("The state cannot be null");

        String screenshotFile = ScreenshotPathUtils.getScreenshotPathForGraphState(ebState, state);
        state.put(REPORT_EB, Collections.singletonList(
                new BugReportElement((String) state.get(EB_DESCRIPTION), ebState, screenshotFile)));
    }
}