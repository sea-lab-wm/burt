package sealab.burt.server.msgparsing;

import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.PROVIDE_PARTICIPANT_ID;
import static sealab.burt.server.msgparsing.Intent.CONFIRM_END_CONVERSATION;
import static sealab.burt.server.msgparsing.Intent.*;

public class MessageParser {

    static ConcurrentHashMap<String, Intent> intentTokens;
    static String[] END_CONVERSATION_TOKENS = {"bye", "good bye", "goodbye", "see ya", "see you",
            "restart.+(conversation|chatbot)"};

    static {
        intentTokens = new ConcurrentHashMap<>();

        //these could be regular expressions
        addIntentTokens(AFFIRMATIVE_ANSWER, Arrays.asList("sure", "yes", "ok", "okay", "absolutely", "yeah", "yep"));
        addIntentTokens(GREETING, Arrays.asList("hi", "hello", "yo", "hey", "what's up", "hola",
                "(report|describe|detail) .+ (bug|issue|problem)"));
        addIntentTokens(NEGATIVE_ANSWER, Arrays.asList("no", "nah", "nope"));
        // we drop this one for now, if we decide to use it, then we need to change also the action
        //        addIntentTokens(THANKS, Arrays.asList("thanks", "thank you"));
        //....
    }

    public static void addIntentTokens(Intent intent, List<String> tokens) {
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public static Intent getIntent(UserResponse userResponse, ConversationState state) {

        if (state.containsKey(REPORT_GENERATED))
            return END_CONVERSATION;

        //------------------------

        if (userResponse.getMessages() == null) return null;

        if (userResponse.getFirstMessage() != null) {

            MessageObj message = userResponse.getFirstMessage();

            if (message.getMessage() != null && !state.containsKey(StateVariable.CONFIRM_END_CONVERSATION)) {
                if (Stream.of(END_CONVERSATION_TOKENS).anyMatch(token -> message.getMessage().toLowerCase().contains(token)
                        || matchRegex(token, message))) {

                    state.put(StateVariable.CONFIRM_END_CONVERSATION, true);
                    ActionName action = (ActionName) state.get(LAST_ACTION);
                    UserResponse lastUserResponse = (UserResponse) state.get(LAST_MESSAGE);

                    state.put(ACTION_NEGATIVE_END_CONVERSATION, Objects.requireNonNullElse(action,
                            PROVIDE_PARTICIPANT_ID));

                    if (lastUserResponse != null) state.put(MSG_NEGATIVE_END_CONVERSATION, lastUserResponse);

                    return CONFIRM_END_CONVERSATION;
                }
            }

        }

        //------------------------

        //get the next intent
        Object intents = state.get(NEXT_INTENTS);
        List<Intent> nextIntents = (List<Intent>) intents;
        if (nextIntents != null) {

            if (nextIntents.size() == 1 && !nextIntents.contains(NO_EXPECTED_INTENT))
                return ((List<Intent>) intents).get(0);
        }

        //------------------------

        MessageObj message = userResponse.getMessages().get(0);

        if (message == null || message.getMessage() == null) return null;
        //determine the intent based on tokens

        Set<Map.Entry<String, Intent>> entries = intentTokens.entrySet();

        //only consider the tokens of the expected intents
        if (nextIntents != null && nextIntents.size() > 1) {
            entries = intentTokens.entrySet().stream()
                    .filter(entry -> nextIntents.contains(entry.getValue()))
                    .collect(Collectors.toSet());
        }

        //check the message
        for (Map.Entry<String, Intent> entry : entries) {
            if (message.getMessage().toLowerCase().contains(entry.getKey()) || matchRegex(entry.getKey(), message))
                return entry.getValue();
        }

        return null;

    }

    private static boolean matchRegex(String regex, MessageObj messageObj) {
        return Pattern.compile(regex).matcher(messageObj.getMessage().toLowerCase()).find();
    }
}