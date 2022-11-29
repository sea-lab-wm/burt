package sealab.burt.qualitychecker.graph;

import com.google.gson.annotations.Expose;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class AppStep implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2189199076066580332L;
    private Long id;
    private Integer action;
    private Integer sequence;
    private Long execution;
    private Path executionPath;
    private Path xmlPath;

    private AppGuiComponent component;
    @Expose(serialize = false)
    private GraphTransition transition;
    private String text;
    private String exception;

    private String screenshotFile;
    @Expose(serialize = false)
    private GraphState currentState;
    private List<String> phrases;

    /**
     *
     */
    public AppStep() {
        super();
    }

    public AppStep(Integer action, AppGuiComponent component) {
        setAction(action);
        this.component = component;
    }


    public AppStep(Integer action, AppGuiComponent component, String text) {
        this(action, component);
        this.text = text;
    }
//    public AppStep(Integer action, AppGuiComponent component, String text) {
//        this(action, component);
//        this.text = text;
//    }

    public Path getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(Path xmlPath) {
        this.xmlPath = xmlPath;
    }

    public Path getExecutionPath() {
        return executionPath;
    }

    public void setExecutionPath(Path executionPath) {
        this.executionPath = executionPath;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the action
     */
    public Integer getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Integer action) {
//        if (action != null && DeviceActions.CLICK_TYPE == action) {
//            throw new RuntimeException("Click-type is deprecated, hence it is not allowed!");
//        }
        this.action = action;
    }

    /**
     * @return the component
     */
    public AppGuiComponent getComponent() {
        return component;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(AppGuiComponent component) {
        this.component = component;
    }

    /**
     * @return the transition
     */
    public GraphTransition getTransition() {
        return transition;
    }

    /**
     * @param transition the transition to set
     */
    public void setTransition(GraphTransition transition) {
        this.transition = transition;
    }

    public Long getExecution() {
        return execution;
    }

    public void setExecution(Long execution) {
        this.execution = execution;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getScreenshotFile() {
        return screenshotFile;
    }

    public void setScreenshotFile(String screenshotFile) {
        this.screenshotFile = screenshotFile;
    }

    //--------------------

    @Override
    public String toString() {
        return "[id=" + id + ", ex=" + execution + ", sq=" + sequence + ", act=(" + action + ") " + GeneralUtils
                .getEventName(action) + ", cp=" + component + ", txt=" + text + ", exp=" + getExceptionHash() + ", " +
                "tr=" + (transition != null ? transition.getUniqueHash() : null) + "]";
    }

    public String getUniqueHash() {
        return "[act=" + action + ", cp=" + (component == null ? "" : component.getUniqueString()) + "]";
    }

    private String getExceptionHash() {
        return (exception != null && !exception.isEmpty()) ? String.valueOf(exception.hashCode()) : "";
    }

    //--------------------------------

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getUniqueHash().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppStep other = (AppStep) obj;
        return this.getUniqueHash().equals(other.getUniqueHash());
    }

    public GraphState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GraphState currentState) {
        this.currentState = currentState;
    }

    public void setPhrases(List<String> phrases){
        this.phrases = phrases;

    }

    public List<String> getPhrases(){return phrases;}
}
