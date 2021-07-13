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
 * DataPrint.java
 * 
 * Created on Aug 10, 2014, 2:40:56 PM
 */
package edu.semeru.android.core.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 10, 2014
 */
public class DataPrint {

    private long executionTime;
    private Date finished;
    private HashMap<String, Transition> transitions;
    private HashMap<String, DynGuiComponentVO> components;

    /**
     * @param executionTime
     * @param transitions
     * @param components
     */
    public DataPrint(long executionTime, HashMap<String, Transition> transitions,
	    HashMap<String, DynGuiComponentVO> components) {
	super();
	this.executionTime = executionTime;
	this.transitions = transitions;
	this.components = components;
	this.finished = Calendar.getInstance().getTime();
    }

    /**
     * @return the executionTime
     */
    public long getExecutionTime() {
	return executionTime;
    }

    /**
     * @param executionTime
     *            the executionTime to set
     */
    public void setExecutionTime(long executionTime) {
	this.executionTime = executionTime;
    }

    /**
     * @return the transitions
     */
    public HashMap<String, Transition> getTransitions() {
	return transitions;
    }

    /**
     * @param transitions
     *            the transitions to set
     */
    public void setTransitions(HashMap<String, Transition> transitions) {
	this.transitions = transitions;
    }

    /**
     * @return the components
     */
    public HashMap<String, DynGuiComponentVO> getComponents() {
	return components;
    }

    /**
     * @param components
     *            the components to set
     */
    public void setComponents(HashMap<String, DynGuiComponentVO> components) {
	this.components = components;
    }

    public static void main(String[] args) {
	long executionTime = 1;
	HashMap<String, Transition> transitions = new HashMap<String, Transition>();
	HashMap<String, DynGuiComponentVO> components = new HashMap<String, DynGuiComponentVO>();

	DataPrint d = new DataPrint(executionTime, transitions, components);

	Gson gson = new Gson();
	String json = gson.toJson(d);
	System.out.println(json);
    }

    /**
     * @return the finished
     */
    public Date getFinished() {
	return finished;
    }

    /**
     * @param finished
     *            the finished to set
     */
    public void setFinished(Date finished) {
	this.finished = finished;
    }
}
