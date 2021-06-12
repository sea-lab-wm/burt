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
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.statecheckers.S2RStateUpdater;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.S2R_ALL_MISSING;
import static sealab.burt.server.StateVariable.S2R_QUALITY_RESULT;

public class SelectMissingS2RAction extends ChatBotAction {

    public SelectMissingS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        S2RQualityAssessment assessment = feedback.getQualityAssessments().stream()
                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.MISSING))
                .findFirst().get();

        final List<AppStep> inferredSteps = assessment.getInferredSteps();
        final List<AppStep> cleanedInferredSteps = cleanSteps(inferredSteps);

        IntStream.range(0, cleanedInferredSteps.size()).forEach( i ->
                cleanedInferredSteps.get(i).setId((long) i)
        );

        List<KeyValues> stepOptions = getStepOptions(cleanedInferredSteps);

        state.put(S2R_ALL_MISSING, cleanedInferredSteps);

        //-----------------

        MessageObj messageObj = new MessageObj(
                "From the following options, select the steps you performed before this step and click the " +
                        "\"done\" button", "S2RScreenSelector");

        return createChatBotMessages(
                "It seems that before that step you had to perform additional steps. ",
                new ChatBotMessage(messageObj, stepOptions, true));

    }

    public static List<KeyValues> getStepOptions(List<AppStep> cleanedInferredSteps) {
        return cleanedInferredSteps.stream()
                .map(step -> {
                    String screenshotFile = step.getScreenshotFile();
                    return new KeyValues(step.getId().toString(),
                            UtilReporter.getNLStep(step, false),
                            screenshotFile == null ? S2RStateUpdater.DEFAULT_SCREENSHOT :
                            screenshotFile);
                })
                .collect(Collectors.toList());
    }

    private List<AppStep> cleanSteps(List<AppStep> steps) {

        if (steps == null) return null;
        if (steps.isEmpty()) return steps;

        List<AppStep> cleanedSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); ) {
            final AppStep appStep = steps.get(i);
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
                            (DeviceUtils.isType(nextStep.getAction()) &&
                                    DeviceUtils.isClick(appStep.getAction()) &&
                                    DeviceUtils.isClick(nextNextStep.getAction())
                            ) &&
                                    ((appStep.getComponent().getDbId().equals(nextStep.getComponent().getDbId())
                                            && nextStep.getComponent().getDbId().equals(nextNextStep.getComponent().getDbId())
                                            ||
                                            ComponentUtils.equalsNoDimensions(appStep.getComponent(),
                                                    nextStep.getComponent())
                                                    && ComponentUtils.equalsNoDimensions(nextStep.getComponent(),
                                                    nextNextStep.getComponent())
                                    ))
                    ) {

//                        if (generateClickBeforeType) {
//                            cleanedSteps.add(appStep);
//                            cleanedSteps.add(nextStep);
//                        } else {
                        cleanedSteps.add(nextStep);
//                        }
                        i = i + 3;
                        continue;
                    }
                }
            }

            cleanedSteps.add(appStep);

            i++;

        }
        return cleanedSteps;
    }

}
