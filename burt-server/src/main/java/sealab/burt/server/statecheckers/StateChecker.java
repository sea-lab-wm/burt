package sealab.burt.server.statecheckers;

import lombok.Data;
import sealab.burt.server.actions.ActionName;

import java.util.concurrent.ConcurrentHashMap;

public @Data
abstract class StateChecker {

    private ActionName defaultAction;

    public StateChecker(ActionName defaultAction){
        this.defaultAction = defaultAction;
    }

    public abstract ActionName nextAction(ConcurrentHashMap<String, Object> state);
}
