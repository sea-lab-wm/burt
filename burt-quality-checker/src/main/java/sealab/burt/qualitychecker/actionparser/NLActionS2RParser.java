package sealab.burt.qualitychecker.actionparser;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.solver.LCSubstringSolver;
import edu.semeru.android.core.dao.DynGuiComponentDao;
import edu.semeru.android.core.dao.exception.CRUDException;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.testing.helpers.AndroidKeyEvents;
import edu.semeru.android.testing.helpers.KeyCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.Appl;
import sealab.burt.qualitychecker.graph.db.DBUtils;
import sealab.burt.qualitychecker.graph.db.DeviceUtils;
import sealab.burt.qualitychecker.graph.db.Transform;
import seers.appcore.utils.JavaUtils;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.PreprocessingOptionsParser;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public @Slf4j
class NLActionS2RParser {

    private static final int NO_SWIPE_DIRECTION = -100;
    /**
     * Inverted index of generalComponentTypeClasses, ie., [class: type]
     */
    private static final HashMap<String, ComponentType> generalComponentClasses = new HashMap<>();
    /**
     * Index of "general_component_types.txt" for component types, ie., [type: specific types]
     */
    private static final HashMap<ComponentType, Set<String>> specificComponentTypes = new HashMap<>();
    private static final List<String> allComponentTypes = new ArrayList<>();
    private static final HashMap<String, ComponentType> invIdxSpecificComponentTypes = new HashMap<>();
    /**
     * Inverted index of "general_action_groups.txt" for the actions, aka, action groups, ie., [group: actions]
     */
    private static HashMap<String, LinkedHashSet<ActionGroup>> generalActionGroups = new HashMap<>();
    /**
     * Inverted index of the actions groups per app, ie., [app: [group: actions]]
     */
    private static HashMap<String, HashMap<String, LinkedHashSet<ActionGroup>>> appActionGroups = new HashMap<>();
    /**
     * Index of "general_component_types_classes.txt" for component types and the Android component classes, ie., [type:
     * classes]
     */
    private static HashMap<ComponentType, Set<String>> generalComponentTypeClasses = new HashMap<>();
    private static HashMap<String, Set<String>> synonyms = new HashMap<>();
    private final String resourcesPath;
    private final boolean useTokenSynonyms;
    private String token;
    private PreProcessedText preProcessedObj2;

    public NLActionS2RParser(String token, String resourcesPath, boolean useTokenSynonyms) {
        this.useTokenSynonyms = useTokenSynonyms;
        this.token = token;
        this.resourcesPath = resourcesPath;
        loadGeneralActionGroups();
        loadGeneralComponentTypeClasses();
        loadSpecificComponentTypes();
        loadSynonyms();
        loadAppActionGroups();
    }

    private void loadSpecificComponentTypes() {
        synchronized (specificComponentTypes) {

            if (!specificComponentTypes.isEmpty())
                return;

            Set<String> allComponentTypesSet = new LinkedHashSet<>();

            try {

                log.debug("Loading specific component types...");

                String fileName = "general_component_types.txt";

                Path path = Paths.get(resourcesPath, fileName);

                if (!Files.exists(path))
                    throw new RuntimeException("Could not find the component types file: " + fileName);

                List<String> lines = GeneralUtils.getAllLines(path, fileName);
                for (String line : lines) {

                    String[] types2 = line.split(":");
                    String[] specificTypes = types2[1].split(",");
                    ComponentType type = ComponentType.valueOf(types2[0].trim());

                    Set<String> set = JavaUtils.getSet(specificTypes);
                    specificComponentTypes.put(type, set);
                    allComponentTypesSet.addAll(set);

                    set.forEach(t -> invIdxSpecificComponentTypes.put(t, type));

                }

                allComponentTypes.addAll(allComponentTypesSet);
                allComponentTypes.sort((c1, c2) -> Integer.compare(c2.split(" ").length, c1.split(" ").length));

            } catch (Exception e) {
                log.error("Could not load the component types", e);
            }
        }
    }

    //-------------------------------------------------------------------------------------

    /**
     * Reads "general_action_groups.txt" and generates the inverted index of action groups-synonyms
     */
    private void loadGeneralActionGroups() {

        synchronized (generalActionGroups) {

            if (!generalActionGroups.isEmpty())
                return;

            generalActionGroups = new HashMap<>();

            try {

                log.debug("Loading general action groups...");

                String fileName = "general_action_groups.txt";

                Path path = Paths.get(resourcesPath, fileName);

                if (!Files.exists(path))
                    throw new RuntimeException("Could not find the action groups file: " + fileName);

                List<String> lines = GeneralUtils.getAllLines(path, fileName);

                for (String line : lines) {

                    String[] groupWords = line.split(":");
                    String[] words = groupWords[1].split(",");
                    ActionGroup group = ActionGroup.valueOf(groupWords[0].trim());

                    for (String word : words) {
                        word = word.trim();

                        LinkedHashSet<ActionGroup> groups = generalActionGroups.computeIfAbsent(word,
                                k -> new LinkedHashSet<>());

                        groups.add(group);
                    }
                }
            } catch (Exception e) {
                log.error("Could not load the action groups", e);
            }
        }
    }

    private void loadSynonyms() {
        synchronized (synonyms) {

            if (!synonyms.isEmpty())
                return;

            synonyms = new HashMap<>();

            try {

                log.debug("Loading synonyms...");

                String fileName = "synonyms.txt";

                Path path = Paths.get(resourcesPath, fileName);

                if (!Files.exists(path))
                    throw new RuntimeException("Could not find file: " + fileName);

                List<String> lines = GeneralUtils.getAllLines(path, fileName);
                for (String line : lines) {
                    String[] words = line.split(",");
                    synonyms.put(words[0], JavaUtils.getSet(words));

                }
            } catch (Exception e) {
                log.error("Could not load the synonyms", e);
            }
        }

    }

    private void loadGeneralComponentTypeClasses() {
        synchronized (generalComponentTypeClasses) {

            if (!generalComponentTypeClasses.isEmpty())
                return;

            generalComponentTypeClasses = new HashMap<>();

            try {

                log.debug("Loading component types...");

                String fileName = "general_component_types_classes.txt";
                Path path = Paths.get(resourcesPath, fileName);

                if (!Files.exists(path))
                    throw new RuntimeException("Could not find the component types file: " + fileName);

                List<String> lines = GeneralUtils.getAllLines(path, fileName);
                for (String line : lines) {

                    String[] typeClasses = line.split(":");
                    String[] classes = typeClasses[1].split(",");
                    ComponentType type = ComponentType.valueOf(typeClasses[0].trim());

                    generalComponentTypeClasses.put(type, JavaUtils.getSet(classes));

                    Arrays.stream(classes).forEach(cl -> {
                        generalComponentClasses.put(cl, type);
                    });

                }
            } catch (Exception e) {
                log.error("Could not load the component types", e);
            }
        }
    }

    /**
     * Generates the inverted index for each app
     */
    private void loadAppActionGroups() {
        //TODO:
    }

    //-------------------------------------------------------------------------------------

