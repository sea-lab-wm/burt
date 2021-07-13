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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.semeru.android.core.entity.model.fusion.ActivityFeature;
import edu.semeru.android.core.entity.model.fusion.Execution;

/**
 * The persistent class for the app database table.
 * 
 */
@Entity
@Table(name = "APP")
@NamedQuery(name = "App.findAll", query = "SELECT s FROM App s")
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "APP_NAME")
    private String name;

    @Column(name = "PACKAGE_NAME")
    private String packageName;
    
    @Column(name = "MAIN_ACTIVITY")
    private String mainActivity;

    @Column(name = "VERSION")
    private String version;

    // bi-directional many-to-one association to Class
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private transient List<ActivityFeature> actFeatures = new ArrayList<ActivityFeature>();

    // bi-directional many-to-one association to Class
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private transient List<Class> clazzs = new ArrayList<Class>();

    // bi-directional many-to-one association to Class
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private transient List<GuiComponent> guiz = new ArrayList<GuiComponent>();

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private transient List<Execution> executions = new ArrayList<Execution>();

    /**
     * @return the actFeatures
     */
    public List<ActivityFeature> getActFeatures() {
        return actFeatures;
    }

    private String apkPath;
    
    /**
	 * @return the apkPath
	 */
	public String getApkPath() {
		return apkPath;
	}

	/**
	 * @param apkPath the apkPath to set
	 */
	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	/**
     * @param actFeatures the actFeatures to set
     */
    public void setActFeatures(List<ActivityFeature> actFeatures) {
        this.actFeatures = actFeatures;
    }
    
    public List<GuiComponent> getGuiz() {
        return this.guiz;
    }

    public void setGuiz(List<GuiComponent> guiz) {
        this.guiz = guiz;
    }

    public GuiComponent addClazz(GuiComponent gui) {
        getGuiz().add(gui);
        gui.setApp(this);

        return gui;
    }

    public GuiComponent removeClazz(GuiComponent gui) {
        getGuiz().remove(gui);
        gui.setApp(null);

        return gui;
    }

    public App() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Class> getClazzs() {
        return this.clazzs;
    }

    public void setClazzs(List<Class> clazzs) {
        this.clazzs = clazzs;
    }

    public Class addClazz(Class clazz) {
        getClazzs().add(clazz);
        clazz.setApp(this);

        return clazz;
    }

    public Class removeClazz(Class clazz) {
        getClazzs().remove(clazz);
        clazz.setApp(null);

        return clazz;
    }

    public void addExecution(Execution e) {
        getExecutions().add(e);
    }

    /**
     * @return the executions
     */
    public List<Execution> getExecutions() {
        return executions;
    }

    /**
     * @param executions
     *            the executions to set
     */
    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }
    
    /**
     * @return the mainActivity
     */
    public String getMainActivity() {
        return mainActivity;
    }

    /**
     * @param mainActivity the mainActivity to set
     */
    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public String toString() {
        return "app{" +
                "i=" + id +
                ", n='" + name + '\'' +
                ", p='" + packageName + '\'' +
                ", v='" + version + '\'' +
                '}';
    }
}