package sealab.burt.qualitychecker;

import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;

import java.util.ArrayList;
import java.util.List;

public class NLParser {

    public static List<NLAction> parseText(String baseFolder, String app, String text) throws Exception {
        text = preprocessText(text);
        ParsedBugReport bugReport = new ParsedBugReport(null, text, null);
        List<BugScenario> scenarios = new HeuristicsNLActionParser(baseFolder).parseActions(app, bugReport);
        if (scenarios.isEmpty()) return new ArrayList<>();
        return scenarios.get(0).getActions();
    }

    private static String preprocessText(String text) {
        text =  text.replace("I type ", "I enter ");
        if(text.toLowerCase().startsWith("enter"))
            text = "I " + text;

        return text;
    }
}
