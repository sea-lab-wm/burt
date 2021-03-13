package sealab.burt.server.statecheckers;

import java.util.concurrent.ConcurrentHashMap;

public class NoStateChecker extends StateChecker {
    public NoStateChecker(String defaultAction) {
        super(defaultAction);
    }

    @Override
    public String nextAction(ConcurrentHashMap<String, Object> state) {
        return getDefaultAction();
    }
}
