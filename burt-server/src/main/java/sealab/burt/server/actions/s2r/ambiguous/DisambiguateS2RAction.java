package sealab.burt.server.actions.s2r.ambiguous;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.UtilReporter;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.qualitychecker.s2rquality.S2RQualityAssessment;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.List;
import java.util.stream.Collectors;

import static sealab.burt.server.StateVariable.S2R_QUALITY_RESULT;

public @Slf4j
class DisambiguateS2RAction extends ChatBotAction {

    public DisambiguateS2RAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {
        QualityFeedback feedback = (QualityFeedback) state.get(S2R_QUALITY_RESULT);

        StringBuilder message = new StringBuilder("Oops, it seems ");
        message.append(getFeedbackMessage(feedback));
        return createChatBotMessages(message.toString(), "Can you please <b>rephrase the step</b> more accurately " +
                "<b>or provide a different step</b>?");

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
