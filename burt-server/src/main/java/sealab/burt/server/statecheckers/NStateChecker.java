package sealab.burt.server.statecheckers;


import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;

import java.util.concurrent.ConcurrentHashMap;

public class NStateChecker extends StateChecker {
    public NStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        return getDefaultAction();
    }
}