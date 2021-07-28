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
 * AbstractStrategy.java
 * 
 * Created on Aug 6, 2014, 2:39:09 PM
 */
package edu.semeru.android.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.testing.helpers.DeviceHelper;
import edu.semeru.android.testing.helpers.StepByStepEngine;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.Transition;



/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 6, 2014
 */
public abstract class AbstractStrategy {

    // private Stack<HVComponentVO> visitedStack = new Stack<HVComponentVO>();
    // private Stack<HVComponentVO> availableStack = new Stack<HVComponentVO>();
    private Stack<DynGuiComponentVO> visitedStack = new Stack<DynGuiComponentVO>();
    private Stack<Step> visitedStackSteps = new Stack<Step>();
    private Stack<DynGuiComponentVO> availableStack = new Stack<DynGuiComponentVO>();
    private Stack<Step> availableStackSteps = new Stack<Step>();
    private HashMap<String, Transition> transitions = new HashMap<String, Transition>();
    private HashMap<String, DynGuiComponentVO> components = new HashMap<String, DynGuiComponentVO>();
    private List<String> transitionsIds = new ArrayList<String>();
    private String idExecution;
    public String androidSDKPath;
    public String currentActivity = "";
    public String currentWindow = "";
    public Screen currentScreen = null;
    public int widthScreen;
    public int heightScreen;
    public App app;
    public String folderScreenshots;
    public String folderOutput;
    protected boolean crash = false;
    public boolean takeScreenshots = false;

    /**
     * @return the folderOutput
     */
    public String getFolderOutput() {
        return folderOutput;
    }

    /**
     * @param folderOutput
     *            the folderOutput to set
     */
    public void setFolderOutput(String folderOutput) {
        this.folderOutput = folderOutput;
    }

    /**
     * 
     */
    public AbstractStrategy() {
        super();
        idExecution = System.currentTimeMillis() + "";
    }

    public final void printComponents() {
        printHashMap(components, String.class);
    }

    public final void printTransitions() {
        printHashMap(transitions, String.class);
    }

    public final void printAvailableStack() {
        System.out.println(".::Available Stack::.");
        for (Step hvComponentVO : availableStackSteps) {
            System.out.println(hvComponentVO);
        }
        System.out.println();
    }

    public final void printVisitedStack() {
        System.out.println(".::Visited Stack::.");
        for (Step hvComponentVO : visitedStackSteps) {
            System.out.println(hvComponentVO);
        }
        System.out.println();
    }

    private final void printHashMap(Map<?, ?> map, Class<?> clazz) {
        System.out.println(".::Printing HashMap::.");
        for (Entry e : map.entrySet()) {
            System.out.println("Key: " + e.getKey() + " ## Value: " + e.getValue());
        }
        System.out.println();
    }

    /**
     * @return the visitedStack
     */
    public Stack<DynGuiComponentVO> getVisitedStack() {
        return visitedStack;
    }

    /**
     * @param visitedStack
     *            the visitedStack to set
     */
    public void setVisitedStack(Stack<DynGuiComponentVO> visitedStack) {
        this.visitedStack = visitedStack;
    }

    /**
     * @return the availableStack
     */
    public Stack<DynGuiComponentVO> getAvailableStack() {
        return availableStack;
    }

