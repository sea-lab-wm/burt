/*******************************************************************************
 * Copyright (c) 2016, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
/**
 * DynGuiComponentVO.java
 * 
 * Created on Aug 18, 2015, 2:32:09 AM
 * 
 */
package edu.semeru.android.core.model;

import java.util.ArrayList;
import java.util.List;

import edu.semeru.android.core.entity.model.fusion.DynGuiComponent.Property;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 18, 2015
 */
public class DynGuiComponentVO {

    private Long id;
    private String activity;
    private String name;
    private String text;
    private String contentDescription;
    private String idXml;
    private int componentIndex;
    private int componentTotalIndex;
    private String currentWindow;
    private String titleWindow;
    private int positionX;
    private int positionY;
    private int height;
    private int width;
    private boolean checkable;
    private boolean checked;
    private boolean clickable;
    private boolean enabled;
    private boolean focusable;
    private boolean focused;
    private boolean longClickable;
    private boolean scrollable;
    private boolean selected;
    private boolean password;
    private boolean itemList;
    private boolean calendarWindow;
    private String relativeLocation;
    private String guiScreenshot;
    private String packageName;

    private String idText;
    private int offset;
    private String visibility;
    private List<Property> properties;
    private double drawTime;
    private List<DynGuiComponentVO> children = new ArrayList<DynGuiComponentVO>();
    private DynGuiComponentVO parent;
    private int sequenceHierarchy;

    public DynGuiComponentVO(String id, int positionX, int positionY, int width, int height, String name) {
        this.idXml = id;
        this.idText = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.height = height;
        this.width = width;
        this.clickable = true;
        this.name = name;
        this.text = "";
    }

    public DynGuiComponentVO(String id, int positionX, int positionY, int width, int height, String name,
            String relativeLocation) {
        this.idXml = id;
        this.idText = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.height = height;
        this.width = width;
        this.clickable = true;
        this.name = name;
        this.text = "";
        this.relativeLocation = relativeLocation;
    }

    /**
     * 
     */
    public DynGuiComponentVO() {
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the activity
     */
    public String getActivity() {
        return activity;
    }

    /**
     * @param activity
     *            the activity to set
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the contentDescription
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * @param contentDescription
     *            the contentDescription to set
     */
    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    /**
     * @return the idXml
     */
    public String getIdXml() {
        return idXml;
    }

    /**
     * @param idXml
     *            the idXml to set
     */
    public void setIdXml(String idXml) {
        this.idXml = idXml;
    }

    /**
     * @return the componentIndex
     */
    public int getComponentIndex() {
        return componentIndex;
    }

    /**
     * @param componentIndex
     *            the componentIndex to set
     */
    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }

    /**
     * @return the componentTotalIndex
     */
    public int getComponentTotalIndex() {
        return componentTotalIndex;
    }

    /**
     * @param componentTotalIndex
     *            the componentTotalIndex to set
     */
    public void setComponentTotalIndex(int componentTotalIndex) {
        this.componentTotalIndex = componentTotalIndex;
    }

    /**
     * @return the currentWindow
     */
    public String getCurrentWindow() {
        return currentWindow;
    }

    /**
     * @param currentWindow
     *            the currentWindow to set
     */
    public void setCurrentWindow(String currentWindow) {
        this.currentWindow = currentWindow;
    }

    /**
     * @return the titleWindow
     */
    public String getTitleWindow() {
        return titleWindow;
    }

    /**
     * @param titleWindow
     *            the titleWindow to set
     */
    public void setTitleWindow(String titleWindow) {
        this.titleWindow = titleWindow;
    }

    /**
     * @return the positionX
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * @param positionX
     *            the positionX to set
     */
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * @return the positionY
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * @param positionY
     *            the positionY to set
     */
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the checkable
     */
    public boolean isCheckable() {
        return checkable;
    }

    /**
     * @param checkable
     *            the checkable to set
     */
    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked
     *            the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the clickable
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * @param clickable
     *            the clickable to set
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the focusable
     */
    public boolean isFocusable() {
        return focusable;
    }

