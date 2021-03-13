package sealab.burt.server.statecheckers;

import java.util.concurrent.ConcurrentHashMap;

public class AffirmativeAnswerStateChecker extends StateChecker {
    public AffirmativeAnswerStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        String nextAction = null;

//        if (state.isEmpty()) {
            nextAction = "PROVIDE_OB";
            state.putIfAbsent("COLLECTING_OB", true);
//        }

        return nextAction;
    }
}
