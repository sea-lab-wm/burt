package sealab.burt.nlparser.euler.actions;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sealab.burt.nlparser.euler.actions.nl.ActionType;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.pattern.NLActionPatternParser;
import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import seers.appcore.utils.JavaUtils;
import seers.bugrepcompl.entity.codingparse.LabeledBugReport;
import seers.bugrepcompl.entity.codingparse.LabeledBugReportTitle;
import seers.bugrepcompl.entity.codingparse.LabeledDescriptionParagraph;
import seers.bugrepcompl.entity.codingparse.LabeledDescriptionSentence;
import seers.bugrepcompl.entity.regularparse.ParsedBugReport;
import seers.bugrepcompl.entity.regularparse.ParsedBugReportDescription;
import seers.bugrepcompl.entity.regularparse.ParsedDescriptionParagraph;
import seers.bugreppatterns.entity.Paragraph;
import seers.bugreppatterns.main.prediction.MainHRClassifier;
import seers.bugreppatterns.pattern.ExpectedBehaviorPatternMatcher;
import seers.bugreppatterns.pattern.PatternMatcher;
import seers.bugreppatterns.pattern.eb.*;
import seers.bugreppatterns.utils.ParsingUtils;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.entity.Sentence;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public @Slf4j
class HeuristicsNLActionParser extends NLActionParser {

    public static final String DEFAULT_PARAG_PATTERNS_FILE = "paragraph_patterns.csv";
    public static final String DEFAULT_SENTCE_PATTERNS_CSV = "sentence_patterns.csv";
    public static final String DEFAULT_PARSERS_FILE = "parsers.csv";
    private static final Set<String> NOT_ALLOWED_ACTIONS = JavaUtils.getSet("know", "think", "wish", "feel", "forgive",
            "exist", "believe", "care", "discuss", "experiment", "hope", "investigate", "like", "love", "mean", "need",
            "talk", "seem", "tell", "tempt", "thrill", "excite", "tire", "try", "understand", "want", "wonder", "woud");
    private static final Set<String> NOT_ALLOWED_OB_ACTIONS = JavaUtils.getSet("see", "view", "watch");
    private static final Set<String> NOT_ALLOWED_SR_ACTIONS = JavaUtils.getSet("say");
    private static final Set<ExpectedBehaviorPatternMatcher> EB_SENTENCE_PATTERNS = JavaUtils
            .getSet(new ExpBehaviorLiteralSentencePM(), new WouldBeSentencePM(), new WouldLikePM(), new ShouldPM());
    private static HashMap<String, NLActionPatternParser> patternParsers = new HashMap<>();
    static private List<PatternMatcher> paragraphPMs = new ArrayList<>();
    static private List<PatternMatcher> sentencePMs = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(HeuristicsNLActionParser.class);

    public HeuristicsNLActionParser() {
        loadParsers(null, null, null);
    }

    public HeuristicsNLActionParser(String baseFolder) {
        if (baseFolder == null)
            loadParsers(null, null, null);
        else {
            loadParsers(Path.of(baseFolder, DEFAULT_PARSERS_FILE).toString(),
                    Path.of(baseFolder, DEFAULT_PARAG_PATTERNS_FILE).toString(),
                    Path.of(baseFolder, DEFAULT_SENTCE_PATTERNS_CSV).toString());
        }
    }

    public HeuristicsNLActionParser(String parsersFile, String paragPatternsFile, String stncePatternsFile) {
        loadParsers(parsersFile, paragPatternsFile, stncePatternsFile);
    }

    public static HashMap<String, NLActionPatternParser> loadParsers(String parsersFile) throws Exception {

        log.debug("Loading NL parsers...");

        HashMap<String, NLActionPatternParser> parsers = new LinkedHashMap<>();

        List<String> allPatterns = FileUtils.readLines(new File(parsersFile), Charset.defaultCharset());

        // -----------------------------
        // For validation:

        HashMap<String, Integer> patternMapping = new HashMap<>();

        // -----------------------------

        for (String patternNameClassIndex : allPatterns) {

            if (patternNameClassIndex.trim().isEmpty()) {
                continue;
            }

            String[] split = patternNameClassIndex.split(";");

            String patternName = split[0];
            String className = "sealab.burt.nlparser.euler.actions.pattern." + split[1];

            // -----------------------------

            Class<?> class1 = Class.forName(className);

            NLActionPatternParser pattern = (NLActionPatternParser) class1.getConstructor(Boolean.TYPE)
                    .newInstance(false);

            String typeStr = patternName.split("_")[1];
            pattern.setActType(ActionType.valueOf(typeStr));

            // -----------------------------
            // For validation: unique IDs and classes uniquely assigned to
            // patterns

            Integer num = patternMapping.get(className);
            if (num == null) {
                patternMapping.put(className, 1);
            } else {
                patternMapping.put(className, num + 1);
            }

            parsers.put(patternName, pattern);

            // -----------------------------
        }

        // ----------------------------------------------
        // Validation

        List<Entry<String, Integer>> invalidMappings = patternMapping.entrySet().stream()
                .filter(entry -> entry.getValue() > 1).collect(Collectors.toList());

        if (!invalidMappings.isEmpty()) {
            throw new RuntimeException("The following classes have more than 1 pattern assigned: " + invalidMappings);
        }

        // ----------------------------------------------

        return parsers;

    }

