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

import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the gui_component database table.
 * 
 */
@Entity
@Table(name = "GUI_COMPONENT")
public class GuiComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_xml")
    private String idXml;

    @Column(name = "file")
    private String file;

    @Column(name = "attributes")
    private String attributes;

    @Column(name = "extends_class")
    private String extended;
    
    @Column(name = "type")
    private String type;
    
   
    @OneToMany(mappedBy = "guiComponent", cascade = CascadeType.ALL)
    private List<LineNums> lines = new ArrayList<LineNums>();
    
    /**
     * @return the lines
     */
    public List<LineNums> getLines() {
        return lines;
    }

    /**
     * @param lines the lines to set
     */
    public void setLines(List<LineNums> lines) {
        this.lines = lines;
    }

    public void addLine(LineNums line) {
	lines.add(line);
    }
    
    public String getType() {
	return type;
    }

    /**
     * @param extended
     *            the extended to set
     */
    public void setType(String type) {
	this.type = type;
    }
    
    
    // bi-directional many-to-one association to ComponentAction
    @OneToMany(mappedBy = "guiComponent", cascade = CascadeType.ALL)
    private List<ComponentAction> componentActions = new ArrayList<ComponentAction>();


    // bi-directional many-to-one association to GuiComponent
    @ManyToOne
    @JoinColumn(name = "gui_component_container_id")
    private GuiComponent guiComponent;

    // bi-directional many-to-one association to GuiComponent
    @OneToMany(mappedBy = "guiComponent", cascade = CascadeType.ALL)
    private List<GuiComponent> guiComponents = new ArrayList<GuiComponent>();

    // bi-directional many-to-one association to GuiComponentType
    @ManyToOne
    @JoinColumn(name = "gui_component_type_id")
    private GuiComponentType guiComponentType;

    @ManyToOne
    private App app;
    
    /**
     * @return the app
     */
    public App getApp() {
        return app;
    }

    /**
     * @param app the app to set
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * @return the appPackage
     */
    public String getAppPackage() {
        return appPackage;
    }

    /**
     * @param appPackage the appPackage to set
     */
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }


    private String appPackage;
    
    @Transient
    private List<String> activities = new ArrayList<String>();

    @ManyToMany(mappedBy = "guiComponents")
    private List<Class> clazzez = new ArrayList<Class>();

    public GuiComponent() {
    }

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public List<ComponentAction> getComponentActions() {
	return this.componentActions;
    }

    public void setComponentActions(List<ComponentAction> componentActions) {
	this.componentActions = componentActions;
    }

    public ComponentAction addComponentAction(ComponentAction componentAction) {
	getComponentActions().add(componentAction);
	componentAction.setGuiComponent(this);

	return componentAction;
    }

    public ComponentAction removeComponentAction(ComponentAction componentAction) {
	getComponentActions().remove(componentAction);
	componentAction.setGuiComponent(null);

	return componentAction;
    }

  /*  public Class getClazz() {
	return this.clazz;
    }

    public void setClazz(Class clazz) {
	this.clazz = clazz;
    }*/

    public GuiComponent getGuiComponent() {
	return this.guiComponent;
    }

    public void setGuiComponent(GuiComponent guiComponent) {
	this.guiComponent = guiComponent;
    }

    public List<GuiComponent> getGuiComponents() {
	return this.guiComponents;
    }

    public void setGuiComponents(List<GuiComponent> guiComponents) {
	this.guiComponents = guiComponents;
    }

    public GuiComponent addGuiComponent(GuiComponent guiComponent) {
	getGuiComponents().add(guiComponent);
	guiComponent.setGuiComponent(this);

	return guiComponent;
    }

    public GuiComponent removeGuiComponent(GuiComponent guiComponent) {
	getGuiComponents().remove(guiComponent);
	guiComponent.setGuiComponent(null);

	return guiComponent;
    }

    public GuiComponentType getGuiComponentType() {
	return this.guiComponentType;
    }

    public void setGuiComponentType(GuiComponentType guiComponentType) {
	this.guiComponentType = guiComponentType;
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
     * @return the attributes
     */
    public String getAttributes() {
	return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(String attributes) {
	this.attributes = attributes;
    }

    /**
     * @return the file
     */
    public String getFile() {
	return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(String file) {
	this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "GuiComponent [id=" + id  + ", idXml=" + idXml + ", file=" + file
		+ ", attributes=" + attributes + ", extended=" + extended + ", componentActions=" + componentActions
		+ ", guiComponent=" + guiComponent + ", guiComponentType=" + guiComponentType
		+ "]";
    }

    /**
     * @return the extended
     */
    public String getExtended() {
	return extended;
    }

    /**
     * @param extended
     *            the extended to set
     */
    public void setExtended(String extended) {
	this.extended = extended;
    }

    /**
     * @return the activities
     */
    public List<String> getActivities() {
	return activities;
    }

    /**
     * @param activities
     *            the activities to set
     */
    public void setActivities(List<String> activities) {
	this.activities = activities;
    }

    /**
     * @param activity
     */
    public void addActivity(String activity) {
	activities.add(activity);
    }

    public List<Class> getClazzez() {
	return clazzez;
    }

    /**
     * @param activities
     *            the activities to set
     */
    public void setClazzez(List<Class> clazzez) {
	this.clazzez = clazzez;
    }

    /**
     * @param activity
     */
    public void addClazzez(Class clazzz) {
	clazzez.add(clazzz);
    }

}