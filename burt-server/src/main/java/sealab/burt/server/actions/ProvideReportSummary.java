package sealab.burt.server.actions;

import sealab.burt.server.ChatbotMessage;
import sealab.burt.server.MessageObj;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideReportSummary extends ChatbotAction{
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<String, Object> state) {
        state.remove("COLLECTING_S2R");
        state.put("COLLECTED_S2R", true);
        // provide the summery of bug report
        String response = "Ok, great. This is all the information we need for now. This is a summary of the problem you reported. We will redirect this information to our development team. Thank you Yang for using BURT.";
        return new ChatbotMessage(response);
    }
}