    private void loadParsers(String parsersFile, String paragPatternsFile, String stncePatternsFile) {
        synchronized (patternParsers) {
            try {
                if (patternParsers.isEmpty()) {
                    if (parsersFile == null) {
                        parsersFile = DEFAULT_PARSERS_FILE;
                    }
                    patternParsers = loadParsers(parsersFile);
                }
            } catch (Exception e) {
                log.error("Error loading the parsers", e);
            }
        }

        try {

            synchronized (paragraphPMs) {
                if (paragraphPMs.isEmpty()) {
                    if (paragPatternsFile == null) {
                        paragPatternsFile = DEFAULT_PARAG_PATTERNS_FILE;
                    }
                    paragraphPMs = MainHRClassifier.loadPatterns(new File(paragPatternsFile));
                }
            }
            synchronized (sentencePMs) {
                if (sentencePMs.isEmpty()) {
                    if (stncePatternsFile == null) {
                        stncePatternsFile = DEFAULT_SENTCE_PATTERNS_CSV;
                    }
                    sentencePMs = MainHRClassifier.loadPatterns(new File(stncePatternsFile));
                }
            }
        } catch (Exception e) {
            log.error("Problem loading the patterns!", e);
        }
    }

    @Override
    public List<BugScenario> parseActions(String systemName, LabeledBugReport bugReport) throws Exception {
        //right now, implemented for perfect synthetic data

        sentencesMatched = findLabeledS2RandOBSentences(bugReport);

        List<HashMap<Sentence, List<String>>> groupedSentences = determineScenarios(sentencesMatched);

        List<BugScenario> scenarios = parseSentences(groupedSentences, bugReport.getId(), systemName);

        // --------------------------------
        // some additional processing

        //scenarios = new NLActionMerger(systemName).mergeActionsInScenarios(scenarios);
//
//		scenarios = reOrderActionsInScenarios(scenarios);
//
        scenarios = discardSomeActionsInScenarios(scenarios);

        // ----------------------------------------

        // any scenario with traces?
        // boolean anyCrashes = scenarios.stream().anyMatch(s ->
        // s.getActions().stream().anyMatch(a -> a.isCrash()));
        // if (anyCrashes) {
        //
        // // parse the first trace found
        // StackTrace trace = BugStackTraceParser.parseFirstTrace(bugReport);
        // setTraceToFirstAction(scenarios, trace);
        // }

        return scenarios;
    }

    @Override
    public List<BugScenario> parseActions(String systemName, ParsedBugReport bugReport) throws Exception {

        sentencesMatched = findS2RandOBSentences(bugReport);

        log.debug("Sentences matched to the patterns: " + sentencesMatched.size());
        log.debug("Patterns matched: " + new ArrayList<>(sentencesMatched.values()));

        List<HashMap<Sentence, List<String>>> groupedSentences = determineScenarios(sentencesMatched);

        List<BugScenario> scenarios = parseSentences(groupedSentences, bugReport.getId(), systemName);

        // --------------------------------
        // some additional processing

        scenarios = new NLActionMerger(systemName).mergeActionsInScenarios(scenarios);

        scenarios = reOrderActionsInScenarios(scenarios);

        scenarios = discardSomeActionsInScenarios(scenarios);

        // ----------------------------------------

        // any scenario with traces?
        boolean anyCrashes = scenarios.stream().anyMatch(s -> s.getActions().stream().anyMatch(NLAction::isCrash));
        if (anyCrashes) {

            // parse the first trace found
            StackTrace trace = BugStackTraceParser.parseFirstTrace(bugReport);
            setTraceToFirstAction(scenarios, trace);
        }

        return scenarios;
    }

    private List<BugScenario> discardSomeActionsInScenarios(List<BugScenario> scenarios) {
        List<BugScenario> outScenarios = new ArrayList<>();
        scenarios.forEach(s -> {
            BugScenario scn = discardSomeActions(s);
            if (scn != null) {
                outScenarios.add(scn);
            }
        });

        return outScenarios;
    }

