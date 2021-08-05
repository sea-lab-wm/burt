package sealab.burt.server.output;

import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.state.ConversationState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static sealab.burt.server.StateVariable.*;

public class ReportingTimeRecorder {

    public static void recordTime(ConversationState state) throws IOException {

        long millis = (long) state.get(REPORTING_END_TIME) - (long) state.get(REPORTING_START_TIME);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        String time = String.format("%02d min - %02d sec", minutes, seconds);

        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        String participant = state.get(PARTICIPANT_ID).toString();
        String sessionId = state.get(SESSION_ID).toString();

        File timeOutputFile = new File(BurtConfigPaths.reportingTimeFilePath);

        String newRecord = String.join(",", MetricsRecorder.dateFormat.format(new Date()),
                participant, appName, appVersion, sessionId,
                time, String.valueOf(minutes), String.valueOf(seconds), String.valueOf(millis));

        if (!timeOutputFile.exists()) {
            timeOutputFile.createNewFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(timeOutputFile, true))) {
                String header = String.join(",", "date", "participant", "app_name", "app_version", "session_id",
                        "reporting_time", "reporting_mins", "reporting_secs", "reporting_millis");
                bw.write(header);
                bw.newLine();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(timeOutputFile, true))) {
            bw.write(newRecord);
            bw.newLine();
        }
    }

}
