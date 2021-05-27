package sealab.burt.server.msgparsing;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.NEXT_INTENT;
import static sealab.burt.server.StateVariable.REPORT_GENERATED;
import static sealab.burt.server.msgparsing.Intent.*;

public class MessageParser {

    static ConcurrentHashMap<String, Intent> intentTokens;

    static {
        intentTokens = new ConcurrentHashMap<>();

        addIntentTokens(AFFIRMATIVE_ANSWER, Arrays.asList("sure", "yes", "ok", "okay", "absolutely", "yeah"));
        addIntentTokens(GREETING, Arrays.asList("hi", "hello", "yo", "hey"));
        addIntentTokens(NEGATIVE_ANSWER, Arrays.asList("no", "nah"));
        addIntentTokens(THANKS, Arrays.asList("thanks", "thank you"));
        //....
    }

    public static void addIntentTokens(Intent intent, List<String> tokens) {
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public static Intent getIntent(UserMessage userMessage, ConcurrentHashMap<StateVariable, Object> state) {

        if (state.get(REPORT_GENERATED) != null)
            return END_CONVERSATION;

        //------------------------

        if (userMessage.getMessages().get(0) != null) {

            MessageObj message = userMessage.getMessages().get(0);

            if (message.getMessage() != null && Stream.of("bye", "good bye", "see ya", "see you")
                    .anyMatch(token -> message.getMessage().toLowerCase().contains(token)))
                return END_CONVERSATION;

        }

        //------------------------

        //get the next intent
        Object intent = state.get(NEXT_INTENT);
        if (intent != null && !NO_EXPECTED_INTENT.equals(intent))
            return (Intent) intent;

        //------------------------

        if (userMessage.getMessages() == null) return null;

        MessageObj message = userMessage.getMessages().get(0);

        if (message == null || message.getMessage() == null) return null;
        //determine the intent based on tokens

        Set<Map.Entry<String, Intent>> entries = intentTokens.entrySet();
        for (Map.Entry<String, Intent> entry : entries) {
            if (message.getMessage().toLowerCase().contains(entry.getKey())) return entry.getValue();
        }

        return null;

    }
}