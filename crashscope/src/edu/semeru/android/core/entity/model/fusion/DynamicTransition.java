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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the DynamicTransition database table.
 * 
 */
@Entity
@Table(name = "DYNAMIC_TRANSITION")
@NamedQuery(name = "DynamicTransition.findAll", query = "SELECT t FROM DynamicTransition t")
public class DynamicTransition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SOURCE_ACTIVITY")
    private String sourceActicity;
    @Column(name = "SOURCE_WINDOW")
    private String sourceWindow;
    @Column(name = "TARGET_ACTIVITY")
    private String targetActivity;
    @Column(name = "TARGET_WINDOW")
    private String targetWindow;

    // bi-directional many-to-one association to DynGuiComponent
    // @OneToMany(mappedBy = "dynamicTransition", cascade = CascadeType.ALL)
    // private List<DynGuiComponent> dynGuiComponents = new
    // ArrayList<DynGuiComponent>();

    // bi-directional many-to-one association to Step
    @OneToOne
    @JoinColumn(name = "ID_STEP")
    private transient Step step;

    @Transient
    private transient DynGuiComponent targetComponent;

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
     * @return the sourceActicity
     */
    public String getSourceActicity() {
        return sourceActicity;
    }

    /**
     * @param sourceActicity
     *            the sourceActicity to set
     */
    public void setSourceActicity(String sourceActicity) {
        this.sourceActicity = sourceActicity;
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
     * @return the step
     */
    public Step getStep() {
        return step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep(Step step) {
        this.step = step;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"DynamicTransition\" :{\"sourceWindow\":\"" + sourceWindow + "\", \"targetWindow\":\"" + targetWindow
                + "\", \"step\":" + step + "}, \"targetComponent\":" + targetComponent + "}";
    }

    /**
     * @return the targetComponent
     */
    public DynGuiComponent getTargetComponent() {
        return targetComponent;
    }

    /**
     * @param targetComponent the targetComponent to set
     */
    public void setTargetComponent(DynGuiComponent targetComponent) {
        this.targetComponent = targetComponent;
    }

}