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
package edu.semeru.android.core.model;

/**
 * 
 * {Insert class description here}
 *
 * @author Mario Linares
 * @since Aug 12, 2014
 */
public class CoverageValuesVO {

    private int methodsInTheAPK;
    private int activitiesInTheAPK;

    private int coveredMethods;
    private int coveredActivities;

    public CoverageValuesVO() {
	super();
    }

    public int getMethodsInTheAPK() {
	return methodsInTheAPK;
    }

    public void setMethodsInTheAPK(int methodsInTheAPK) {
	this.methodsInTheAPK = methodsInTheAPK;
    }

    public int getActivitiesInTheAPK() {
	return activitiesInTheAPK;
    }

    public void setActivitiesInTheAPK(int activitiesInTheAPK) {
	this.activitiesInTheAPK = activitiesInTheAPK;
    }

    public int getCoveredMethods() {
	return coveredMethods;
    }

    public void setCoveredMethods(int coveredMethods) {
	this.coveredMethods = coveredMethods;
    }

    public int getCoveredActivities() {
	return coveredActivities;
    }

    public void setCoveredActivities(int coveredActivities) {
	this.coveredActivities = coveredActivities;
    }

    public double getActivityCoverage() {
	return (getActivitiesInTheAPK() != 0 ? ((double) getCoveredActivities()) / getActivitiesInTheAPK() : 0);
    }

    public double getMethodCoverage() {
	return (getMethodsInTheAPK() != 0 ? ((double) getCoveredMethods()) / getMethodsInTheAPK() : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "CoverageValuesVO [methodsInTheAPK=" + methodsInTheAPK + ", activitiesInTheAPK=" + activitiesInTheAPK
		+ ", coveredMethods=" + coveredMethods + ", coveredActivities=" + coveredActivities + "]";
    }

}
