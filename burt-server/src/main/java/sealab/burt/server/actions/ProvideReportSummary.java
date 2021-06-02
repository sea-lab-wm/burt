package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatBotMessage;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.output.HTMLOutputGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.REPORT_GENERATED;
import static sealab.burt.server.StateVariable.SESSION_ID;

public class ProvideReportSummary extends ChatBotAction {


    @Override
    public List<ChatBotMessage> execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {

        // provide the summary of bug report
        String response = "The link below will take to the summary of the problem you reported.";
        MessageObj messageObj = new MessageObj(response, "ReportGenerator");
        File outputFile = Paths.get("../data/generated_bug_reports", state.get(SESSION_ID) + ".html").toFile();
        new HTMLOutputGenerator().generateOutput(outputFile, state);
        state.put(REPORT_GENERATED, true);
        ChatBotMessage chatBotMessage = new ChatBotMessage(messageObj, state.get(SESSION_ID).toString() + ".html");

        return createChatBotMessages("Ok, great. This is all the information we need for now.",
                chatBotMessage,
                "We will redirect this information to our development team.",
                "Thank you for using BURT."
        );
    }
}
