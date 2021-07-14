package sealab.burt.server.actions.commons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphDataSource;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.APP_PACKAGE;
import static sealab.burt.server.StateVariable.APP_VERSION;

public @Slf4j
class ScreenshotPathUtils {

    public static final String DEFAULT_SCREENSHOT = "NO_SCREEN_AVAILABLE.png";

    public static String getScreenshotPathForGraphState(GraphState graphState,
                                                        ConcurrentHashMap<StateVariable, Object> state) {
        if (graphState == null) return getScreenshotPath(state, null, null);
        String stateScreenshotPath = graphState.getScreenshotPath();
        return getScreenshotPath(state, stateScreenshotPath, graphState.getDataSource());
    }

    private static String getScreenshotPath(ConcurrentHashMap<StateVariable, Object> state,
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
        String prefix = BurtConfigPaths.crashScopeDataFolder;
        if (GraphDataSource.TR.equals(dataSource) && !screenshotPath.equals(Path.of(DEFAULT_SCREENSHOT))) {
            prefix = BurtConfigPaths.traceReplayerDataFolder;
            fullScreenshotPath = Path.of(BurtConfigPaths.traceReplayerDataPath);
        }
        fullScreenshotPath = fullScreenshotPath.resolve(screenshotPath);

        if (!Files.exists(fullScreenshotPath)) {
            log.warn("Screenshot file does not exist: " + fullScreenshotPath);
            prefix = BurtConfigPaths.crashScopeDataFolder;
            screenshotPath = Path.of(DEFAULT_SCREENSHOT);
        }

        //-------------------

        return FilenameUtils.separatorsToUnix("/" + prefix + "/" + screenshotPath.toString());
    }

    public static String getScreenshotPathForStep(AppStep step, ConcurrentHashMap<StateVariable, Object> state) {
        String stepScreenshotPath = step.getScreenshotFile();
        GraphDataSource dataSource = step.getCurrentState().getDataSource();
        if (step.getTransition() != null)
            dataSource = step.getTransition().getDataSource();
        return getScreenshotPath(state, stepScreenshotPath, dataSource);
    }
}
