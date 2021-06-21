package sealab.burt.server.msgparsing;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.NEXT_INTENTS;
import static sealab.burt.server.StateVariable.REPORT_GENERATED;
import static sealab.burt.server.msgparsing.Intent.*;

public class MessageParser {

    static ConcurrentHashMap<String, Intent> intentTokens;

    static {
        intentTokens = new ConcurrentHashMap<>();

        addIntentTokens(AFFIRMATIVE_ANSWER, Arrays.asList("sure", "yes", "ok", "okay", "absolutely", "yeah", "yep"));
        addIntentTokens(GREETING, Arrays.asList("hi", "hello", "yo", "hey", "what's up", "hola"));
        addIntentTokens(NEGATIVE_ANSWER, Arrays.asList("no", "nah", "nope"));
        addIntentTokens(THANKS, Arrays.asList("thanks", "thank you"));
        //....
    }

    public static void addIntentTokens(Intent intent, List<String> tokens) {
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public static Intent getIntent(UserResponse userResponse, ConcurrentHashMap<StateVariable, Object> state) {

        if (state.containsKey(REPORT_GENERATED))
            return END_CONVERSATION;

        //------------------------

        if (userResponse.getMessages().get(0) != null) {

            MessageObj message = userResponse.getFirstMessage();

            if (message.getMessage() != null && Stream.of("bye", "good bye", "see ya", "see you")
                    .anyMatch(token -> message.getMessage().toLowerCase().contains(token)))
                return END_CONVERSATION;

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

        if (userResponse.getMessages() == null) return null;

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

        for (Map.Entry<String, Intent> entry : entries) {
            if (message.getMessage().toLowerCase().contains(entry.getKey())) return entry.getValue();
        }

        return null;

    }
}