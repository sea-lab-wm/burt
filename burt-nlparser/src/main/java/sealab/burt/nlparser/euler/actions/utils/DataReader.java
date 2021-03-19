package sealab.burt.nlparser.euler.actions.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.nl.ActionType;
import sealab.burt.nlparser.euler.actions.nl.BugScenario;
import sealab.burt.nlparser.euler.actions.trace.StackTrace;
import seers.appcore.csv.CSVHelper;
import sealab.burt.nlparser.euler.actions.nl.NLAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataReader.class);

    public static List<BugScenario> readScenarios(String filePath) throws IOException {
        return readScenarios(new File(filePath), true);
    }

    public static List<BugScenario> readScenarios(File file, boolean reSequence) throws IOException {

        Gson gson = new Gson();
        Function<List<String>, NLAction> function = (line) -> {
            try {
                NLAction action = new NLAction(Integer.valueOf(line.get(2)), line.get(3), line.get(5), line.get(6),
                        line.get(7), line.get(8), "not".equalsIgnoreCase(line.get(4)), ActionType.valueOf(line.get(1)));
                action.setScenarioId(0);
                action.setParsingClass(line.get(9));
                action.setOriginalSentence(line.get(10));
                action.setSentenceId(line.get(11));

                String traceJson = line.get(12);

                if (!traceJson.isEmpty()) {
                    StackTrace trace = gson.fromJson(traceJson, StackTrace.class);
                    action.setTrace(trace);
                }

                return action;
            } catch (Exception e) {
                LOGGER.error("Error for line: " + line, e);
                throw e;
            }
        };
        List<NLAction> actions = CSVHelper.readCsv(file, true, function, ';', CSVHelper.DEFAULT_CHARSET);

        // ---------------------

        Map<Integer, List<NLAction>> scenariosActions = actions.stream()
                .collect(Collectors.groupingBy(NLAction::getScenarioId));

        List<BugScenario> bugScenarios = new ArrayList<>();
        scenariosActions.forEach((id, acts) -> {
            bugScenarios.add(new BugScenario(id, new LinkedList<>(acts), reSequence));
        });

        return bugScenarios;
    }
}
