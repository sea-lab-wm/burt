package sealab.burt.server.actions.others;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.BurtConfigPaths;
import sealab.burt.server.StateVariable;
import sealab.burt.server.actions.ChatBotAction;
import sealab.burt.server.conversation.entity.ChatBotMessage;
import sealab.burt.server.conversation.state.ConversationState;
import sealab.burt.server.conversation.entity.MessageObj;
import sealab.burt.server.conversation.entity.WidgetName;
import sealab.burt.server.output.HTMLBugReportGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static sealab.burt.server.StateVariable.*;

public @Slf4j
class GenerateBugReportAction extends ChatBotAction {

    @Override
    public List<ChatBotMessage> execute(ConversationState state) throws Exception {

        // provide the summary of bug report
        String response = "The button below will take you to the summary of the problem you reported.";
        MessageObj messageObj = new MessageObj(response, WidgetName.ReportGenerator);

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