    public Integer determineEvent(NLAction nlAction, Appl app, List<AppGuiComponent> currentScreen) throws
            ActionParsingException {

        String action = nlAction.getAction();

        //get the actions groups, by synonyms
        LinkedHashSet<ActionGroup> actionGroups = generalActionGroups.get(action);

        //if no general action groups, then try specific ones from the system
        if (actionGroups == null || actionGroups.isEmpty()) {
            actionGroups = getAppSpecificActionGroups(action, app);
        }

        // the action group could not be found, it means the action is generic, then use click by default
        if (actionGroups == null || actionGroups.isEmpty()) {
//            actionGroups = new LinkedHashSet<>();
//            actionGroups.add(ActionGroup.CLICK);

            int noEvent = -1;
            Entry<AppGuiComponent, Double> componentFound = null;

            PreProcessedText preprocessedAll = preprocessText(String.format("%s %s %s", action, nlAction.getObject(),
                    nlAction.getObject2()));
            try {
                componentFound = findComponent(currentScreen, preprocessedAll, JavaUtils.getSet(), false,
                        true, noEvent);
            } catch (ActionParsingException e2) {
                preprocessedAll = preprocessText(action);
                try {
                    componentFound = findComponent(currentScreen, preprocessedAll, JavaUtils.getSet(), false,
                            true, noEvent);
                } catch (ActionParsingException e3) {
                }
            }

            if (componentFound != null) {
                if (componentFound.getKey().getClickable())
                    return DeviceActions.CLICK;
                else if (componentFound.getKey().getLongClickable())
                    return DeviceActions.CLICK;
                else {
                    final AppGuiComponent parent = componentFound.getKey().getParent();
                    if (parent != null) {
                        final AppGuiComponent grandParent = parent.getParent();
                        if (parent.getClickable() || (grandParent != null && grandParent.getClickable()))
                            return DeviceActions.CLICK;
                    }
                }
            }

            throw new ActionParsingException(ParsingResult.UNKNOWN_ACTION);
        }

        //** at this point we found the action group **//

        //disambiguate the action group, i.e., determine the right the action group

        ActionGroup actionGroup = disambiguateActionGroup(actionGroups, nlAction, currentScreen);

        //could not disambiguate the action
        if (actionGroup == null) {
            throw new ActionParsingException(ParsingResult.AMBIGUOUS_ACTION, new ArrayList<>(actionGroups));
        }

        //transform the action group into an event
        Integer event = mapActionGroupToEvent(actionGroup, nlAction, app);

        //determine the event, ie. action not mapped
        if (event == null)
            throw new ActionParsingException(ParsingResult.ACTION_NOT_MAPPED, Collections.singletonList(actionGroup));

        if (DeviceUtils.isClick(event)) {

            //hack for case: "click outside the popup" --> click back button
            String object = nlAction.getObject();
            String preposition = nlAction.getPreposition();
            PreProcessedText preprocessedObject2 = preprocessText(nlAction.getObject2());
            if (StringUtils.isEmpty(object) && "outside".equalsIgnoreCase(preposition) && ComponentType.WINDOW.equals
                    (invIdxSpecificComponentTypes.get(preprocessedObject2.componentType))) {
                event = DeviceActions.BACK;
            }
        }

        return event;
    }

    public Entry<AppGuiComponent, Double> determineComponentForOb(NLAction nlAction,
                                                                  List<AppGuiComponent> currentScreen,
                                                                  Integer event,
                                                                  boolean skipFocused) throws ActionParsingException {

        Entry<AppGuiComponent, Double> componentFound;
        try {
            componentFound = findComponentToClick(nlAction, currentScreen, event, skipFocused);
            return componentFound;
        } catch (ActionParsingException e) {
            //ok
        }

        //------------------------------------------------------

        String subject = nlAction.getSubject();
        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String action = nlAction.getAction();

        Set<ComponentType> componentTypes = new HashSet<>();

        PreProcessedText preprocessedAll = preprocessText(String.format("%s %s %s %s",
                subject, action, object, object2));
        try {
            componentFound = findComponent(currentScreen, preprocessedAll,
                    componentTypes, false, true, event, skipFocused, true);
            return componentFound;
        } catch (ActionParsingException e) {
            //it's ok
        }

        //-----------------------------------------------------------

        PreProcessedText preProcessedSubject = preprocessText(subject);

        return findComponent(currentScreen, preProcessedSubject,
                componentTypes, false, true, event, skipFocused, true);
    }

    public Entry<AppGuiComponent, Double> determineComponent(NLAction nlAction, List<AppGuiComponent> currentScreen,
                                                             Integer event,
                                                             boolean skipFocused) throws ActionParsingException {

        boolean componentSkipped = false;

        Entry<AppGuiComponent, Double> componentFound = null;
        Set<ComponentType> componentTypes;
        if (DeviceUtils.isOpenApp(event) || DeviceUtils.isClickBackButton(event)
                || DeviceUtils.isChangeRotation(event)) {
            DynGuiComponent temp = new DynGuiComponent();
            temp.setId(0L);
            return getEntry(temp, 1d);
        }

        if (DeviceUtils.isClick(event)) {
            componentFound = findComponentToClick(nlAction, currentScreen, event, skipFocused);
        } else if (DeviceUtils.isAnyType(event)) {
            componentFound = findComponentToType(nlAction, currentScreen, event, skipFocused);
        } else if (DeviceUtils.isSwipe(event)) {
            //case: scroll down on 'y'
            //This is not working properly
            /*if (!StringUtils.isEmpty(object2)) {
                try {
                    componentFound = findComponent(currentScreen, preprocessedObject2, componentTypes, false,
                            true, event);
                } catch (ActionParsingException e) {
                    log.debug("Could not find component for swipe, skipping component...");
                }
            }*/
            componentSkipped = true;
        }
        /*else if (DeviceUtils.isKeyEvent(event)) {
            final AppGuiComponent focusedComponent = getFocusedComponent();
            if (focusedComponent != null)
                componentFound = getEntry(focusedComponent, 1d);
        }*/
        else if (DeviceUtils.isClickMenuButton(event)) {
            try {
                componentFound = findComponentToClick(nlAction, currentScreen, event, skipFocused);
            } catch (ActionParsingException e) {
                componentSkipped = true;
            }
        }

        if (componentFound == null && !componentSkipped) {
            throw new ActionParsingException(ParsingResult.COMPONENT_NOT_FOUND);
        }

        return componentFound;
    }

    private Entry<AppGuiComponent, Double> findComponentToType(NLAction nlAction,
                                                               List<AppGuiComponent> currentScreen,
                                                               Integer event,
                                                               boolean skipFocused) throws ActionParsingException {

        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String preposition = nlAction.getPreposition();
        Set<ComponentType> componentTypes = new HashSet<>();
        PreProcessedText preprocessedObject = preprocessText(object);
        PreProcessedText preprocessedObject2 = preprocessText(object2);


        Entry<AppGuiComponent, Double> componentFound = null;
        if (!StringUtils.isEmpty(object2)) {
            //case: type 'x' on 'y'
            if (JavaUtils.getSet("on", "in", "into", "for", "of", "as").contains(preposition)) {

                //case: type 'x' in 'one line'
            /*    if (!skipFocused && preprocessedObject2.preprocessedTokens.stream().anyMatch(token1 -> "line"
                        .equalsIgnoreCase(token1.getLemma()))) {
                    final AppGuiComponent focusedComponent = getFocusedComponent();
                    if (focusedComponent != null)
                        componentFound = getEntry(focusedComponent, 1d);
                } else {*/
                try {
                    componentFound = findComponent(currentScreen, preprocessedObject2,
                            componentTypes, false, true, event, skipFocused);
                } catch (ActionParsingException e) {

                    if(StringUtils.isEmpty(object)) throw e;

                    // Use information of object since it could have matching words
                    componentFound = findComponent(currentScreen, preprocessedObject,
                            componentTypes, false, true, event, skipFocused);
                }
//                }
                //case: set 'x' to 'y'
            } else if (JavaUtils.getSet("to", "with").contains(preposition))
                //case: set 'line' to 'x'
            /*    if (!skipFocused && preprocessedObject.preprocessedTokens.stream().anyMatch(token1 -> "line"
                        .equalsIgnoreCase
                                (token1.getLemma()))) {
                    final AppGuiComponent focusedComponent = getFocusedComponent();
                    if (focusedComponent != null)
                        componentFound = getEntry(focusedComponent, 1d);
                } else*/
                componentFound = findComponent(currentScreen, preprocessedObject,
                        componentTypes, false, true,
                        event, skipFocused);
            else
                //other cases
                componentFound = findComponent(currentScreen, preprocessedObject,
                        componentTypes, false, true,
                        event, skipFocused);
        } else if (!StringUtils.isEmpty(object)) {
            //case: type 'x'
//            final boolean isObjectLiteral = isLiteralValue(object) || getLiteralValue(object) != null;
         /*   if (!skipFocused && isObjectLiteral) {
                final AppGuiComponent focusedComponent = getFocusedComponent();
                if (focusedComponent != null)
                    componentFound = getEntry(focusedComponent, 1d);
            } else */

            //I commented the next if because we don't allow things like "I enter 23",
            //We require things like "I enter 23 gallons"
//            if (!isObjectLiteral) {
            componentFound = findComponent(currentScreen, preprocessedObject,
                    componentTypes, false, true,
                    event, skipFocused);
//            }
        }
        return componentFound;
    }