    private BugScenario discardSomeActions(BugScenario scenario) {
        LinkedList<NLAction> finalActions = new LinkedList<>();

        LinkedList<NLAction> actions = scenario.getActions();

        for (NLAction nlAction : actions) {

            if (SentenceUtils.matchTermsByLemma(NOT_ALLOWED_ACTIONS, nlAction.getAction())) {
                continue;
            }

            // avoid certain verbs for ob actions
            if (nlAction.isOBAction()
                    && SentenceUtils.matchTermsByLemma(NOT_ALLOWED_OB_ACTIONS, nlAction.getAction())) {
                continue;
            }

            // avoid certain verbs for ob actions
            if (nlAction.isSRAction()
                    && SentenceUtils.matchTermsByLemma(NOT_ALLOWED_SR_ACTIONS, nlAction.getAction())) {
                continue;
            }

            finalActions.add(nlAction);
        }

        if (finalActions.isEmpty()) {
            return null;
        }

        return new BugScenario(scenario.getId(), finalActions, true);
    }

    public static boolean isNotWorkAction(NLAction nlAction) {
        return nlAction.isActionNegated() != null && nlAction.isActionNegated()
                && "work".equalsIgnoreCase(nlAction.getAction()) && nlAction.getPreposition() == null
                && nlAction.getObject2() == null;
    }

    private List<BugScenario> reOrderActionsInScenarios(List<BugScenario> scenarios) {
        List<BugScenario> mergedScenarios = new ArrayList<>();
        scenarios.forEach(s -> {
            BugScenario mrgScn = reOrderActions(s);
            mergedScenarios.add(mrgScn);
        });

        return mergedScenarios;
    }

    private BugScenario reOrderActions(BugScenario scenario) {

        // only reorder the actions that are crashes
        LinkedList<NLAction> actions = scenario.getActions();
        List<NLAction> crashActions = actions.stream().filter(a -> a.isCrash()).collect(Collectors.toList());
        if (crashActions.isEmpty()) {
            return scenario;
        }

        // move the crash actions to the end of the actions
        LinkedList<NLAction> reOrderedActions = new LinkedList<>(actions);
        for (NLAction nlAction : crashActions) {
            int i = reOrderedActions.indexOf(nlAction);
            reOrderedActions.add(nlAction);
            reOrderedActions.remove(i);
        }

        return new BugScenario(scenario.getId(), reOrderedActions, true);
    }

    private void setTraceToFirstAction(List<BugScenario> scenarios, StackTrace trace) {

        for (BugScenario scenario : scenarios) {
            Optional<NLAction> first = scenario.getActions().stream().filter(a -> a.isCrash()).findFirst();
            if (first.isPresent()) {
                first.get().setTrace(trace);
            }
        }

    }

