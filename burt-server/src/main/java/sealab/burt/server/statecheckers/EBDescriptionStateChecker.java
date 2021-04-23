package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.EBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class EBDescriptionStateChecker extends StateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private final static ConcurrentHashMap<String, ActionName> nextActions= new ConcurrentHashMap<>(){{
        put(QualityResult.Result.MATCH.name(), PROVIDE_S2R_FIRST);
        put(QualityResult.Result.NO_MATCH.name(), CLARIFY_EB);
    }};

    public EBDescriptionStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        try {
            UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
            EBChecker ebChecker = (EBChecker) state.get(EB_CHECKER);
            QualityResult result = ebChecker.checkEb(userMessage.getMessages().get(0).getMessage());
            state.put(EB_QUALITY_RESULT, result);
            return nextActions.get(result.getResult().name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return null;
        }
    }

}
