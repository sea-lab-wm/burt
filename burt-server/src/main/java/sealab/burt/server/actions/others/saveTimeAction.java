package sealab.burt.server.actions.others;

import org.apache.commons.io.FileUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static sealab.burt.server.StateVariable.*;
public class saveTimeAction{

    public static void saveTime(ConcurrentHashMap<StateVariable, Object> state) throws IOException {

        long millis = (long) state.get(END_TIME) - (long) state.get(START_TIME);
        String time = String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );

        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        String participant = state.get(PARTICIPANT_ID).toString();
        String sessionId = state.get(SESSION_ID).toString();

        String fileName = "RecordTime";
        File timeOutputFile = new File(Paths.get(BurtConfigPaths.generatedBugReportsPath, fileName).toString());
        String join = String.join("-", participant, appName, appVersion, sessionId, time);
        if (timeOutputFile.createNewFile()){
            System.out.println("File created");
            FileWriter fw = new FileWriter(timeOutputFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(join);
            bw.newLine();
            bw.close();
        }else{
            FileWriter fw = new FileWriter(timeOutputFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(join);
            bw.newLine();
            bw.close();
        }
    }
}
