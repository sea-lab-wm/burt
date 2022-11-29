package sealab.burt.qualitychecker.graph;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import sealab.burt.nlparser.euler.actions.DeviceActions;
import seers.textanalyzer.TextProcessor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class GraphTransition extends DefaultWeightedEdge implements Serializable {

    private static final long serialVersionUID = -7906613289658288453L;
    private static final HashMap<Integer, String> ACTION_STRINGS = new LinkedHashMap<>();

    static {
        ACTION_STRINGS.put(DeviceActions.CLICK, "tap");
        ACTION_STRINGS.put(DeviceActions.LONG_CLICK, "long tap");
        ACTION_STRINGS.put(DeviceActions.SWIPE, "swipe");
        ACTION_STRINGS.put(DeviceActions.SWIPE_UP, "swipe up");
        ACTION_STRINGS.put(DeviceActions.SWIPE_RIGHT, "swipe right");
        ACTION_STRINGS.put(DeviceActions.SWIPE_DOWN, "swipe down");
        ACTION_STRINGS.put(DeviceActions.SWIPE_LEFT, "swipe left");
        ACTION_STRINGS.put(DeviceActions.CLICK_TYPE, "type");
        ACTION_STRINGS.put(DeviceActions.BACK, "Go back");
        ACTION_STRINGS.put(DeviceActions.TYPE, "type");
        ACTION_STRINGS.put(DeviceActions.OPEN_APP, "open app");
        ACTION_STRINGS.put(DeviceActions.ROTATION, "rotate");
        ACTION_STRINGS.put(DeviceActions.GPS, "toggle gps");
        ACTION_STRINGS.put(DeviceActions.NETWORK, "toggle network");
        ACTION_STRINGS.put(DeviceActions.TYPE_RANDOM, "type random");
        ACTION_STRINGS.put(DeviceActions.MENU_BTN, "Tap the menu button");
    }

    private GraphState sourceState;
    private GraphState targetState;
    private String name;
    private String uniqueHash;
    private double weight = 1;
    private GraphDataSource dataSource;
    private AppStep step;

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public static String splitAndSpecialRemoveChars(String text) {
        String[] tokens = StringUtils.splitByCharacterTypeCamelCase(text);
        List<String> validTokens = Arrays.stream(tokens).filter(t -> !TextProcessor.isSpecialChar(t))
                .collect(Collectors.toList());
        return StringUtils.join(validTokens, ' ');
    }

    public static String getAction(Integer action) {
        String actStr = ACTION_STRINGS.get(action);
        if (actStr == null) {
            return action.toString();
        }
        return actStr;
    }

    public static String getWindowString(String activity, String currentWindow) {

        if (currentWindow == null || currentWindow.isEmpty()) {
            return getActivityString(activity);
        }

        String[] tokens = currentWindow.split("\\:");

        if (tokens.length == 1) {
            return getActivityString(activity);
        }

        String windowName = tokens[tokens.length - 1];
        if (tokens.length == 2 && "ACTIVITY".equals(tokens[0])) {
            return getActivityString(windowName);
        }

        List<String> lowerCaseToken = Arrays.stream(tokens).map(WordUtils::capitalizeFully)
                .collect(Collectors.toList());

        return StringUtils.join(lowerCaseToken.subList(0, tokens.length - 1), ' ') + " - "
                + getActivityString(windowName);

    }

    public static String getActivityString(String activity) {
        if (activity == null) return "";
        activity = activity.replaceAll("->-?\\d+", "");
        String[] activityTokens = activity.split("\\.");
        String suffix = activityTokens[activityTokens.length - 1];
        String[] cmTokens = StringUtils.splitByCharacterTypeCamelCase(suffix);
        return StringUtils.join(cmTokens, ' ');
    }

    public static String getComponentString(String compType) {
        if (compType == null)
            return "";
        String[] tokens = compType.split("\\.");
        String[] cmTokens = StringUtils.splitByCharacterTypeCamelCase(tokens[tokens.length - 1]);
        return StringUtils.join(cmTokens, ' ');
    }

    public static boolean isTypeAction(Integer action) {

        if (action == null) {
            return false;
        }

        return DeviceActions.CLICK_TYPE == action || DeviceActions.TYPE == action
                || DeviceActions.TYPE_RANDOM == action;
    }

    public GraphDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(GraphDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * @return the sourceState
     */
    public GraphState getSourceState() {
        return sourceState;
    }

    /**
     * @param sourceState the sourceState to set
     */
    public void setSourceState(GraphState sourceState) {
        this.sourceState = sourceState;
    }

    /**
     * @return the targetState
     */
    public GraphState getTargetState() {
        return targetState;
    }

    /**
     * @param targetState the targetState to set
     */
    public void setTargetState(GraphState targetState) {
        this.targetState = targetState;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueHash() {
        return uniqueHash;
    }

    public void setUniqueHash(String uniqueHash) {
        this.uniqueHash = uniqueHash;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uniqueHash == null) ? 0 : uniqueHash.hashCode());
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
        GraphTransition other = (GraphTransition) obj;
        if (uniqueHash == null) {
            if (other.uniqueHash != null)
                return false;
        } else if (!uniqueHash.equals(other.uniqueHash))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "t[" + uniqueHash + ", " + dataSource + ", w="  + weight + "]";
    }

    public AppStep getStep() {
        return step;
    }

    public void setStep(AppStep step) {
        this.step = step;
    }

    public String toNaturalLanguage() {

        AppStep step = this.getStep();
        Integer action = step.getAction();

        if (DeviceActions.OPEN_APP == action) {
            return getAction(action);
        }

        StringBuilder builder = new StringBuilder();

        AppGuiComponent component = step.getComponent();
        if (component != null) {

            builder.append("on [");
            builder.append(getWindowString(component.getActivity(), component.getCurrentWindow()));
            builder.append("], ");

        }

        // action
        builder.append("[");
        builder.append(getAction(action));
        builder.append("] ");

        String stepText = step.getText();
        if (!stepText.trim().isEmpty()) {
            builder.append(stepText);
            builder.append(" ");
        }

        if (component != null) {

            String compType = component.getType();
            builder.append("on [");
            builder.append(getComponentString(compType));
            builder.append("] ");

            // -----------------------------------

            String compText = component.getText();
            if (compText == null) {
                compText = "";
            }

            final int LIMIT_COMPONENT_TEXT = 80;
            if (compText.length() > LIMIT_COMPONENT_TEXT) {
                compText = compText.substring(0, LIMIT_COMPONENT_TEXT) + "...";
            }

            compText = compText.replace("\n", " ").replace("\r", " ");

            // -----------------------------------

            String idXml = component.getLastPartOfIdXml();

            if (idXml != null && !idXml.trim().isEmpty()) {

                builder.append("[");
                builder.append(splitAndSpecialRemoveChars(idXml));

                if (!compText.isEmpty()) {
                    builder.append(" | ");
                    builder.append(compText);
                }
                builder.append("]");

            } else {

                if (!compText.isEmpty()) {
                    builder.append("[");
                    builder.append(compText);
                    builder.append("]");
                }
            }

        }

        return builder.toString().trim();
    }

    public boolean isOpenApp() {
        return DeviceActions.OPEN_APP == step.getAction();
    }

    public void incrementWeightByOne() {
        this.weight++;
    }
}
