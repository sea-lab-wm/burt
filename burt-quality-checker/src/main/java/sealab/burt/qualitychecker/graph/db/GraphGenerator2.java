package sealab.burt.qualitychecker.graph.db;

import edu.semeru.android.core.dao.AppDao;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.helpers.device.DeviceHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import sealab.burt.nlparser.euler.actions.utils.AppNamesMappings;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import sealab.burt.qualitychecker.graph.*;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class GraphGenerator2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphGenerator2.class);

    private HashMap<Integer, GraphState> states;
    private HashMap<String, GraphTransition> transitions;

    public GraphGenerator2() {
        states = new LinkedHashMap<>();
        transitions = new LinkedHashMap<>();
    }

    public static App getApp(String appName, String appVersion, EntityManager em) throws SQLException {

        // -----------------------------------------
        List<App> apps = new ArrayList<>();
        AppDao appDao = new AppDao();
        String normalizedAppName = AppNamesMappings.normalizeAppName(appName);

        if(normalizedAppName==null) throw new RuntimeException("Could not normalize app name: "+ appName + "-" + appVersion);

        List<String> packages = AppNamesMappings.getPackageNames(normalizedAppName.toLowerCase());
        if (packages == null || packages.isEmpty()) {
            throw new RuntimeException("No packages found for: " + appName + "-" + appVersion);
        }

        if (appVersion != null) {
            for (String packageName : packages) {
                App aliasApps = appDao.getUniqueByPackage(packageName, appVersion, em);
                if (aliasApps != null) {
                    apps.add(aliasApps);
                }
            }
        } else {
            for (String packageName : packages) {
                List<App> aliasApps = appDao.getUniqueByPackage(packageName, em);
                apps.addAll(aliasApps);
            }
        }

        if (apps.isEmpty()) {
            throw new RuntimeException("App not found: " + appName + "-" + appVersion);
        } else if (apps.size() == 1) {
            return apps.get(0);
        } else {
            throw new RuntimeException("Multiple versions for app " + appName + "-" + appVersion);
        }

    }

    public AppGraphInfo generateGraph(EntityManager em, Long idApp) throws Exception {

        AppDao appDao = new AppDao();
        App app = appDao.getById(idApp, em);
        if (app != null) {
            return generateGraph(app);
        }

        return null;
    }

    public AppGraphInfo generateGraph(EntityManager em, String appName, String appVersion) throws Exception {
        App app = getApp(appName, appVersion, em);
        return generateGraph(app);
    }

    public AppGraphInfo generateGraph(Execution execution) throws Exception {

        states.clear();
        transitions.clear();

        App app = execution.getApp();
        
        List<AppStep> allSteps = new ArrayList<>();
        
            try {
                if (!execution.getSteps().isEmpty()) {
                    List<AppStep> steps = processExecution(execution);
                    System.out.println(steps.size());
                    allSteps.addAll(steps);
                    // System.out.println("=="+execution.getSteps().size()+"==");
                    // System.out.println("====================");
                }
            } catch (Exception e) {
                LOGGER.error("Error for execution " + execution.getId(), e);
                throw e;
            }
            // }
        

        AppGraph<GraphState, GraphTransition> graph = buildDirectedGraph();

        AppGraphInfo graphInfo = new AppGraphInfo();
        graphInfo.setGraph(graph);
        graphInfo.setSteps(allSteps);
        graphInfo.setApp(Transform.getAppl(app));

        return graphInfo;
    }
    
    public AppGraphInfo generateGraph(App app) throws Exception {

        states.clear();
        transitions.clear();

        List<AppStep> allSteps = new ArrayList<>();
        for (Execution execution : app.getExecutions()) {
            // if (execution.getId() == 9) {
            try {
                if (!execution.getSteps().isEmpty()) {
                    List<AppStep> steps = processExecution(execution);
                    allSteps.addAll(steps);
                    // System.out.println("=="+execution.getSteps().size()+"==");
                    // System.out.println("====================");
                }
            } catch (Exception e) {
                LOGGER.error("Error for execution " + execution.getId(), e);
                throw e;
            }
            // }
        }

        AppGraph<GraphState, GraphTransition> graph = buildDirectedGraph();

        AppGraphInfo graphInfo = new AppGraphInfo();
        graphInfo.setGraph(graph);
        graphInfo.setSteps(allSteps);
        graphInfo.setApp(Transform.getAppl(app));

        return graphInfo;
    }

    public AppGraph<GraphState, GraphTransition> buildDirectedGraph() {

        AppGraph<GraphState, GraphTransition> directedGraph = new AppGraph<>(GraphTransition.class);

        getStates().forEach((k, s) -> {
            boolean added = directedGraph.addVertex(s);
            if (!added) {
                LOGGER.warn("Vertex not added: " + s);
            }
        });

        getTransitions().forEach((k, t) -> {
            boolean added = directedGraph.addEdge(t.getSourceState(), t.getTargetState(), t);
            if (!added) {
                LOGGER.warn("Edge not added: " + t);
            }
        });

        return directedGraph;
    }

    private List<AppStep> processExecution(Execution execution) throws Exception {
        return updateGraph(execution.getId(), execution.getSteps(), true, GraphState.END_STATE);
    }

    public List<AppStep> updateGraph(Long executionId, List<Step> steps, boolean addOpenApp, GraphState endState)
            throws Exception {

        List<AppStep> appSteps = new ArrayList<>();

        //no steps
        if (steps == null || steps.isEmpty()) {
            return appSteps;
        }

        // -------------------------------------------
        //filter out the 'nothing' and 'open app' events

        steps = steps.stream()
                .filter(step -> !DeviceUtils.isNothing(step.getAction()) && !DeviceUtils.isOpenApp(step.getAction()))
                .collect(Collectors.toList());
        System.out.println(steps.size());

        //no steps
        if (steps.isEmpty()) {
            return appSteps;
        }

        // -------------------------------------------
        HashMap<Long, ImmutablePair<Screen, GraphState>> stepScreens = new HashMap<>();

        GraphState firstState = null;
        for (int i = 0; i < steps.size() - 1; i++) {

            Step previousStep = null;
            if (i > 0) {
                previousStep = steps.get(i - 1);
            }
            final Step step = steps.get(i);
            final Step nextStep = steps.get(i + 1);
            final Long executionId1 = executionId;
            final int stepAction = step.getAction();

            //----------------------------------------

            Screen sourceScreen = step.getScreen();
            Screen tgtScreen = nextStep.getScreen();

            //----------------------------------------
            boolean skip = false;

            //no src/tgt screens
            if (sourceScreen == null && tgtScreen == null) {
                skip = true;
            } else if (sourceScreen == null) {
                //set the same screen of the previous step to "back" steps
                if ((DeviceUtils.isClickBackButton(stepAction) ||
                        DeviceUtils.isAnyType(stepAction) || DeviceHelper.CLICK_TYPE == stepAction)
                        && previousStep != null) {
                    final ImmutablePair<Screen, GraphState> screenPair = stepScreens.get(previousStep.getId());
                    //only if the previous step has a screen
                    if (screenPair != null) {
                        stepScreens.put(step.getId(), screenPair);
                        sourceScreen = screenPair.left;
                        step.setScreen(sourceScreen);
                    } else {
                        skip = true;
                    }
                } else {
                    skip = true;
                }
            } else if (tgtScreen == null) {
                //assume that types lead to the same screen, i.e., src screen == tgt screen
                if (DeviceUtils.isAnyType(stepAction) || DeviceHelper.CLICK_TYPE == stepAction) {
                    tgtScreen = sourceScreen;
                    nextStep.setScreen(tgtScreen);
                } else {
                    skip = true;
                }
            }

            if (skip) {
                LOGGER.warn(String.format("Skipping step %s-%s-%s, no src and/or tgt screens, " +
                                "action: (%s) %s",
                        executionId1, step.getId(), step.getSequenceStep(), stepAction,
                        GeneralUtils.getEventName(stepAction)));
                continue;
            }


            //----------------------------------------
            //add states and transition

            GraphState sourceState = getStepState(step, stepScreens, sourceScreen);
            GraphState targetState = getStepState(nextStep, stepScreens, tgtScreen);

            addGraphTransition(executionId1, appSteps, sourceState, targetState, step);

            //----------------------------------------
            //save the first state

            if (firstState == null) {
                firstState = sourceState;
            }
        }

        //----------------------------------------

        //add last step

        Step previousStep = null;
        if (steps.size() > 1) {
            previousStep = steps.get(steps.size() - 2);
        }
        Step step = steps.get(steps.size() - 1);

        Integer stepAction = step.getAction();
        Screen sourceScreen = step.getScreen();
        final Long executionId1 = executionId != null ? executionId : step.getExecution().getId();

        boolean skip = false;
        if (endState == null) {
            skip = true;
        } else if (sourceScreen == null) {
            if (!DeviceUtils.isClickBackButton(stepAction) || previousStep == null) {
                skip = true;
            } else {
                final ImmutablePair<Screen, GraphState> screenPair = stepScreens.get(previousStep.getId());
                if (screenPair != null) {
                    stepScreens.put(step.getId(), screenPair);
                    sourceScreen = screenPair.left;
                    step.setScreen(sourceScreen);
                } else {
                    skip = true;
                }
            }
        }

        if (skip) {
            LOGGER.warn(String.format("Skipping last step %s-%s-%s, no src and/or tgt screens, " +
                            "action: (%s) %s",
                    executionId1, step.getId(), step.getSequenceStep(), step.getAction(),
                    GeneralUtils.getEventName(step.getAction())));
        } else {
            states.putIfAbsent(endState.getUniqueHash(), endState);
            GraphState sourceState = getStepState(step, stepScreens, sourceScreen);
            addGraphTransition(executionId1, appSteps, sourceState, endState, step);

            if (firstState == null) {
                firstState = sourceState;
            }
        }

        // ---------------------------------------------------

        //add "open app" step

        if (addOpenApp && firstState != null) {
            GraphState sourceState = GraphState.START_STATE;
            states.put(sourceState.getUniqueHash(), sourceState);

            Step startAppStep = new Step();
            startAppStep.setAction(DeviceHelper.OPEN_APP);
            startAppStep.setSequenceStep(0);

            final long executionId2 = executionId != null ? executionId : 0L;
            addGraphTransition(executionId2, appSteps, sourceState, firstState, startAppStep);
        }

        // ---------------------------------------------------

        return appSteps;
    }

    private GraphState getStepState(Step step, HashMap<Long, ImmutablePair<Screen, GraphState>> stepScreens,
                                    Screen screen) {
        final ImmutablePair<Screen, GraphState> screenPair = stepScreens.get(step.getId());
        if (screenPair != null)
            return screenPair.right;
        System.out.println(step.getId());
        final GraphState graphState = addGraphState(screen);
        stepScreens.put(step.getId(), new ImmutablePair<>(screen, graphState));
        return graphState;
    }

    public GraphState addGraphState(Screen screen) {
        List<DynGuiComponent> screenComponents = screen.getDynGuiComponents();

//        HierarchyNode node = getUniqueState2(screenComponents);
        
        DynGuiComponent root = findRootComponent(screenComponents);
        
        
        StringBuilder builder = getUniqueState(screenComponents, root);
       
        int hashCode = builder.toString().hashCode();
        System.out.println(hashCode);
        // StringBuilder builder = getUniqueState(screenComponents);
        // int hashCode = builder.toString().hashCode();

        // System.out.print(stateName);
        GraphState currentState;
        // Get state from the map
        if (states.containsKey(hashCode)) {
            // System.out.println(" found " +
            // states.get(hashCode).getName());
            currentState = states.get(hashCode);
        } else {
            // Get the GraphSate node
            currentState = getGraphState(screen, hashCode);

            // System.out.println("S - " + newState.toString() + ": " +
            // builder.toString());
            states.put(hashCode, currentState);
        }
        return currentState;
    }

    private void addGraphTransition(Long executionId, List<AppStep> appSteps, GraphState sourceState,
                                    GraphState targetState, Step step) throws Exception {

        // System.out.print("Step #" + executionId + "-" +
        // previousStep.getSequenceStep());

        // -----------------------------------

        // Set component that triggered the action
        AppGuiComponent component = Transform.getGuiComponent(step.getDynGuiComponent(), null);

        // Transform a Step to AppStep
        List<AppStep> newAppSteps = getAppSteps(executionId, step, component);
        for (AppStep appStep : newAppSteps) {

            appStep.setCurrentState(sourceState);
            // Create transition attached to any AppStep
            GraphTransition transition = getGraphTransition(sourceState, targetState, appStep);
            // Add relationship
            appStep.setTransition(transition);
            appSteps.add(appStep);

        }
    }

    /**
     * @param sourceState
     * @param targetState
     * @param appStep
     * @return
     */
    private GraphTransition getGraphTransition(GraphState sourceState, GraphState targetState, AppStep appStep) {
        // ----------------------------------

        String hashTransition = getTransitionHash(sourceState, targetState, appStep);

        // System.out.print(hashTransition);
        GraphTransition transition = null;
        if (transitions.containsKey(hashTransition)) {
            transition = transitions.get(hashTransition);
            // System.out.println(" found " + transition.getUniqueHash());
        } else {
            transition = getGraphTransition(sourceState, targetState, appStep, hashTransition);
            transitions.put(hashTransition, transition);
        }
        // System.out.println("T - " + transition.getId() + ": " +
        // transition.getName());
        return transition;
    }

    /**
     * @param sourceState
     * @param targetState
     * @param appStep
     * @param hashTransition
     * @return
     */
    private GraphTransition getGraphTransition(GraphState sourceState, GraphState targetState, AppStep
            appStep, String hashTransition) {
        GraphTransition transition;
        // Create a new transition
        transition = new GraphTransition();
        transition.setId(hashTransition.hashCode());
        transition.setSourceState(sourceState);
        transition.setTargetState(targetState);
        transition.setName(getTransitionName(appStep, sourceState, targetState));
        transition.setUniqueHash(hashTransition);
        transition.setStep(appStep);
        return transition;
    }

    public List<AppStep> getAppSteps(Step step) {
        AppGuiComponent component = Transform.getGuiComponent(step.getDynGuiComponent(), null);
        return getAppSteps(step.getExecution().getId(), step, component);

    }

    private List<AppStep> getAppSteps(Long executionId, Step step, AppGuiComponent component) {
        List<AppStep> appSteps = new ArrayList<>();

        final int stepAction = step.getAction();

        if (DeviceHelper.CLICK_TYPE == stepAction) {
//            appSteps.add(getNewStep(executionId, previousStep, component, DeviceHelper.CLICK));
            appSteps.add(getNewStep(executionId, step, component, DeviceHelper.TYPE));
        } else {
            appSteps.add(getNewStep(executionId, step, component, stepAction));
        }

        return appSteps;
    }

    private AppStep getNewStep(Long executionId, Step previousStep, AppGuiComponent component, int stepAction) {
        AppStep appStep = new AppStep();
        appStep.setAction(stepAction);
        appStep.setComponent(component);
        appStep.setExecution(executionId);
        appStep.setSequence(previousStep.getSequenceStep());
        appStep.setText(getStepText(previousStep));
        appStep.setException(previousStep.getExceptions());
        appStep.setScreenshotFile(previousStep.getScreenshot());
        appStep.setId(previousStep.getId());
        return appStep;
    }

    private String getTransitionHash(GraphState currentState, GraphState newState, AppStep appStep) {
        return currentState.getUniqueHash() + ":" + newState.getUniqueHash() + ":" + appStep.getUniqueHash();
    }

    private String getTransitionName(AppStep appStep, GraphState srcState, GraphState tgtState) {
        return String.format("(s:%s,t:%s): %s",
                srcState.getUniqueHash(),
                tgtState.getUniqueHash(),
                appStep.toString());
    }

    private String getStepText(Step step) {

        if (step.getAction() == DeviceHelper.TYPE) {
            String textEntry = step.getTextEntry();
            Pattern p = Pattern.compile("(?s).+shell input text (.+)");
            Matcher m = p.matcher(textEntry);
            if (m.matches()) {
                return m.group(1);
            }
        }
        return "";
    }

    private String getFormattedXml(String xml) throws Exception {

        // Turn xml string into a document
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // initialize StreamResult with File object to save to file
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    private String getStateName(DynGuiComponent dynGuiComponent, int hashCode) {
        String stateName;
        if (dynGuiComponent != null) {
            String[] activityTokens = dynGuiComponent.getActivity().split("\\.");
            String suffix = activityTokens[activityTokens.length - 1];

            stateName = hashCode + ", " + suffix;
        } else {
            stateName = hashCode + "";
        }
        return stateName;
    }

    public GraphState getGraphState(Screen screen, int pHashCode) {
        List<DynGuiComponent> components = screen.getDynGuiComponents();

        DynGuiComponent root = findRootComponent(components);
//	    HierarchyNode node = getUniqueState2(components);
        StringBuilder builder = getUniqueState(components, root);
        // Don't recompute the hashCode if it is -1
        int hashCode = pHashCode == -1 ? builder.toString().hashCode() : pHashCode;
        System.out.println(hashCode);
        final DynGuiComponent firstComponent = root;
        String stateName = getStateName(firstComponent, hashCode);

        // Create a new state
        GraphState currentState = new GraphState();
        currentState.setUniqueHash(hashCode);
        currentState.setName(stateName);
        currentState.setScreen(screen);

        // Transform components
        final List<AppGuiComponent> guiComponents = Transform.getGuiComponents(components);
        currentState.setComponents(guiComponents);

        String xml = builder.toString();
        try {
            currentState.setUnformattedXml(xml);
//        	currentState.setFormattedXml(getFormattedXml(xml));
        } catch (Exception e) {
            LOGGER.error("XML error for screen " + screen.getId() + ": " + xml);
            throw e;
        }
        return currentState;
    }

    private DynGuiComponent findRootComponent(List<DynGuiComponent> components) {
        final Optional<DynGuiComponent> compOpt = components.stream().filter(c -> c.getParent() == null && "NO_ID"
                .equals(c.getIdXml())).findFirst();

        if (compOpt.isPresent())
            return compOpt.get();

        return null;

    }


    /*private HierarchyNode getUniqueState2(List<DynGuiComponent> components) {

        // Find root node
        DynGuiComponent root = findRootComponent(components);
        if (root == null) return null;
        HierarchyNode node = visitComponents2(root);

        return node;
    }


    private HierarchyNode visitComponents2(DynGuiComponent comp) {


        List<HierarchyNode> childrenSet = new ArrayList<>();
        for (DynGuiComponent child : comp.getChildren()) {
            HierarchyNode childNode = visitComponents2(child);
            childrenSet.add(childNode);
        }

        HierarchyNode node = new HierarchyNode(comp, childrenSet);

        return node;

    }

    public static class HierarchyNode {

        DynGuiComponent component;
        List<HierarchyNode> children;

        public HierarchyNode(DynGuiComponent component, List<HierarchyNode> children) {
            super();
            this.component = component;
            this.children = children;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((children == null) ? 0 : children.hashCode());
            result = prime * result + ((component == null) ? 0 : component.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            HierarchyNode other = (HierarchyNode) obj;
            if (children == null) {
                if (other.children != null)
                    return false;
            } else if (!children.equals(other.children))
                return false;
            if (component == null) {
                if (other.component != null)
                    return false;
            } else if (!component.equals(other.component))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "HierarchyNode [comp=" + component + ", children=" + children + "]";
        }

    }*/

    /**
     * Gives a unique screen based on the hierarchy
     *
     * @param root
     * @return
     */
    private StringBuilder getUniqueState(List<DynGuiComponent> compList, DynGuiComponent root) {

        StringBuilder builder = new StringBuilder();
    
        
        builder.append(String.format("<w>%s</w><h>%s</h>",
                root.getWidth(), root.getHeight()));
        builder.append(String.format("<x>%s</x><y>%s</y>",
                root.getPositionX(), root.getPositionY()));
        
    for(DynGuiComponent currComp : compList) {
        
        visitComponents(currComp, builder);
    }


         System.out.println(builder.toString());
         System.out.println("----------------------------");

        return builder;
    }

    /**
     * This method generates an string with the hierarchy of the screen just
     * considering the type of the component. Most likely we will play with this
     * method to change the number of states we will generate in the final graph
     *
     * @param component
     * @param builder
     */
    private void visitComponents(DynGuiComponent component, StringBuilder builder) {
//		String elementName = getXmlElementName(component.getName());
//		String elementName = component.getName();

        String elementName = String.join("_", component.getName(), component.getIdXml().replace("id/", ""),
                //UiAutoConnector.getTextcomponent(component.getName(), component.getText()),
                component.getContentDescription()
                //String.valueOf(component.getPositionX()), String.valueOf(component
                // .getPositionY()),
                //        String.valueOf(component.getHeight())
        );

        builder.append("<" + (elementName.isEmpty() ? "root" : elementName) + ">");

        // sort
        DynGuiComponent parent = component.getParent();

//        children.sort(Comparator.comparingInt(DynGuiComponent::getComponentIndex));

//        for (DynGuiComponent child : children) {
        if(parent != null) {
            visitComponents(parent, builder);
        }
            
        builder.append("</" + (elementName.isEmpty() ? "root" : elementName) + ">");
    }

    private String getXmlElementName(String name) {
        if (name == null) {
            throw new RuntimeException("The XML element name is null");
        }
        return name.replace("android.widget.", "").replace("android.view.", "").replace("android.webkit.", "")
                .replace("android.support.", "").replace("$", ".");
    }

    /**
     * @return the states
     */
    public HashMap<Integer, GraphState> getStates() {
        return states;
    }

    /**
     * @param states the states to set
     */
    public void setStates(HashMap<Integer, GraphState> states) {
        this.states = states;
    }

    /**
     * @return the transitions
     */
    public HashMap<String, GraphTransition> getTransitions() {
        return transitions;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(HashMap<String, GraphTransition> transitions) {
        this.transitions = transitions;
    }
}