    /**
     * @param groupedSentences
     * @param bugId
     * @param systemName
     * @return
     */
    private List<BugScenario> parseSentences(List<HashMap<Sentence, List<String>>> groupedSentences, String bugId,
                                             String systemName) {

        List<BugScenario> scenarios = new ArrayList<>();

        // -----------------------------

        Integer scenarioId = 1;
        for (HashMap<Sentence, List<String>> sentences : groupedSentences) {

            Set<Entry<Sentence, List<String>>> sntcEntrySet = sentences.entrySet();
            BugScenario scenario = new BugScenario(scenarioId++);

            for (Entry<Sentence, List<String>> sntc : sntcEntrySet) {

                Sentence sentence = sntc.getKey();
                List<String> patternsMatched = sntc.getValue();

                Sentence stncParsed = ParserUtils.parseSentenceWithDepsAndNoEnum(sentence);

                try {

                    // S2R
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_SIMPLE_PRESENT_SUBORDINATES");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_IMPERATIVE_SEQUENCE");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_IMPERATIVE_PAST_SEQUENCE");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_SIMPLE_PAST");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_COND_OBS");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_ACTIONS_PRESENT_PERFECT");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "P_SR_ACTIONS_INF");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "P_SR_LABELED_LIST");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "P_SR_ACTIONS_MULTI_OBS_BEHAVIOR");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_COND_POS", "S_SR_COND_OBS");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_COND", "S_SR_COND_OBS");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_AFTER", "S_SR_COND_OBS");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_ACTION_SUBJECT",
                            "S_SR_GERUND_ACTION");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_SR_ACTIONS_SEPARATOR");

                    // OB
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_ERROR_NOUN_PHRASE");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_AUX_VERB");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_VERB_ERROR");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_VERB");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_OUTPUT_VERB");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_PASSIVE_VOICE");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_ADV_ADJ");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_COND_POS");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_NEG_COND");
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_OB_COND_NEG", "S_OB_NEG_COND");

                    //EB
                    parseSentenceWithPattern(scenario, patternsMatched, stncParsed, "S_EB_SHOULD");


                } catch (Exception e) {
                    log.error("Error for sentence " + sentence.getId() + " [" + systemName + " #" + bugId + "]", e);
                }

            }

            scenarios.add(scenario);
        }

        return scenarios;
    }

    private void parseSentenceWithPattern(BugScenario scenario, List<String> patternsMatched, Sentence stncParsed,
                                          String patternMatched, String patternParser) throws Exception {
        if (patternsMatched.contains(patternMatched)) {

            NLActionPatternParser parser = patternParsers.get(patternParser);

            if (parser == null) {
                throw new RuntimeException("No parsers for " + patternMatched + ":" + patternParser);
            }

            List<NLAction> actions = parser.parseSentence(stncParsed);

            // -------------

            if (actions != null) {
                actions.stream().forEach(a -> {
                    a.setParsingClass(parser.getClass().getSimpleName());
                    a.setOriginalSentence(restoreSentenceWithQuotes(stncParsed));
                    a.setSentenceId(stncParsed.getId());
                });
                scenario.addActions(actions);
            }
        }
    }

    private String restoreSentenceWithQuotes(Sentence stncParsed) {
        // TODO: make this recursive
        String text = stncParsed.getText();

        Set<Entry<String, List<Sentence>>> quotes = stncParsed.getQuotes().entrySet();

        for (Entry<String, List<Sentence>> entry : quotes) {
            text = text.replace(entry.getKey(), entry.getValue().get(0).getText());
        }

        return text;
    }

    private void parseSentenceWithPattern(BugScenario scenario, List<String> patternsMatched, Sentence stncParsed,
                                          String patternName) throws Exception {
        parseSentenceWithPattern(scenario, patternsMatched, stncParsed, patternName, patternName);
    }

    private List<HashMap<Sentence, List<String>>> determineScenarios(HashMap<Sentence, List<String>> sentencesMatched) {
        // TODO: come up with a way to determine scenarios
        // for now, we just have one per bug report
        return Arrays.asList(sentencesMatched);
    }

    private HashMap<Sentence, List<String>> findLabeledS2RandOBSentences(LabeledBugReport bugReport) throws Exception {
        HashMap<Sentence, List<String>> selectedSentences = new LinkedHashMap<>();

        // -------------------------

        // process the title

        findLabeledTitle(bugReport, selectedSentences);

        // -----------------------

        // process the paragraph

        findLabeledParagraphs(bugReport, selectedSentences);

        // ----------

        // check the sentences
        findLabeledSentences(bugReport, selectedSentences);

        return selectedSentences;
    }

    private void findLabeledSentences(LabeledBugReport bugReport, HashMap<Sentence, List<String>> selectedSentences)
            throws Exception {

        if (sentencePMs == null) {
            return;
        }

        if (bugReport.getDescription() == null) {
            return;
        }

        List<LabeledDescriptionSentence> allSentences = bugReport.getDescription().getAllSentences();

        if (allSentences == null || allSentences.isEmpty()) {
            return;
        }

        for (LabeledDescriptionSentence labSentence : allSentences) {

            if (!labSentence.isObLabeled() && !labSentence.isSrLabeled()) {
                continue;
            }

            Sentence sentence = SentenceUtils.parseSentence(labSentence.getId(), labSentence.getValue());

            if (avoidSentence(sentence)) {
                continue;
            }

            for (PatternMatcher sentPM : sentencePMs) {
                int match = sentPM.matchSentence(sentence);
                if (match == 1) {
                    addSentence(selectedSentences, sentPM, sentence);
                }
            }
        }

    }

    private void findLabeledParagraphs(LabeledBugReport bugReport, HashMap<Sentence, List<String>> selectedSentences)
            throws Exception {

        if (paragraphPMs == null) {
            return;
        }

        if (bugReport.getDescription() == null) {
            return;
        }

        List<LabeledDescriptionParagraph> paragraphs = bugReport.getDescription().getParagraphs();

        if (paragraphs == null || paragraphs.isEmpty()) {
            return;
        }

        for (LabeledDescriptionParagraph labParagraph : paragraphs) {

            if (!labParagraph.isObLabeled() && !labParagraph.isSrLabeled()) {
                continue;
            }

            Paragraph parsedParagraph = ParsingUtils.parseParagraph(bugReport.getId(), labParagraph);

            for (PatternMatcher parPM : paragraphPMs) {

                int match = parPM.matchParagraph(parsedParagraph);
                if (match == 1) {
                    addSentences(selectedSentences, parsedParagraph.getSentences(), parPM);
                }

            }

        }
    }

    private void findLabeledTitle(LabeledBugReport bugReport, HashMap<Sentence, List<String>> selectedSentences)
            throws Exception {

        if (sentencePMs == null) {
            return;
        }

        LabeledBugReportTitle title = bugReport.getTitle();

        if (title == null) {
            return;
        }

        if (!title.isObLabeled() && !title.isSrLabeled()) {
            return;
        }

        String titleTxt = title.getValue();

        if (titleTxt == null || titleTxt.trim().isEmpty()) {
            return;
        }

        Sentence sentence = SentenceUtils.parseSentence("0", titleTxt);

        for (PatternMatcher sentPM : sentencePMs) {
            int match = sentPM.matchSentence(sentence);
            if (match == 1) {
                addSentence(selectedSentences, sentPM, sentence);
            }
        }
    }

    private HashMap<Sentence, List<String>> findS2RandOBSentences(ParsedBugReport bugReport) throws Exception {

        HashMap<Sentence, List<String>> selectedSentences = new LinkedHashMap<>();

        // -------------------------

        // process the title

        if (sentencePMs != null) {

            Sentence sentence = SentenceUtils.parseSentence("0", bugReport.getTitle());

            for (PatternMatcher sentPM : sentencePMs) {
                int match = sentPM.matchSentence(sentence);
                if (match == 1) {
                    addSentence(selectedSentences, sentPM, sentence);
                }
            }
        }

        // -----------------------

        ParsedBugReportDescription description = bugReport.getDescription();

        if (description == null || description.getParagraphs() == null || description.getParagraphs().isEmpty()) {
            return selectedSentences;
        }

        // --------------------------

        // process the description

        List<ParsedDescriptionParagraph> paragraphs = description.getParagraphs();

        ExpBehaviorLiteralMultiSentencePM ebParcLiteralPM = new ExpBehaviorLiteralMultiSentencePM();

        for (ParsedDescriptionParagraph paragraph : paragraphs) {

            // check the paragraph first

            Paragraph parsedParagraph = ParsingUtils.parseParagraph(bugReport.getId(), paragraph);

            if (paragraphPMs != null) {

                // avoid expected behavior paragraphs
                if (ebParcLiteralPM.matchParagraph(parsedParagraph) != 0) {
                    continue;
                }

                for (PatternMatcher parPM : paragraphPMs) {

                    int match = parPM.matchParagraph(parsedParagraph);
                    if (match == 1) {
                        addSentences(selectedSentences, parsedParagraph.getSentences(), parPM);
                    }

                }
            }

            // ----------

            // check the sentences

            List<Sentence> sentences = parsedParagraph.getSentences();

            if (sentences.isEmpty()) {
                continue;
            }

            if (sentencePMs != null) {

                for (Sentence sentence : sentences) {

                    // avoid expected behavior sentences
                    if (SentenceUtils.matchAnyPattern(sentence, EB_SENTENCE_PATTERNS)) {
                        continue;
                    }

                    if (avoidSentence(sentence)) {
                        continue;
                    }

                    for (PatternMatcher sentPM : sentencePMs) {
                        int match = sentPM.matchSentence(sentence);
                        if (match == 1) {
                            addSentence(selectedSentences, sentPM, sentence);
                        }
                    }
                }
            }

        }

        // -----------------------------

        return selectedSentences;
    }

    private boolean avoidSentence(Sentence sentence) {

        String text = restoreSentenceWithQuotes(sentence);

        // avoid stack trace lines
        if (text.matches("^\\w\\/\\w+\\( ?\\d+\\)\\:.+")) {
            return true;
        }
        return false;
    }

    private void addSentences(HashMap<Sentence, List<String>> selectedSentences, List<Sentence> sentences,
                              PatternMatcher parPM) {
        for (Sentence sentence : sentences) {
            addSentence(selectedSentences, parPM, sentence);
        }
    }

    private void addSentence(HashMap<Sentence, List<String>> selectedSentences, PatternMatcher parPM,
                             Sentence sentence) {
        List<String> patternsMatched = selectedSentences.get(sentence);
        if (patternsMatched == null) {
            patternsMatched = new ArrayList<>();
            selectedSentences.put(sentence, patternsMatched);
        }
        patternsMatched.add(parPM.getName());
    }

}
