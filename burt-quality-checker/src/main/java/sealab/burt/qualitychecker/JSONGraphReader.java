package sealab.burt.qualitychecker;

import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import sealab.burt.BurtConfigPaths;
import sealab.burt.nlparser.euler.actions.utils.AppNamesMappings;
import sealab.burt.qualitychecker.graph.*;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.graph.db.GraphGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public @Slf4j
class JSONGraphReader {

    private static final ConcurrentHashMap<String, AppGraphInfo> graphs = new ConcurrentHashMap<>();

    public static AppGraphInfo getGraph(String appName, String appVersion) throws Exception {
        AppGraphInfo graph = graphs.get(getKey(appName, appVersion));
        if (graph == null) {
            readGraph(appName, appVersion);
            graph = graphs.get(getKey(appName, appVersion));
        }
        return graph;
    }

    public static String getKey(String app, String appVersion) {
        return MessageFormat.format("{0}-{1}", app, appVersion);
    }

    public static String getFirstPackageName(String appName) {
        String normalizedAppName = AppNamesMappings.normalizeAppName(appName);

        if (normalizedAppName == null) throw new RuntimeException("Could not normalize app name: " + appName);

        List<String> packages = AppNamesMappings.getPackageNames(normalizedAppName.toLowerCase());
        if (packages == null || packages.isEmpty()) {
            throw new RuntimeException("No packages found for: " + appName);
        }

        return packages.get(0);
    }

    public static void readGraph(String appName, String appVersion) throws Exception {

        String packageName = getFirstPackageName(appName);
        String dataLocation = Paths.get(BurtConfigPaths.crashScopeDataPath,
                String.join("-", packageName, appVersion)).toString();

        String key = getKey(appName, appVersion);
        log.debug("Reading graph from JSON files for " + key);

        List<Execution> crashScopeExecutions = readExecutions(dataLocation);
        GraphGenerator generator = new GraphGenerator();

        // check if crashScopeExecutions is empty
        if (!crashScopeExecutions.isEmpty()) {
            App app = crashScopeExecutions.get(0).getApp();
            generator.generateGraph(crashScopeExecutions, app, GraphDataSource.CS);
        }

        ///-------------------------------------------------

        //1. read execution files for TraceReplayer
        String traceReplayerFolder = BurtConfigPaths.traceReplayerDataPath;

        String traceReplayerDataLocation =
                Paths.get(traceReplayerFolder, String.join("-", packageName, appVersion)).toString();
        List<Execution> traceReplayerExecutions = readExecutions(traceReplayerDataLocation);

        //2. update the graph (update the weights, and create new GraphStates and Transitions if needed)

        AppGraphInfo finalGraph = null;
        if (!traceReplayerExecutions.isEmpty()) {
            App app = traceReplayerExecutions.get(0).getApp();
            finalGraph = generator.updateGraphWithWeights(app, traceReplayerExecutions, GraphDataSource.TR);
        }

        if (finalGraph == null || finalGraph.getGraph().vertexSet().isEmpty())
            throw new RuntimeException("The graph is empty");

        checkScreenshots(finalGraph);

        graphs.put(key, finalGraph);
    }

    private static void checkScreenshots(AppGraphInfo graphInfo) {
        AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();
        Appl app = graphInfo.getApp();
        String packageName = app.getPackageName();

        Set<GraphTransition> edgeSet = graph.edgeSet();
        for (GraphTransition edge : edgeSet) {

            String screenshotFile = edge.getStep().getScreenshotFile();

            if (screenshotFile == null) {
                if (!DeviceUtils.isOpenApp(edge.getStep().getAction()))
                    log.warn("Step has no screenshot: " + edge.getName());
                continue;
            }

            String dataLocation =
                    Paths.get(BurtConfigPaths.crashScopeDataPath, String.join("-", packageName,
                            app.getVersion())).toString();
            if (edge.getDataSource().equals(GraphDataSource.TR))
                dataLocation = Paths.get(BurtConfigPaths.traceReplayerDataPath, String.join("-", packageName,
                        app.getVersion())).toString();

            File srcFileStep = Path.of(dataLocation, "screenshots", screenshotFile).toFile();

            if (!screenshotFile.endsWith(".png") || !srcFileStep.exists() || !srcFileStep.isFile()) {
                log.warn("The screenshot file may not exist: " + srcFileStep);
            }
        }
    }

