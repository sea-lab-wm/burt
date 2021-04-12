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
 * Transition.java
 * 
 * Created on Aug 7, 2014, 3:09:34 PM
 */
package edu.semeru.android.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 7, 2014
 */
public class Transition {

    private String sourceWindow;
    private String targetWindow;
    private String sourceActivity;
    private String targetActivity;
    private DynGuiComponentVO component;
    private List<DynGuiComponentVO> sourceScreen = new ArrayList<DynGuiComponentVO>();
    private List<DynGuiComponentVO> targetScreen = new ArrayList<DynGuiComponentVO>();
    private int action;

    /**
     * 
     */
    public Transition() {
	super();
    }

    /**
     * @param sourceWindow
     * @param targetWindow
     * @param sourceActivity
     * @param targetActivity
     * @param component
     */
    public Transition(String sourceWindow, String targetWindow, String sourceActivity, String targetActivity,
	    DynGuiComponentVO component) {
	super();
	this.sourceWindow = sourceWindow;
	this.targetWindow = targetWindow;
	this.sourceActivity = sourceActivity;
	this.targetActivity = targetActivity;
	this.component = component;
    }

    /**
     * @return the sourceWindow
     */
    public String getSourceWindow() {
	return sourceWindow;
    }

    /**
     * @param sourceWindow
     *            the sourceWindow to set
     */
    public void setSourceWindow(String sourceWindow) {
	this.sourceWindow = sourceWindow;
    }

    /**
     * @return the targetWindow
     */
    public String getTargetWindow() {
	return targetWindow;
    }

    /**
     * @param targetWindow
     *            the targetWindow to set
     */
    public void setTargetWindow(String targetWindow) {
	this.targetWindow = targetWindow;
    }

    /**
     * @return the sourceActivity
     */
    public String getSourceActivity() {
	return sourceActivity;
    }

    /**
     * @param sourceActivity
     *            the sourceActivity to set
     */
    public void setSourceActivity(String sourceActivity) {
	this.sourceActivity = sourceActivity;
    }

    /**
     * @return the targetActivity
     */
    public String getTargetActivity() {
	return targetActivity;
    }

    /**
     * @param targetActivity
     *            the targetActivity to set
     */
    public void setTargetActivity(String targetActivity) {
	this.targetActivity = targetActivity;
    }

    /**
     * @return the component
     */
    public DynGuiComponentVO getComponent() {
	return component;
    }

    /**
     * @param component
     *            the component to set
     */
    public void setComponent(DynGuiComponentVO component) {
	this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Transition [sourceWindow=" + sourceWindow + ", targetWindow=" + targetWindow + ", sourceActivity="
		+ sourceActivity + ", targetActivity=" + targetActivity + ", component=" + component + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((component == null) ? 0 : component.hashCode());
	result = prime * result + ((sourceActivity == null) ? 0 : sourceActivity.hashCode());
	result = prime * result + ((sourceWindow == null) ? 0 : sourceWindow.hashCode());
	result = prime * result + ((targetActivity == null) ? 0 : targetActivity.hashCode());
	result = prime * result + ((targetWindow == null) ? 0 : targetWindow.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
        return true;
    }
	if (obj == null) {
        return false;
    }
	if (getClass() != obj.getClass()) {
        return false;
    }
	Transition other = (Transition) obj;
	if (component == null) {
	    if (other.component != null) {
            return false;
        }
	} else if (!component.equals(other.component)) {
        return false;
    }
	if (sourceActivity == null) {
	    if (other.sourceActivity != null) {
            return false;
        }
	} else if (!sourceActivity.equals(other.sourceActivity)) {
        return false;
    }
	if (sourceWindow == null) {
	    if (other.sourceWindow != null) {
            return false;
        }
	} else if (!sourceWindow.equals(other.sourceWindow)) {
        return false;
    }
	if (targetActivity == null) {
	    if (other.targetActivity != null) {
            return false;
        }
	} else if (!targetActivity.equals(other.targetActivity)) {
        return false;
    }
	if (targetWindow == null) {
	    if (other.targetWindow != null) {
            return false;
        }
	} else if (!targetWindow.equals(other.targetWindow)) {
        return false;
    }
	return true;
    }

    /**
     * @return the action
     */
    public int getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * @return the sourceScreen
     */
    public List<DynGuiComponentVO> getSourceScreen() {
        return sourceScreen;
    }

    /**
     * @param sourceScreen the sourceScreen to set
     */
    public void setSourceScreen(List<DynGuiComponentVO> sourceScreen) {
        this.sourceScreen = sourceScreen;
    }

    /**
     * @return the targetsourceScreen
     */
    public List<DynGuiComponentVO> getTargetScreen() {
        return targetScreen;
    }

    /**
     * @param targetsourceScreen the targetsourceScreen to set
     */
    public void setTargetScreen(List<DynGuiComponentVO> targetsourceScreen) {
        this.targetScreen = targetsourceScreen;
    }

}
