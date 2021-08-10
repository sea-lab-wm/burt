package sealab.burt.server.actions.ob;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.KeyValues;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.msgparsing.Intent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sealab.burt.server.StateVariable.OB_QUALITY_RESULT;
import static sealab.burt.server.actions.commons.ScreenshotPathUtils.getScreenshotPathForGraphState;

public @Slf4j
class SelectOBScreenAction extends ChatBotAction {

    public static final int MAX_OB_SCREENS_TO_SHOW = 5;

    public SelectOBScreenAction(Intent nextExpectedIntent) {
        super(nextExpectedIntent);
    }

    @Override
    public List<ChatBotMessage> execute(ConversationState state) {

        QualityResult result = (QualityResult) state.get(OB_QUALITY_RESULT);
        List<GraphState> matchedStates = result.getMatchedStates();
        //--------------------------------

        int currentObScreenPosition = 0;
        state.put(StateVariable.CURRENT_OB_SCREEN_POSITION, currentObScreenPosition);

        state.initOrIncreaseCurrentAttemptObScreens();

        //-----------------------------------

        log.debug("Multiple matched states: " + matchedStates.size());
//        log.debug("Current attempt for OB screen selection: " + currentAttempt);

        //----------------------------------------

        List<KeyValues> options = getObScreenOptions(matchedStates, state, currentObScreenPosition);

        //it is guaranteed there are initial options
        if (options.isEmpty())
            throw new RuntimeException("There are no options to show");

        MessageObj messageObj = new MessageObj(
                " Please click the \"<b>done</b>\" button after you have selected it", WidgetName.OBScreenSelector);

        return createChatBotMessages(
                "Got it. From the list below, can you please select the screen that is <b>having or triggering</b> " +
                        "the problem?",
                new ChatBotMessage(messageObj, options, false));
    }

    public static List<KeyValues> getObScreenOptions(List<GraphState> matchedStates,
                                                     ConversationState state,
                                                     int initialResult) {
        int maxNumOfResults = initialResult + MAX_OB_SCREENS_TO_SHOW;
        Set<String> uniqueOptionKeys = new LinkedHashSet<>();
        return IntStream.range(initialResult, maxNumOfResults)
                .mapToObj(optionPosition -> {
                            if (matchedStates.size() <= optionPosition) return null;
                            GraphState graphState = matchedStates.get(optionPosition);

                          /*  String description = GraphTransition.getWindowString(graphState.getScreen().getActivity(),
                                    graphState.getScreen().getWindow());
                            final int LIMIT_WINDOW_TEXT = 100;
                            if (description.length() > LIMIT_WINDOW_TEXT) {
                                description = description.substring(0, LIMIT_WINDOW_TEXT) + "...";
                            }*/

                            String screenshotFile = getScreenshotPathForGraphState(graphState, state);
                            String key = Integer.toString(optionPosition);

                            if (uniqueOptionKeys.contains(key))
                                throw new RuntimeException(String.format("An option with the key %s already exists",
                                        key));
                            else
                                uniqueOptionKeys.add(key);

//                    String optionDescription = (optionPosition + 1) + ". " + description;
                            String optionDescription = "";
                            return new KeyValues(key,
                                    optionDescription
                                    //+ " (" + graphState.getUniqueHash().toString() + ")"
                                    , screenshotFile);
                        }

                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
