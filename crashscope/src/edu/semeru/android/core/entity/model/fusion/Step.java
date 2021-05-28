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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Steps database table.
 * 
 */
@Entity
@Table(name = "STEP")
@NamedQuery(name = "Step.findAll", query = "SELECT s FROM Step s")
public class Step implements Serializable, Comparable<Step> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACTION")
    private int action;

    @Column(name = "SEQUENCE_STEP")
    private int sequenceStep;

    @Column(name = "SCREENSHOT")
    private String screenshot;

    @Lob
    @Column(name = "TEXT_ENTRY")
    private String textEntry;
    
    @Lob
    @Column(name = "EXCEPTIONS")
    private String exceptions;

    @Column(name = "AREA_EDIT")
    private int areaEdit;
    @Column(name = "HASH_STEP")
    private String hashStep;
    @Column(name = "AREA_VIEW")
    private int areaView;
    @Column(name = "AREA_LIST")
    private int areaList;
    @Column(name = "AREA_SELECT")
    private int areaSelect;
    @Column(name = "INITIAL_X")
    private int initialX;
    @Column(name = "INITIAL_Y")
    private int initialY;
    @Column(name = "FINAL_X")
    private int finalX;
    @Column(name = "FINAL_Y")
    private int finalY;
    @Column(name = "USE_CASE_TRAN_TYPE")
    private int useCaseTranType;
    @Column(name = "NETWORK")
    private boolean network;
    @Column(name = "ACCELEROMETER")
    private boolean acellerometer;
    @Column(name = "MAGNETOMETER")
    private boolean magentometer;
    @Column(name = "TEMPERATURE")
    private boolean temperature;
    @Column(name = "GPS")
    private boolean gps;

    // bi-directional many-to-one association to DynGuiComponent
    @ManyToOne
    @JoinColumn(name = "ID_DYN_GUI_COMPONENT")
    private DynGuiComponent dynGuiComponent;

    // bi-directional many-to-one association to Execution
    @ManyToOne
    @JoinColumn(name = "ID_EXECUTION")
    private transient Execution execution;

    // bi-directional many-to-one association to Execution
    @ManyToOne
    @JoinColumn(name = "ID_USE_CASE")
    private UseCase useCase;

    // bi-directional many-to-one association to Execution
    @ManyToOne
    @JoinColumn(name = "ID_TRIGGER")
    private UseCase trigger;

    // bi-directional many-to-one association to Screenshot
    // @OneToOne(mappedBy = "step")
    // private Screenshot screenshot;

    // bi-directional many-to-one association to Screenshot
    @OneToOne(mappedBy = "step")
    private DynamicTransition dynamicTransition;
    
    @OneToOne(mappedBy = "step")
    private Screen screen;

    public Step() {
    }

    
    /**
     * @return the network
     */
    public boolean getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(boolean network) {
        this.network = network;
    }

    
    
    /**
     * @return the gps
     */
    public boolean getGps() {
        return gps;
    }

    /**
     * @param gps the gps to set
     */
    public void setGps(boolean gps) {
        this.gps = gps;
    }

 

    
    
    /**
     * @return the acellerometer
     */
    public boolean isAcellerometer() {
        return acellerometer;
    }

    /**
     * @param acellerometer the acellerometer to set
     */
    public void setAcellerometer(boolean acellerometer) {
        this.acellerometer = acellerometer;
    }

    /**
     * @return the magentometer
     */
    public boolean isMagentometer() {
        return magentometer;
    }

    /**
     * @param magentometer the magentometer to set
     */
    public void setMagentometer(boolean magentometer) {
        this.magentometer = magentometer;
    }

    /**
     * @return the temperature
     */
    public boolean isTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(boolean temperature) {
        this.temperature = temperature;
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
     * @return the exceptions
     */
    public String getExceptions() {
        return exceptions;
    }

    /**
     * @param exceptions
     *            the exceptions to set
     */
    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * @return the screenshot
     */
    public String getScreenshot() {
        return screenshot;
    }

    /**
     * @param screenshot
     *            the screenshot to set
     */
    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    /**
     * @return the action
     */
    public int getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(int action) {
        this.action = action;
        this.hashStep = hashCode() + "";
    }

    /**
     * @return the sequenceStep
     */
    public int getSequenceStep() {
        return sequenceStep;
    }

    /**
     * @param sequenceStep
     *            the sequenceStep to set
     */
    public void setSequenceStep(int sequenceStep) {
        this.sequenceStep = sequenceStep;
    }

    /**
     * @return the dynGuiComponent
     */
    public DynGuiComponent getDynGuiComponent() {
        return dynGuiComponent;
    }

    /**
     * @param dynGuiComponent
     *            the dynGuiComponent to set
     */
    public void setDynGuiComponent(DynGuiComponent dynGuiComponent) {
        this.dynGuiComponent = dynGuiComponent;
        this.hashStep = hashCode() + "";
    }

    /**
     * @return the execution
     */
    public Execution getExecution() {
        return execution;
    }

    /**
     * @param execution
     *            the execution to set
     */
    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    /**
     * @return the screenshot
     */
    // public Screenshot getScreenshot() {
    // return screenshot;
    // }
    //
    // /**
    // * @param screenshot
    // * the screenshot to set
    // */
    // public void setScreenshot(Screenshot screenshot) {
    // this.screenshot = screenshot;
    // }

    /**
     * @return the dynamicTransition
     */
    public DynamicTransition getDynamicTransition() {
        return dynamicTransition;
    }

    /**
     * @param dynamicTransition
     *            the dynamicTransition to set
     */
    public void setDynamicTransition(DynamicTransition dynamicTransition) {
        this.dynamicTransition = dynamicTransition;
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
        result = prime * result + action;
        result = prime * result + ((dynGuiComponent == null) ? 0 : dynGuiComponent.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
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
        Step other = (Step) obj;
        if (action != other.action) {
            return false;
        }
        if (dynGuiComponent == null) {
            if (other.dynGuiComponent != null) {
                return false;
            }
        } else if (!dynGuiComponent.equals(other.dynGuiComponent)) {
            return false;
        }
        return true;
    }

    /**
     * @return the textEntry
     */
    public String getTextEntry() {
        return textEntry;
    }

    /**
     * @param textEntry
     *            the textEntry to set
     */
    public void setTextEntry(String textEntry) {
        this.textEntry = textEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"Step\" :{\"id\":\"" + id + "\", \"action\":\"" + action + "\", \"sequenceStep\":\"" + sequenceStep
                + "\", \"screenshot\":\"" + screenshot + "\", \"textEntry\":\"" + textEntry
                + "\", \"dynGuiComponent\":" + dynGuiComponent + "}}";
    }

    public static void main(String[] args) {
        DynGuiComponent comp = new DynGuiComponent();
        comp.setActivity("1");
        comp.setLongClickable(true);

        DynGuiComponent comp1 = new DynGuiComponent();
        comp1.setActivity("1");
        comp1.setLongClickable(true);

        Step step = new Step();
        step.setAction(1);
        step.setDynGuiComponent(comp);

        Step step1 = new Step();
        step1.setAction(2);
        step1.setDynGuiComponent(comp1);

        ArrayList<Step> list = new ArrayList<Step>();
        list.add(step);
        System.out.println(list.contains(step1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Step arg) {
        if (this.sequenceStep > arg.sequenceStep) {
            return 1;
        } else if (this.sequenceStep < arg.sequenceStep) {
            return -1;
        } else {
            if (this.sequenceStep > arg.sequenceStep) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * @return the areaEdit
     */
    public int getAreaEdit() {
        return areaEdit;
    }

    /**
     * @param areaEdit
     *            the areaEdit to set
     */
    public void setAreaEdit(int areaEdit) {
        this.areaEdit = areaEdit;
    }

    /**
     * @return the areaView
     */
    public int getAreaView() {
        return areaView;
    }

    /**
     * @param areaView
     *            the areaView to set
     */
    public void setAreaView(int areaView) {
        this.areaView = areaView;
    }

    /**
     * @return the areaList
     */
    public int getAreaList() {
        return areaList;
    }

    /**
     * @param areaList
     *            the areaList to set
     */
    public void setAreaList(int areaList) {
        this.areaList = areaList;
    }

    /**
     * @return the areaSelect
     */
    public int getAreaSelect() {
        return areaSelect;
    }

    /**
     * @param areaSelect
     *            the areaSelect to set
     */
    public void setAreaSelect(int areaSelect) {
        this.areaSelect = areaSelect;
    }

    /**
     * @return the useCase
     */
    public UseCase getUseCase() {
        return useCase;
    }

    /**
     * @param useCase
     *            the useCase to set
     */
    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * @return the trigger
     */
    public UseCase getTrigger() {
        return trigger;
    }

    /**
     * @param trigger
     *            the trigger to set
     */
    public void setTrigger(UseCase trigger) {
        this.trigger = trigger;
    }

    /**
     * @return the initialX
     */
    public int getInitialX() {
        return initialX;
    }

    /**
     * @param initialX
     *            the initialX to set
     */
    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    /**
     * @return the initialY
     */
    public int getInitialY() {
        return initialY;
    }

    /**
     * @param initialY
     *            the initialY to set
     */
    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    /**
     * @return the finalX
     */
    public int getFinalX() {
        return finalX;
    }

    /**
     * @param finalX
     *            the finalX to set
     */
    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    /**
     * @return the finalY
     */
    public int getFinalY() {
        return finalY;
    }

    /**
     * @param finalY
     *            the finalY to set
     */
    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    /**
     * @return the useCaseTranType
     */
    public int getUseCaseTranType() {
        return useCaseTranType;
    }

    /**
     * @param useCaseTranType
     *            the useCaseTranType to set
     */
    public void setUseCaseTranType(int useCaseTranType) {
        this.useCaseTranType = useCaseTranType;
    }

    /**
     * @return the hashStep
     */
    public String getHashStep() {
        return hashStep;
    }

    /**
     * @param hashStep
     *            the hashStep to set
     */
    public void setHashStep(String hashStep) {
        this.hashStep = hashStep;
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