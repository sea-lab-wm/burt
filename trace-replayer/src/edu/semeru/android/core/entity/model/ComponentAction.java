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
import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the component_action database table.
 * 
 */
@Entity
@Table(name = "COMPONENT_ACTION")
public class ComponentAction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bi-directional many-to-one association to ActionType
    @ManyToOne
    @JoinColumn(name = "action_type_id")
    private ActionType actionType;

    // bi-directional many-to-one association to GuiComponent
    @ManyToOne
    @JoinColumn(name = "gui_component_id")
    private GuiComponent guiComponent;

    // bi-directional many-to-one association to Transition
    @OneToMany(mappedBy = "componentAction", cascade = CascadeType.ALL)
    private List<Transition> transitions;

    public ComponentAction() {
    }

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public ActionType getActionType() {
	return this.actionType;
    }

    public void setActionType(ActionType actionType) {
	this.actionType = actionType;
    }

    public GuiComponent getGuiComponent() {
	return this.guiComponent;
    }

    public void setGuiComponent(GuiComponent guiComponent) {
	this.guiComponent = guiComponent;
    }

    public List<Transition> getTransitions() {
	return this.transitions;
    }

    public void setTransitions(List<Transition> transitions) {
	this.transitions = transitions;
    }

    public Transition addTransition(Transition transition) {
	getTransitions().add(transition);
	transition.setComponentAction(this);

	return transition;
    }

    public Transition removeTransition(Transition transition) {
	getTransitions().remove(transition);
	transition.setComponentAction(null);

	return transition;
    }

}