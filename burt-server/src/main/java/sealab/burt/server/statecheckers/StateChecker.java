package sealab.burt.server.statecheckers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

public @Data @AllArgsConstructor
abstract class StateChecker {

    private String defaultAction;

    public abstract String nextAction(ConcurrentHashMap<String, Object> state);
}
