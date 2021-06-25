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
package sealab.burt.qualitychecker.actionparser;

import edu.semeru.android.core.entity.model.App;
import sealab.burt.nlparser.euler.actions.utils.GeneralUtils;
import sealab.burt.nlparser.euler.actions.DeviceActions;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class DevServerCommand {

    private Long componentId;
    private int event;
    private String text;
    private Long screenId;

    public DevServerCommand(Long componentId, int event, String text, Long screenId) {
        super();
        this.componentId = componentId;
        this.event = event;
        this.text = text;
        this.screenId = screenId;
    }

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }

    /**
     * @return the id
     */
    public Long getComponentId() {
        return componentId;
    }

    /**
     * @param componentId
     *            the id to set
     */
    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    /**
     * @return the event
     */
    public int getEvent() {
        return event;
    }

    /**
     * @param event
     *            the event to set
     */
    public void setEvent(int event) {
        this.event = event;
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

    public boolean isOpenAppCommand(){
        return DeviceActions.OPEN_APP == this.event;
    }

    public static DevServerCommand getOpenAppCommand(App app){
        return new DevServerCommand(0l, DeviceActions.OPEN_APP, app.getPackageName(), null);
    }

    @Override
    public String toString() {
        return "cm{" +
                "c=" + componentId +
                ", e=(" + event +") "+ GeneralUtils.getEventName(event) +
                ", t=" + text  +
                ", s=" + screenId  +
                '}';
    }


}