    private Entry<AppGuiComponent, Double> findComponentToClick(NLAction nlAction, List<AppGuiComponent>
            currentScreen, Integer event, boolean skipFocused)
            throws ActionParsingException {

        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String preposition = nlAction.getPreposition();
        final String action = nlAction.getAction();

        Set<ComponentType> componentTypes = new HashSet<>();

        //------------------------------------------------------

        Entry<AppGuiComponent, Double> componentFound;

        PreProcessedText preprocessedAll = preprocessText(String.format("%s %s %s", action, object, object2));
        try {
            componentFound = findComponent(currentScreen, preprocessedAll,
                    componentTypes, false, true, event, skipFocused, true);

            return componentFound;
        } catch (ActionParsingException e) {
            //it's ok
        }

        //------------------------------------------------------


        PreProcessedText preprocessedObject = preprocessText(object);
        PreProcessedText preprocessedObject2 = preprocessText(object2);
//        PreProcessedText preprocessedAction = preprocessText(action);


        /*// let's try the verb
        try {
            componentFound = findComponent(currentScreen, preprocessedAction,
                    componentTypes, false, true, event, skipFocused, matchFirst);

            return componentFound;
        } catch (ActionParsingException e) {
            //it's ok
        }*/

        //------------------------------------------------------


        if (StringUtils.isEmpty(object)) {
            //cases:
            //    click on 'xyz'
            //    go to 'xyz'
            if (("on".equalsIgnoreCase(preposition) || "to".equalsIgnoreCase(preposition)) &&
                    !StringUtils.isEmpty(object2)) {

                if ("screen".equals(preprocessedObject2.lemmatized)) {
                    //case: click on screen
                    //take the first component of the screen
                    componentFound = getEntry(currentScreen.get(1), 1d);
                } else {
                    componentFound = findComponent(currentScreen, preprocessedObject2,
                            componentTypes, false,
                            true, event, skipFocused);
                }
            } else {
//                PreProcessedText preprocessedAction = preprocessText(nlAction.getAction());
//                componentFound = findComponent(currentScreen, preprocessedAction,
//                        componentTypes, false,
//                        true, event, skipFocused);
                componentFound = findComponent(currentScreen, preprocessedAll,
                        componentTypes, false, true, event, skipFocused, false);

            }
        } else {
            if (isCheckAction(nlAction) && anyCheckeableComponent(currentScreen)) {
                //case: select 'xyz'
                componentTypes = JavaUtils.getSet(ComponentType.CHECKED_COMPONENT);
            } else if (isPickerInAction(preprocessedObject) && anyPickerComponent(currentScreen)) {
                componentTypes = JavaUtils.getSet(ComponentType.PICKER);
            }

            if ("screen".equals(preprocessedObject.lemmatized)) {
                //case: click on screen
                //take the first component of the screen
                componentFound = getEntry(currentScreen.get(1), 1d);
            } else {
                //case: click/select 'xyz'
                try {
                    componentFound = findComponent(currentScreen, preprocessedObject,
                            componentTypes, false, true, event, skipFocused);
                } catch (ActionParsingException e) {
                    try {
                        final PreProcessedText preProcessedText = preprocessText(String.format("%s %s", action, object));
                        componentFound = findComponent(currentScreen, preProcessedText,
                                componentTypes, false, true, event, skipFocused);
                    } catch (ActionParsingException e2) {
                        //FIXME: maybe in some cases, we would need to throw e2
                      throw e;
                    }
                  /*  componentFound = findComponent(currentScreen, preprocessedAll,
                            componentTypes, false, true, event, skipFocused, false);*/
                }
            }
        }
        return componentFound;
    }

    private Entry<AppGuiComponent, Double> getEntry(DynGuiComponent component, Double i) {
        AppGuiComponent guiComponent = Transform.getGuiComponent(component, null);
        return getEntry(guiComponent, i);
    }

    private Entry<AppGuiComponent, Double> getEntry(AppGuiComponent component, Double i) {
        if (component == null)
            throw new RuntimeException("Component is null");
        return new AbstractMap.SimpleEntry(component, i);
    }

    private boolean anyPickerComponent(List<AppGuiComponent> currentScreen) {
        return currentScreen.stream().anyMatch(component -> isPickerComponent(component.getType()));
    }

    private boolean isPickerInAction(PreProcessedText preprocessedObject) {
        return specificComponentTypes.get(ComponentType.PICKER).contains(preprocessedObject.componentType);
    }

    public String determineText(Appl app, Integer event, Long componentId, NLAction nlAction)
            throws ActionParsingException {
        return determineText(app, event, componentId, nlAction, true);
    }

    /**
     * Method that determines the text that goes with the component action based
     * on many different attributes
     *
     * @param skipInputType true if you don't want to call deviceServerClient
     */
    public String determineText(Appl app, Integer event, Long componentId, NLAction nlAction,
                                boolean skipInputType)
            throws ActionParsingException {
        String text = null;

        String preposition = nlAction.getPreposition();
        if (DeviceUtils.isOpenApp(event))
            text = app.getPackageName();
        else if (DeviceUtils.isKeyEvent(event)) {
            String textVal = nlAction.getObject();
            if (textVal == null)
                throw new ActionParsingException(ParsingResult.EMPTY_OBJECTS);
            String keyValue = getKeyValue(textVal);
            if (keyValue != null)
                text = keyValue;
            //FIXME: handle other cases
        } else if (DeviceUtils.isChangeRotation(event)) {
            //default rotation command
            text = "landscape";

            Set<String> portraitKeyword = JavaUtils.getSet("portrait");
            if (SentenceUtils.stringEqualsToAnyToken(portraitKeyword, nlAction.getObject())
                    || SentenceUtils.stringEqualsToAnyToken(portraitKeyword, nlAction.getObject2())
                    || SentenceUtils.stringContainsAnyToken(portraitKeyword, nlAction.getObject())
                    || SentenceUtils.stringContainsAnyToken(portraitKeyword, nlAction.getObject2())) {
                text = "portrait";
            }
        } else if (DeviceUtils.isType(event)) {
            String textVal = nlAction.getObject();

            if (textVal == null)
                throw new ActionParsingException(ParsingResult.EMPTY_OBJECTS);

            //case: set xyz to/with 5
            String object2 = nlAction
                    .getObject2();
            if (JavaUtils.getSet("to", "with").contains(preposition) && !StringUtils.isEmpty(object2))
                textVal = object2;

            //------------------------------

            //literal values
            if (isLiteralValue(textVal))
                text = textVal;
           /* else
                //char values
                if (textVal.length() == 1)
                    text = textVal;
                else {
                    //cases: "5 gallons"
                    String literalVal = getLiteralValue(textVal);
                    if (literalVal != null)
                        text = literalVal;
                    else {

                        //input generation
                        if (componentId != null && !skipInputType) {
                            int inputType = DeviceServerClient.getInputType(token, componentId);
                            //if the component accepts only strings, then just take the incoming value as the text to
                            // type
                            if (inputType == 1)
                                text = textVal;
                            else
                                text = InputHelper.generateInput(inputType, "expected");
                        }
                    }
                }*/

            if (text == null)
                throw new ActionParsingException(ParsingResult.EMPTY_TEXT);
        }

        return text;
    }

    private String getKeyValue(String textVal) {
        PreProcessedText preProcessedText = preprocessText(textVal);
        if (preProcessedText.preprocessedTokens.size() != 2) return null;
        String firstToken = preProcessedText.preprocessedTokens.get(0).getWord();
        String secondToken = preProcessedText.preprocessedTokens.get(1).getWord();
        if ("key".equalsIgnoreCase(secondToken)) {
            //FIXME: support other key event
            if ("enter".equalsIgnoreCase(firstToken)) {
                return AndroidKeyEvents.getStringKeyEvent(KeyCode.ENTER);
            }
        }
        return null;
    }

    public String getLiteralValue(String textVal) {
        PreProcessedText preProcessedText = preprocessText(textVal);
        if (preProcessedText.preprocessedTokens.size() != 2) return null;
        String firstToken = preProcessedText.preprocessedTokens.get(0).getWord();
        //cases: "5 gallons" --> return 5
        if (isLiteralValue(firstToken))
            return firstToken;
        //cases: "text xyz" --> return xyz
        if ("text".equalsIgnoreCase(firstToken))
            return preProcessedText.preprocessedTokens.get(1).getWord();
        return null;
    }

    public boolean isLiteralValue(String token) {

        if (isQuoted(token)) return true;

        return StringUtils.isNumeric(token) || (token != null && TextProcessor.isNumber(token));
    }

    private boolean isQuoted(String token) {
        if (token == null)
            return false;

        return token.matches("[\"\'].+[\"\']");
    }

    private Entry<AppGuiComponent, Double> findComponent(Screen currentScreen,
                                                         PreProcessedText textToMatch,
                                                         Set<ComponentType> componentTypes,
                                                         boolean skipTextViews,
                                                         boolean checkSynonyms,
                                                         int event) throws ActionParsingException {
        List<AppGuiComponent> guiComponents = Transform.getGuiComponents(currentScreen.getDynGuiComponents());
        return findComponent(guiComponents, textToMatch, componentTypes, skipTextViews,
                checkSynonyms, event, false);
    }

