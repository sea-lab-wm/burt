package sealab.burt.nlparser.parser;

import sealab.burt.nlparser.euler.actions.HeuristicsNLActionParser;
import sealab.burt.nlparser.euler.actions.NLActionParser;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;

import java.util.LinkedList;
import java.util.List;

public class BurtNLParser {

    public static void main(String[] args) throws Exception {
        NLActionParser parse = new HeuristicsNLActionParser();
        ParsedBugReport bugReport = new ParsedBugReport();
        bugReport.setTitle("My application is crashing");
//        ParsedBugReportDescription desc = new ParsedBugReportDescription();
//        ParsedDescriptionParagraph parag = new ParsedDescriptionParagraph();
//        ParsedDescriptionSentence sents = new ParsedDescriptionSentence();
//        sents.setValue("My application is crashing");
//        parag.setSentences(Arrays.asList(sents));
//        desc.setParagraphs(Arrays.asList(parag));
//        bugReport.setDescription(desc);
        List<BugScenario> scenarios = parse.parseActions("GnuCash", bugReport);
        LinkedList<NLAction> actions = scenarios.get(0).getActions();
        System.out.println(actions);
    }

}
