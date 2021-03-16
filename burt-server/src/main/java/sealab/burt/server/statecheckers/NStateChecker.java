package sealab.burt.server.statecheckers;


import java.util.concurrent.ConcurrentHashMap;

public class NStateChecker extends StateChecker {
    public NStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        return getDefaultAction();
    }
}