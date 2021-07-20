package sealab.burt.server.statecheckers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.ConversationState;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.conversation.UserResponse;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;

public @Data @Slf4j
abstract class StateChecker {

    private ActionName defaultAction;

    public StateChecker(ActionName defaultAction){
        this.defaultAction = defaultAction;
    }

    public abstract ActionName nextAction(ConversationState state);

    protected QualityFeedback runS2RQualityCheck(ConversationState state) throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        S2RChecker checker = (S2RChecker) state.get(S2R_CHECKER);
        MessageObj messageObj = userResponse.getFirstMessage();
        QualityFeedback qualityResult = checker.checkS2R(messageObj.getMessage());
        state.put(S2R_QUALITY_RESULT, qualityResult);
        log.debug("S2R quality result: " + qualityResult.getAssessmentResults());
        return qualityResult;
    }

    protected QualityResult runOBQualityCheck(ConversationState state) throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        OBChecker obChecker = (OBChecker) state.get(OB_CHECKER);
        QualityResult result = obChecker.checkOb(userResponse.getFirstMessage().getMessage());
        state.put(OB_QUALITY_RESULT, result);
        log.debug("OB quality check: " + result);
        return result;
    }

    protected QualityResult runEBQualityCheck(ConversationState state, GraphState obState,
                                              String obDescription)
            throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        EBChecker ebChecker = (EBChecker) state.get(EB_CHECKER);
        QualityResult result = ebChecker.checkEb(userResponse.getFirstMessage().getMessage(), obState,
                obDescription);
        state.put(EB_QUALITY_RESULT, result);
        log.debug("EB quality check: " + result);
        return result;
    }
}