    public Entry<AppGuiComponent, Double> findComponent(List<AppGuiComponent> currentScreen,
                                                        PreProcessedText textToMatch,
                                                        Set<ComponentType> componentTypes,
                                                        boolean skipTextViews,
                                                        boolean checkSynonyms,
                                                        int event) throws ActionParsingException {
        return findComponent(currentScreen, textToMatch, componentTypes, skipTextViews,
                checkSynonyms, event, false);
    }

    //---------------------------------------------------------------

    private Entry<AppGuiComponent, Double> findComponent(List<AppGuiComponent> currentScreenComponents,
                                                         PreProcessedText textToMatch,
                                                         Set<ComponentType> componentTypes,
                                                         boolean skipTextViews, boolean checkSynonyms,
                                                         int event, boolean skipFocused)
            throws ActionParsingException {
        return findComponent(currentScreenComponents, textToMatch, componentTypes, skipTextViews,
                checkSynonyms, event, skipFocused, false);
    }

    private Entry<AppGuiComponent, Double> findComponent(List<AppGuiComponent> currentScreenComponents,
                                                         PreProcessedText textToMatch,
                                                         Set<ComponentType> componentTypes,
                                                         boolean skipTextViews, boolean checkSynonyms,
                                                         int event, boolean skipFocused, boolean matchFirst)
            throws ActionParsingException {

        //-------------------------------------


        //case: "field" or "button"
        if (StringUtils.isEmpty(textToMatch.otherText) && !skipFocused &&
                !"menu".equalsIgnoreCase(textToMatch.lemmatized)) {

            //is there only one component with the corresponding type?
            if (textToMatch.componentType != null) {
                final List<AppGuiComponent> comps = currentScreenComponents.stream()
                        .filter(c -> c.getType().toLowerCase().endsWith("." + textToMatch.componentType))
                        .collect(Collectors.toList());
                if (comps.size() == 1)
                    return getEntry(comps.get(0), 1d);
            }

            //not a text field? then which component should I find? -> throw exception
            if (!ComponentType.TEXT_FIELD.equals(invIdxSpecificComponentTypes.get(textToMatch.componentType)))
                throw new ActionParsingException(ParsingResult.COMPONENT_NOT_SPECIFIED,
                        Collections.singletonList(textToMatch.original));

/*
            //get current focused component and check if it is a text field
            AppGuiComponent focusedComponent = getFocusedComponent();
            if (focusedComponent != null && isTextField(focusedComponent.getType())) {
                return getEntry(focusedComponent, 1d);
            }*/

        }


        //---------------------------------------------------------

        List<String> allowedComponents = componentTypes.stream().flatMap(type -> generalComponentTypeClasses.get(type)
                .stream()).collect(Collectors.toList());
        List<Entry<AppGuiComponent, Double>> matchedComponents = getMatchedComponents(skipTextViews, textToMatch,
                currentScreenComponents, allowedComponents, matchFirst);

        log.debug("Component matches [" + matchedComponents.size() + "]: " + matchedComponents.toString());

        //---------------------------------------------------------

        Entry<AppGuiComponent, Double> component;
        if (matchedComponents.size() == 1) {
            AppGuiComponent temp = handleOneMatch(skipTextViews, allowedComponents, matchedComponents.get(0), event,
                    currentScreenComponents);
            component = getEntry(temp, 1d);
        } else if (matchedComponents.size() > 1) {
            component = handleMultipleMatches(skipTextViews, textToMatch, allowedComponents, matchedComponents,
                    event, matchFirst, currentScreenComponents);
        } else {
            component = handleNoMatches(currentScreenComponents, componentTypes, skipTextViews, checkSynonyms,
                    textToMatch, event, matchFirst);
        }
        return component;
    }

    private boolean isButton(String componentName) {
        return ComponentType.BUTTON.equals(getComponentType(componentName));
    }

    public boolean isCheckedComponent(String componentName) {
        return ComponentType.CHECKED_COMPONENT.equals(getComponentType(componentName));
    }

    private boolean isPickerComponent(String componentName) {
        return ComponentType.PICKER.equals(getComponentType(componentName));
    }

    private boolean isTextField(String componentName) {
        return ComponentType.TEXT_FIELD.equals(getComponentType(componentName));
    }

    private PreProcessedText preprocessText(String rawText) {

        PreProcessedText preprocessedText = new PreProcessedText();

        if (rawText == null)
            return preprocessedText;

        //filter out "null" tokens
        rawText = rawText.replace("null", "").trim();

        String unquotedText = rawText;
        if (isQuoted(rawText))
            unquotedText = rawText.substring(1, rawText.length() - 1);

        //--------------------------------

        List<Token> tokens = tokenize(unquotedText.toLowerCase(), new String[]{});

        Object[] lemmas = tokens.stream().map(Token::getLemma).toArray();
        String lemmatizedText = StringUtils.join(lemmas, ' ');

        //--------------------------------

        String[] preprocessingOptions = {PreprocessingOptionsParser.PUNCTUATION_REMOVAL, PreprocessingOptionsParser
                .CAMEL_CASE_SPLITTING, PreprocessingOptionsParser.SPECIAL_CHARS_REMOVAL};
        List<Token> preprocessedTokens = tokenize(unquotedText, preprocessingOptions);

        //--------------------------------

        String componentType = null;
        String otherText = null;
        for (String type : allComponentTypes) {
            otherText = lemmatizedText.replace(type, "");
            if (!lemmatizedText.equals(otherText)) {
                componentType = type;
                break;
            }
        }

        //--------------------------------

        //remove any component type from the tokens
        preprocessedTokens = preprocessedTokens.stream().filter(t -> !allComponentTypes.contains(t.getLemma())).collect
                (Collectors.toList());

        //--------------------------------

        preprocessedText.original = rawText;
        preprocessedText.lowerCased = rawText.toLowerCase();
        preprocessedText.allTokens = tokens;
        preprocessedText.lemmatized = lemmatizedText;
        preprocessedText.preprocessedTokens = preprocessedTokens;
        preprocessedText.preprocessed = StringUtils.join(preprocessedTokens.stream()
                .map(Token::getLemma)
                .map(String::toLowerCase)
                .toArray(), ' ');
        preprocessedText.otherText = otherText;
        preprocessedText.componentType = componentType;

        return preprocessedText;

    }

    private List<Entry<AppGuiComponent, Double>> getMatchedComponents(boolean skipTextViews,
                                                                      PreProcessedText textToMatch,
                                                                      List<AppGuiComponent> guiComponents,
                                                                      List<String> allowedComponents,
                                                                      boolean matchFirst) {
        String lemmatizedText = textToMatch.lemmatized;

        //---------------------------------------------------------
        //check 'exact' textual matching

        List<Entry<AppGuiComponent, Double>> matchedComponents = guiComponents.stream().filter(component -> {
            if (!isAllowedComponent(skipTextViews, allowedComponents, component)) return false;

            PreProcessedText preProcessedComponent = preprocessText(component.getText());
            String lemmatizedCompText = preProcessedComponent.lemmatized;
            if (lemmatizedText.equalsIgnoreCase(lemmatizedCompText)) return true;

            PreProcessedText preProcessedCompDesc = preprocessText(component.getContentDescription());
            String lemmatizedCompDesc = preProcessedCompDesc.lemmatized;
            if (lemmatizedText.equalsIgnoreCase(lemmatizedCompDesc)) return true;

            String idXml = component.getIdXml();
            if (!StringUtils.isEmpty(idXml) && idXml.contains("/")) {
                String compId = idXml.substring(idXml.indexOf("/") + 1, idXml.length());
                PreProcessedText preProcessedCompId = preprocessText(compId);
                String lemmatizedCompId = preProcessedCompId.lemmatized;
                return lemmatizedText.equalsIgnoreCase(lemmatizedCompId);
            }

            return false;
        }).map(c -> getEntry(c, 1d)).collect(Collectors.toList());

        //---------------------------------------------------------------

        if (matchedComponents.isEmpty()) {

            List<Entry<AppGuiComponent, Double>> nonZeroMatches = getMatchedComponentByLCS(skipTextViews,
                    textToMatch, guiComponents, allowedComponents, matchFirst);

            //filter the components
            List<Entry<AppGuiComponent, Double>> matchList = nonZeroMatches.stream()
                    .filter(entry -> entry.getValue() >= 0.5).collect(Collectors.toList());

            log.debug("LCS matches:" + matchList);

            matchedComponents = matchList.stream().filter(m -> m.getValue() >= 1).collect
                    (Collectors.toList());
            if (matchedComponents.isEmpty())
                matchedComponents = matchList.stream().filter(m -> m.getValue() >= 0.7).collect
                        (Collectors.toList());
            if (matchedComponents.isEmpty())
                matchedComponents = matchList.stream().filter(m -> m.getValue() >= 0.6).collect
                        (Collectors.toList());
            if (matchedComponents.isEmpty())
                matchedComponents = matchList;

        }

        //filter out TabWidgets
        matchedComponents = matchedComponents.stream()
                .filter(m -> !m.getKey().getType().endsWith(".TabWidget"))
                .collect(Collectors.toList());

        return matchedComponents;
    }

