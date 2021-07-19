package sealab.burt.qualitychecker.graph.db;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.JSONGraphReader;
import sealab.burt.qualitychecker.actionparser.GraphLayout;
import sealab.burt.qualitychecker.actionparser.GraphUtils;
import sealab.burt.qualitychecker.graph.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public
@Slf4j
class MainJSONGraphGenerator {
//
//    private static final Set<ImmutablePair<String, String>> SYSTEMS_ALLOWED = JavaUtils.getSet(
//            //			new ImmutablePair<>			("com.evancharlton.mileage", "3.0.8")
//            //			new ImmutablePair<>			("com.evancharlton.mileage", "3.1.1")
//            //			new ImmutablePair<>("me.kuehle.carreport", "3.6.0"),
//            //			new ImmutablePair<>("me.kuehle.carreport", "3.11.2")
//            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.20")
//            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.15")
//            //			new ImmutablePair<>("nerd.tuxmobil.fahrplan.camp", "1.32.2"),
//            //			new ImmutablePair<>("aarddict.android", "1.6.10")
//            //			new ImmutablePair<>("aarddict.android", "1.6.10")
//            //			new ImmutablePair<>("com.markuspage.android.atimetracker", "0.15")
//            //			new ImmutablePair<>("org.gnucash.android", "2.1.3")
//            //			new ImmutablePair<>("org.gnucash.android", "2.1.1")
//            //			new ImmutablePair<>("org.gnucash.android", "2.2.0")
//            //			new ImmutablePair<>("org.gnucash.android", "2.0.3")
//            new ImmutablePair<>("org.gnucash.android", "2.0.4")
//    );

    private static final String outFolder = Path.of("..", "data", "graphs_json_data").toString();

    public static void main(String[] args) throws Exception {

        AppGraphInfo graphInfo = JSONGraphReader.getGraph("mileage", "3.1.1");
//        AppGraphInfo graphInfo = JSONGraphReader.getGraph("GnuCash", "2.1.3");
        AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();
        Appl app = graphInfo.getApp();

        //----------------------------------------

        String sysString = File.separator + app.getId() + "-" + app.getPackageName() + "-" + app.getVersion();

        File sysFolder = new File(outFolder + File.separator + sysString);

        if (sysFolder.exists()) {
            FileUtils.deleteDirectory(sysFolder);
        }

        String pathname = sysFolder + File.separator + sysString;

        // ------------------------------------------------------

        File graphFile = new File(pathname + "-graph.txt");
        String graphStr = graphInfo.graphToString();
        FileUtils.write(graphFile, graphStr, Charset.defaultCharset());

        // ------------------------------------------------------

        KosarajuStrongConnectivityInspector<GraphState, GraphTransition> inspector =
                new KosarajuStrongConnectivityInspector<>(graph);
        List<Set<GraphState>> stronglyConnectedSets = inspector.stronglyConnectedSets();

        String ccStr = getConnectedComponentsStr(stronglyConnectedSets);
        File ccFile = new File(pathname + "-connected-comp.txt");
        FileUtils.write(ccFile, ccStr, StandardCharsets.UTF_8);

        // ------------------------------------------------------

        String packageName = app.getPackageName();

        String pathnameStates = sysFolder + File.separator + "states";
        String pathnameTransitions = sysFolder + File.separator + "transitions";

        new File(pathnameStates).mkdir();
        new File(pathnameTransitions).mkdir();

        Set<GraphTransition> edgeSet = graph.edgeSet();

        for (GraphTransition edge : edgeSet) {

            String screenshotFile = edge.getStep().getScreenshotFile();

            if (screenshotFile == null) {
//                log.error("Step has no screenshot: " + edge.getId());
                continue;
            }

            String dataLocation =
                    Paths.get(BurtConfigPaths.crashScopeDataPath, String.join("-", packageName, app.getVersion())).toString();
            if (edge.getDataSource().equals(GraphDataSource.TR))
                dataLocation =
                        Paths.get(BurtConfigPaths.traceReplayerDataPath, String.join("-", packageName, app.getVersion())).toString();

            File srcFileStep = Path.of(dataLocation, "screenshots", screenshotFile).toFile();
            File srcFileState = Path.of(dataLocation, "screenshots", edge.getSourceState().getScreenshotPath()).toFile();

            if (screenshotFile.endsWith(".png") && srcFileStep.exists() && srcFileStep.isFile()) {
                File destFile = new File(pathnameTransitions + File.separator + edge.getId() + ".png");
                FileUtils.copyFile(srcFileStep, destFile);
            }

            if (srcFileState.exists()) {
                GraphState sourceState = edge.getSourceState();
                File destFile2 = new File(
                        pathnameStates + File.separator + sourceState.getUniqueHash() + ".png");
                if (!destFile2.exists()) {
                    FileUtils.copyFile(srcFileState, destFile2);
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
