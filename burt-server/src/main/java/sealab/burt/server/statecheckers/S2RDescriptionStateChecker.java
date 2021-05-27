package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.qualitychecker.S2RChecker;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.conversation.UserMessage;
import sealab.burt.server.output.outputMessageObj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.StateVariable.OB_DESCRIPTION;
import static sealab.burt.server.actions.ActionName.*;

public class S2RDescriptionStateChecker extends StateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBDescriptionStateChecker.class);

    private static final ConcurrentHashMap<String, ActionName> nextActions = new ConcurrentHashMap<>() {{
        put(QualityResult.Result.MATCH.name(), PROVIDE_S2R);
        put(QualityResult.Result.MULTIPLE_MATCH.name(), ActionName.DISAMBIGUATE_S2R);
        put(QualityResult.Result.NO_MATCH.name(), REPHRASE_S2R);
        put(QualityResult.Result.NO_S2R_INPUT.name(), SPECIFY_INPUT_S2R);
        put(QualityResult.Result.MISSING_STEPS.name(), SELECT_MISSING_S2R);
        put(QualityResult.Result.NO_PARSED.name(), PROVIDE_S2R_NO_PARSE);
    }};

    public S2RDescriptionStateChecker(ActionName defaultAction) {
        super(defaultAction);
    }

    @Override
    public ActionName nextAction(ConcurrentHashMap<StateVariable, Object> state) {

        try {
            UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
            String message = userMessage.getMessages().get(0).getMessage();
            String targetString = "last step";
            if (message.toLowerCase().contains(targetString.toLowerCase())){
                return ActionName.CONFIRM_LAST_STEP;
            }else {
                QualityResult result = runS2RChecker(state);
//                String description = result.getDescription();
//                String screenshotPath = result.getScreenshotPath();
//                String qualityFeedback = result.getQualityFeedback();
//                state.put(S2R_DESCRIPTION, description);
//                state.put(S2R_SCREEN, screenshotPath);
                String description = "S2R description";
                String screenshotPath = "app_logos/S2RScreen1.png";
                state.put(S2R_DESCRIPTION, description);
                state.put(S2R_SCREEN, screenshotPath);

                if (result.getResult().equals(QualityResult.Result.MATCH)){
                    if (!state.containsKey(REPORT_S2R)){
                        List<outputMessageObj> outputMessageList = new ArrayList<>();
                        outputMessageList.add(new outputMessageObj(description, screenshotPath));
                        state.put(REPORT_S2R, outputMessageList);
                    }else{
                        List<outputMessageObj> outputMessage = (List<outputMessageObj>) state.get(REPORT_S2R);
                        outputMessage.add(new outputMessageObj(description, screenshotPath));
                    }
                }

                return nextActions.get(result.getResult().name());
            }
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return null;
        }

    }

}
