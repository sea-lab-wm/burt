package sealab.burt.server.actions.newapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import sealab.burt.BurtConfigPaths;

public class NewAppControllerTest {

    @Test
    void testAddNewApp() throws Exception {
        Path iconFile = Path.of("..", "data", "new_app_tests", "NO_APP_LOGO_test.png");
        String packageNameVersion = "com.android.gpstest-3.8.0";
        Path csFile = Path.of("..", "data", "new_app_tests", "com.android.gpstest-3.8.0-cs.zip");
        Path trFile = Path.of("..", "data", "new_app_tests", "com.android.gpstest-3.8.0-tr.zip");
        String sessionId = "sessionId";

        addNewApp(iconFile, packageNameVersion, csFile, trFile, sessionId);

    }

    @Test
    void testAddNewApp2() throws Exception {
        Path iconFile = Path.of("..", "data", "new_app_tests", "NO_APP_LOGO_test.png");
        String packageNameVersion = "com.android.gpstest-3.8.0";
        Path csFile = Path.of("..", "data", "new_app_tests", "com.android.gpstest-3.8.0-cs2.zip");
        Path trFile = Path.of("..", "data", "new_app_tests", "com.android.gpstest-3.8.0-tr2.zip");
        String sessionId = "sessionId";

        addNewApp(iconFile, packageNameVersion, csFile, trFile, sessionId);

    }

    @Test
    void testAddNewApp3() throws Exception {
        Path iconFile = Path.of("..", "data", "new_app_tests", "NO_APP_LOGO_test.png");
        String packageNameVersion = "com.cohenadair.anglerslog-1.3.1";
        Path csFile = Path.of("..", "data", "new_app_tests", "com.cohenadair.anglerslog-1.3.1.zip");
        Path trFile = null;
        String sessionId = "sessionId";

        addNewApp(iconFile, packageNameVersion, csFile, trFile, sessionId);

    }

    private void addNewApp(Path iconFile, String packageNameVersion, Path csFile, Path trFile, String sessionId)
            throws IOException, Exception {
        try {
            MultipartFile appIcon = new MockMultipartFile(iconFile.toFile().getName(), iconFile.toFile().getName(),
                    null,
                    FileUtils.readFileToByteArray(iconFile.toFile()));
            MultipartFile crashScopeZip = new MockMultipartFile(csFile.toFile().getName(), csFile.toFile().getName(),
                    null,
                    FileUtils.readFileToByteArray(csFile.toFile()));
            MultipartFile traceReplayerZip = null;
            if (trFile != null) {
                traceReplayerZip = new MockMultipartFile(trFile.toFile().getName(), trFile.toFile().getName(),
                        null,
                        FileUtils.readFileToByteArray(trFile.toFile()));
            }

            NewAppController.addNewApp(sessionId, appIcon, crashScopeZip, traceReplayerZip);
            // FIXME: need assertions
        } finally {
            Paths.get(BurtConfigPaths.appLogosPath, packageNameVersion + ".png").toFile().delete();
            File csFolder = Paths.get(BurtConfigPaths.crashScopeDataPath, packageNameVersion).toFile();
            FileUtils.deleteDirectory(csFolder);
            File trFolder = Paths.get(BurtConfigPaths.traceReplayerDataPath, packageNameVersion).toFile();
            FileUtils.deleteDirectory(trFolder);
        }
    }
}
