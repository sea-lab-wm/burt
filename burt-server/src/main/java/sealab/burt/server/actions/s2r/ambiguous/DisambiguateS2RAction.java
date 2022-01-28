package sealab.burt.server.actions.s2r.ambiguous;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.actions.commons.ScreenshotPathUtils;
import sealab.burt.server.conversation.entity.*;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import static sealab.burt.server.StateVariable.*;
public @Slf4j
class DisambiguateS2RAction extends ChatBotAction {

    public DisambiguateS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        String currentMessage = userResponse.getFirstMessage().getMessage();
        state.put(S2R_AMBIGUOUS_MSG, currentMessage);

        List<AppStep> matchedSteps = feedback.getQualityAssessments().get(0).getMatchedSteps();

        Set<String> uniqueOptionKeys = new LinkedHashSet<>();
        List<KeyValues> stepOptions = IntStream.range(0, matchedSteps.size())
                .mapToObj(optionPosition -> {
                            AppStep step = matchedSteps.get(optionPosition);
                            GraphTransition transition = step.getTransition();
                            String screenshotFile = ScreenshotPathUtils.getScreenshotPathForStep(step, state);
                            String nlStep = UtilReporter.getNLStep(step, false);

                            String key = Integer.toString(optionPosition);

                            if (uniqueOptionKeys.contains(key))
                                throw new RuntimeException(String.format("An option with the key %s already exists", key));
                            else
                                uniqueOptionKeys.add(key);

                            return new KeyValues(key,
                                    nlStep
                                    //        + " (" + getUniqueHashFromTransition2(transition) + ")"
                                    , screenshotFile);
                        }

                )
                .collect(Collectors.toList());

        state.put(S2R_ALL_AMBIGUOUS, matchedSteps);

        MessageObj messageObj = new MessageObj(
                "Remember that the displayed screenshots are <b>for reference only</b>."
                , WidgetName.S2RScreenSelector, true);

        return createChatBotMessages(
                "Got it! " +
                "It seems that you perform the following steps",
                "From the following options, please select the <b>steps that you performed</b> and click " +
                        "the \"<b>done</b>\" button", new ChatBotMessage(messageObj, stepOptions));


  /*      UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
        List<KeyValue> S2RScreens = Arrays.asList(
                new KeyValue("S2RScreen1","S2RScreen1.png"),
                new KeyValue("S2RScreen2","S2RScreen2.png"),
                new KeyValue("S2RScreen3","S2RScreen3.png"));
        MessageObj messageObj = new MessageObj(MessageFormat.format("Okay, it seems ambiguous, which of the following
         do you mean by \"{0}\"?",
                userMessage.getMessages().get(0).getMessage()), "S2RScreenSelector" );
        return createChatBotMessages(new ChatBotMessage(messageObj, S2RScreens, false));
*/
    }

    private String getFeedbackMessage(QualityFeedback feedback) {

        S2RQualityAssessment assessment = feedback.getQualityAssessments().get(0);

        final List<AppGuiComponent> components = assessment.getAmbiguousComponents();
        final List<String> actions = assessment.getAmbiguousActions();

        final String preFix = "this step refers to multiple ";

        if (!components.isEmpty() && !actions.isEmpty()) {
            final String assessmentTemplate = preFix + "UI components (e.g., %s) and multiple actions " +
                    "(e.g., %s).";
            return String.format(assessmentTemplate, getComponentsString(components), getActionsString(actions));
        } else if (!components.isEmpty()) {
            final String assessmentTemplate = preFix + "UI components (e.g., %s)";
            return String.format(assessmentTemplate, getComponentsString(components));
        } else {
            final String assessmentTemplate = preFix + "actions (e.g., %s)";
            return String.format(assessmentTemplate, getActionsString(actions));
        }
    }

    private String getComponentsString(List<AppGuiComponent> components) {
        int limit = 3;
        if (components.size() < limit)
            limit = components.size();
        return components.subList(0, limit).stream()
                .map(c -> "the " + UtilReporter.getComponentDescription(c))
                .collect(Collectors.joining(" or "));
    }


    private String getActionsString(List<String> actions) {
        return actions.stream().map(s -> "\"" + s.trim() + "\"").collect(Collectors.joining(" or "));
    }
}
