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
package edu.semeru.android.core.entity.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * The persistent class for the transition database table.
 * 
 */
@Entity
@Table(name = "TRANSITION")
public class Transition implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TransitionPK id;

    private Integer frequency;

    // bi-directional many-to-one association to Class
    @ManyToOne
    @MapsId("classIdSource")
    @JoinColumn(name = "class_id_source")
    private Class classSource;

    // bi-directional many-to-one association to Class
    @ManyToOne
    @MapsId("classIdTarget")
    @JoinColumn(name = "class_id_target")
    private Class classTarget;

    // bi-directional many-to-one association to ComponentAction
    @ManyToOne
    @MapsId("componentActionId")
    @JoinColumn(name = "component_action_id")
    private ComponentAction componentAction;

    public Transition() {
    }

    public TransitionPK getId() {
	return this.id;
    }

    public void setId(TransitionPK id) {
	this.id = id;
    }

    public Integer getFrequency() {
	return this.frequency;
    }

    public void setFrequency(Integer frequency) {
	this.frequency = frequency;
    }

    public Class getClassSource() {
	return classSource;
    }

    public void setClassSource(Class classSource) {
	this.classSource = classSource;
    }

    public Class getClassTarget() {
	return classTarget;
    }

    public void setClassTarget(Class classTarget) {
	this.classTarget = classTarget;
    }

    public ComponentAction getComponentAction() {
	return this.componentAction;
    }

    public void setComponentAction(ComponentAction componentAction) {
	this.componentAction = componentAction;
    }

}