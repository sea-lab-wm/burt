import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import sealab.burt.BurtConfigPaths;
import sealab.burt.qualitychecker.JSONGraphReader;
import sealab.burt.qualitychecker.actionmatcher.GraphLayout;
import sealab.burt.qualitychecker.actionmatcher.GraphUtils;
import sealab.burt.qualitychecker.graph.*;
import seers.appcore.utils.JavaUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public
@Slf4j
class MainJSONGraphGenerator {

    private static final Set<ImmutablePair<String, String>> ALL_SYSTEMS = JavaUtils.getSet(
//            new ImmutablePair<>("gnucash", "2.1.3"),
//            new ImmutablePair<>("mileage", "3.1.1"),
//            new ImmutablePair<>("droidweight", "1.5.4"),
//            new ImmutablePair<>("GnuCash", "1.0.3"),
//            new ImmutablePair<>("AntennaPod", "1.6.2.3"),
//            new ImmutablePair<>("ATimeTracker", "0.20"),
            // new ImmutablePair<>("growtracker", "2.3.1")
            // new ImmutablePair<>("omninotes", "5.5.2")
            // new ImmutablePair<>("anglerslog", "1.3.1")
            // new ImmutablePair<>("gpstest", "3.8.0")
            // new ImmutablePair<>("fieldbook", "4.3.3")
            // new ImmutablePair<>("files", "1.0.0-beta.11")
            new ImmutablePair<>("phimpme", "1.4.0")
//            new ImmutablePair<>("androidtoken", "2.10")
    );

    private static final String outFolder = Path.of("..", "data", "graphs_json_data").toString();

    public static void main(String[] args) throws Exception {

        int nThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        //list of all futures
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        try {

                for (ImmutablePair<String, String> system : ALL_SYSTEMS) {
                    futures.add(CompletableFuture.supplyAsync(new Supplier<>() {
                        @SneakyThrows
                        @Override
                        public Boolean get() {
                            generateAndSaveGraph(system);
                            return true;
                        }
                    }, executor));
                }


            log.debug("Waiting for futures: " + futures.size());

            //wait until all futures finish, and then continue with the processing
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                    .thenAccept(ignored -> log.debug("All systems were processed")
                    ).exceptionally(exception -> {
                log.error("There was an error: " + exception.getMessage(), exception);
                return null;
            }).join();

        } finally {
            executor.shutdown();
        }

        log.debug("Reached end");

    }

    private static void generateAndSaveGraph(ImmutablePair<String, String> system) throws Exception {
        log.debug("Processing system: " + system);

        AppGraphInfo graphInfo = JSONGraphReader.getGraph(system.getLeft(), system.getRight());

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
        FileUtils.write(graphFile, graphStr, StandardCharsets.UTF_8);

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
                        Paths.get(BurtConfigPaths.traceReplayerDataPath, String.join("-", packageName,
                                app.getVersion())).toString();

            File srcFileStep = Path.of(dataLocation, "screenshots", screenshotFile).toFile();
            File srcFileState =
                    Path.of(dataLocation, "screenshots", edge.getSourceState().getScreenshotPath()).toFile();

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
