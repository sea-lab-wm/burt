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
package edu.semeru.android.core.entity.model.fusion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;

import edu.semeru.android.core.entity.model.App;

/**
 * The persistent class for the Execution database table.
 * 
 */
@Entity
@Table(name = "EXECUTION")
@NamedQuery(name = "Execution.findAll", query = "SELECT e FROM Execution e")
public class Execution implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // private String appPackage;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date = Calendar.getInstance().getTime();
    @Column(name = "DEVICE_DIMENSIONS")
    private String deviceDimensions;
    @Column(name = "EXECUTION_TYPE")
    private String executionType;
    @Column(name = "TOP_DOWN")
    private Boolean topDown;
    @Column(name = "BOTTOM_UP")
    private Boolean bottomUp;
    @Column(name = "NO_TEXT")
    private Boolean noText;
    @Column(name = "EXPECTED_TEXT")
    private Boolean expectedText;
    @Column(name = "UNEXPECTED_TEXT")
    private Boolean unexpectedText;
    @Column(name = "CONTEXT_FEAT_ENABLED")
    private Boolean contextFeatsEnabled;
    @Column(name = "CONTEXT_FEAT_DISABLED")
    private Boolean contextFeatsDisabled;
    @Column(name = "EXECUTION_NUMBER")
    private int executionNum;
    @Column(name = "CRASH")
    private boolean crash;
    @Column(name = "DEVICE_NAME")
    private String deviceName;
    @Column(name = "ELAPSED_TIME")
    private long elapsedTime;
    // 0 - Portrait, 1 - Landscape
    private int orientation;
    // @Column(name = "APP_VERSION")
    // private String appVersion;
    // bi-directional many-to-one association to Step
    @Column(name = "MAIN_ACTIVITY")
    private String mainActivity;
    @Column(name = "ANDROID_VERSION")
    private String androidVersion;

    @OneToMany(mappedBy = "execution", cascade = CascadeType.ALL)
    @OrderBy("sequenceStep ASC")
    private List<Step> steps = new ArrayList<Step>();

    @ManyToOne
    @JoinColumn(name = "ID_APP")
    private App app;

    /**
     * @return the executionType
     */
    public String getExecutionType() {
        return executionType;
    }

    /**
     * @param executionType
     *            the executionType to set
     */
    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    /**
     * @return the crash
     */
    public boolean isCrash() {
        return crash;
    }

    /**
     * @param crash
     *            the crash to set
     */
    public void setCrash(boolean crash) {
        this.crash = crash;
    }

    /**
     * @return the executionNum
     */
    public int getExecutionNum() {
        return executionNum;
    }

    /**
     * @param executionNum
     *            the executionNum to set
     */
    public void setExecutionNum(int executionNum) {
        this.executionNum = executionNum;
    }

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName
     *            the deviceName to set
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return the elapsedTime
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @param elapsedTime
     *            the elapsedTime to set
     */
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Execution() {
    }

    /**
     * @return the idExecution
     */
    public Long getId() {
        return id;
    }

    /**
     * @param idExecution
     *            the idExecution to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the steps
     */
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * @param steps
     *            the steps to set
     */
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    /**
     * @return the deviceDimensions
     */
    public String getDeviceDimensions() {
        return deviceDimensions;
    }

    /**
     * @param deviceDimensions
     *            the deviceDimensions to set
     */
    public void setDeviceDimensions(String deviceDimensions) {
        this.deviceDimensions = deviceDimensions;
    }

    /**
     * @return the orientation
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * @param orientation
     *            the orientation to set
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * @return the app
     */
    public App getApp() {
        return app;
    }

    /**
     * @param app
     *            the app to set
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * @return the mainActivity
     */
    public String getMainActivity() {
        return mainActivity;
    }

    /**
     * @param mainActivity
     *            the mainActivity to set
     */
    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * @return the androidVersion
     */
    public String getAndroidVersion() {
        return androidVersion;
    }

    /**
     * @param androidVersion
     *            the androidVersion to set
     */
    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    /**
     * @return the topDown
     */
    public Boolean getTopDown() {
        return topDown;
    }

    /**
     * @param topDown
     *            the topDown to set
     */
    public void setTopDown(Boolean topDown) {
        this.topDown = topDown;
    }

    /**
     * @return the bottomUp
     */
    public Boolean getBottomUp() {
        return bottomUp;
    }

    /**
     * @param bottomUp
     *            the bottomUp to set
     */
    public void setBottomUp(Boolean bottomUp) {
        this.bottomUp = bottomUp;
    }

    /**
     * @return the noText
     */
    public Boolean getNoText() {
        return noText;
    }

    /**
     * @param noText
     *            the noText to set
     */
    public void setNoText(Boolean noText) {
        this.noText = noText;
    }

    /**
     * @return the expectedText
     */
    public Boolean getExpectedText() {
        return expectedText;
    }

    /**
     * @param expectedText
     *            the expectedText to set
     */
    public void setExpectedText(Boolean expectedText) {
        this.expectedText = expectedText;
    }

    /**
     * @return the unexpectedText
     */
    public Boolean getUnexpectedText() {
        return unexpectedText;
    }

    /**
     * @param unexpectedText
     *            the unexpectedText to set
     */
    public void setUnexpectedText(Boolean unexpectedText) {
        this.unexpectedText = unexpectedText;
    }

    /**
     * @return the contextFeatsEnabled
     */
    public Boolean getContextFeatsEnabled() {
        return contextFeatsEnabled;
    }

    /**
     * @param contextFeatsEnabled
     *            the contextFeatsEnabled to set
     */
    public void setContextFeatsEnabled(Boolean contextFeatsEnabled) {
        this.contextFeatsEnabled = contextFeatsEnabled;
    }

    /**
     * @return the contextFeatsDisabled
     */
    public Boolean getContextFeatsDisabled() {
        return contextFeatsDisabled;
    }

    /**
     * @param contextFeatsDisabled
     *            the contextFeatsDisabled to set
     */
    public void setContextFeatsDisabled(Boolean contextFeatsDisabled) {
        this.contextFeatsDisabled = contextFeatsDisabled;
    }

    public void setStratsFalse() {
        topDown = false;
        bottomUp = false;
        unexpectedText = false;
        expectedText = false;
        noText = false;
        contextFeatsDisabled = false;
        contextFeatsEnabled = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id + " : " + (app.getName() == null || app.getName().isEmpty() ? app.getPackageName() : app.getName());
    }

}