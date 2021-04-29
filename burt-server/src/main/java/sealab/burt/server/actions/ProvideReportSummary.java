package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.output.HTMLOutputGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.COLLECTING_S2R;
public class ProvideReportSummary extends ChatbotAction{
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {
        // provide the summery of bug report
        String response = "Ok, great. This is all the information we need for now. This is a summary of the problem you reported. We will redirect this information to our development team. Thank you for using BURT.";
        File outputFile = Paths.get("D:/Projects/burt/burt-server/html_template/test.html").toFile();
        new HTMLOutputGenerator().generateOutput(outputFile, state);
        return new ChatbotMessage(response);
    }
}
