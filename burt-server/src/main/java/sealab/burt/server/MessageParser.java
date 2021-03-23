package sealab.burt.server;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class MessageParser {

    static ConcurrentHashMap<String, String> intentTokens;

    static {
        intentTokens = new ConcurrentHashMap<>();

        addIntentTokens("AFFIRMATIVE_ANSWER", Arrays.asList("sure", "yes", "ok", "okay", "absolutely", "yeah"));
        addIntentTokens("GREETING", Arrays.asList("hi", "hello", "yo", "hey", "hello"));
        addIntentTokens("NEGATIVE_ANSWER",Arrays.asList("no") );
        //....
    }

    public static void addIntentTokens(String intent, List<String> tokens) {
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public static String getIntent(UserMessage userMessage, ConcurrentHashMap<String, Object> state) {

        //------------------------

        if (userMessage.getMessages().get(0) != null) {

            MessageObj message = userMessage.getMessages().get(0);

            if (message.getMessage()!= null && Stream.of("bye", "good bye").anyMatch(token -> message.getMessage().toLowerCase().contains(token)))
                return "END_CONVERSATION";

        }


        //------------------------

        //get the next intent
        Object intent = state.get("NEXT_INTENT");
        if (intent != null && !"NO_EXPECTED_INTENT".equals(intent))
            return intent.toString();

        //------------------------

        if (userMessage.getMessages() == null) return null;

        MessageObj message = userMessage.getMessages().get(0);

        if (message == null) return null;
        //determine the intent based on tokens

        Set<Map.Entry<String, String>> entries = intentTokens.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (message.getMessage().toLowerCase().contains(entry.getKey())) return entry.getValue();
        }

        return null;

    }
}