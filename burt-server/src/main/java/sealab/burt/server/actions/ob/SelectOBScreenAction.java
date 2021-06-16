package sealab.burt.server.actions.ob;

import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.KeyValues;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.msgparsing.Intent;
import sealab.burt.server.statecheckers.QualityStateUpdater;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.OB_QUALITY_RESULT;

public class SelectOBScreenAction extends ChatBotAction {

    public SelectOBScreenAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) {

        MessageObj messageObj = new MessageObj(
                " Please hit the \"Done\" button after you have selected it.", "OBScreenSelector");

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        List<GraphState> matchedStates = result.getMatchedStates();

        List<KeyValues> options = getObScreenOptions(matchedStates);

        if (options.isEmpty())
            throw new RuntimeException("There are no options to show");

        return createChatBotMessages(
                "Got it. From the list below, can you please select the screen that is having the problem?",
                new ChatBotMessage(messageObj, options, true));
    }

    public static List<KeyValues> getObScreenOptions(List<GraphState> matchedStates) {
        int maxNumOfResults = 5;
        int initialResult = 0;
        return IntStream.range(initialResult, maxNumOfResults)
                .mapToObj(i -> {
                            if (matchedStates.size() <= i) return null;
                            GraphState graphState = matchedStates.get(i);
                            String screenshotFile = graphState.getScreenshotPath();

                            String description = GraphTransition.getWindowString(graphState.getScreen().getActivity(),
                                    graphState.getScreen().getWindow());
                            final int LIMIT_WINDOW_TEXT = 100;
                            if (description.length() > LIMIT_WINDOW_TEXT) {
                                description = description.substring(0, LIMIT_WINDOW_TEXT) + "...";
                            }

                            return new KeyValues(Integer.toString(i),
                                    description,
                                    screenshotFile == null ? QualityStateUpdater.DEFAULT_SCREENSHOT : screenshotFile);
                        }

                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
