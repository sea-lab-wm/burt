package sealab.burt.server.statecheckers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public @Data @Slf4j
abstract class StateChecker {

    private ActionName defaultAction;

    public StateChecker(ActionName defaultAction){
        this.defaultAction = defaultAction;
    }

    public abstract ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state);

    protected QualityFeedback runS2RChecker(ConcurrentHashMap<StateVariable, Object> state) throws Exception {
        UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
        S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
        QualityFeedback qualityResult = checker.checkS2R(userMessage.getMessages().get(0).getMessage());
        state.put(S2R_QUALITY_RESULT, qualityResult);
        log.debug("S2R quality result: " + qualityResult);
        return qualityResult;
    }

    protected QualityResult runOBQualityCheck(ConcurrentHashMap<StateVariable, Object> state) throws Exception {
        UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
        OBChecker obChecker = (OBChecker) state.get(OB_CHECKER);
        QualityResult result = obChecker.checkOb(userMessage.getMessages().get(0).getMessage());
        state.put(OB_QUALITY_RESULT, result);
        log.debug("OB quality check: " + result);
        return result;
    }

    protected QualityResult runEBCheck(ConcurrentHashMap<StateVariable, Object> state) throws Exception {
        UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
        EBChecker ebChecker = (EBChecker) state.get(EB_CHECKER);
        QualityResult result = ebChecker.checkEb(userMessage.getMessages().get(0).getMessage());
        state.put(EB_QUALITY_RESULT, result);
        log.debug("EB quality check: " + result);
        return result;
    }
}
