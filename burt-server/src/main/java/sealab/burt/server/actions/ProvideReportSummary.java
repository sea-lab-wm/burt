package sealab.burt.server.actions;

import sealab.burt.server.StateVariable;
import sealab.burt.server.conversation.ChatbotMessage;

import java.util.concurrent.ConcurrentHashMap;

import static sealab.burt.server.StateVariable.COLLECTING_S2R;
public class ProvideReportSummary extends ChatbotAction{
    @Override
    public ChatbotMessage execute(ConcurrentHashMap<StateVariable, Object> state) {
        // provide the summery of bug report
        String response = "Ok, great. This is all the information we need for now. This is a summary of the problem you reported. We will redirect this information to our development team. Thank you for using BURT.";
        return new ChatbotMessage(response);
    }
}