    /**
     * @param availableStack
     *            the availableStack to set
     */
    public void setAvailableStack(Stack<DynGuiComponentVO> availableStack) {
        this.availableStack = availableStack;
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
     * @return the transitionsIds
     */
    public List<String> getTransitionsIds() {
        return transitionsIds;
    }

    /**
     * @param transitionsIds
     *            the transitionsIds to set
     */
    public void setTransitionsIds(List<String> transitionsIds) {
        this.transitionsIds = transitionsIds;
    }

    /**
     * 
     * @param id
     */
    public void addTransitionId(String id) {
        if (!transitionsIds.contains(id)) {
            transitionsIds.add(id);
        }
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

    /**
     * 
     * @param key
     * @param vo
     */
    public void addComponent(String key, DynGuiComponentVO vo) {
        if (!components.containsKey(key)) {
            components.put(key, vo);
        }
    }

    /**
     * @return the idExecution
     */
    public String getIdExecution() {
        return idExecution;
    }

    /**
     * @param idExecution
     *            the idExecution to set
     */
    public void setIdExecution(String idExecution) {
        this.idExecution = idExecution;
    }

    public void addVisited(DynGuiComponentVO vo) {
        visitedStack.push(vo);
    }

    /**
     * @return the widthScreen
     */
    public int getWidthScreen() {
        return widthScreen;
    }

    /**
     * @param widthScreen
     *            the widthScreen to set
     */
    public void setWidthScreen(int widthScreen) {
        this.widthScreen = widthScreen;
    }

    /**
     * @return the heightScreen
     */
    public int getHeightScreen() {
        return heightScreen;
    }

    /**
     * @param heightScreen
     *            the heightScreen to set
     */
    public void setHeightScreen(int heightScreen) {
        this.heightScreen = heightScreen;
    }

    public void addAvailable(DynGuiComponentVO vo) {
        if (!availableStack.contains(vo) && !visitedStack.contains(vo)) {
            availableStack.push(vo);
        }
    }

    public void addAvailableStep(DynGuiComponentVO vo) {
        Step step = new Step();
        
        if(vo.getName().equals("MENU-BUTTON")) {
            step.setAction(StepByStepEngine.MENU_BTN);
             step.setDynGuiComponent(StepByStepEngine.getEntityFromVO(vo, false));
             if (!availableStackSteps.contains(step) && !visitedStackSteps.contains(step)) {
                 // We have already checked that is at least clickable
                 availableStackSteps.push(step);
             }
             return;
        }
        
        if (vo.isLongClickable()) {
            step.setAction(StepByStepEngine.LONG_CLICK);
            step.setDynGuiComponent(StepByStepEngine.getEntityFromVO(vo, false));
            if (!availableStackSteps.contains(step) && !visitedStackSteps.contains(step)) {
                availableStackSteps.push(step);
            }
        }
        step = new Step();
        step.setAction(StepByStepEngine.CLICK);
        step.setDynGuiComponent(StepByStepEngine.getEntityFromVO(vo, false));
//        System.out.println("Checking: " + step);
//        if(visitedStackSteps.contains(step)) {
//          System.out.println("--Step is in visited list!");
//          System.out.println("Visited Stack: ");
//          printVisitedStack();
//        }
//        if(availableStackSteps.contains(step)) {
//          System.out.println("-- Step is in available list!");
//        }
        if (!availableStackSteps.contains(step) && !visitedStackSteps.contains(step)) {
            // We have already checked that is at least clickable
            availableStackSteps.push(step);
        }
    }

    /**
     * @return the app
     */
    public App getApp() {
        return app;
    }

    /**
     * @param app
     *            the app to set
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * @return the folderScreenshots
     */
    public String getFolderScreenshots() {
        return folderScreenshots;
    }

    /**
     * @param folderScreenshots
     *            the folderScreenshots to set
     */
    public void setFolderScreenshots(String folderScreenshots) {
        this.folderScreenshots = folderScreenshots;
    }

    /**
     * @return the availableStackSteps
     */
    public Stack<Step> getAvailableStackSteps() {
        return availableStackSteps;
    }

    /**
     * @param availableStackSteps
     *            the availableStackSteps to set
     */
    public void setAvailableStackSteps(Stack<Step> availableStackSteps) {
        this.availableStackSteps = availableStackSteps;
    }

    /**
     * @return the visitedStackSteps
     */
    public Stack<Step> getVisitedStackSteps() {
        return visitedStackSteps;
    }

    /**
     * @param visitedStackSteps
     *            the visitedStackSteps to set
     */
    public void setVisitedStackSteps(Stack<Step> visitedStackSteps) {
        this.visitedStackSteps = visitedStackSteps;
    }

    /**
     * @param appPackage
     * @param mainActivity
     * @param androidSDKPath
     * @param avdPort
     * @param adbPort
     */
    public void checkForCrash(String appPackage, String mainActivity, String androidSDKPath, String avdPort,
            String adbPort) {
        // TODO Auto-generated method stub
        
    }

}

