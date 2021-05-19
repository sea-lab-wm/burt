package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatbotMessage;
import sealab.burt.server.conversation.MessageObj;
import sealab.burt.server.output.HTMLOutputGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.COLLECTING_S2R;
import static sealab.burt.server.StateVariable.SESSION_ID;

public class ProvideReportSummary extends ChatbotAction{
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) throws Exception {
        // provide the summery of bug report
        String response = "Ok, great. This is all the information we need for now. This is a summary of the problem you reported. We will redirect this information to our development team. Thank you for using BURT. ";
        //FIXME: we need to generate the report in a location accessible by the GUI and BURT should provide a link to
        // the report to the user
        MessageObj messageObj = new MessageObj(response, "ReportGenerator");
        File outputFile = Paths.get("../data/generated_bug_reports",  state.get(SESSION_ID)+".html").toFile();
        new HTMLOutputGenerator().generateOutput(outputFile, state);
        return new ChatbotMessage(messageObj, state.get(SESSION_ID).toString() + ".html");
    }
}
