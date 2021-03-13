package sealab.burt.server;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class MessageParser {

    static ConcurrentHashMap<String, String> intentTokens;

    static {
        intentTokens = new ConcurrentHashMap<>();

        addIntentTokens("GREETING", Arrays.asList("hi", "hello", "yo", "hey"));
        addIntentTokens("AFFIRMATIVE_ANSWER", Arrays.asList("sure", "yes"));
        //....
    }

    public static void addIntentTokens(String intent, List<String> tokens) {
        for (String token : tokens) {
            intentTokens.put(token, intent);
        }
    }

    public static String getIntent(MessageObj message, ConcurrentHashMap<String, Object> state) {

        //get the next intent
        Object intent = state.get("NEXT_INTENT");
        if (intent !=null && !"NO_EXPECTED_INTENT".equals(intent))
            return intent.toString();

        //------------------------

        //determine the intent based on tokens

        Set<Map.Entry<String, String>> entries = intentTokens.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (message.getMessage().contains(entry.getKey())) return entry.getValue();
        }

        return null;

    }
}