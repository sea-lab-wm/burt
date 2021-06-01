package sealab.burt.qualitychecker.graph.db;

import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import sealab.burt.qualitychecker.actionparser.GraphLayout;
import sealab.burt.qualitychecker.actionparser.GraphUtils;
import sealab.burt.qualitychecker.graph.AppGraph;
import sealab.burt.qualitychecker.graph.AppGraphInfo;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import seers.appcore.threads.ThreadExecutor;
import seers.appcore.threads.processor.ThreadParameters;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.JavaUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public
@Slf4j
class MainJSONGraphGenerator {

    private static final Set<ImmutablePair<String, String>> SYSTEMS_ALLOWED = JavaUtils.getSet(
            //			new ImmutablePair<>			("com.evancharlton.mileage", "3.0.8")
            //			new ImmutablePair<>			("com.evancharlton.mileage", "3.1.1")
            //			new ImmutablePair<>("me.kuehle.carreport", "3.6.0"),
            //			new ImmutablePair<>("me.kuehle.carreport", "3.11.2")
            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.20")
            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.15")
            //			new ImmutablePair<>("nerd.tuxmobil.fahrplan.camp", "1.32.2"),
            //			new ImmutablePair<>("aarddict.android", "1.6.10")
            //			new ImmutablePair<>("aarddict.android", "1.6.10")
            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.15")
            //			new ImmutablePair<>("org.gnucash.android", "2.1.3")
            //			new ImmutablePair<>("org.gnucash.android", "2.1.1")
            //			new ImmutablePair<>("org.gnucash.android", "2.2.0")
            //			new ImmutablePair<>("org.gnucash.android", "2.0.3")
            new ImmutablePair<>("org.gnucash.android", "2.0.4")
    );

    private static String outFolder = "C:\\Users\\ojcch\\Documents\\Projects\\Burt\\burt\\data\\graphs2";

    //This is the location where the CrashScope data is stored (the .xmls and screenshots)
    private static String uiDumpLocation = "../data/CrashScope-Data-Droidweight";

    public static void main(String[] args) throws Exception {

        //Set up a new Gson object and read it in.
        // Currently this is set up to work for a single file
        // TODO: Set up to read in and combine multiple json files for the same app.
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(uiDumpLocation + "/Execution-1.json"));

        // De-serialize the Execution object. I currently set a manual ID for testing purposes.
        Execution exec = gson.fromJson(reader, Execution.class);
        exec.setId((long) 1);

        App app = exec.getApp();
        app.setId(1L);

        //----------------------------------------

        //Add IDs to steps
        List<Step> steps = exec.getSteps();
        for (int i = 0; i < exec.getSteps().size(); i++) {

            steps.get(i).setId((long) i);
        }
        exec.setSteps(steps);

        //----------------------------------------

        //This for loop reads in the list of DynGuiComponentVOs for each screen
        for (Step currStep : exec.getSteps()) {

            // Get the current screen and read in the corresponding xml file.
            // Note that I decrement the "sequenceStep" by one since it is not zero indexed.
            String xmlPath =
                    uiDumpLocation + File.separator + exec.getApp().getPackageName() + "-" +
                            exec.getApp().getVersion() + "-" + exec.getExecutionNum() + "-" +
                            exec.getExecutionType() + (currStep.getSequenceStep() - 1) + ".xml";
            System.out.println(xmlPath); //For debug

            //Parse the xml file into a BasicTreeNode and then set up variables required by the visitNodes method.
            UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
            BasicTreeNode tree = loader.parseXml(xmlPath);
            StringBuilder builder = new StringBuilder();
            ArrayList<DynGuiComponentVO> currComps = new ArrayList<>();

            //Visit the nodes. The list of components should be returned in the "currComps" ArrayList as
            // DynGUIComponentVOs.
            Screen screen = currStep.getScreen();
            UiAutoConnector.visitNodes(screen.getActivity(), tree, currComps, 1080, 1920, true, null,
                    true, 0, builder, 1);

            List<DynGuiComponent> guiComponents = convertVOstoGUIComps(currComps, screen);
            screen.setDynGuiComponents(guiComponents);

//            System.out.println(guiComponents);
        }

