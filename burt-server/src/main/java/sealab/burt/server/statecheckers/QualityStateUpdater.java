package sealab.burt.server.statecheckers;

import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.StateVariable;
import sealab.burt.server.output.OutputMessageObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.*;

public class QualityStateUpdater {

    public static final String DEFAULT_SCREENSHOT = "NO_SCREEN_AVAILABLE.png";

    public static void addStepAndUpdateGraphState(ConcurrentHashMap<StateVariable, Object> state,
                                                  String step,
                                                  S2RQualityAssessment assessment) {
        List<OutputMessageObj> outputMessageList = (List<OutputMessageObj>) state.get(REPORT_S2R);
        if (!state.containsKey(REPORT_S2R)) {
            String screenshotFile = assessment.getMatchedSteps().get(0).getScreenshotFile();
            outputMessageList = new ArrayList<>(Collections.singletonList(
                    new OutputMessageObj(step,
                            screenshotFile == null ? DEFAULT_SCREENSHOT : screenshotFile)
            ));
        } else {
            String screenshotFile = assessment.getMatchedSteps().get(0).getScreenshotFile();
            outputMessageList.add(new OutputMessageObj(step,
                    screenshotFile == null ? DEFAULT_SCREENSHOT : screenshotFile));
        }
        state.put(REPORT_S2R, outputMessageList);

        //---------------------

        S2RChecker s2rChecker = (S2RChecker) state.get(S2R_CHECKER);
//        OBChecker obChecker = (OBChecker) state.get(OB_CHECKER);
//        EBChecker ebChecker = (EBChecker) state.get(EB_CHECKER);

        s2rChecker.updateState(assessment.getMatchedSteps().get(0).getCurrentState());

    }


    public static void addStepsToState(ConcurrentHashMap<StateVariable, Object> state,
                                       List<AppStep> selectedSteps) {
        List<OutputMessageObj> outputMessageList = (List<OutputMessageObj>) state.get(REPORT_S2R);
        if (!state.containsKey(REPORT_S2R)) {
            outputMessageList = getOutputMessages(selectedSteps);
        } else {
            outputMessageList.addAll(getOutputMessages(selectedSteps));
        }
        state.put(REPORT_S2R, outputMessageList);
    }


    private static List<OutputMessageObj> getOutputMessages(List<AppStep> selectedSteps) {
        return new ArrayList<>(selectedSteps.stream()
                .map(step -> {
                    String screenshotFile = step.getScreenshotFile();
                    return new OutputMessageObj(UtilReporter.getNLStep(step, false),
                            screenshotFile == null ? DEFAULT_SCREENSHOT : screenshotFile);
                })
                .collect(Collectors.toList()));
    }


}