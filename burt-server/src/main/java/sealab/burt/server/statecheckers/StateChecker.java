package sealab.burt.server.statecheckers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.NewEBChecker;
import sealab.burt.qualitychecker.NewOBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.NewS2RChecker;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.s2rquality.QualityFeedback;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.UserResponse;

import static sealab.burt.server.StateVariable.*;

public @Data
@Slf4j
abstract class StateChecker {

    private ActionName defaultAction;

    public StateChecker(ActionName defaultAction) {
        this.defaultAction = defaultAction;
    }

    public abstract ActionName nextAction(ConversationState state) throws Exception;

    protected QualityFeedback runS2RQualityCheck(ConversationState state) throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        NewS2RChecker checker = (NewS2RChecker) state.get(S2R_CHECKER);
        MessageObj messageObj = userResponse.getFirstMessage();
        QualityFeedback qualityResult = checker.checkS2R(messageObj.getMessage());
        state.put(S2R_QUALITY_RESULT, qualityResult);
        log.debug("S2R quality result: " + qualityResult.getAssessmentResults());
        return qualityResult;
    }

    protected QualityResult runOBQualityCheck(ConversationState state) throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        NewOBChecker obChecker = (NewOBChecker) state.get(OB_CHECKER);
        QualityResult result = obChecker.checkOb(userResponse.getFirstMessage().getMessage());
        state.put(OB_QUALITY_RESULT, result);
        log.debug("OB quality check: " + result);
        return result;
    }

    protected QualityResult runEBQualityCheck(ConversationState state, GraphState obState,
                                              String obDescription) throws Exception {
        UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
        NewEBChecker ebChecker = (NewEBChecker) state.get(EB_CHECKER);
        QualityResult result = ebChecker.checkEb(userResponse.getFirstMessage().getMessage(), obState,
                obDescription);
        state.put(EB_QUALITY_RESULT, result);
        log.debug("EB quality check: " + result);
        return result;
    }
}
