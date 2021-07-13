package sealab.burt.server.actions.s2r;

import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.ComponentUtils;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;
import sealab.burt.server.statecheckers.QualityStateUpdater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;

public class SelectMissingS2RAction extends ChatBotAction {

    public SelectMissingS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    public static List<KeyValues> getStepOptions(List<AppStep> cleanedInferredSteps,
                                                 ConcurrentHashMap<StateVariable, Object> state) {
        return cleanedInferredSteps.stream()
                .map(step -> {

                    String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(step, state);
                    return new KeyValues(step.getId().toString(),
                            UtilReporter.getNLStep(step, false), screenshotFile);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        S2RQualityAssessment assessment = feedback.getQualityAssessments().stream()
                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.MISSING))
                .findFirst().get();

        final List<AppStep> inferredSteps = assessment.getInferredSteps();
        final List<AppStep> cleanedInferredSteps = cleanSteps(inferredSteps, state);

        //---------------------------------------------------

        if (cleanedInferredSteps.isEmpty()) {

            S2RQualityAssessment highQualityAssessment = feedback.getQualityAssessments().stream()
                    .filter(qa -> qa.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                    .findFirst().orElse(null);
            String s2rHQMissing = (String) state.get(S2R_HQ_MISSING);
            this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

            if (s2rHQMissing != null)
                QualityStateUpdater.addStepAndUpdateGraphState(state, s2rHQMissing, highQualityAssessment);

            return createChatBotMessages("Got it, what is the next step?");
        }

        //---------------------------------------------------

        IntStream.range(0, cleanedInferredSteps.size()).forEach(i ->
                cleanedInferredSteps.get(i).setId((long) i)
        );

        List<KeyValues> stepOptions = getStepOptions(cleanedInferredSteps, state);

        state.put(S2R_ALL_MISSING, cleanedInferredSteps);

        //-----------------

        MessageObj messageObj = new MessageObj(
                "From the following options, select the steps you performed before this step and click the " +
                        "\"done\" button", "S2RScreenSelector");
        String highQualityStepMessage = (String) state.get(S2R_HQ_MISSING);
        return createChatBotMessages(
                "Got it! You reported the step \"" + highQualityStepMessage + "\"",
                "However, it seems that before that step you had to perform additional steps. ",
                new ChatBotMessage(messageObj, stepOptions, true));

    }

    private List<AppStep> cleanSteps(List<AppStep> steps, ConcurrentHashMap<StateVariable, Object> state) {

        if (steps == null) return null;
        if (steps.isEmpty()) return steps;

        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);

        if (containsOpenAppStep(stepElements))
            steps = steps.stream()
                    .filter(appStep -> !DeviceUtils.isOpenApp(appStep.getAction()))
                    .collect(Collectors.toList());

        List<AppStep> cleanedSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); ) {
            final AppStep currentStep = steps.get(i);

            AppStep nextStep = null;
            if ((i + 1) < steps.size()) {
                nextStep = steps.get(i + 1);
            }

            if (nextStep != null) {

                AppStep nextNextStep = null;
                if ((i + 2) < steps.size()) {
                    nextNextStep = steps.get(i + 2);
                }

                if (nextNextStep != null) {
                    if (
                            (DeviceUtils.isClick(currentStep.getAction()) &&
                                    DeviceUtils.isType(nextStep.getAction()) &&
                                    DeviceUtils.isClick(nextNextStep.getAction())
                            ) &&
                                    (
                                            /*(currentStep.getComponent().getDbId().equals(nextStep.getComponent()
                                            .getDbId())
                                            && nextStep.getComponent().getDbId().equals(nextNextStep.getComponent()
                                            .getDbId())
                                            ||*/
                                            ComponentUtils.equalsNoDimensions(currentStep.getComponent(),
                                                    nextStep.getComponent())
                                                    && ComponentUtils.equalsNoDimensions(nextStep.getComponent(),
                                                    nextNextStep.getComponent())
                                    )
                    ) {

//                        if (generateClickBeforeType) {
//                            cleanedSteps.add(currentStep);
//                            cleanedSteps.add(nextStep);
//                        } else {
                        cleanedSteps.add(nextStep);
//                        }
                        i = i + 3;
                        continue;
                    }
                }
            }

            cleanedSteps.add(currentStep);

            i++;

        }
        return cleanedSteps;
    }

    private boolean containsOpenAppStep(List<BugReportElement> stepElements) {

        if (stepElements == null) return false;

        return stepElements.stream()
                .anyMatch(step -> step.getOriginalElement() != null
                        && DeviceUtils.isOpenApp(((AppStep) step.getOriginalElement()).getAction()));
    }

}
