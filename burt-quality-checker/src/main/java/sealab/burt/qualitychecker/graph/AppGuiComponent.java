package sealab.burt.qualitychecker.graph;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class AppGuiComponent implements Serializable {

    private static final long serialVersionUID = 7411423752969634668L;

    private Long dbId;
    private String type;
    private String namePackage;
    private String activity;
    private Boolean checkable;
    private Boolean checked;
    private Boolean clickable;
    private Boolean longClickable;
    private Boolean enabled;
    private Boolean focusable;
    private Boolean focused;
    private Boolean password;
    private Boolean scrollable;
    private Boolean selected;
    private Integer index;
    private Integer totalIndex;
    private String contentDescription;
    private Integer height;
    private Integer width;
    private String idXml;
    private Integer x;
    private Integer y;
    private String relativeLocation;
    private String text;
    private String currentWindow;
    private Long screenId;

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }


    @Expose(serialize = false)
    private AppGuiComponent parent;
    @Expose(serialize = false)
    private List<AppGuiComponent> children = new ArrayList<>();
    
    private List<String> phrases;

    public AppGuiComponent() {
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the namePackage
     */
    public String getNamePackage() {
        return namePackage;
    }

    /**
     * @param namePackage the namePackage to set
     */
    public void setNamePackage(String namePackage) {
        this.namePackage = namePackage;
    }

    /**
     * @return the activity
     */
    public String getActivity() {
        return activity;
    }

    /**
     * @param activity the activity to set
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * @return the checkable
     */
    public Boolean getCheckable() {
        return checkable;
    }

    /**
     * @param checkable the checkable to set
     */
    public void setCheckable(Boolean checkable) {
        this.checkable = checkable;
    }

    /**
     * @return the checked
     */
    public Boolean getChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the clickable
     */
    public Boolean getClickable() {
        return clickable;
    }

    /**
     * @param clickable the clickable to set
     */
    public void setClickable(Boolean clickable) {
        this.clickable = clickable;
    }

    /**
     * @return the longClickable
     */
    public Boolean getLongClickable() {
        return longClickable;
    }

    /**
     * @param longClickable the longClickable to set
     */
    public void setLongClickable(Boolean longClickable) {
        this.longClickable = longClickable;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the focusable
     */
    public Boolean getFocusable() {
        return focusable;
    }

    /**
     * @param focusable the focusable to set
     */
    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    /**
     * @return the focused
     */
    public Boolean getFocused() {
        return focused;
    }

    /**
     * @param focused the focused to set
     */
    public void setFocused(Boolean focused) {
        this.focused = focused;
    }

    /**
     * @return the password
     */
    public Boolean getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(Boolean password) {
        this.password = password;
    }

    /**
     * @return the scrollable
     */
    public Boolean getScrollable() {
        return scrollable;
    }

    /**
     * @param scrollable the scrollable to set
     */
    public void setScrollable(Boolean scrollable) {
        this.scrollable = scrollable;
    }

    /**
     * @return the selected
     */
    public Boolean getSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * @return the totalIndex
     */
    public Integer getTotalIndex() {
        return totalIndex;
    }

    /**
     * @param totalIndex the totalIndex to set
     */
    public void setTotalIndex(Integer totalIndex) {
        this.totalIndex = totalIndex;
    }

    /**
     * @return the contentDescription
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * @param contentDescription the contentDescription to set
     */
    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the idXml
     */
    public String getIdXml() {
        return idXml;
    }

    /**
     * @param idXml the idXml to set
     */
    public void setIdXml(String idXml) {
        this.idXml = idXml;
    }

    /**
     * @return the x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * @return the relativeLocation
     */
    public String getRelativeLocation() {
        return relativeLocation;
    }

    /**
     * @param relativeLocation the relativeLocation to set
     */
    public void setRelativeLocation(String relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the parent
     */
    public AppGuiComponent getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(AppGuiComponent parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    public List<AppGuiComponent> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<AppGuiComponent> children) {
        this.children = children;
    }

    public String getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(String currentWindow) {
        this.currentWindow = currentWindow;
    }

    public String getUniqueString() {
        return getComponentValues().toString();
    }

    @Override
    public String toString() {
        final List<String> componentValues = getComponentValues();
        componentValues.add(0, (this.dbId != null ? this.dbId.toString() : ""));
        componentValues.add(getContentDescriptionString());
        return componentValues.toString();
    }

    private String getContentDescriptionString() {
        if (this.contentDescription == null)
            return "";

        String value = this.contentDescription;
        final int LIMIT = 30;
        if (this.contentDescription.length() > LIMIT) {
            value = this.contentDescription.substring(0, LIMIT) +"...";
        }

        return "dsc=" + value;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }


    @Override
    public int hashCode() {
        return getComponentValues().hashCode();
    }

    /*
     * (non-Javadoc)
     *
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
        AppGuiComponent component = (AppGuiComponent) obj;
        return getComponentValues().equals(getComponentValues(component));
    }

   /* public boolean equalsNoWidth(AppGuiComponent component) {
        return GeneralUtils.equalsNoWidth(this, component);
    }

    public boolean equalsNoDimensions(AppGuiComponent component) {
        return GeneralUtils.equalsNoDimensions(this, component);
    }*/

    //-----------

    private List<String> getComponentValues() {
        return getComponentValues(this);
    }

    private List<String> getComponentValues(AppGuiComponent component) {
        return new LinkedList<>(Arrays.asList(
                "ty=" + getStringType(getStringValue(component.getType())),
                "idx=" + getLastPartOfIdXml(getStringValue(component.getIdXml())),
                "idnx=" + getStringValue(component.getIndex()),
                "tx=" + getStringText(getStringValue(component.getType()), component.getText()),
                "x=" + getStringValue(component.getX()),
                "y=" + getStringValue(component.getY()),
                "h=" + getStringValue(component.getHeight()),
                "w=" + getStringValue(component.getWidth())
        ));
    }

    public List<String> getComponentValuesNoDimensions(AppGuiComponent component) {
        return new LinkedList<>(Arrays.asList(
                getStringType(component.getType()),
                getLastPartOfIdXml(component.getIdXml()),
                getStringValue(component.getIndex()),
                getStringText(component.getType(), component.getText())
        ));
    }

    private String getStringValue(String i) {
        return i == null ? "" : i;
    }

    private String getStringValue(Integer i) {
        return i == null ? "" : i.toString();
    }

    //----------------------

    private String getStringType(String type2) {
        String[] tokens = type2.split("\\.");
        return tokens[tokens.length - 1];
    }

    public String getLastPartOfIdXml() {
        return getLastPartOfIdXml(this.idXml);
    }

    public String getLastPartOfIdXml(String idXml2) {
        if (idXml2 == null) {
            return "";
        }
        String[] tokens = idXml2.split("/");
        return tokens[tokens.length - 1];
    }

    private String getStringText(String type2, String text2) {
        if (type2.endsWith("EditText")) {
            return "";
        }
        return text2;
    }

    public List<String> getPhrases(){
       return this.phrases;
    }

    public void setPhrases(List<String> phrases){
        this.phrases = phrases;
    }



    //----------------------


    //------------------------------------

   /* public boolean equals(AppGuiComponent component1, AppGuiComponent component2) {
        return equalsNoWidth(component1, component2) && component1.getWidth().equals(component2.getWidth());
    }

    public boolean equalsNoWidth(AppGuiComponent component1, AppGuiComponent component2) {
        return component1.getX().equals(component2.getX())
                && component1.getY().equals(component2.getY())
                && component1.getHeight().equals(component2.getHeight())
                && component1.getType().equals(component2.getType())
                && ((component1.getIdXml() == null ? "" : component1.getIdXml()).equals(
                component2.getIdXml() == null ? "" : component2.getIdXml()))
                && component1.getIndex().equals(component2.getIndex());
    }*/

    public boolean equalsNoDimensions(AppGuiComponent component2) {
        return getComponentValuesNoDimensions(this).equals(getComponentValuesNoDimensions(component2));
    }


}
