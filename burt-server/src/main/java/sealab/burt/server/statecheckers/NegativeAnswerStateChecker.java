package sealab.burt.server.statecheckers;

import java.util.concurrent.ConcurrentHashMap;

public class NegativeAnswerStateChecker extends StateChecker {

    public NegativeAnswerStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        String nextAction = null;

        if (state.containsKey("CONFIRM_LAST_STEP")) {
            nextAction = "PROVIDE_S2R";
            state.putIfAbsent("CONVERSATION_STATE", "COLLECTING_S2R");
        } else if(state.containsKey("APP_ASKED")){
            nextAction = "SELECT_APP";
        } else if (state.containsKey("COLLECTING_EB")){
            nextAction = "CLARIFY_EB";
        }
        return nextAction;


    }
}
