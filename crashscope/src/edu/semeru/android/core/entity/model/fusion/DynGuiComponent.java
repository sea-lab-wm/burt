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
package edu.semeru.android.core.entity.model.fusion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the DynGuiComponent database table.
 * 
 */
@Entity
@Table(name = "DYN_GUI_COMPONENT")
@NamedQuery(name = "DynGuiComponent.findAll", query = "SELECT c FROM DynGuiComponent c")
public class DynGuiComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ACTIVITY")
    private String activity;
    @Column(name = "NAME")
    private String name;
    @Lob
    @Column(name = "TEXT")
    private String text;
    @Lob
    @Column(name = "CONTENT_DESCRIPTION")
    private String contentDescription;
    @Column(name = "ID_XML")
    private String idXml;
    @Column(name = "COMPONENT_INDEX")
    private int componentIndex;
    @Column(name = "COMPONENT_TOTAL_INDEX")
    // if itemList is false this will be 0
    private int componentTotalIndex;
    @Column(name = "CURRENT_WINDOW")
    private String currentWindow;
    @Column(name = "TITLE_WINDOW")
    private String titleWindow;
    @Column(name = "POSITION_X")
    private int positionX;
    @Column(name = "POSITION_Y")
    private int positionY;
    private int height;
    private int width;
    private boolean checkable;
    private boolean checked;
    private boolean clickable;
    private boolean enabled;
    private boolean focusable;
    private boolean focused;
    @Column(name = "LONG_CLICKABLE")
    private boolean longClickable;
    private boolean scrollable;
    private boolean selected;
    private boolean password;
    @Column(name = "ITEM_LIST")
    private boolean itemList;
    @Column(name = "CALENDAR_WINDOW")
    private boolean calendarWindow;
    @Column(name = "RELATIVE_LOCATION")
    private String relativeLocation;
    @Column(name = "GUI_SCREENSHOT")
    private String guiScreenshot;
    @ManyToOne
    @JoinColumn(name = "ID_PARENT")
    private transient DynGuiComponent parent;
    // bi-directional many-to-one association to Step
    // @OneToMany(mappedBy = "dynGuiComponent")
    // private List<Step> steps = new ArrayList<Step>();

     // bi-directional many-to-one association to DynamicTransition
     @ManyToOne
     @JoinColumn(name = "Screen")
     private transient Screen screen;

	@Transient
    private String idText;
    @Transient
    private int offset;
    @Transient
    private String visibility;
    @Transient
    private List<Property> properties;
    @Transient
    private double drawTime;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private transient List<DynGuiComponent> children = new ArrayList<DynGuiComponent>();
   

    public DynGuiComponent() {
    }

    /**
     * @param id
     * @param positionX
     * @param positionY
     * @param height
     * @param width
     */
    public DynGuiComponent(String id, int positionX, int positionY, int width, int height, String name) {
        super();
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

    /**
     * @param id
     * @param positionX
     * @param positionY
     * @param height
     * @param width
     */
    public DynGuiComponent(String id, int positionX, int positionY, int width, int height, String name,
            String relativeLocation) {
        super();
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
     * @return the componentText
     */
    public String getText() {
        return text;
    }

    /**
     * @param componentText
     *            the componentText to set
     */
    public void setText(String componentText) {
        this.text = componentText;
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
     * @return the steps
     */
    // public List<Step> getSteps() {
    // return steps;
    // }

    /**
     * @param steps
     *            the steps to set
     */
    // public void setSteps(List<Step> steps) {
    // this.steps = steps;
    // }

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

    public static class Property {
        public String name;

        public String value;

        /**
         * 
         */
        public Property() {
            super();
        }

        /**
         * @param name
         * @param value
         */
        public Property(String name, String value) {
            super();
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + '=' + value;
        }
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
        DynGuiComponent other = (DynGuiComponent) obj;
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
        String windowThis = null;
        String windowOther = null;
        if(currentWindow.contains("->")) {
        	windowThis = currentWindow.substring(currentWindow.indexOf("->")+2);
        }else {
        	windowThis = currentWindow;
        }
        if(other.currentWindow.contains("->")) {
        	windowOther = other.currentWindow.substring(other.currentWindow.indexOf("->")+2);
        }else {
        	windowOther = other.currentWindow;
        }
        //System.out.println("COMPARING: " + windowThis + " to " + windowOther);
        if(windowThis == null) {
        		if(windowOther != null) {
        			return false;
        		}
        }else if (!windowThis.equals(windowOther)) {
        	//System.out.println("WINDOWS DO NOT MATCH!!!!");
            return false;
        }
        return true;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
//    @Override
//    public String toString() {
//        return "{\"DynGuiComponent\" :{\"hash\":\"" + hashCode() + "\",\"id\":\"" + id + "\", \"activity\":\""
//                + activity + "\", \"name\":\"" + name + "\", \"text\":\"" + text + "\", \"idXml\":\"" + idXml
//                + "\", \"componentIndex\":\"" + componentIndex + "\", \"contentDescription\":\"" + contentDescription
//                + "\", \"componentTotalIndex\":\"" + componentTotalIndex + "\", \"currentWindow\":\"" + currentWindow
//                + "\", \"positionX\":\"" + positionX + "\", \"positionY\":\"" + positionY + "\", \"height\":\""
//                + height + "\", \"width\":\"" + width + "\", \"checkable\":\"" + checkable + "\", \"checked\":\""
//                + checked + "\", \"clickable\":\"" + clickable + "\", \"enabled\":\"" + enabled
//                + "\", \"focusable\":\"" + focusable + "\", \"itemList\":\"" + itemList + "\", \"focused\":\""
//                + focused + "\", \"longClickable\":\"" + longClickable + "\", \"scrollable\":\"" + scrollable
//                + "\", \"selected\":\"" + selected + "\", \"password\":\"" + password + "\", \"idText\":\"" + idText
//                + "\", \"visibility\":\"" + visibility + "\", \"properties\":\"" + properties
//                + "\", \"titleWindow\":\"" + titleWindow + "\", \"drawTime\":\"" + drawTime + "\"}}";
//    }


    @Override
    public String toString() {
        return "comp{" +
                "id=" + id +
                ", n='" + name + '\'' +
                ", t='" + text + '\'' +
                ", d='" + contentDescription + '\'' +
                ", ix='" + idXml + '\'' +
                '}';
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
     * @return the children
     */
    public List<DynGuiComponent> getChildren() {
        return children;
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(List<DynGuiComponent> children) {
        this.children = children;
    }

    /**
     * 
     * @param child
     *            the child to add
     */
    public void addChild(DynGuiComponent child) {
        children.add(child);
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
     * @return the parent
     */
    public DynGuiComponent getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(DynGuiComponent parent) {
        this.parent = parent;
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
	 * @return the screen
	 */
	public Screen getScreen() {
		return screen;
	}

	/**
	 * @param screen the screen to set
	 */
	public void setScreen(Screen screen) {
		this.screen = screen;
	}
}