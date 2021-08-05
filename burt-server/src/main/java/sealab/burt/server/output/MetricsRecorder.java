package sealab.burt.server.output;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.state.ConversationState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sealab.burt.server.StateVariable.*;
import static sealab.burt.server.output.MetricsRecorder.MetricsType.*;

public @Slf4j
class MetricsRecorder {

    public static final String YES = "y";
    public static final String NO = "n";

    private static final String[] matchHeaderElements = {"date", "participant", "app_name", "app_version", "session_id",
            "record_type", "max_attempts", "current_attempt", "confirmation"};
    private static final String[] recommendationHeaderElements = {"date", "participant", "app_name", "app_version",
            "session_id", "record_type", "num_options", "num_selected_options"};

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public enum MetricsType {
        OB_MATCHED, EB_NO_MATCH, S2R_MATCHED, OB_SCREENS, S2R_MISSING, S2R_PREDICT
    }

    public static void saveMatchRecord(ConversationState state, MetricsType metricsType, String result) {

        try {

            String appName = state.get(StateVariable.APP_NAME).toString();
            String appVersion = state.get(APP_VERSION).toString();
            String participant = state.get(PARTICIPANT_ID).toString();
            String sessionId = state.get(SESSION_ID).toString();

            Integer currentAttempt = null;
            Integer maxAttempts = null;
            if (OB_MATCHED.equals(metricsType)) {
                currentAttempt = state.getCurrentAttemptObMatched();
                maxAttempts = state.getMaxAttemptsObMatched();
            } else if (S2R_MATCHED.equals(metricsType)) {
                currentAttempt = state.getCurrentAttemptS2RMatched();
                maxAttempts = state.getMaxAttemptsS2RMatched();
            } else if (OB_SCREENS.equals(metricsType)) {
                currentAttempt = state.getCurrentAttemptObScreens();
                maxAttempts = state.getMaxAttemptObScreens();
            }

            String newRecord = String.join(",", dateFormat.format(new Date()),
                    participant, appName, appVersion, sessionId,
                    metricsType.toString(), String.valueOf(maxAttempts), String.valueOf(currentAttempt),
                    String.valueOf(result));

            File outputFile = new File(BurtConfigPaths.matchedRecordFilePath);
            writeRecord(outputFile, newRecord, matchHeaderElements);
        } catch (Exception e) {
            log.error(String.format("Error adding match record (%s, %s)", metricsType, result), e);
        }
    }

    public static void saveRecommendationRecord(ConversationState state, MetricsType metricsType,
                                                Integer numOptions, Integer numSelectedOptions) {

        try {

            String appName = state.get(StateVariable.APP_NAME).toString();
            String appVersion = state.get(APP_VERSION).toString();
            String participant = state.get(PARTICIPANT_ID).toString();
            String sessionId = state.get(SESSION_ID).toString();

            String newRecord = String.join(",", dateFormat.format(new Date()),
                    participant, appName, appVersion, sessionId,
                    metricsType.toString(), String.valueOf(numOptions), String.valueOf(numSelectedOptions));

            File outputFile = new File(BurtConfigPaths.recommendationsRecordFilePath);
            writeRecord(outputFile, newRecord, recommendationHeaderElements);
        } catch (Exception e) {
            log.error(String.format("Error adding recommendation record (%s, %s, %s)",
                    metricsType, numOptions, numSelectedOptions), e);
        }
    }

    private static void writeRecord(File outputFile, String newRecord, CharSequence[] headerElements) throws IOException {

        if (!outputFile.exists()) {
            File folder = outputFile.getParentFile();
            if(!folder.exists())
                FileUtils.forceMkdir(folder);
            outputFile.createNewFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true))) {
                String header = String.join(",", headerElements);
                bw.write(header);
                bw.newLine();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true))) {
            bw.write(newRecord);
            bw.newLine();
        }
    }
}
