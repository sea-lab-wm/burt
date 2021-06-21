package sealab.burt.server.actions.commons;

import org.apache.commons.io.FilenameUtils;
import sealab.burt.qualitychecker.graph.AppStep;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.server.StateVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.APP_PACKAGE;
import static sealab.burt.server.StateVariable.APP_VERSION;

public class ScreenshotPathUtils {

    public static final String DEFAULT_SCREENSHOT = "NO_SCREEN_AVAILABLE.png";

    public static String getScreenshotPathForGraphState(GraphState graphState,
                                                        ConcurrentHashMap<StateVariable, Object> state) {
        String stateScreenshotPath = graphState.getScreenshotPath();
        return getScreenshotPath(state, stateScreenshotPath);
    }

    private static String getScreenshotPath(ConcurrentHashMap<StateVariable, Object> state, String inputScreenshotPath) {
        Path screenshotPath;
        if (inputScreenshotPath != null) {
            String packageName = (String) state.get(APP_PACKAGE);
            String appVersion = (String) state.get(APP_VERSION);
            screenshotPath = Path.of(packageName + "-" + appVersion, "screenshots",
                    inputScreenshotPath);
        } else {
            screenshotPath = Path.of(DEFAULT_SCREENSHOT);
        }

        //-------------------

        Path fullScreenshotPath = Path.of("..", "data", "CrashScope-Data");
        fullScreenshotPath = fullScreenshotPath.resolve(screenshotPath);

        if (!Files.exists(fullScreenshotPath))
            screenshotPath = Path.of(DEFAULT_SCREENSHOT);

        //-------------------

        return FilenameUtils.separatorsToUnix(screenshotPath.toString());
    }

    public static String getScreenshotPathForStep(AppStep step, ConcurrentHashMap<StateVariable, Object> state) {
        String stepScreenshotPath = step.getScreenshotFile();
        return getScreenshotPath(state, stepScreenshotPath);
    }
}
