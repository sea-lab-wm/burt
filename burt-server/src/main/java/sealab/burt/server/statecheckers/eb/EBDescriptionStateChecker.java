package sealab.burt.server.statecheckers.eb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.entity.UserResponse;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.state.QualityStateUpdater;
import sealab.burt.server.output.BugReportElement;
import sealab.burt.server.statecheckers.StateChecker;
import sealab.burt.server.statecheckers.ob.OBDescriptionStateChecker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class EBDescriptionStateChecker extends StateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private final static ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), PROVIDE_S2R_FIRST);
        put(QualityResult.Result.NO_MATCH.name(), CLARIFY_EB);
        put(QualityResult.Result.NOT_PARSED.name(), PROVIDE_EB_NO_PARSE);
    }};

    public EBDescriptionStateChecker() {
        super(null);
    }

    @Override
    public ActionName nextAction(ConversationState state) {
        try {

            List<BugReportElement> obReportElements = (List<BugReportElement>) state.get(REPORT_OB);

            GraphState obState = null;
            String obDescription = null;
            if (obReportElements != null) {
                BugReportElement bugReportElement = obReportElements.get(0);
                obState = (GraphState) bugReportElement.getOriginalElement();
                obDescription = bugReportElement.getStringElement();
            }

            QualityResult result = runEBQualityCheck(state, obState, obDescription);

            UserResponse userResponse = (UserResponse) state.get(CURRENT_MESSAGE);
            state.put(EB_DESCRIPTION, userResponse.getFirstMessage().getMessage());

            ActionName nextAction = nextActions.get(result.getResult().name());
            if (result.getResult().equals(QualityResult.Result.MATCH)) {
                state.getStateUpdater().updateEBState(state, obState);
            } else if (result.getResult().equals(QualityResult.Result.NO_MATCH)) {
                //if there is no OB match, we "skip" EB quality checking (only if there is no EB match)
                if (obReportElements == null || obReportElements.get(0).getOriginalElement() == null) {

                    state.getStateUpdater().updateEBState(state, null);
                    nextAction = PROVIDE_S2R_FIRST;
                } else {

                    state.initOrIncreaseCurrentAttemptEbNoMatch();

                    boolean nextAttempt = state.checkNextAttemptAndResetEbNoMatch();

                    if (!nextAttempt) {
                        nextAction = PROVIDE_S2R_FIRST;
                        state.getStateUpdater().updateEBState(state, null);
                    }
                }
            } else if (result.getResult().equals(QualityResult.Result.NOT_PARSED)) {
                state.initOrIncreaseCurrentAttemptEbNotParsed();

                boolean nextAttempt = state.checkNextAttemptAndResetEbNotParsed();

                if (!nextAttempt) {
                    nextAction = PROVIDE_S2R_FIRST;
                    state.getStateUpdater().updateEBState(state, null);
                }
            }

            return nextAction;
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }
    }

}