    private static List<Execution> readExecutions(String dataLocation) throws Exception {

        //------ check if the path exists---------------//
        List<Path> executionFiles = new ArrayList<>();
        File folder = new File(dataLocation);
        if (folder.exists()) {
            executionFiles = Files.find(Paths.get(dataLocation), 1,
                            (path, attr) -> path.toFile().getName().startsWith("Execution-"))
                    .collect(Collectors.toList());
        }
        //------ check if the path exists---------------//

        if (executionFiles.isEmpty())
            log.debug("There are no execution files in " + dataLocation);

        log.debug("Reading execution data from : " + executionFiles);

        //----------------------
        List<Execution> executions = new ArrayList<>();
        Long componentId = 0L;
        App app = null;

        Gson gson = new Gson();
        for (int i = 0; i < executionFiles.size(); i++) {
            Path executionFile = executionFiles.get(i);
            try {

                //Set up a new Gson object
//            JsonReader reader = new JsonReader(new FileReader(executionFile.toFile()));
                JsonReader reader = new JsonReader(new InputStreamReader(
                        new FileInputStream(executionFile.toFile()), StandardCharsets.UTF_8
                ));

                // De-serialize the Execution object.
                Execution execution = gson.fromJson(reader, Execution.class);
                execution.setId((long) i);

                app = execution.getApp();
                app.setId(1L);

                //----------------------------------------

                //Add IDs to steps
                List<Step> steps = execution.getSteps();
                for (int j = 0; j < execution.getSteps().size(); j++) {
                    steps.get(j).setId((long) j);
                }
                execution.setSteps(steps);

                //----------------------------------------

                //This for loop reads in the list of DynGuiComponentVOs for each screen
                for (Step currStep : execution.getSteps()) {

                    // Get the current screen and read in the corresponding xml file.
                    String xmlPath = Path.of(dataLocation, String.join("-",
                                    execution.getApp().getPackageName(),
                                    execution.getApp().getVersion(), String.valueOf(execution.getExecutionNum()),
                                    execution.getExecutionType() + (currStep.getSequenceStep() - 1)) + ".xml")
                            .toString();
                    try {

                        //Parse the xml file into a BasicTreeNode and then set up variables required by the visitNodes
                        // method.
                        UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
                        BasicTreeNode tree = loader.parseXml(xmlPath);
                        StringBuilder builder = new StringBuilder();
                        ArrayList<DynGuiComponentVO> currComps = new ArrayList<>();

                        //Visit the nodes. The list of components should be returned in the "currComps" ArrayList as
                        // DynGUIComponentVOs.
                        Screen screen = currStep.getScreen();
                        UiAutoConnector.visitNodes(screen.getActivity(), tree, currComps, 1080, 1920, true, null,
                                true, 0, builder, 1);

                        List<DynGuiComponent> guiComponents = convertVOstoGUIComps(currComps, screen, componentId);
                        screen.setDynGuiComponents(guiComponents);
                    } catch (Exception e) {
                        log.debug(String.format("Error parsing step: %s - %s", executionFile, xmlPath), e);
                    }

                    //            System.out.println(guiComponents);
                }
                executions.add(execution);
            } catch (Exception e) {
                log.debug("Error trying to read execution: " + executionFile.toString(), e);
            }
        }


        if (executions.isEmpty())
//            throw new RuntimeException("There is no execution data to build the graph");
            log.debug("There is no execution data to build the graph");

        return executions;

    }

    private static List<DynGuiComponent> convertVOstoGUIComps(ArrayList<DynGuiComponentVO> currComps, Screen screen,
                                                              Long componentId) {

        HashMap<Long, Pair<DynGuiComponentVO, DynGuiComponent>> cache = new HashMap<>();

        for (DynGuiComponentVO voComp : currComps) {
            DynGuiComponent component = convertVOtoGUIComp(voComp);
            component.setScreen(screen);
            component.setId(componentId++);

            voComp.setId(component.getId());
            cache.put(component.getId(), new ImmutablePair<>(voComp, component));
        }

        cache.forEach((id, pair) -> {
            DynGuiComponentVO voComp = pair.getKey();
            List<DynGuiComponentVO> voChildren = voComp.getChildren();
            List<DynGuiComponent> compChildren = voChildren.stream()
                    .map(vo -> cache.get(vo.getId()).getValue())
                    .collect(Collectors.toList());

            //set children and parent
            pair.getValue().setChildren(compChildren);
            DynGuiComponentVO parent = voComp.getParent();
            if (parent != null) pair.getValue().setParent(cache.get(parent.getId()).getValue());
        });

        return cache.values().stream()
                .map(Pair::getValue)
                .collect(Collectors.toList());
    }

    private static DynGuiComponent convertVOtoGUIComp(DynGuiComponentVO currComp) {
        DynGuiComponent component = new DynGuiComponent();

        component.setActivity(currComp.getActivity());
        component.setName(currComp.getName());
        component.setText(currComp.getText());
        component.setContentDescription(currComp.getContentDescription());
        component.setIdXml(currComp.getIdXml());
        component.setComponentIndex(currComp.getComponentIndex());
        component.setComponentTotalIndex(currComp.getComponentTotalIndex());
        component.setCurrentWindow(currComp.getCurrentWindow());
        component.setTitleWindow(currComp.getTitleWindow());
        component.setPositionX(currComp.getPositionX());
        component.setPositionY(currComp.getPositionY());
        component.setHeight(currComp.getHeight());
        component.setWidth(currComp.getWidth());
        component.setCheckable(currComp.isCheckable());
        component.setChecked(currComp.isChecked());
        component.setClickable(currComp.isClickable());
        component.setEnabled(currComp.isEnabled());
        component.setFocusable(currComp.isFocusable());
        component.setFocused(currComp.isFocused());
        component.setLongClickable(currComp.isLongClickable());
        component.setScrollable(currComp.isScrollable());
        component.setSelected(currComp.isSelected());
        component.setPassword(currComp.isPassword());
        component.setItemList(currComp.isItemList());
        component.setCalendarWindow(currComp.isCalendarWindow());
        component.setRelativeLocation(currComp.getRelativeLocation());
        component.setGuiScreenshot(currComp.getGuiScreenshot());
        component.setIdText(currComp.getIdText());
        component.setOffset(currComp.getOffset());
        component.setVisibility(currComp.getVisibility());
        component.setProperties(currComp.getProperties());
        component.setDrawTime(currComp.getDrawTime());

        return component;
    }
}
