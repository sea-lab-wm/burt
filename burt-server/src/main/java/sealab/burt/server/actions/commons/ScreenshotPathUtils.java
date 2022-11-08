package sealab.burt.server.actions.commons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphDataSource;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.server.conversation.state.ConversationState;

import java.nio.file.Files;
import java.nio.file.Path;

import static sealab.burt.server.StateVariable.APP_PACKAGE;
import static sealab.burt.server.StateVariable.APP_VERSION;

public @Slf4j
class ScreenshotPathUtils {

    public static final String DEFAULT_SCREENSHOT = "NO_SCREEN_AVAILABLE.png";
    private static final String OPEN_APP_SCREENSHOT = "OPEN_APP.png";

    public static String getScreenshotPathForGraphState(GraphState graphState,
                                                        ConversationState state) {
        if (graphState == null) return getScreenshotPath(state, null, null);
        String stateScreenshotPath = graphState.getScreenshotPath();
        return getScreenshotPath(state, stateScreenshotPath, graphState.getDataSource());
    }

    public static String getScreenshotPath(ConversationState state,
                                           String inputScreenshotPath,
                                           GraphDataSource dataSource) {
        Path screenshotPath;
        if (inputScreenshotPath != null) {
            String packageName = (String) state.get(APP_PACKAGE);
            String appVersion = (String) state.get(APP_VERSION);
            screenshotPath = Path.of(packageName + "-" + appVersion, "screenshots",
                    inputScreenshotPath);
        } else {
            screenshotPath = Path.of(DEFAULT_SCREENSHOT);
        }

        //--------------------------

        Path fullScreenshotPath = Path.of(BurtConfigPaths.crashScopeDataPath);
        String prefix = BurtConfigPaths.crashScopeDataFolderName;
        if (GraphDataSource.TR.equals(dataSource) && !screenshotPath.equals(Path.of(DEFAULT_SCREENSHOT))) {
            prefix = BurtConfigPaths.traceReplayerDataFolderName;
            fullScreenshotPath = Path.of(BurtConfigPaths.traceReplayerDataPath);
        } else if (GraphDataSource.US.equals(dataSource)) {
            screenshotPath = Path.of(inputScreenshotPath);
            prefix = BurtConfigPaths.userScreenshotPath;
            fullScreenshotPath = Path.of(BurtConfigPaths.fullUserScreenshotPath);
        }

        fullScreenshotPath = fullScreenshotPath.resolve(screenshotPath);

        if (!Files.exists(fullScreenshotPath)) {
            log.warn("Screenshot file does not exist: " + fullScreenshotPath);
            prefix = BurtConfigPaths.crashScopeDataFolderName;
            screenshotPath = Path.of(DEFAULT_SCREENSHOT);
        }

        //-------------------

        return FilenameUtils.separatorsToUnix("/" + prefix + "/" + screenshotPath);
    }

    public static String getScreenshotPathForStep(AppStep step, ConversationState state) {

        if (DeviceUtils.isOpenApp(step.getAction())) {
            return FilenameUtils.separatorsToUnix("/" + BurtConfigPaths.crashScopeDataFolderName + "/" + OPEN_APP_SCREENSHOT);
        }

        String stepScreenshotPath = step.getScreenshotFile();
        GraphDataSource dataSource = step.getCurrentState().getDataSource();
        if (step.getTransition() != null)
            dataSource = step.getTransition().getDataSource();
        return getScreenshotPath(state, stepScreenshotPath, dataSource);
    }
}
