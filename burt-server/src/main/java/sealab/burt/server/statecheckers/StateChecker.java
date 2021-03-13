package sealab.burt.server.statecheckers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

public @Data
abstract class StateChecker {

    private String defaultAction;

    public StateChecker(String defaultAction){
        this.defaultAction = defaultAction;
    }

    public abstract String nextAction(ConcurrentHashMap<String, Object> state);
}
