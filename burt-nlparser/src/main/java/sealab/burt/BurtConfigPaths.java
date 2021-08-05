package sealab.burt;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BurtConfigPaths {

    public static final String crashScopeDataFolder = "CrashScope-Data";
    public static final String crashScopeDataPath = Path.of("..", "data", crashScopeDataFolder).toString();
    public static final String traceReplayerDataFolder = "TraceReplayer-Data";
    public static final String traceReplayerDataPath = Path.of("..", "data", traceReplayerDataFolder).toString();

    public static final String nlParsersBaseFolder = Path.of("..", "burt-nlparser").toString();
    public static final String qualityCheckerResourcesPath =
            Path.of("..", "burt-quality-checker", "src", "main", "resources").toString();

    public static final String appLogosPath = Paths.get("..", "data", "app_logos").toString();
    public static final String generatedBugReportsPath = Paths.get("..", "data", "generated_bug_reports").toString();

    public static final String reportingTimeFilePath =
            Paths.get(generatedBugReportsPath, "Reporting-Time.csv").toString();

    public static String conversationDumpsPath = Paths.get("..", "data", "conversation_dumps").toString();
    public static String metricsPath = Paths.get("..", "data", "metrics").toString();

    public static final String matchedRecordFilePath = Paths.get(metricsPath, "matched.csv").toString();
    public static final String recommendationsRecordFilePath = Paths.get(metricsPath, "recommendations.csv").toString();
}
