package sealab.burt.qualitychecker.graph.db;

import edu.semeru.android.core.dao.AppDao;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import sealab.burt.nlparser.euler.actions.DeviceActions;
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
public @Slf4j
class GraphGenerator {

    private HashMap<Integer, GraphState> states;
    private HashMap<String, GraphTransition> transitions;
    private boolean updateWeights = false;
    private GraphDataSource currentDataSource;

    public GraphGenerator() {
        states = new LinkedHashMap<>();
        transitions = new LinkedHashMap<>();
    }

    public static App getApp(String appName, String appVersion, EntityManager em) throws SQLException {

        // -----------------------------------------
        List<App> apps = new ArrayList<>();
        AppDao appDao = new AppDao();
        String normalizedAppName = AppNamesMappings.normalizeAppName(appName);

        if (normalizedAppName == null)
            throw new RuntimeException("Could not normalize app name: " + appName + "-" + appVersion);

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

    public AppGraphInfo generateGraph(EntityManager em, String appName, String appVersion,
                                      GraphDataSource dataSource) throws Exception {
        App app = getApp(appName, appVersion, em);
        return generateGraph(app, dataSource);
    }


    public AppGraphInfo generateGraph(List<Execution> executions, App app, GraphDataSource dataSource) throws Exception {
        return generateGraphWithNoWeights(executions, app, dataSource);
    }

    public AppGraphInfo generateGraphWithNoWeights(List<Execution> executions, App app, GraphDataSource dataSource)
            throws Exception {

        states.clear();
        transitions.clear();

        return buildGraphFromExecutions(executions, app, false, dataSource);
    }

    private AppGraphInfo buildGraphFromExecutions(List<Execution> executions, App app, boolean updateWeights,
                                                  GraphDataSource dataSource) throws Exception {
        this.updateWeights = updateWeights;
        this.currentDataSource = dataSource;

        List<AppStep> allSteps = new ArrayList<>();
        for (Execution execution : executions) {
            // if (execution.getId() == 9) {
            try {
                if (!execution.getSteps().isEmpty()) {
                    List<AppStep> steps = processExecution(execution);
                    allSteps.addAll(steps);
                    // System.out.println("=="+execution.getSteps().size()+"==");
                    // System.out.println("====================");
                }
            } catch (Exception e) {
                log.error("Error for execution " + execution.getId(), e);
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

    public AppGraphInfo generateGraph(App app, GraphDataSource dataSource) throws Exception {
        return generateGraphWithNoWeights(app.getExecutions(), app, dataSource);
    }

    private AppGraph<GraphState, GraphTransition> buildDirectedGraph() {

        AppGraph<GraphState, GraphTransition> directedGraph = new AppGraph<>(GraphTransition.class);

        getStates().forEach((k, s) -> {
            boolean added = directedGraph.addVertex(s);
            if (!added) {
                log.warn("Vertex not added: " + s);
            }
        });

        getTransitions().forEach((k, transition) -> {
            GraphState sourceState = transition.getSourceState();
            String screenshotFile = transition.getStep().getScreenshotFile();

            if ((sourceState.getScreenshotPath() == null || GraphDataSource.TR.equals(transition.getDataSource()))
                    && screenshotFile != null) {
                String nonAugmentedScreenshotPath = getNonAugmentedScreenshotPath(screenshotFile);
                sourceState.setScreenshotPath(nonAugmentedScreenshotPath);
//                if(sourceState.getUniqueHash().equals(1863964359)){
//                    log.debug(sourceState.getScreenshotPath());
//                }
            }

            boolean added = directedGraph.addEdge(sourceState, transition.getTargetState(), transition);
            if (!added) {
                log.warn("Edge not added: " + transition);
            } else {
                directedGraph.setEdgeWeight(transition, transition.getWeight());
            }
        });

        return directedGraph;
    }

    private String getNonAugmentedScreenshotPath(String screenshotFile) {

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(screenshotFile);
        List<String> allNumbers = new ArrayList<>();
        while (m.find()) {
            allNumbers.add(m.group());
        }

        if (allNumbers.isEmpty()) {
            log.debug(String.format("The screenshot file does not have numbers: %s", screenshotFile));
            return screenshotFile;
        }

        try {
            String lastNumber = allNumbers.get(allNumbers.size() - 1);
            Integer newNumber = Integer.parseInt(lastNumber) - 1;
            return screenshotFile.replace(lastNumber + "_augmented.png", newNumber + ".png");
        } catch (NumberFormatException e) {
            log.error(String.format("Could not identify the non-augmented screenshot: %s", screenshotFile), e);
            return screenshotFile;
        }
    }

    private List<AppStep> processExecution(Execution execution) throws Exception {
        return updateGraph(execution.getId(), execution.getSteps(), GraphState.END_STATE);
    }

    private List<AppStep> updateGraph(Long executionId, List<Step> steps, GraphState endState)
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

        //no steps
        if (steps.isEmpty()) {
            return appSteps;
        }

        // -------------------------------------------
        HashMap<Long, ImmutablePair<Screen, GraphState>> stepScreens = new HashMap<>();

        GraphState firstCreatedState = null;
        for (int i = 0; i < steps.size() - 1; i++) {

            Step previousStep = null;
            if (i > 0) {
                previousStep = steps.get(i - 1);
            }
            final Step step = steps.get(i);
            final Step nextStep = steps.get(i + 1);
            final Long executionId1 = executionId != null ? executionId : step.getExecution().getId();
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
                        DeviceUtils.isAnyType(stepAction) || DeviceActions.CLICK_TYPE == stepAction)
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
                if (DeviceUtils.isAnyType(stepAction) || DeviceActions.CLICK_TYPE == stepAction) {
                    tgtScreen = sourceScreen;
                    nextStep.setScreen(tgtScreen);
                } else {
                    skip = true;
                }
            }

            if (skip) {
                log.warn(String.format("Skipping step %s-%s-%s, no src and/or tgt screens, " +
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

            if (firstCreatedState == null) {
                firstCreatedState = sourceState;
            }
        }

        //----------------------------------------

        //add last step

        Step previousStep = null;
        if (steps.size() > 1) {
            previousStep = steps.get(steps.size() - 2);
        }
        Step lastStep = steps.get(steps.size() - 1);

        Integer stepAction = lastStep.getAction();
        Screen sourceScreen = lastStep.getScreen();
        final Long executionId1 = executionId != null ? executionId : lastStep.getExecution().getId();

        boolean skipLastStep = false;
        if (endState == null) {
            skipLastStep = true;
        } else if (sourceScreen == null) {
            if (!DeviceUtils.isClickBackButton(stepAction) || previousStep == null) {
                skipLastStep = true;
            } else {
                final ImmutablePair<Screen, GraphState> screenPair = stepScreens.get(previousStep.getId());
                if (screenPair != null) {
                    stepScreens.put(lastStep.getId(), screenPair);
                    sourceScreen = screenPair.left;
                    lastStep.setScreen(sourceScreen);
                } else {
                    skipLastStep = true;
                }
            }
        }

        if (skipLastStep) {
            log.warn(String.format("Skipping last step %s-%s-%s, no src and/or tgt screens, " +
                            "action: (%s) %s",
                    executionId1, lastStep.getId(), lastStep.getSequenceStep(), lastStep.getAction(),
                    GeneralUtils.getEventName(lastStep.getAction())));
        } else {
            states.putIfAbsent(endState.getUniqueHash(), endState);
            GraphState sourceState = getStepState(lastStep, stepScreens, sourceScreen);
            addGraphTransition(executionId1, appSteps, sourceState, endState, lastStep);

            if (firstCreatedState == null) {
                firstCreatedState = sourceState;
            }
        }

        // ---------------------------------------------------

        //add "open app" step

        if (firstCreatedState != null) {
            GraphState sourceState = GraphState.START_STATE;
            states.put(sourceState.getUniqueHash(), sourceState);

            Step startAppStep = new Step();
            startAppStep.setAction(DeviceActions.OPEN_APP);
            startAppStep.setSequenceStep(0);

            final long executionId2 = executionId != null ? executionId : 0L;
            addGraphTransition(executionId2, appSteps, sourceState, firstCreatedState, startAppStep);
        }

        // ---------------------------------------------------

        return appSteps;
    }

    private GraphState getStepState(Step step, HashMap<Long, ImmutablePair<Screen, GraphState>> stepScreens,
                                    Screen screen) {
        final ImmutablePair<Screen, GraphState> screenPair = stepScreens.get(step.getId());
        if (screenPair != null)
            return screenPair.right;

        final GraphState graphState = addGraphState(screen);
        stepScreens.put(step.getId(), new ImmutablePair<>(screen, graphState));
        return graphState;
    }

    private GraphState addGraphState(Screen screen) {
        List<DynGuiComponent> screenComponents = screen.getDynGuiComponents();

//        HierarchyNode node = getUniqueState2(screenComponents);

        DynGuiComponent root = findRootComponent(screenComponents);
        StringBuilder builder = getUniqueState(root);
        int hashCode = builder.toString().hashCode();

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

    private GraphTransition getGraphTransition(GraphState sourceState, GraphState targetState, AppStep appStep) {
        // ----------------------------------

        String hashTransition = getTransitionHash(sourceState, targetState, appStep);

        // System.out.print(hashTransition);
        GraphTransition transition;
        if (transitions.containsKey(hashTransition)) {

            transition = transitions.get(hashTransition);

            //update step if it comes from TR
            if (GraphDataSource.TR.equals(currentDataSource))
                transition.setStep(appStep);

            //we only increase the weight by one if updateWeights == true
            //and set the data source as TR
            if (updateWeights) {
                transition.incrementWeightByOne();
            }

            // System.out.println(" found " + transition.getUniqueHash());
        } else {

            Double weight = null;
            if (GraphDataSource.TR.equals(currentDataSource))
                weight = 2d;
            else if (GraphDataSource.CS.equals(currentDataSource))
                weight = 1d;
            else throw new RuntimeException("Not supported data source");

            transition = createNewGraphTransition(sourceState, targetState, appStep, hashTransition, weight);

            transitions.put(hashTransition, transition);
        }

        transition.setDataSource(currentDataSource);
        sourceState.setDataSource(currentDataSource);
//        targetState.setDataSource(currentDataSource);

        // System.out.println("T - " + transition.getId() + ": " +
        // transition.getName());
        return transition;
    }

    /**
     * @param sourceState
     * @param targetState
     * @param appStep
     * @param hashTransition
     * @param weight
     * @return
     */
    private GraphTransition createNewGraphTransition(GraphState sourceState, GraphState targetState,
                                                     AppStep appStep, String hashTransition, double weight) {
        GraphTransition transition = new GraphTransition();
        transition.setId(hashTransition.hashCode());
        transition.setSourceState(sourceState);
        transition.setTargetState(targetState);
        transition.setName(getTransitionName(appStep, sourceState, targetState));
        transition.setUniqueHash(hashTransition);
        transition.setStep(appStep);
        transition.setWeight(weight);

        return transition;
    }

    private List<AppStep> getAppSteps(Long executionId, Step step, AppGuiComponent component) {
        List<AppStep> appSteps = new ArrayList<>();

        final int stepAction = step.getAction();

        if (DeviceActions.CLICK_TYPE == stepAction) {
//            appSteps.add(getNewStep(executionId, previousStep, component, DeviceActions.CLICK));
            appSteps.add(getNewStep(executionId, step, component, DeviceActions.TYPE));
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
        if (component != null)
            appStep.setPhrases(component.getPhrases());
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

        if (step.getAction() == DeviceActions.TYPE) {
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

    private GraphState getGraphState(Screen screen, int pHashCode) {
        List<DynGuiComponent> components = screen.getDynGuiComponents();

        DynGuiComponent root = findRootComponent(components);
//	    HierarchyNode node = getUniqueState2(components);
        StringBuilder builder = getUniqueState(root);
        // Don't recompute the hashCode if it is -1
        int hashCode = pHashCode == -1 ? builder.toString().hashCode() : pHashCode;

        final DynGuiComponent firstComponent = root.getChildren().get(0);
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
            log.error("XML error for screen " + screen.getId() + ": " + xml);
            throw e;
        }
        return currentState;
    }

    private DynGuiComponent findRootComponent(List<DynGuiComponent> components) {
        final Optional<DynGuiComponent> compOpt = components.stream().filter(c -> c.getParent() == null && "NO_ID"
                .equals(c.getIdXml())).findFirst();
        return compOpt.orElse(null);
    }

    /**
     * Gives a unique screen based on the hierarchy
     *
     * @param root
     * @return
     */
    private StringBuilder getUniqueState(DynGuiComponent root) {

        StringBuilder builder = new StringBuilder();

        final DynGuiComponent firstComponent = root.getChildren().get(0);
        builder.append(String.format("<w>%s</w><h>%s</h>",
                firstComponent.getWidth(), firstComponent.getHeight()));
        builder.append(String.format("<x>%s</x><y>%s</y>",
                firstComponent.getPositionX(), firstComponent.getPositionY()));


        visitComponents(root, builder);

        // System.out.println(builder.toString());
        // System.out.println("----------------------------");

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
        List<DynGuiComponent> children = component.getChildren();

        children.sort(Comparator.comparingInt(DynGuiComponent::getComponentIndex));

        for (DynGuiComponent child : children) {
            visitComponents(child, builder);
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

    public AppGraphInfo updateGraphWithWeights(App app, List<Execution> executions, GraphDataSource dataSource) throws Exception {
        return buildGraphFromExecutions(executions, app, true, dataSource);
    }
}
