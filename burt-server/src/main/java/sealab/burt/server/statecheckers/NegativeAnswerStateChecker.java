package sealab.burt.server.statecheckers;

import java.util.concurrent.ConcurrentHashMap;

public class NegativeAnswerStateChecker extends StateChecker {

    public NegativeAnswerStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        String nextAction = null;

        if (state.get("CONVERSATION_STATE").equals("CONFIRM_LAST_STEP")) {
            nextAction = "PROVIDE_S2R";
            state.putIfAbsent("CONVERSATION_STATE", "COLLECTING_S2R");
        } else if(state.get("CONVERSATION_STATE").equals("APP_ASKED")){
            nextAction = "SELECT_APP";
        }
        return nextAction;


    }
}
