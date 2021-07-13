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
 * UseCase.java
 * 
 * Created on Aug 5, 2015, 3:44:45 PM
 * 
 */
package edu.semeru.android.core.entity.model.fusion;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 5, 2015
 */
@Entity
@Table(name = "USE_CASE")
@NamedQuery(name = "UseCase.findAll", query = "SELECT s FROM UseCase s")
public class UseCase implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6479449452200748094L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Lob
    private String name;
    @Column(name = "WINDOW_ID")
    private String window;
    @Column(name = "IMAGE_PATH")
    private String imagePath;
    @Column(name = "PACKAGE_NAME")
    private String packageName;
//    @OneToMany(mappedBy = "useCase", cascade = CascadeType.ALL)
//    private List<Step> stepUseCase = new ArrayList<Step>();
//    @OneToMany(mappedBy = "trigger", cascade = CascadeType.ALL)
//    private List<Step> stepTrigger = new ArrayList<Step>();

    /**
     * 
     */
    public UseCase() {
    }

    /**
     * @param window
     */
    public UseCase(String window) {
        this.window = window;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the window
     */
    public String getWindow() {
        return window;
    }

    /**
     * @param window
     *            the window to set
     */
    public void setWindow(String window) {
        this.window = window;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath
     *            the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the stepUseCase
     */
//    public List<Step> getStepUseCase() {
//        return stepUseCase;
//    }

    /**
     * @param stepUseCase
     *            the stepUseCase to set
     */
//    public void setStepUseCase(List<Step> stepUseCase) {
//        this.stepUseCase = stepUseCase;
//    }

    /**
     * @return the stepTrigger
     */
//    public List<Step> getStepTrigger() {
//        return stepTrigger;
//    }

    /**
     * @param stepTrigger
     *            the stepTrigger to set
     */
//    public void setStepTrigger(List<Step> stepTrigger) {
//        this.stepTrigger = stepTrigger;
//    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 1 : id.hashCode());
        // result = prime * result + ((name == null) ? 1 : name.hashCode());
        result = prime * result + ((window == null) ? 1 : window.hashCode());
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
        UseCase other = (UseCase) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        // if (name == null) {
        // if (other.name != null) {
        // return false;
        // }
        // } else if (!name.equals(other.name)) {
        // return false;
        // }
        if (window == null) {
            if (other.window != null) {
                return false;
            }
        } else if (!window.equals(other.window)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"UseCase\" :{\"id\":\"" + id + "\", \"name\":\"" + name + "\", \"window\":\"" + window
                + "\", \"imagePath\":\"" + imagePath + "\"}}";
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
