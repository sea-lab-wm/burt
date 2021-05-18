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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the class database table.
 * 
 */
@Entity
@Table(name = "CLASS")
public class Class implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isActivity;

    private boolean isMain;
    
    private String name;
    
    @Column(name = "package")
    private String package_;

    @Transient
    private String extends_;

    @Transient
    private List<String> implements_;

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL)
    private List<LineNums> lines = new ArrayList<LineNums>();
   // private String signature;
    public void addLine(LineNums line) {
   	lines.add(line);
       }
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

    // bi-directional many-to-one association to App
    @ManyToOne
    private App app;

    
    private String classAppPack;
    
    /**
     * @return the classAppPack
     */
    public String getClassAppPack() {
        return classAppPack;
    }
    /**
     * @param classAppPack the classAppPack to set
     */
    public void setClassAppPack(String classAppPack) {
        this.classAppPack = classAppPack;
    }

    // bi-directional many-to-one association to GuiComponent
    @ManyToMany
    @JoinTable(name = "COMPONENT_CLASSES", joinColumns = {@JoinColumn(name = "class_id") }, inverseJoinColumns = { @JoinColumn(name = "gui_component_id") })
    private List<GuiComponent> guiComponents = new ArrayList<GuiComponent>();
    
    // bi-directional many-to-one association to Method
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL)
    private List<Method> methods = new ArrayList<Method>();

    // bi-directional many-to-one association to Transition
    @OneToMany(mappedBy = "classSource", cascade = CascadeType.ALL)
    private List<Transition> transitionsSource = new ArrayList<Transition>();

    // bi-directional many-to-one association to Transition
    @OneToMany(mappedBy = "classTarget", cascade = CascadeType.ALL)
    private List<Transition> transitionsTarget = new ArrayList<Transition>();

    public Class() {
    }

    /**
     * @param isActivity
     * @param name
     * @param package_
     * @param signature
     */
    public Class(boolean isActivity, String name, String package_, String signature, boolean isMain) {
	super();
	this.isActivity = isActivity;
	this.isMain = isMain;
	this.name = name;
	this.package_ = package_;
	//this.signature = signature;
    }

    /**
     * @param isActivity
     * @param name
     * @param package_
     * @param signature
     * @param methods
     */
    public Class(boolean isActivity, String name, String package_, String signature, List<Method> methods, boolean isMain) {
	super();
	this.isActivity = isActivity;
	this.name = name;
	this.isMain = isMain;
	this.package_ = package_;
	//this.signature = signature;
	this.methods = methods;
    }

    /**
     * @param isActivity
     * @param name
     * @param package_
     * @param extends_
     * @param implements_
     * @param signature
     * @param methods
     */
    public Class(boolean isActivity, String name, String package_, String signature, List<Method> methods,
	    String extends_, List<String> implements_, boolean isMain) {
	super();
	this.isActivity = isActivity;
	this.name = name;
	this.isMain = isMain;
	this.package_ = package_;
	this.extends_ = extends_;
	this.implements_ = implements_;
	//this.signature = signature;
	this.methods = methods;
    }

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public boolean isActivity() {
	return this.isActivity;
    }

    public void setIsActivity(boolean isActivity) {
	this.isActivity = isActivity;
    }

    public boolean isMain() {
   	return this.isMain;
       }

       public void setIsMain(boolean isMain) {
   	this.isMain = isMain;
       }
    
    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPackage_() {
	return this.package_;
    }

    public void setPackage_(String package_) {
	this.package_ = package_;
    }

  /*  public String getSignature() {
	return this.signature;
    }*/

    /*public void setSignature(String signature) {
	this.signature = signature;
    }*/

    public App getApp() {
	return this.app;
    }

    public void setApp(App app) {
	this.app = app;
    }

    public List<GuiComponent> getGuiComponents() {
	return this.guiComponents;
    }

    public void setGuiComponents(List<GuiComponent> guiComponents) {
	this.guiComponents = guiComponents;
    }

    public GuiComponent addGuiComponent(GuiComponent guiComponent) {
	getGuiComponents().add(guiComponent);
	guiComponent.addClazzez(this);
	return guiComponent;
    }

    public GuiComponent removeGuiComponent(GuiComponent guiComponent) {
	getGuiComponents().remove(guiComponent);
	guiComponent.setClazzez(null);

	return guiComponent;
    }

    public List<Method> getMethods() {
	return this.methods;
    }

    public void setMethods(List<Method> methods) {
	this.methods = methods;
    }

    public Method addMethod(Method method) {
	getMethods().add(method);
	method.setClazz(this);

	return method;
    }

    public Method removeMethod(Method method) {
	getMethods().remove(method);
	method.setClazz(null);

	return method;
    }

    public List<Transition> getTransitionsSource() {
	return this.transitionsSource;
    }

    public void setTransitionsSource(List<Transition> transitions1) {
	this.transitionsSource = transitions1;
    }

    public Transition addTransitionsSource(Transition transitions1) {
	getTransitionsSource().add(transitions1);
	transitions1.setClassSource(this);

	return transitions1;
    }

    public Transition removeTransitionsSource(Transition transitions1) {
	getTransitionsSource().remove(transitions1);
	transitions1.setClassSource(null);

	return transitions1;
    }

    public List<Transition> getTransitionsTarget() {
	return this.transitionsTarget;
    }

    public void setTransitionsTarget(List<Transition> transitions2) {
	this.transitionsTarget = transitions2;
    }

    public Transition addTransitionsTarget(Transition transitions2) {
	getTransitionsTarget().add(transitions2);
	transitions2.setClassTarget(this);

	return transitions2;
    }

    public Transition removeTransitionsTarget(Transition transitions2) {
	getTransitionsTarget().remove(transitions2);
	transitions2.setClassTarget(null);

	return transitions2;
    }

    /**
     * @return the extends_
     */
    public String getExtends_() {
	return extends_;
    }

    /**
     * @param extends_
     *            the extends_ to set
     */
    public void setExtends_(String extends_) {
	this.extends_ = extends_;
    }

    /**
     * @return the implements_
     */
    public List<String> getImplements_() {
	return implements_;
    }

    /**
     * @param implements_
     *            the implements_ to set
     */
    public void setImplements_(List<String> implements_) {
	this.implements_ = implements_;
    }

    @Transient
    private List<Class> activityClasses = new ArrayList<Class>();

    
    public List<Class> getActivityClass() {
	return activityClasses;
    }

    /**
     * @param extended
     *            the extended to set
     */
    public void addActivityClass(Class activityClass) {
	activityClasses.add(activityClass);
    }

    public void setActivityClasses(List<Class> activityClasses){
	this.activityClasses = activityClasses;
    }
    
    @Transient
    private Class baseClass;
    
    public Class getBaseClass() {
   	return baseClass;
       }

       /**
        * @param extended
        *            the extended to set
        */
       public void setBaseClass(Class baseClass) {
   	this.baseClass = baseClass;
       }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
	return "Class [id=" + id + ", isActivity=" + isActivity + ", name=" + name + ", package_=" + package_
		 + ", app=" + app + ", extends=" + extends_ + ", implements=" + implements_
	 + ", guiComponents=" + guiComponents + ", methods=" + methods + "]";
    }

}