    private List<Entry<AppGuiComponent, Double>> getMatchedComponentByLCS(boolean skipTextViews, PreProcessedText
            textToMatch, List<AppGuiComponent> guiComponents, List<String> allowedComponents, boolean matchFirst) {

        //do token and longest common substring-based matching
        LinkedHashMap<AppGuiComponent, Double> matchesScores = guiComponents.stream().collect(
                Collectors.toMap(Function.identity(), component -> {
                    String name = component.getType();
                    String text = component.getText();
                    String description = component.getContentDescription();

                    if (skipTextViews && !StringUtils.isEmpty(name) && name.endsWith(".TextView"))
                        return 0.0;
                    double matchingScore = computeLcsScore(textToMatch, text, matchFirst);
                    if (matchingScore != 0) return matchingScore;
                    matchingScore = computeLcsScore(textToMatch, description, matchFirst);
                    if (matchingScore != 0) return matchingScore;

                    String idXml = component.getIdXml();
                    if (!StringUtils.isEmpty(idXml) && idXml.contains("/")) {
                        String compId = idXml.substring(idXml.indexOf("/") + 1, idXml.length());
                        matchingScore = computeLcsScore(textToMatch, compId, matchFirst);
                        if (matchingScore != 0) return matchingScore;
                    }

                    return 0.0;
                }, (u, v) -> {
                    if (!u.equals(v))
                        throw new IllegalStateException(String.format("Duplicate key, values (%s, %s)", u, v));
                    return u;
                }, LinkedHashMap::new)
        );

        //non-zero matches and those allowed by type
        List<Entry<AppGuiComponent, Double>> nonZeroMatches = matchesScores.entrySet().stream()
                .filter(entry ->
                {
                    boolean nonZero = entry.getValue() > 0.0;
                    boolean isComponentTypeAllowed = true;
                    if (!allowedComponents.isEmpty())
                        isComponentTypeAllowed = allowedComponents.contains(getComponentTypeClass(entry.getKey()
                                .getType()));
                    return nonZero && isComponentTypeAllowed;
                })
                .collect(Collectors.toList());

        //sort in descending order by score
        nonZeroMatches.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return nonZeroMatches;
    }

    private AppGuiComponent handleOneMatch(boolean skipTextViews, List<String> allowedComponents,
                                           Entry<AppGuiComponent, Double> entry, int event,
                                           List<AppGuiComponent> currentScreenComponents)
            throws ActionParsingException {
        AppGuiComponent component = null;

        //--------------------------------------------

        AppGuiComponent entryComp = entry.getKey();

        //first handle special cases

        String componentType = entryComp.getType();
        String componentIdXml = entryComp.getIdXml();
        if (componentType.endsWith("Layout")) {

            List<AppGuiComponent> children = entryComp.getChildren().stream().filter(childComponent -> {
                if (!isAllowedComponent(skipTextViews, allowedComponents, childComponent)) return false;
                return true;
            }).collect(Collectors.toList());


            if (children.size() == 1) {
                component = children.get(0);
            }
        } else if (componentType.endsWith("TextView") && DeviceUtils.isAnyType(event)) {
            try {

                //if the component is a text view, try to find the text field that corresponds to it
                List<AppGuiComponent> siblings = getSiblings(entryComp, currentScreenComponents);
                List<AppGuiComponent> textFields = siblings.stream()
                        .filter(c -> isTextField(c.getType()))
                        .collect(Collectors.toList());

                if (textFields.size() == 1)
                    component = textFields.get(0);
                else if (textFields.isEmpty()) {

                    //if not found, then try to find in the children of the layouts

                    List<AppGuiComponent> layoutComponents = siblings.stream().filter(dynGuiComponent ->
                            dynGuiComponent.getType().endsWith("Layout")).collect(Collectors.toList());
                    List<AppGuiComponent> children = layoutComponents.stream().flatMap(c -> c.getChildren().stream())
                            .collect(Collectors.toList());

                    List<AppGuiComponent> childrenTextFields = children.stream().filter(c -> isTextField(c.getType())
                    ).collect
                            (Collectors.toList());
                    if (childrenTextFields.size() == 1)
                        component = childrenTextFields.get(0);
                }

            } catch (SQLException e) {
                log.debug("Error", e);
            }

        } else if (componentType.endsWith("TextView") && DeviceUtils.isClick(event)) {

            //this is not working for AARDICT-81
            try {

                //if the component is a text view, try to find the component that corresponds to it (not a text field!)
                List<AppGuiComponent> siblings = getSiblings(entryComp, currentScreenComponents);
                if (siblings.size() == 1) {
                    AppGuiComponent sibling = siblings.get(0);

                    //check that the sibling is right next to it
                    if (!isTextField(sibling.getType()) && (entryComp.getIndex() + 1)
                            == sibling.getIndex() && isPickerComponent(sibling.getType())) {
                        component = sibling;
                    }
                }

            } catch (SQLException e) {
                log.debug("Error", e);
            }
        } else
            //hack: for FAB buttons, there may be a View that gives the floating aspect to the FAB, then we pick the
            // first child of the View
            if (componentIdXml != null && componentIdXml.endsWith("fab") && componentType.endsWith(".View")) {
                if (!entryComp.getChildren().isEmpty())
                    component = entryComp.getChildren().get(0);
            }

        //--------------------------------------------

        //if the code above didn't or couldn't handle the special case, then just return the candidate

        if (component == null) {
            //component found!
            component = entryComp;
        }

        //only text fields are allowed for type events
        if (DeviceUtils.isAnyType(event) && !isTextField(component.getType())) {
            throw new ActionParsingException(ParsingResult.INCORRECT_COMPONENT_FOUND,
                    Collections.singletonList(component));
        }

        return component;
    }

    private Entry<AppGuiComponent, Double> handleMultipleMatches(boolean skipTextViews, PreProcessedText textToMatch,
                                                                 List<String> allowedComponents,
                                                                 List<Entry<AppGuiComponent, Double>> matchedComponents,
                                                                 int event, boolean matchFirst,
                                                                 List<AppGuiComponent> currentScreenComponents)
            throws ActionParsingException {
        
        log.debug("Handling multiple matched components...");

        Entry<AppGuiComponent, Double> componentFound = null;
        for (Entry<AppGuiComponent, Double> entry : matchedComponents) {

            AppGuiComponent component = entry.getKey();
            String componentType = component.getType();

            if (!StringUtils.isEmpty(componentType) && componentType.endsWith("Layout")) {

                //check if the Layout has one child
                List<Entry<AppGuiComponent, Double>> children = getChildren(component, matchedComponents).stream()
                        .filter(childComponent -> {
                            if (!isAllowedComponent(skipTextViews, allowedComponents, childComponent.getKey()))
                                return false;
                            return true;
                        })
                        .collect(Collectors.toList());

                //if so, this is the one
                if (children.size() == 1) {
                    componentFound = children.get(0);
                    break;
                }
            }

        }

        //--------------------------------------------------

        //is there a set of components that are the same?
        if (componentFound == null && hasSameComponents(matchedComponents)) {

            //score the components by looking at their sibling components
            LinkedHashMap<AppGuiComponent, Double> matchesScores = matchedComponents.stream()
                    .map(Entry::getKey)
                    .collect(Collectors.toMap
                            (Function.identity(), component -> {

                                try {
                                    List<AppGuiComponent> siblingsComponents = getSiblings(component,
                                            currentScreenComponents);

                                    return matchSiblings(siblingsComponents, textToMatch, skipTextViews,
                                            allowedComponents, matchFirst);
                                } catch (SQLException e) {
                                    log.error("Error getting the sibling components of " + component);
                                }

                                return 0.0;
                            }, (u, v) -> {
                                if (!u.equals(v))
                                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                                return u;
                            }, LinkedHashMap::new));

            List<Entry<AppGuiComponent, Double>> nonZeroMatches = matchesScores.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0.0)
                    .collect(Collectors.toList());

            log.debug(String.format("Sibling matches (%s): %s", nonZeroMatches.size(), nonZeroMatches));

            if (!nonZeroMatches.isEmpty()) {
                if (nonZeroMatches.size() == 1)
                    componentFound = nonZeroMatches.get(0);
            }

            /*//take the first one, which has the highest score
            if (!nonZeroMatches.isEmpty()) {
                if (nonZeroMatches.size() > 1)
                    log.warn("Sibling matches greater than 1");
                componentFound = nonZeroMatches.get(0);
            } else {
                //take the first one if the above fails
                if (matchedComponents.stream().allMatch(c -> c.getValue().equals(1.0))) {
                    componentFound = matchedComponents.get(0);
                }
            }*/

        }

