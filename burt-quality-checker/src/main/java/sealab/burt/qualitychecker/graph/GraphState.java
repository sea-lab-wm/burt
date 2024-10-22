package sealab.burt.qualitychecker.graph;

import edu.semeru.android.core.entity.model.fusion.Screen;

import java.io.Serializable;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class GraphState implements Serializable {

    public static final GraphState START_STATE = buildDummyState("Start state");
    public static final GraphState END_STATE = buildDummyState("End state");

    /**
     *
     */
    private static final long serialVersionUID = -3716915968428615126L;
    private String name;
    private Integer uniqueHash;
    private List<AppGuiComponent> components;
    //	private String formattedXml;
    private String unformattedXml;
    private Screen screen;
    private String screenshotPath;

    private GraphDataSource dataSource;

    public GraphDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(GraphDataSource dataSource) {
        this.dataSource = dataSource;
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

    /**
     * @return the components
     */
    public List<AppGuiComponent> getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(List<AppGuiComponent> components) {
        this.components = components;
    }

    /**
     * @return the uniqueHash
     */
    public Integer getUniqueHash() {
        return uniqueHash;
    }

    /**
     * @param uniqueHash the uniqueHash to set
     */
    public void setUniqueHash(Integer uniqueHash) {
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
        GraphState other = (GraphState) obj;
        if (uniqueHash == null) {
            if (other.uniqueHash != null)
                return false;
        } else if (!uniqueHash.equals(other.uniqueHash))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "st [" + name
                //+ ", " + screen
                + ", " +  dataSource +
                "]";
    }

	/*public String getFormattedXml() {
		return formattedXml;
	}

	public void setFormattedXml(String formattedXml) {
		this.formattedXml = formattedXml;
	}*/


    public String getUnformattedXml() {
        return unformattedXml;
    }

    public void setUnformattedXml(String unformattedXml) {
        this.unformattedXml = unformattedXml;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public static GraphState buildDummyState(String nameSuffixEnd) {
        int hashCodeEnd = nameSuffixEnd.hashCode();
        String stateNameEnd = hashCodeEnd + ", " + nameSuffixEnd;

        GraphState endState = new GraphState();
        endState.setUniqueHash(hashCodeEnd);
        endState.setName(stateNameEnd);
        return endState;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }
}
