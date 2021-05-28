/*******************************************************************************
 * Copyright (c) 2017, SEMERU
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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Steps database table.
 * 
 */
@Entity
@Table(name = "SCREEN")
@NamedQuery(name = "Screen.findAll", query = "SELECT s FROM Screen s")
public class Screen implements Serializable, Comparable<Screen> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "ACTIVITY")
    private String activity;

	@Column(name = "SCREENSHOT")
    private String screenshot;
    
    @Column(name = "WINDOW")
    private String window;

    @Column(name = "NUM_COMPONENTS")
    private String numComponents;
    
    @Column(name = "HASH_SCREEN")
    private String hash;
   
    // bi-directional many-to-one association to DynGuiComponent
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<DynGuiComponent> dynGuiComponents = new ArrayList<DynGuiComponent>();

    @OneToOne
    @JoinColumn(name = "ID_STEP")
    private transient Step step;
    
    // bi-directional many-to-one association to Screenshot
    // @OneToOne(mappedBy = "step")
    // private Screenshot screenshot;

	// bi-directional many-to-one association to Screenshot

    public Screen() {
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
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the window
	 */
	public String getWindow() {
		return window;
	}

	/**
	 * @param window the window to set
	 */
	public void setWindow(String window) {
		this.window = window;
	}

	/**
	 * @return the numComponents
	 */
	public String getNumComponents() {
		return numComponents;
	}

	/**
	 * @param numComponents the numComponents to set
	 */
	public void setNumComponents(String numComponents) {
		this.numComponents = numComponents;
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
 
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"Screen\" :{\"id\":\"" + id 
                + "\", \"screenshot\":\"" + screenshot 
                + "\", \"dynGuiComponents\":" + dynGuiComponents + "}}";
    }


    public String toStringShort() {
        return "s{" +
                "i=" + id +
                ", a='" + activity + '\'' +
                '}';
    }


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Screen o) {
		// TODO Auto-generated method stub
		return 0;
	}

    /**
     * @return the dynGuiComponents
     */
    public List<DynGuiComponent> getDynGuiComponents() {
        return dynGuiComponents;
    }

    /**
     * @param dynGuiComponents the dynGuiComponents to set
     */
    public void setDynGuiComponents(List<DynGuiComponent> dynGuiComponents) {
        this.dynGuiComponents = dynGuiComponents;
    }

    /**
     * @return the step
     */
    public Step getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(Step step) {
        this.step = step;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

  }