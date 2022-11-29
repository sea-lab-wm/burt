package sealab.burt.qualitychecker.graph.db;

import edu.semeru.android.core.dao.AppDao;
import edu.semeru.android.core.entity.model.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import sealab.burt.qualitychecker.graph.*;
import seers.appcore.threads.ThreadExecutor;
import seers.appcore.threads.processor.ThreadParameters;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.JavaUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public
@Slf4j
class MainDBGraphGenerator {

    private static final Set<ImmutablePair<String, String>> SYSTEMS_ALLOWED = JavaUtils.getSet(
//			new ImmutablePair<>			("com.evancharlton.mileage", "3.0.8")
//			new ImmutablePair<>			("com.evancharlton.mileage", "3.1.1")
//			new ImmutablePair<>("me.kuehle.carreport", "3.6.0"),
//			new ImmutablePair<>("me.kuehle.carreport", "3.11.2")
//			new ImmutablePair<>("com.markuspage.aThere was an error processing the messagendroid.atimetracker", "0.20")
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
    private static String screenShotsFolder = "C:/Users/ojcch/Documents/Projects/Amadeus/study-data/CS-Data/screenshots";
    private static String entityManager = DBUtils.DEFAULT_EM;

    // private static String outFolder =
    // "C:/Users/ojcch/Documents/Projects/Andriod_bug_reproduction/testing-graphs2";
    // private static String screenShotsFolder =
    // "C:/Users/ojcch/Documents/Projects/Andriod_bug_reproduction/backup/Data/screenshots";
    // private static String entityManager = MainGraphBasedAmadeus.dbEntityManager;

    public static void main(String[] args) throws Exception {

        EntityManager em = DBUtils.createEntityManager(entityManager);

        log.debug(entityManager);

        AppDao daoApp = new AppDao();

        List<App> apps = daoApp.findAll(em);

        List<App> filteredApps = apps;
        if (!SYSTEMS_ALLOWED.isEmpty()) {
            filteredApps = apps.stream().filter(a -> SYSTEMS_ALLOWED.stream().anyMatch(pair -> pair.left.equals(a
                    .getPackageName()) && pair.right.equals(a.getVersion())) )
                    .collect(Collectors.toList());
        }

        ThreadExecutor.executePaginated(filteredApps, AppThreadProcessor.class, new ThreadParameters(), 7);

        log.debug("Done");

    }

    public static class AppThreadProcessor extends ThreadProcessor {

        private List<App> apps;

        public AppThreadProcessor(ThreadParameters params) {
            super(params);
            apps = params.getListParam(App.class, ThreadExecutor.ELEMENTS_PARAM);
        }

        @Override
        public void executeJob() throws Exception {

            for (App app : apps) {

//				if (app.getName() == null || app.getName().isEmpty()) {
//					continue;
//				}

                String appNameVersion = app.getPackageName() + "-" + app.getVersion();
                log.debug("Processing system " + appNameVersion);

                try {

                    GraphGenerator generator = new GraphGenerator();

                    AppGraphInfo graphInfo = generator.generateGraph(app, GraphDataSource.CS);
                    AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();

                    if (graph.vertexSet().isEmpty()) {
                        log.warn("No graph for " + appNameVersion);
                        continue;
                    }

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

                    KosarajuStrongConnectivityInspector<GraphState, GraphTransition> inspector = new KosarajuStrongConnectivityInspector<>(
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

                        File srcFile = new File(screenShotsFolder + File.separator + screenshotFile);

                        if (screenshotFile != null && screenshotFile.endsWith(".png") && srcFile.exists()
                                && srcFile.isFile()) {

                            File destFile = new File(pathnameTransitions + File.separator + edge.getUniqueHash() + ".png");
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
					/*log.debug("Saving image");

					mxGraph visualGraph = GraphUtils.getVisualGraph(graph, GraphLayout.CIRCLE);
					BufferedImage image = mxCellRenderer.createBufferedImage(visualGraph, null, 1, Color.WHITE, true,
							null);
					ImageIO.write(image, "PNG", new File(pathname + ".png"));*/
                } catch (Exception e) {
                    log.error("Error for: " + appNameVersion, e);
                }
            }
        }

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
