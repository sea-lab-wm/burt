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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the transition database table.
 * 
 */
@Embeddable
public class TransitionPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "class_id_source")
    private Long classIdSource;

    @Column(name = "class_id_target")
    private Long classIdTarget;

    @Column(name = "component_action_id")
    private Long componentActionId;

    public TransitionPK() {
    }

    public Long getClassIdSource() {
	return this.classIdSource;
    }

    public void setClassIdSource(Long classIdSource) {
	this.classIdSource = classIdSource;
    }

    public Long getClassIdTarget() {
	return this.classIdTarget;
    }

    public void setClassIdTarget(Long classIdTarget) {
	this.classIdTarget = classIdTarget;
    }

    public Long getComponentActionId() {
	return this.componentActionId;
    }

    public void setComponentActionId(Long componentActionId) {
	this.componentActionId = componentActionId;
    }

    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	}
	if (!(other instanceof TransitionPK)) {
	    return false;
	}
	TransitionPK castOther = (TransitionPK) other;
	return this.classIdSource.equals(castOther.classIdSource)
		&& this.classIdTarget.equals(castOther.classIdTarget)
		&& this.componentActionId.equals(castOther.componentActionId);
    }

    public int hashCode() {
	final int prime = 31;
	int hash = 17;
	hash = hash * prime + this.classIdSource.hashCode();
	hash = hash * prime + this.classIdTarget.hashCode();
	hash = hash * prime + this.componentActionId.hashCode();

	return hash;
    }
}