        //--------------------------------------------------

        //for types, favor text fields
        if (componentFound == null && DeviceUtils.isAnyType(event)) {

            List<Entry<AppGuiComponent, Double>> onlyTextFields = matchedComponents.stream()
                    .filter(c -> isTextField(c.getKey().getType()))
                    .collect(Collectors.toList());
            if (onlyTextFields.size() == 1)
                componentFound = onlyTextFields.get(0);
        }

        //--------------------------------------------------

        //for clicks, favor buttons
        if (componentFound == null && DeviceUtils.isClick(event)) {

            List<Entry<AppGuiComponent, Double>> onlyButtons = matchedComponents.stream()
                    .filter(c -> isButton(c.getKey().getType()))
                    .collect(Collectors.toList());
            if (onlyButtons.size() == 1)
                componentFound = onlyButtons.get(0);
        }

        //--------------------------------------------------

        if (componentFound == null && !StringUtils.isEmpty(textToMatch.componentType)) {
            final ComponentType componentType = invIdxSpecificComponentTypes.get(textToMatch.componentType);
            if (componentType != null) {
                final List<Entry<AppGuiComponent, Double>> sameTypeComponents = matchedComponents.stream()
                        .filter(c -> componentType.equals(getComponentType(c.getKey().getType())))
                        .collect(Collectors.toList());

                if (sameTypeComponents.size() == 1)
                    componentFound = sameTypeComponents.get(0);
            }
        }


        //--------------------------------------------------

