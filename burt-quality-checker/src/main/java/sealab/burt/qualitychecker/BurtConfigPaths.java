package sealab.burt.qualitychecker;

import java.nio.file.Path;

public class BurtConfigPaths {

    private static final String crashScopeDataPath = Path.of("..", "data", "CrashScope-Data").toString();
//    private static final String crashScopeDataPath = null;


    public static String getCrashScopeDataPath() {
        return crashScopeDataPath;
    }
}