        //----------------------------------------


        GraphGenerator generator = new GraphGenerator();

        AppGraphInfo graphInfo = generator.generateGraph(Collections.singletonList(exec), app);
        AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();
        System.out.println(graph.toString());


        //----------------------------------------

        String sysString = File.separator + app.getId() + "-" + graphInfo.getApp().getPackageName() + "-"
                + graphInfo.getApp().getVersion();

        File sysFolder = new File(outFolder + File.separator + sysString);

        if (sysFolder.exists()) {
            FileUtils.deleteDirectory(sysFolder);
        }

        String pathname = sysFolder + File.separator + sysString;

        // ------------------------------------------------------

        File file = new File(pathname + "-graph.txt");
        String graphStr = graphInfo.graphToString();
        FileUtils.write(file, graphStr);

        // ------------------------------------------------------

        KosarajuStrongConnectivityInspector<GraphState, GraphTransition> inspector =
                new KosarajuStrongConnectivityInspector<>(
                graph);
        List<Set<GraphState>> stronglyConnectedSets = inspector.stronglyConnectedSets();

        String ccStr = getConnectedComponentsStr(stronglyConnectedSets);
        File ccfile = new File(pathname + "-connected-comp.txt");
        FileUtils.write(ccfile, ccStr, StandardCharsets.UTF_8);

        // ------------------------------------------------------

        String pathnameStates = sysFolder + File.separator + "states";
        String pathnameTransitions = sysFolder + File.separator + "transitions";

        new File(pathnameStates).mkdir();
        new File(pathnameTransitions).mkdir();

        Set<GraphTransition> edgeSet = graphInfo.getGraph().edgeSet();

        for (GraphTransition edge : edgeSet) {

            String screenshotFile = edge.getStep().getScreenshotFile();

            File srcFile = new File(uiDumpLocation + "/screenshots/" + File.separator + screenshotFile);

            if (screenshotFile != null && screenshotFile.endsWith(".png") && srcFile.exists()
                    && srcFile.isFile()) {

                File destFile = new File(pathnameTransitions + File.separator + edge.getId() + ".png");
                FileUtils.copyFile(srcFile, destFile);

                GraphState sourceState = edge.getSourceState();

                File destFile2 = new File(
                        pathnameStates + File.separator + sourceState.getUniqueHash() + ".png");
                if (!destFile2.exists()) {
                    FileUtils.copyFile(srcFile, destFile2);
                }

            }
        }

        // ------------------------------------------------------
        log.debug("Saving image");

        mxGraph visualGraph = GraphUtils.getVisualGraph(graph, GraphLayout.CIRCLE);
        BufferedImage image = mxCellRenderer.createBufferedImage(visualGraph, null, 1, Color.WHITE, true,
                null);
        ImageIO.write(image, "PNG", new File(pathname + ".png"));

        log.debug("Done");

    }

    private static List<DynGuiComponent> convertVOstoGUIComps(ArrayList<DynGuiComponentVO> currComps, Screen screen) {
        long compId = 0;
        HashMap<Long, Pair<DynGuiComponentVO, DynGuiComponent>> cache = new HashMap<>();

        for (DynGuiComponentVO voComp : currComps) {
            DynGuiComponent component = convertVOtoGUIComp(voComp);
            component.setScreen(screen);
            component.setId(compId++);

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

    private static String getConnectedComponentsStr(List<Set<GraphState>> stronglyConnectedSets) {

        StringBuilder builder = new StringBuilder();

        builder.append("# of components: " + stronglyConnectedSets.size());
        builder.append("\n");
        builder.append("\n");

        for (int i = 0; i < stronglyConnectedSets.size(); i++) {
            Set<GraphState> set = stronglyConnectedSets.get(i);

            builder.append("Component " + (i + 1));
            builder.append("\n");

            set.forEach(s -> {
                builder.append(s.getName() + ": " + s.getUnformattedXml());
                builder.append("\n");
            });

            builder.append("\n");
        }

        return builder.toString();
    }

}
