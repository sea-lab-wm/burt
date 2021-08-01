package sealab.burt.nlparser;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public
@Slf4j
class NLParser {

    public static List<NLAction> parseText(String baseFolder, String app, String text) throws Exception {
        text = preprocessText(text);
        log.debug("Parsing text: " + text);

        ParsedBugReport bugReport = new ParsedBugReport(null, text, null);
        List<BugScenario> scenarios = new HeuristicsNLActionParser(baseFolder).parseActions(app, bugReport);

        if (scenarios.isEmpty()) return new ArrayList<>();

        LinkedList<NLAction> actions = scenarios.get(0).getActions();
        log.debug("Parsed actions: " + actions);

        return actions;
    }

    private static String preprocessText(String text) {
        text = text.replace("I type ", "I enter ")
                .replace("i type", "I enter")
                .replace("i input", "I enter")
                .replace("I input", "I enter")
                .replace(" typing", " entering");
        if (text.toLowerCase().startsWith("enter"))
            text = "I " + text;
        else if (text.trim().startsWith("i ")) {
            StringBuilder builder = new StringBuilder(text.trim());
            builder.setCharAt(0, 'I');
            text = builder.toString();
        }

        return text;
    }
}