    /**
     * @param focusable
     *            the focusable to set
     */
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    /**
     * @return the focused
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * @param focused
     *            the focused to set
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * @return the longClickable
     */
    public boolean isLongClickable() {
        return longClickable;
    }

    /**
     * @param longClickable
     *            the longClickable to set
     */
    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    /**
     * @return the scrollable
     */
    public boolean isScrollable() {
        return scrollable;
    }

    /**
     * @param scrollable
     *            the scrollable to set
     */
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the password
     */
    public boolean isPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(boolean password) {
        this.password = password;
    }

    /**
     * @return the itemList
     */
    public boolean isItemList() {
        return itemList;
    }

    /**
     * @param itemList
     *            the itemList to set
     */
    public void setItemList(boolean itemList) {
        this.itemList = itemList;
    }

    /**
     * @return the calendarWindow
     */
    public boolean isCalendarWindow() {
        return calendarWindow;
    }

    /**
     * @param calendarWindow
     *            the calendarWindow to set
     */
    public void setCalendarWindow(boolean calendarWindow) {
        this.calendarWindow = calendarWindow;
    }

    /**
     * @return the relativeLocation
     */
    public String getRelativeLocation() {
        return relativeLocation;
    }

    /**
     * @param relativeLocation
     *            the relativeLocation to set
     */
    public void setRelativeLocation(String relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    /**
     * @return the guiScreenshot
     */
    public String getGuiScreenshot() {
        return guiScreenshot;
    }

    /**
     * @param guiScreenshot
     *            the guiScreenshot to set
     */
    public void setGuiScreenshot(String guiScreenshot) {
        this.guiScreenshot = guiScreenshot;
    }

    /**
     * @return the idText
     */
    public String getIdText() {
        return idText;
    }

    /**
     * @param idText
     *            the idText to set
     */
    public void setIdText(String idText) {
        this.idText = idText;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset
     *            the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return the visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * @param visibility
     *            the visibility to set
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * @return the drawTime
     */
    public double getDrawTime() {
        return drawTime;
    }

    /**
     * @param drawTime
     *            the drawTime to set
     */
    public void setDrawTime(double drawTime) {
        this.drawTime = drawTime;
    }

    /**
     * @return the children
     */
    public List<DynGuiComponentVO> getChildren() {
        return children;
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(List<DynGuiComponentVO> children) {
        this.children = children;
    }

    /**
     * @return the parent
     */
    public DynGuiComponentVO getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(DynGuiComponentVO parent) {
        this.parent = parent;
    }

    /**
     * @param component
     */
    public void addChild(DynGuiComponentVO component) {
        children.add(component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activity == null) ? 1 : activity.hashCode());
        result = prime * result + (checkable ? 1231 : 1237);
        result = prime * result + (checked ? 1231 : 1237);
        result = prime * result + (clickable ? 1231 : 1237);
        result = prime * result + componentIndex;
        result = prime * result + ((currentWindow == null) ? 1 : currentWindow.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + (focusable ? 1231 : 1237);
        // result = prime * result + (focused ? 1231 : 1237);
        result = prime * result + height;
        result = prime * result + ((idXml == null) ? 1 : idXml.hashCode());
        result = prime * result + (longClickable ? 1231 : 1237);
        result = prime * result + ((name == null) ? 1 : name.hashCode());
        result = prime * result + (password ? 1231 : 1237);
        result = prime * result + positionX;
        result = prime * result + positionY;
        result = prime * result + (scrollable ? 1231 : 1237);
        result = prime * result + (selected ? 1231 : 1237);
        // result = prime * result + ((text == null) ? 1 : text.hashCode());
        result = prime * result + ((visibility == null) ? 1 : visibility.hashCode());
        result = prime * result + width;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        /*
         * if (this == obj) { return true; } if (obj == null) { return false; }
         * if (getClass() != obj.getClass()) { return false; } DynGuiComponent
         * other = (DynGuiComponent) obj; if (activity == null) { if
         * (other.activity != null) { return false; } } else if
         * (!activity.equals(other.activity)) { return false; } if (checkable !=
         * other.checkable) { return false; } if (checked != other.checked) {
         * return false; } if (clickable != other.clickable) { return false; }
         * if (componentClass == null) { if (other.componentClass != null) {
         * return false; } } else if
         * (!componentClass.equals(other.componentClass)) { return false; } if
         * (currentWindow == null) { if (other.currentWindow != null) { return
         * false; } } else if (!currentWindow.equals(other.currentWindow)) {
         * return false; } if (enabled != other.enabled) { return false; } if
         * (focusable != other.focusable) { return false; } if (focused !=
         * other.focused) { return false; } if (height != other.height) { return
         * false; } if (idXml == null) { if (other.idXml != null) { return
         * false; } } else if (!idXml.equals(other.idXml)) { return false; } if
         * (longClickable != other.longClickable) { return false; } if (name ==
         * null) { if (other.name != null) { return false; } } else if
         * (!name.equals(other.name)) { return false; } if (password !=
         * other.password) { return false; } if (positionX != other.positionX) {
         * return false; } if (positionY != other.positionY) { return false; }
         * if (scrollable != other.scrollable) { return false; } if (selected !=
         * other.selected) { return false; } if (text == null) { if (other.text
         * != null) { return false; } } else if (!text.equals(other.text)) {
         * return false; } if (visibility == null) { if (other.visibility !=
         * null) { return false; } } else if
         * (!visibility.equals(other.visibility)) { return false; } if (width !=
         * other.width) { return false; } return true;
         */
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DynGuiComponentVO other = (DynGuiComponentVO) obj;
        if (height != other.height) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (componentIndex != other.componentIndex) {
            return false;
        }
        if (clickable != other.clickable) {
            return false;
        }
        if (longClickable != other.longClickable) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (positionX != other.positionX) {
            return false;
        }
        if (positionY != other.positionY) {
            return false;
        }
        if (width != other.width) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"DynGuiComponentVO\" :{\"id\":\"" + id + "\", \"activity\":\"" + activity + "\", \"parent\":\""
                + ((parent == null) ? parent : parent.sequenceHierarchy) + "\", \"sequenceHierarchy\":\""
                + sequenceHierarchy + "\", \"name\":\"" + name + "\", \"text\":\"" + text
                + "\", \"contentDescription\":\"" + contentDescription + "\", \"idXml\":\"" + idXml
                + "\", \"componentIndex\":\"" + componentIndex + "\", \"componentTotalIndex\":\"" + componentTotalIndex
                + "\", \"currentWindow\":\"" + currentWindow + "\", \"titleWindow\":\"" + titleWindow
                + "\", \"positionX\":\"" + positionX + "\", \"positionY\":\"" + positionY + "\", \"height\":\"" + height
                + "\", \"width\":\"" + width + "\", \"checkable\":\"" + checkable + "\", \"checked\":\"" + checked
                + "\", \"clickable\":\"" + clickable + "\", \"enabled\":\"" + enabled + "\", \"focusable\":\""
                + focusable + "\", \"focused\":\"" + focused + "\", \"longClickable\":\"" + longClickable
                + "\", \"scrollable\":\"" + scrollable + "\", \"selected\":\"" + selected + "\", \"password\":\""
                + password + "\", \"itemList\":\"" + itemList + "\", \"calendarWindow\":\"" + calendarWindow
                + "\", \"relativeLocation\":\"" + relativeLocation + "\", \"guiScreenshot\":\"" + guiScreenshot
                + "\", \"idText\":\"" + idText + "\", \"offset\":\"" + offset + "\", \"visibility\":\"" + visibility
                + "\", \"drawTime\":\"" + drawTime + "\"}}";
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName
     *            the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the sequenceHierarchy
     */
    public int getSequenceHierarchy() {
        return sequenceHierarchy;
    }

    /**
     * @param sequenceHierarchy
     *            the sequenceHierarchy to set
     */
    public void setSequenceHierarchy(int sequenceHierarchy) {
        this.sequenceHierarchy = sequenceHierarchy;
    }

}
