package sealab.burt.server.statecheckers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.qualitychecker.QualityResult;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ActionName;
import sealab.burt.server.output.OutputMessageObj;
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
//            String screenshotPath = result.getScreenshotPath();
//            String description = result.getDescription();
//            String qualityFeedback = result.getQualityFeedback();
//            state.put(OB_DESCRIPTION, description);
//            state.put(OB_SCREEN, screenshotPath);
//            state.put(OB_QUALITY_FEEDBACK, qualityFeedback);

            // if result is MULTIPLE_MATCH, it will return multiple screenshots
            String description = "OB description";
            String screenshotPath = "app_logos/OBScreen.png";
            state.put(OB_DESCRIPTION, description);
            state.put(OB_SCREEN, screenshotPath);

            if (result.getResult().equals(QualityResult.Result.MATCH)){
                // UserMessage userMessage = (UserMessage) state.get(CURRENT_MESSAGE);
                //String message = userMessage.getMessages().get(0).getMessage();
                if (!state.containsKey(REPORT_OB)){
                    List<OutputMessageObj> outputMessageList = new ArrayList<>();
                    outputMessageList.add(new OutputMessageObj(description, screenshotPath));
                    state.put(REPORT_OB, outputMessageList);
                }else{
                    List<OutputMessageObj> outputMessage = (List<OutputMessageObj>) state.get(REPORT_OB);
                    outputMessage.add(new OutputMessageObj(description, screenshotPath));
                }
            }
            return nextActions.get(result.getResult().name());
        } catch (Exception e) {
            LOGGER.error("There was an error", e);
            return UNEXPECTED_ERROR;
        }
    }

}
