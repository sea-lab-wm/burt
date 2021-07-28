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
 * @author Carlos Bernal
 * @since Jul 27, 2014
 */
public class GUIEventVO {

    private double duration = 1;

    private int initialX;
    private int initialY;

    private int finalX;
    private int finalY;

    private int realInitialX;
    private int realInitialY;

    private int realFinalX;
    private int realFinalY;

    private String direction;

    private int eventTypeId = -1;

    private String eventLabel;

    private String object = "??";

    private String text = "";

    private String activity;

	//    private DynGuiComponent hvInfoComponent;
    private DynGuiComponentVO hvInfoComponent;

    private int areaList;
    private int areaEdit;
    private int areaView;
    private int areaSelect;

    // public static final GUIEventVO BACK

    public GUIEventVO(double duration, int initialX, int initialY, int finalX, int finalY, int eventTypeId,
            String eventLabel, String direction) {
        super();
        this.duration = duration;
        this.initialX = initialX;
        this.initialY = initialY;
        this.finalX = finalX;
        this.finalY = finalY;
        this.eventTypeId = eventTypeId;
        this.eventLabel = eventLabel;
        this.direction = direction;
    }

    /**
     * @param realInitialX
     * @param realInitialY
     * @param eventTypeId
     */
    public GUIEventVO(int realInitialX, int realInitialY, int eventTypeId) {
        super();
        this.realInitialX = realInitialX;
        this.realInitialY = realInitialY;
        this.eventTypeId = eventTypeId;
    }

    /**
     * 
     */
    public GUIEventVO() {
        super();
    }

    /**
     * @param eventTypeId
     */
    public GUIEventVO(int eventTypeId) {
        super();
        this.eventTypeId = eventTypeId;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public int getFinalX() {
        return finalX;
    }

    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getRealInitialX() {
        return realInitialX;
    }

    public void setRealInitialX(int realInitialX) {
        this.realInitialX = realInitialX;
    }

    public int getRealInitialY() {
        return realInitialY;
    }

    public void setRealInitialY(int realInitialY) {
        this.realInitialY = realInitialY;
    }

    public int getRealFinalX() {
        return realFinalX;
    }

    public void setRealFinalX(int realFinalX) {
        this.realFinalX = realFinalX;
    }

    public int getRealFinalY() {
        return realFinalY;
    }

    public void setRealFinalY(int realFinalY) {
        this.realFinalY = realFinalY;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"GUIEventVO\" :{\"duration\":\"" + duration + "\", \"initialX\":\"" + initialX
                + "\", \"initialY\":\"" + initialY + "\", \"finalX\":\"" + finalX + "\", \"finalY\":\"" + finalY
                + "\", \"realInitialX\":\"" + realInitialX + "\", \"realInitialY\":\"" + realInitialY
                + "\", \"realFinalX\":\"" + realFinalX + "\", \"realFinalY\":\"" + realFinalY + "\", \"direction\":\""
                + direction + "\", \"eventTypeId\":\"" + eventTypeId + "\", \"eventLabel\":\"" + eventLabel
                + "\", \"object\":\"" + object + "\", \"text\":\"" + text + "\", \"activity\":\"" + activity
                + "\", \"hvInfoComponent\":\"" + hvInfoComponent + "\", \"areaList\":\"" + areaList
                + "\", \"areaEdit\":\"" + areaEdit + "\", \"areaView\":\"" + areaView + "\", \"areaSelect\":\""
                + areaSelect + "\"}}";
    }

    /**
     * @return the hvInfoComponent
     */
    public DynGuiComponentVO getHvInfoComponent() {
        return hvInfoComponent;
    }

    /**
     * @param hvInfoComponent
     *            the hvInfoComponent to set
     */
    public void setHvInfoComponent(DynGuiComponentVO hvInfoComponent) {
        this.hvInfoComponent = hvInfoComponent;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the areaList
     */
    public int getAreaList() {
        return areaList;
    }

    /**
     * @param areaList
     *            the areaList to set
     */
    public void setAreaList(int areaList) {
        this.areaList = areaList;
    }

    /**
     * @return the areaEdit
     */
    public int getAreaEdit() {
        return areaEdit;
    }

    /**
     * @param areaEdit
     *            the areaEdit to set
     */
    public void setAreaEdit(int areaEdit) {
        this.areaEdit = areaEdit;
    }

    /**
     * @return the areaView
     */
    public int getAreaView() {
        return areaView;
    }

    /**
     * @param areaView
     *            the areaView to set
     */
    public void setAreaView(int areaView) {
        this.areaView = areaView;
    }

    /**
     * @return the areaSelect
     */
    public int getAreaSelect() {
        return areaSelect;
    }

    /**
     * @param areaSelect
     *            the areaSelect to set
     */
    public void setAreaSelect(int areaSelect) {
        this.areaSelect = areaSelect;
    }

}
