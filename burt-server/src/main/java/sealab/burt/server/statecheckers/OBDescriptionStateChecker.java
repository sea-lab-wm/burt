package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.OBChecker;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.output.outputMessageObj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.actions.ActionName.*;

public class OBDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), PROVIDE_EB);
        put(QualityResult.Result.MULTIPLE_MATCH.name(), SELECT_OB_SCREEN);
        put(QualityResult.Result.NO_MATCH.name(), REPHRASE_OB);
        put(QualityResult.Result.NO_PARSED.name(), PROVIDE_OB_NO_PARSE);
    }};

    public OBDescriptionStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {
        try {
            QualityResult result = runOBQualityCheck(state);
            if (result.getResult().name().equals("MATCH")){
                UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
                String message = userMessage.getMessages().get(0).getMessage();
                if (!state.containsKey(OB_DESCRIPTION)){
                    List<outputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new outputMessageObj(message, null));
                    state.put(OB_DESCRIPTION, outputMessageList);
                }else{
                    List<outputMessageObj> outputMessage = (List<outputMessageObj>) state.get(OB_DESCRIPTION);
                    outputMessage.add(new outputMessageObj(message, null));
                }
            }
            return nextActions.get(result.getResult().name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }
    }

}
