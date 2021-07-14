package sealab.burt.server.actions.others;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.output.HTMLBugReportGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class GenerateBugReportAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        // provide the summary of bug report
        String response = "The button below will take you to the summary of the problem you reported.";
        MessageObj messageObj = new MessageObj(response, "ReportGenerator");

        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        String participant = state.get(PARTICIPANT_ID).toString();
        String sessionId = state.get(SESSION_ID).toString();

        String reportName = String.join("-", participant, appName, appVersion, sessionId)
                .replace(" ", "_") + ".html";

        File outputFile = Paths.get(BurtConfigPaths.generatedBugReportsPath, reportName).toFile();
        new HTMLBugReportGenerator().generateOutput(outputFile, state);
        state.put(REPORT_GENERATED, true);
        ChatBotMessage chatBotMessage = new ChatBotMessage(messageObj, reportName);

        //-------------------------------------------

        long endTime = System.currentTimeMillis();
        state.put(END_TIME, endTime);
        TimeRecorder.recordTime(state);

        return createChatBotMessages("Okay, great. This is all the information we need for now.",
                chatBotMessage,
                "We will redirect this information to our development team.",
                "Thank you for using BURT."
        );
    }

}
