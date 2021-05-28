package sealab.burt.qualitychecker.graph.db;

import edu.semeru.android.core.dao.AppDao;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.testing.helpers.UiAutoConnector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import sealab.burt.qualitychecker.graph.AppGraph;
import sealab.burt.qualitychecker.graph.AppGraphInfo;
import sealab.burt.qualitychecker.graph.GraphState;
import sealab.burt.qualitychecker.graph.GraphTransition;
import seers.appcore.threads.ThreadExecutor;
import seers.appcore.threads.processor.ThreadParameters;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.JavaUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainGraphGenerator {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(MainGraphGenerator.class);

    private static String outFolder = "C:\\Users\\ojcch\\Documents\\Projects\\Burt\\burt\\data\\graphs2";
    private static String screenShotsFolder = "C:/Users/ojcch/Documents/Projects/Amadeus/study-data/CS-Data/screenshots";
    private static String entityManager = DBUtils.DEFAULT_EM;

    // private static String outFolder =
    // "C:/Users/ojcch/Documents/Projects/Andriod_bug_reproduction/testing-graphs2";
    // private static String screenShotsFolder =
    // "C:/Users/ojcch/Documents/Projects/Andriod_bug_reproduction/backup/Data/screenshots";
    // private static String entityManager = MainGraphBasedAmadeus.dbEntityManager;

    public static void main(String[] args) throws Exception {

        
        // This is the old code that extracted the Graph from the database. This has been replaced with the 
        // new code below which reads things from the json file.
        //		EntityManager em = DBUtils.createEntityManager(entityManager);
        //		
        //		LOGGER.debug(entityManager);
        //
        //		AppDao daoApp = new AppDao();
        //
        //		List<App> apps = daoApp.findAll(em);
        //
        //		List<App> filteredApps = apps;
        //		if (!SYSTEMS_ALLOWED.isEmpty()) {
        //			filteredApps = apps.stream().filter(a -> SYSTEMS_ALLOWED.stream().anyMatch(pair -> pair.left.equals(a
        //					.getPackageName()) && pair.right.equals(a.getVersion())) )
        //					.collect(Collectors.toList());
        //		}
        //
        //		ThreadExecutor.executePaginated(filteredApps, AppThreadProcessor.class, new ThreadParameters(), 7);


        String uiDumpLocation = "/Users/KevinMoran/Desktop/CrashScope-Data"; //This is the location where the CrashScope data is stored (the .xmls and screenshots)
        
        //Set up a new Gson object and read it in. Currently this is set up to work for 
        // a single file
        // TODO: Set up to read in and combine multiple json files for the same app.
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("/Users/KevinMoran/Desktop/CrashScope-Data/Execution-1.json"));
        
        // De-serialize the Execution object. I currently set a manual ID for testing purposes.
        Execution exec = gson.fromJson(reader, Execution.class);
        exec.setId((long) 1);

        
        //Add IDs to steps
        List<Step> steps = exec.getSteps();
        for(int i = 0; i < exec.getSteps().size(); i++) {
            
            steps.get(i).setId((long)i);
        }
        
        exec.setSteps(steps);

        
        //This for loop reads in the list of DynGuiComponentVOs for each screen 
        for(Step currStep : exec.getSteps()) {
            
            // Get the current screen and read in the correpsonding xml file. 
            // Note that I decrement the "sequenceStep" by one since it is not zero indexed.
            Screen currScreen = new Screen();
            UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
            String xmlPath =  uiDumpLocation + File.separator + exec.getApp().getPackageName() + "-" + exec.getApp().getVersion() + "-" + exec.getExecutionNum() + "-" + exec.getExecutionType() + (currStep.getSequenceStep()-1) + ".xml";
            System.out.println(xmlPath); //For debug
            
            //Parse the xml file into a BasicTreeNode and then set up variables required by the visitNodes method.
            BasicTreeNode tree = loader.parseXml(xmlPath);
            StringBuilder builder = new StringBuilder();
            ArrayList<DynGuiComponentVO> currComps = new ArrayList<DynGuiComponentVO>();
            
            //Visit the nodes. The list of components should be returned in the "currComps" ArrayList as DynGUIComponentVOs.
            UiAutoConnector.visitNodes(currStep.getScreen().getActivity(),tree,currComps,1080,1920,true,null,true,0,builder,1);
        }
            
            
                    GraphGenerator generator = new GraphGenerator();
            
                    AppGraphInfo graphInfo = generator.generateGraph(exec);
                    AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();
                    System.out.println(graph.toString());
                    

       

        LOGGER.debug("Done");

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
                LOGGER.debug("Processing system " + appNameVersion);

                try {

                    GraphGenerator generator = new GraphGenerator();

                    AppGraphInfo graphInfo = generator.generateGraph(app);
                    AppGraph<GraphState, GraphTransition> graph = graphInfo.getGraph();

                    if (graph.vertexSet().isEmpty()) {
                        LOGGER.warn("No graph for " + appNameVersion);
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

                    String ccStr = getConnecterComponentsStr(stronglyConnectedSets);
                    File ccfile = new File(pathname + "-connected-comp.txt");
                    FileUtils.write(ccfile, ccStr);

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
                    /*LOGGER.debug("Saving image");

					mxGraph visualGraph = GraphUtils.getVisualGraph(graph, GraphLayout.CIRCLE);
					BufferedImage image = mxCellRenderer.createBufferedImage(visualGraph, null, 1, Color.WHITE, true,
							null);
					ImageIO.write(image, "PNG", new File(pathname + ".png"));*/
                } catch (Exception e) {
                    LOGGER.error("Error for: " + appNameVersion, e);
                }
            }
        }

    }

    private static String getConnecterComponentsStr(List<Set<GraphState>> stronglyConnectedSets) {

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