        if (componentFound == null) {
            log.debug("Couldn't resolve multiple matched components");
            throw new ActionParsingException(ParsingResult.MULTIPLE_COMPONENTS_FOUND,
                    matchedComponents.stream()
                            .map(Entry::getKey)
                            .collect(Collectors.toList()));
        }else
            log.debug("Selected component: " + componentFound);
        return componentFound;
    }

    //---------------------------------------------------------------

    private List<Entry<AppGuiComponent, Double>> getChildren(AppGuiComponent component,
                                                             List<Entry<AppGuiComponent, Double>> matchedComponents) {
        List<AppGuiComponent> children = component.getChildren();
        List<Entry<AppGuiComponent, Double>> result = new ArrayList<>();
        for (AppGuiComponent child : children) {
            Optional<Entry<AppGuiComponent, Double>> findFirst = matchedComponents.stream()
                    .filter(e -> e.getKey().equals(child)).findFirst();
            findFirst.ifPresent(result::add);
        }
        return result;
    }

    private List<AppGuiComponent> getSiblings(AppGuiComponent component,
                                              List<AppGuiComponent> currentScreenComponents) throws SQLException {

        List<AppGuiComponent> siblings = currentScreenComponents.stream()
                .filter(comp -> comp.getParent() != null && comp.getParent().getDbId().equals(component.getParent().getDbId())
                        && !comp.getDbId().equals(component.getDbId()))
                .collect(Collectors.toList());
        return siblings;


        //------------------------------------

//        DynGuiComponentDao dao = new DynGuiComponentDao();
//        EntityManager em = DBUtils.createEntityManager();
//        List<AppGuiComponent> guiComponents = Transform.getGuiComponents(dao.findSiblings(component.getDbId(), em));
//        return guiComponents;
    }

    private double matchSiblings(List<AppGuiComponent> siblingsComponents, PreProcessedText textToMatch, boolean
            skipTextViews, List<String> allowedComponents, boolean matchFirst) {

        //non-layout components
        List<AppGuiComponent> nonLayoutComponents = siblingsComponents.stream().filter(dynGuiComponent ->
                !dynGuiComponent.getType().endsWith("Layout")).collect(Collectors.toList());
        List<Entry<AppGuiComponent, Double>> allMatchedComponents = getMatchedComponentByLCS(skipTextViews,
                textToMatch, nonLayoutComponents, allowedComponents, matchFirst);

        //layout components
        List<AppGuiComponent> layoutComponents = siblingsComponents.stream().filter(dynGuiComponent ->
                dynGuiComponent.getType().endsWith("Layout")).collect(Collectors.toList());
        allMatchedComponents.addAll(getMatchedComponentByLCS(skipTextViews, textToMatch, layoutComponents,
                allowedComponents, matchFirst));

        //layout children components
        List<AppGuiComponent> children = layoutComponents.stream().flatMap(c -> c.getChildren().stream())
                .collect(Collectors.toList());
        allMatchedComponents.addAll(getMatchedComponentByLCS(skipTextViews, textToMatch, children, allowedComponents,
                matchFirst));

        if (!allMatchedComponents.isEmpty()) {
            //compute the mean of the scores
            double[] values = allMatchedComponents.stream().mapToDouble(Entry::getValue).toArray();
            DescriptiveStatistics stats = new DescriptiveStatistics(values);
            return stats.getMean();
        }
        return 0;

    }

    private boolean hasSameComponents(List<Entry<AppGuiComponent, Double>> matchedComponents) {
        for (int i = 0; i < matchedComponents.size(); i++) {
            AppGuiComponent component1 = matchedComponents.get(i).getKey();
            String componentSignature1 = getComponentSignature(component1);
            for (int j = i + 1; j < matchedComponents.size(); j++) {
                AppGuiComponent component2 = matchedComponents.get(j).getKey();
                String componentSignature2 = getComponentSignature(component2);

                //found at least a pair of components that are the same
                if (componentSignature1.equals(componentSignature2))
                    return true;
            }
        }
        return false;
    }

    private String getComponentSignature(AppGuiComponent component) {
        //by idXml, name, description, and text
        return component.getIdXml() + "-" + component.getType() +
                "-" + component.getContentDescription() + "-" + component.getText();
    }

    private Entry<AppGuiComponent, Double> handleNoMatches(List<AppGuiComponent> currentScreen,
                                                           Set<ComponentType> componentTypes,
                                                           boolean skipTextViews,
                                                           boolean checkSynonyms,
                                                           PreProcessedText textToMatch,
                                                           int event,
                                                           boolean matchFirst)
            throws ActionParsingException {

        Entry<AppGuiComponent, Double> component = null;
        if (checkSynonyms) {

            Set<String> textSynonyms = getTextSynonyms(textToMatch.otherText);
            log.debug("Checking synonyms of \"" + textToMatch.otherText + "\": " + textSynonyms);

            //no synonyms? try with the original text
            if (textSynonyms == null) {
                textSynonyms = getTextSynonyms(textToMatch.original);
                log.debug("Checking synonyms of \"" + textToMatch.original + "\": " + textSynonyms);
            }

            //no synonyms? try with the synonyms of the tokens
            if (useTokenSynonyms && textSynonyms == null) {
                textSynonyms = getTextSynonymsOfTokens(textToMatch.otherText);
                log.debug("Checking synonyms of \"" + textToMatch.otherText + "\": " + textSynonyms);
            }

            if (textSynonyms != null) {

                for (String textSynonym : textSynonyms) {
                    try {
                        PreProcessedText preProcessedSyn = preprocessText(textSynonym);
                        component = findComponent(currentScreen, preProcessedSyn, componentTypes,
                                skipTextViews, false, event, false, matchFirst);
                        if (component != null)
                            break;
                    } catch (ActionParsingException e) {
                        //it is ok not to find any match
                    }
                }


            }

        }

        if (component == null)
            throw new ActionParsingException(ParsingResult.COMPONENT_NOT_FOUND,
                    Collections.singletonList(textToMatch.original));

        return component;
    }


    private boolean isAllowedComponent(boolean skipTextViews, List<String> allowedComponents, AppGuiComponent
            childComponent) {
        String componentType = childComponent.getType();
        if (skipTextViews && !StringUtils.isEmpty(componentType) && componentType.endsWith(".TextView"))
            return false;
        if (!allowedComponents.isEmpty() && !allowedComponents.contains(getComponentTypeClass(componentType)))
            return false;
        return true;
    }


    private Set<String> getTextSynonymsOfTokens(String text) {
        if (StringUtils.isEmpty(text))
            return null;

        List<String> tokens = Arrays.asList(text.split(" "));

        return tokens.stream()
                .flatMap(token -> {
                    Set<String> syn = synonyms.get(token);
                    if (syn == null) return null;
                    return syn.stream();
                })
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

    }

    private Set<String> getTextSynonyms(String text) {

        if (StringUtils.isEmpty(text))
            return null;

        //first try by key
        Set<String> textSynonyms = synonyms.get(text.toLowerCase());

        if (textSynonyms == null) {

            //get the last token of the text
            List<Token> tokens = tokenize(text, new String[]{});
            Token lastToken = tokens.get(tokens.size() - 1);
            String lastTokenWord = lastToken.getWord().toLowerCase();

            //try by the last token
            textSynonyms = synonyms.get(lastTokenWord);

            if (textSynonyms == null) {

                //find the first set of synonyms that contains the last token
                Optional<Set<String>> firstFound = synonyms.values().stream().filter(syn -> syn.contains
                        (lastTokenWord)).findFirst();
                if (firstFound.isPresent())
                    textSynonyms = firstFound.get();
            }

            //if we found the set
            if (textSynonyms != null) {

                //delete the token from the synonym set
                //TODO: should I delete all entries that match the token
                List<String> textSynonymsTemp = new LinkedList<>(textSynonyms);
                OptionalInt indexOpt = IntStream.range(0, textSynonymsTemp.size())
                        .filter(i -> textSynonymsTemp.get(i).contains(lastTokenWord))
                        .findFirst();
                textSynonymsTemp.remove(indexOpt.getAsInt());
                textSynonyms = new HashSet<>(textSynonymsTemp);
            }
        } else {
            textSynonyms = new HashSet<>(textSynonyms);
            textSynonyms.remove(text.toLowerCase());
        }


        return textSynonyms;
    }

    //---------------------------------------------------------------

    private double computeLcsScore(PreProcessedText text1, String text2, boolean matchFirst) {

        if (StringUtils.isEmpty(text2))
            return 0;

        PreProcessedText preProcessedText2 = preprocessText(text2);

        List<Token> tokens1 = text1.preprocessedTokens;
        List<Token> tokens2 = preProcessedText2.preprocessedTokens;

        if (tokens1.isEmpty() || tokens2.isEmpty())
            return 0;

        List<String> lemmas1 = tokens1.stream().map(Token::getLemma).collect(Collectors.toList());
        List<String> lemmas2 = tokens2.stream().map(Token::getLemma).collect(Collectors.toList());

        LinkedHashSet<String> allLemmas = new LinkedHashSet<>(lemmas1);
        allLemmas.addAll(lemmas2);

        List<String> allLemmasList = new ArrayList<>(allLemmas);

        String encodedText1 = asciiEncodeLemmas(allLemmasList, lemmas1);
        String encodedText2 = asciiEncodeLemmas(allLemmasList, lemmas2);

        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
        solver.add(encodedText1);
        solver.add(encodedText2);
        String lcs = solver.getLongestCommonSubstring().toString();

        //-----------------------------
        if (matchFirst && !lcs.isEmpty()) {
            if (lcs.charAt(0) != encodePosition(0) || lcs.length() <= 1) {
                return 0;
            }
        }

        double lengthAvg = ((double) (encodedText1.length() + encodedText2.length())) / 2;
        return ((double) lcs.length()) / lengthAvg;
    }

    private String asciiEncodeLemmas(List<String> allLemmas, List<String> lemmasToEncode) {

        if (allLemmas.size() > 93)
            throw new RuntimeException("Big alphabet: " + allLemmas.size());

        StringBuilder builder = new StringBuilder();
        for (String lemma : lemmasToEncode) {
            int i = allLemmas.indexOf(lemma);
            //ASCII encoding
            builder.append(encodePosition(i));
        }

        return builder.toString();
    }

    private char encodePosition(int i) {
        return (char) (i + 33);
    }

    private List<Token> tokenize(String text, String[] preprocessingOptions) {
        List<Sentence> sentences = TextProcessor.preprocessText(text, null, preprocessingOptions);

        //must be only one sentence
        if (sentences.isEmpty())
            return new ArrayList<>();
        return sentences.get(0).getTokens();
    }

    private synchronized LinkedHashSet<ActionGroup> getAppSpecificActionGroups(String action, Appl app) {
        //TODO
        return null;
    }

    private Integer mapActionGroupToEvent(ActionGroup actionGroup, NLAction nlAction, Appl app) {

        switch (actionGroup) {
            case OPEN:
                //open the application
                if (GeneralUtils.isAppWord(nlAction.getObject(), app.getName(), app.getPackageName()))
                    return DeviceActions.OPEN_APP;
                else
                    return DeviceActions.CLICK;

            case LONG_CLICK:
                return DeviceActions.LONG_CLICK;
            case CLICK:
            case TOGGLE:

                final PreProcessedText preProcessedAction = preprocessText(nlAction.getAction());
                final PreProcessedText preProcessedObj = preprocessText(nlAction.getObject());
                final PreProcessedText preProcessedObj2 = preprocessText(nlAction.getObject2());

                if (isGoBack(preProcessedAction, preProcessedObj, preProcessedObj2)) {
                    return DeviceActions.BACK;
                } else if (isMenuInActionOrObject(preProcessedObj, preProcessedObj2)) {
                    return DeviceActions.MENU_BTN;
                }
                return DeviceActions.CLICK;
            case TYPE:
                PreProcessedText preprocessedObject = preprocessText(nlAction.getObject());
                if (containsBlankToken(preprocessedObject))
                    return DeviceActions.DELETE_TEXT;
                if (getKeyValue(nlAction.getObject()) != null)
                    return DeviceActions.KEYEVENT;
                return DeviceActions.TYPE;
            case SWIPE:
                return getSwipeDirection(nlAction);
            case ROTATE:
                return DeviceActions.ROTATION;
            case CLOSE:
                return DeviceActions.CLOSE_APP;
        }

        return null;
    }

    private boolean isGoBack(PreProcessedText preProcessedAction, PreProcessedText preProcessedObj,
                             PreProcessedText preProcessedObj2) {
        return isActionOrObject(preProcessedAction, preProcessedObj, "back") || isObjects(preProcessedObj,
                preProcessedObj2, "back")
                || preProcessedAction.allTokens.stream().anyMatch(t -> "leave".equalsIgnoreCase(t.getLemma()));
    }

    private boolean isActionOrObject(PreProcessedText preProcessedAction, PreProcessedText
            preProcessedObj, String term) {
        return preProcessedAction.allTokens.stream().anyMatch(t -> term.equalsIgnoreCase(t.getLemma()))
                || preProcessedObj.allTokens.stream().anyMatch(t -> term.equalsIgnoreCase(t.getLemma()));
    }

    public boolean isObjects(PreProcessedText preProcessedObj, PreProcessedText
            preProcessedObj2, String term) {
        return preProcessedObj2.allTokens.stream().anyMatch(t -> term.equalsIgnoreCase(t.getLemma()))
                || preProcessedObj.allTokens.stream().anyMatch(t -> term.equalsIgnoreCase(t.getLemma()));
    }

    public boolean isMenuInActionOrObject(PreProcessedText preProcessedObj, PreProcessedText preProcessedObj2) {
        return (isObjects(preProcessedObj, preProcessedObj2, "menu") || isObjects(preProcessedObj, preProcessedObj2,
                "more option")
                //|| isObjects(nlAction, "more")
                || (isObjects(preProcessedObj, preProcessedObj2, "dot") && isObjects(preProcessedObj,
                preProcessedObj2, "three"))) && !isObjects(preProcessedObj, preProcessedObj2, "left");
    }

    private int getSwipeDirection(NLAction nlAction) {
        String action = nlAction.getAction();

        String[] actionTokens = action.split(" ");
        if (actionTokens.length > 1) {
            String directionTok = actionTokens[1];
            int swipeDirection = getSwipeDirection(action, directionTok);
            if (swipeDirection != NO_SWIPE_DIRECTION) {
                return swipeDirection;
            }
        }

        Set<String> tokens = JavaUtils.getSet(nlAction.getObject(), nlAction.getPreposition(), nlAction.getObject2());

        for (String tok : tokens) {
            int swipeDirection = getSwipeDirection(action, tok);
            if (swipeDirection != NO_SWIPE_DIRECTION) {
                return swipeDirection;
            }

        }

        //FIXME: do something else for actions such as "swipe until costs" or "drag on all regions"
        return DeviceActions.SWIPE_UP;

    }

    private int getSwipeDirection(String action, String directionTok) {
        if (action.contains("scroll")) {
            if ("down".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_UP;
            if ("up".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_DOWN;
            if ("left".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_RIGHT;
            if ("right".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_LEFT;
        } else {
            if ("down".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_DOWN;
            if ("up".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_UP;
            if ("left".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_LEFT;
            if ("right".equalsIgnoreCase(directionTok))
                return DeviceActions.SWIPE_RIGHT;
        }
        return NO_SWIPE_DIRECTION;
    }

    private ActionGroup disambiguateActionGroup(LinkedHashSet<ActionGroup> actionGroups, NLAction nlAction,
                                                List<AppGuiComponent> currentScreen) {

        if (actionGroups.size() == 1)
            return actionGroups.iterator().next();

        //---------------------------------------------------------------------

        //check for rotation
        boolean anyRotation = actionGroups.stream().anyMatch(a -> a.equals(ActionGroup.ROTATE));
        if (anyRotation) {
            Set<String> rotationModes = JavaUtils.getSet("portrait", "landscape", "orientation", "phone");
            if (SentenceUtils.stringEqualsToAnyToken(rotationModes, nlAction.getObject())
                    || SentenceUtils.stringContainsAnyToken(rotationModes, nlAction.getObject())
                    || SentenceUtils.stringEqualsToAnyToken(rotationModes, nlAction.getObject2())
                    || SentenceUtils.stringContainsAnyToken(rotationModes, nlAction.getObject2()))
                return ActionGroup.ROTATE;
            else
                actionGroups.remove(ActionGroup.ROTATE);
        }

        //---------------------------------------------------------------------

        //check for type
        boolean anyType = actionGroups.stream().anyMatch(a -> a.equals(ActionGroup.TYPE));
        if (anyType) {
            String preposition = nlAction.getPreposition();
            if (SentenceUtils.stringEqualsToAnyToken(JavaUtils.getSet("to", "as"), preposition)) {
                if (isLiteralValue(nlAction.getObject2()) && isValidComponent(nlAction.getObject()))
                    return ActionGroup.TYPE;
                else {
                    if (JavaUtils.getSet("change", "modify", "set").contains(nlAction.getAction())) {
                        PreProcessedText preprocessedObject = preprocessText(nlAction.getObject());
                        //case: change 'first line' to 'x'
                        if (preprocessedObject.preprocessedTokens.stream().anyMatch(token1 -> "line".equalsIgnoreCase
                                (token1.getLemma())))
                            return ActionGroup.TYPE;
                    }

                }
            } else if (StringUtils.isEmpty(nlAction.getObject2()) && getKeyValue(nlAction.getObject()) != null) {
                return ActionGroup.TYPE;
            }


        }

        //----------------------------------------------------------------------------

        int noEvent = -100;
        if (JavaUtils.getSet(ActionGroup.CLICK, ActionGroup.LONG_CLICK).equals(actionGroups)) {

            if (isCheckAction(nlAction) && anyCheckeableComponent(currentScreen)) {
                return ActionGroup.CLICK;
            } else if (noLongClickableComponents(currentScreen)) {
                return ActionGroup.CLICK;
            }

            //-----------------------------------------------------


            Entry<AppGuiComponent, Double> componentFound = null;
            PreProcessedText preprocessedAction = preprocessText(nlAction.getAction());
            try {
                componentFound = findComponent(currentScreen, preprocessedAction, JavaUtils.getSet(), false,
                        true, noEvent);
            } catch (ActionParsingException e2) {
                PreProcessedText preprocessedObject = preprocessText(nlAction.getObject());
                try {
                    componentFound = findComponent(currentScreen, preprocessedObject, JavaUtils.getSet(), false,
                            true, noEvent);
                } catch (ActionParsingException e) {
                    PreProcessedText preprocessedObject2 = preprocessText(nlAction.getObject2());
                    try {
                        componentFound = findComponent(currentScreen, preprocessedObject2, JavaUtils.getSet(), false,
                                true, noEvent);
                    } catch (ActionParsingException e1) {
                        //It's ok to have the exception
                    }

                }
            }

            if (componentFound != null) {
                if (componentFound.getKey().getClickable())
                    return ActionGroup.CLICK;
                else if (componentFound.getKey().getLongClickable())
                    return ActionGroup.CLICK;
            }

        }

        //---------------------------------------------------------------------

        //check for type
        boolean anyClick = actionGroups.stream().anyMatch(a -> a.equals(ActionGroup.CLICK));
        if (anyClick && anyType) {

            PreProcessedText preProcessedAction = preprocessText(nlAction.getAction());
            PreProcessedText preprocessedObject = preprocessText(nlAction.getObject());
            PreProcessedText preprocessedObject2 = preprocessText(nlAction.getObject2());

            if (isGoBack(preProcessedAction, preprocessedObject, preprocessedObject2)) {
                return ActionGroup.CLICK;
            }

            //----------------------------------------------
            //case: set 'xyz'

            Entry<AppGuiComponent, Double> componentFound = null;
            try {
                componentFound = findComponent(currentScreen, preprocessedObject, JavaUtils.getSet(), false,
                        true, noEvent);
            } catch (ActionParsingException e) {
                try {
                    componentFound = findComponent(currentScreen, preprocessedObject2, JavaUtils.getSet(), false,
                            true, noEvent);
                } catch (ActionParsingException e1) {
                    //It's ok to have the exception
                }

            }

            if (componentFound != null) {
                //case: set 'xyz blank'
                if (containsBlankToken(preprocessedObject)) {
                    return ActionGroup.TYPE;
                } else {

                    ComponentType componentType = getComponentType(componentFound.getKey().getType());

                    if (componentType != null) {
                        switch (componentType) {
                            case BUTTON:
                            case CHECKED_COMPONENT:
                            case PICKER:
                            case IMAGE_VIEW:
                            case TEXT_VIEW:
                                return ActionGroup.CLICK;
                            case TEXT_FIELD:
                                return ActionGroup.TYPE;

                        }
                    }
                }
            }

            //----------------------------------------------

        }


        return null;
    }

    private boolean containsBlankToken(PreProcessedText preprocessedObject) {
        return preprocessedObject.lemmatized != null &&
                preprocessedObject.lemmatized.endsWith("blank") &&
                preprocessedObject.preprocessedTokens.size() > 2;
    }

    private boolean noLongClickableComponents(List<AppGuiComponent> currentScreen) {
        return currentScreen.stream().noneMatch(AppGuiComponent::getLongClickable);
    }

    private ComponentType getComponentType(String componentName) {
        String componentTypeClass = getComponentTypeClass(componentName);
        return generalComponentClasses.get(componentTypeClass);
    }

    private boolean anyCheckeableComponent(List<AppGuiComponent> currentScreen) {
        return currentScreen.stream().anyMatch(component -> {
            String componentClassType = component.getType();

            if (StringUtils.isEmpty(componentClassType))
                return false;

            return isCheckedComponent(component.getType());
        });
    }

    private String getComponentTypeClass(String componentClassType) {
        return componentClassType.substring(componentClassType.lastIndexOf(".") + 1);
    }

    private boolean isCheckAction(NLAction nlAction) {
        return JavaUtils.getSet("select", "choose", "pick", "mark").contains(nlAction.getAction());
    }

    private boolean isValidComponent(String object) {
        //FIXME
        return true;
    }

    public Entry<AppGuiComponent, Double> matchAnyComponent(NLAction nlAction, List<AppGuiComponent> stateComponents) {

        String object = nlAction.getObject();
        String object2 = nlAction.getObject2();
        String preposition = nlAction.getPreposition();
        final String action = nlAction.getAction();

        //------------------------------------------------------

        Map.Entry<AppGuiComponent, Double> componentFound;

        PreProcessedText preprocessedAll = preprocessText(String.format("%s %s %s", action, object, object2));

        PreProcessedText preprocessedObject = preprocessText(object);
        PreProcessedText preprocessedObject2 = preprocessText(object2);


        return null;
    }


    public enum ActionGroup {
        OPEN, TOGGLE, LONG_CLICK, CLICK, SWIPE, TYPE, ROTATE, CLOSE
    }

    public enum ComponentType {
        BUTTON, CHECKED_COMPONENT, IMAGE_VIEW, PICKER, BAR, TEXT_FIELD, TEXT_VIEW, WINDOW, OTHER
    }

}
