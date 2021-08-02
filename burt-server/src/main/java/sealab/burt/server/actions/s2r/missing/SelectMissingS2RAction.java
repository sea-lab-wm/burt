package sealab.burt.server.actions.s2r.missing;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.ComponentUtils;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.qualitychecker.s2rquality.S2RQualityCategory;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.actions.s2r.prediction.S2RPredictor;
import sealab.burt.server.conversation.entity.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.output.BugReportElement;
import sealab.burt.server.conversation.state.QualityStateUpdater;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.msgparsing.Intent.S2R_DESCRIPTION;

public @Slf4j
class SelectMissingS2RAction extends ChatBotAction {

    public SelectMissingS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    public static List<KeyValues> getStepOptions(List<AppStep> steps, ConversationState state) {

        Set<String> uniqueOptionKeys = new LinkedHashSet<>();
        return IntStream.range(0, steps.size())
                .mapToObj(optionPosition -> {
                    AppStep step = steps.get(optionPosition);
                    GraphTransition transition = step.getTransition();
                    String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(step, state);
                    String nlStep = UtilReporter.getNLStep(step, false);

                    String key = Integer.toString(optionPosition);

                    if (uniqueOptionKeys.contains(key))
                        throw new RuntimeException(String.format("An option with the key %s already exists", key));
                    else
                        uniqueOptionKeys.add(key);

                    return new KeyValues(key,
                            nlStep + " (" + getUniqueHashFromTransition2(transition) + ")", screenshotFile);
                        }

                )
                .collect(Collectors.toList());

    }

    private static String getUniqueHashFromTransition2(GraphTransition transition) {
        return (transition == null) ? "null" : transition.getId().toString();
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        S2RQualityAssessment assessment = feedback.getQualityAssessments().stream()
                .filter(qa -> qa.getCategory().equals(S2RQualityCategory.MISSING))
                .findFirst().get();

        final List<AppStep> inferredSteps = assessment.getInferredSteps();
        final List<AppStep> cleanedInferredSteps = cleanSteps(inferredSteps, state);

        //---------------------------------------------------

        List<AppStep> cleanedInferredSteps2 = removeLastReportSteps(state, cleanedInferredSteps);

        //---------------------------------------------------


        UserResponse msg = (UserResponse) state.get(S2R_MATCHED_MSG);
        String highQualityStepMessage = msg.getMessages().get(0).getMessage();

        //---------------------------------------------------

        if (cleanedInferredSteps2.isEmpty()) {

            S2RQualityAssessment highQualityAssessment = feedback.getQualityAssessments().stream()
                    .filter(qa -> qa.getCategory().equals(S2RQualityCategory.HIGH_QUALITY))
                    .findFirst().orElse(null);

            if (highQualityAssessment == null)
                throw new RuntimeException("The high quality assessment is required");

            this.nextExpectedIntents = Collections.singletonList(S2R_DESCRIPTION);

            state.getStateUpdater().addStepAndUpdateGraphState(state, highQualityStepMessage, highQualityAssessment);

            return createChatBotMessages("Got it, what is the next step?");
        }

        //---------------------------------------------------

        List<KeyValues> stepOptions = getStepOptions(cleanedInferredSteps2, state);

        state.put(S2R_ALL_MISSING, cleanedInferredSteps2);

        //-----------------

        MessageObj messageObj = new MessageObj(
                "Remember that the displayed screenshots are for reference only."
                , WidgetName.S2RScreenSelector);
        return createChatBotMessages(
                "Got it! You reported the step \"" + highQualityStepMessage + "\"",
                "It seems that before that step you had to perform additional steps. ",
                "From the following options, please select the steps that you performed before this step and click " +
                        "the \"done\" button", new ChatBotMessage(messageObj, stepOptions, true));

    }

    private List<AppStep> removeLastReportSteps(ConversationState state, List<AppStep> cleanedInferredSteps) {

        //remove the last five "report" steps from the missing steps
        List<BugReportElement> stepElements = (List<BugReportElement>) state.get(REPORT_S2R);

        List<AppStep> cleanedInferredSteps2 = cleanedInferredSteps;
        if (stepElements !=null ) {
            List<AppStep> lastSteps = stepElements
                    .subList(Math.max(stepElements.size() - 5, 0), stepElements.size())
                    .stream()
                    .map(el -> (AppStep) el.getOriginalElement())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            cleanedInferredSteps2 = cleanedInferredSteps.stream()
                    .filter(step -> lastSteps.stream().noneMatch(
                            lasStep -> S2RPredictor.matchByTransitionId.test(step, lasStep)
                    ))
                    .collect(Collectors.toList());
        }
        return cleanedInferredSteps2;
    }

    private List<AppStep> cleanSteps(List<AppStep> steps, ConversationState state) {

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
