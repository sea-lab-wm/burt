package sealab.burt.server.actions.others;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.output.HTMLBugReportGenerator;
import sealab.burt.server.output.ReportingTimeRecorder;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class GenerateBugReportAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        // provide the summary of bug report

        File reportFile = generateBugReport(state);
        String reportName = FilenameUtils.getBaseName(reportFile.toString());
        state.put(REPORT_GENERATED, true);

        //-------------------------------------------

        long endTime = System.currentTimeMillis();
        state.put(REPORTING_END_TIME, endTime);
        ReportingTimeRecorder.recordTime(state);

        state.saveConversationMessages();

        //-------------------------------------------

        String response = "The button below will take you to the <b>bug report</b> you just issued";
        MessageObj messageObj = new MessageObj(response, WidgetName.ReportGenerator);
        ChatBotMessage chatBotMessage = new ChatBotMessage(messageObj, reportFile.getName());

        return createChatBotMessages("Okay, great!",
//                "At this point, please <b>stop the stopwatch</b> and provide the <b>elapsed time</b> in the survey",
                chatBotMessage,
                "Thank you for using BURT"
        );
    }

    public static File generateBugReport(ConversationState state) throws Exception {
        boolean anyNotCreated = Stream.of(StateVariable.APP_NAME, StateVariable.APP_VERSION,
                StateVariable.PARTICIPANT_ID).anyMatch(v -> state.get(v) == null);
        if (anyNotCreated)
            return null;

        String appName = state.get(StateVariable.APP_NAME).toString();
        String appVersion = state.get(APP_VERSION).toString();
        String participant = state.get(PARTICIPANT_ID).toString();
        String sessionId = state.get(SESSION_ID).toString();

        String reportName = String.join("-", participant, appName, appVersion, sessionId).replace(" ", "_");
        String reportFileName = reportName + ".html";

        File outputFile = Paths.get(BurtConfigPaths.generatedBugReportsPath, reportFileName).toFile();
        new HTMLBugReportGenerator().generateOutput(outputFile, state);
        return outputFile;
    }

}
