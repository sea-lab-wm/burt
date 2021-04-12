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
 * HVComponentVO.java
 * 
 * Created on Jul 27, 2014, 6:17:14 PM
 */
package edu.semeru.android.core.model;

import java.util.List;

import com.android.hierarchyviewerlib.models.ViewNode.Property;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Jul 27, 2014
 */
public class HVComponentVO {

    private Long idDb;
    private String id;
    private int index;
    private String name;
    private double drawTime;
    private int positionX;
    private int positionY;
    private int height;
    private int width;
    private String text;
    private String visibility;
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

    private String currentWindow;
    private List<Property> properties;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"HVComponentVO\" :{\"id\":\"" + id + "\", \"index\":\"" + index + "\", \"name\":\"" + name
                + "\", \"drawTime\":\"" + drawTime + "\", \"positionX\":\"" + positionX + "\", \"positionY\":\""
                + positionY + "\", \"height\":\"" + height + "\", \"width\":\"" + width + "\", \"text\":\"" + text
                + "\", \"visibility\":\"" + visibility + "\", \"checkable\":\"" + checkable + "\", \"checked\":\""
                + checked + "\", \"clickable\":\"" + clickable + "\", \"enabled\":\"" + enabled
                + "\", \"focusable\":\"" + focusable + "\", \"focused\":\"" + focused + "\", \"longClickable\":\""
                + longClickable + "\", \"scrollable\":\"" + scrollable + "\", \"selected\":\"" + selected
                + "\", \"password\":\"" + password + "\", \"currentWindow\":\"" + currentWindow
                + "\", \"properties\":\"" + properties + "\"}}";
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
     * @return the clickable
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * @param clickable
     *            the clickable to set
     */
    public void setClickable(boolean isClickable) {
        this.clickable = isClickable;
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
        result = prime * result + height;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + index;
        result = prime * result + (clickable ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + positionX;
        result = prime * result + positionY;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HVComponentVO other = (HVComponentVO) obj;
        if (height != other.height)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (index != other.index)
            return false;
        if (clickable != other.clickable)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (positionX != other.positionX)
            return false;
        if (positionY != other.positionY)
            return false;
        if (width != other.width)
            return false;
        return true;
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
     * @return the idDb
     */
    public Long getIdDb() {
        return idDb;
    }

    /**
     * @param idDb
     *            the idDb to set
     */
    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

}
