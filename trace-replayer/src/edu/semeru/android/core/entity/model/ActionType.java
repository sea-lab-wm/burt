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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the action_type database table.
 * 
 */
@Entity
@Table(name = "ACTION_TYPE")
public class ActionType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String description;

    private String name;
    
    private String textEntry;

    // bi-directional many-to-one association to ComponentAction
    @OneToMany(mappedBy = "actionType", cascade = CascadeType.ALL)
    private List<ComponentAction> componentActions;

    // bi-directional many-to-many association to GuiComponentType
    @ManyToMany(mappedBy = "actionTypes")
    private List<GuiComponentType> guiComponentTypes;

    public ActionType() {
    }

    /**
     * @param id
     * @param name
     */
    public ActionType(Long id, String name) {
	super();
	this.id = id;
	this.name = name;
    }

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getDescription() {
	return this.description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<ComponentAction> getComponentActions() {
	return this.componentActions;
    }

    public void setComponentActions(List<ComponentAction> componentActions) {
	this.componentActions = componentActions;
    }

    public ComponentAction addComponentAction(ComponentAction componentAction) {
	getComponentActions().add(componentAction);
	componentAction.setActionType(this);

	return componentAction;
    }

    public ComponentAction removeComponentAction(ComponentAction componentAction) {
	getComponentActions().remove(componentAction);
	componentAction.setActionType(null);

	return componentAction;
    }

    public List<GuiComponentType> getGuiComponentTypes() {
	return this.guiComponentTypes;
    }

    public void setGuiComponentTypes(List<GuiComponentType> guiComponentTypes) {
	this.guiComponentTypes = guiComponentTypes;
    }

    /**
     * @return the textEntry
     */
    public String getTextEntry() {
        return textEntry;
    }

    /**
     * @param textEntry the textEntry to set
     */
    public void setTextEntry(String textEntry) {
        this.textEntry = textEntry;
    }

}