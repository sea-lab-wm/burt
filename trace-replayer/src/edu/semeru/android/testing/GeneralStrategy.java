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
 * GeneralStrategy.java
 * 
 * Created on Sep 26, 2014, 7:11:26 PM
 */
package edu.semeru.android.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.semeru.android.testing.helpers.StepByStepEngine;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.LogHelper;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Sep 26, 2014
 */
public abstract class GeneralStrategy extends AbstractStrategy implements ILabStrategy {

    /**
     * @param step
     */
    final public void executeStep(GUIEventVO step, String androidSDKPath, String appPackage, String executionType) {
        // Execute step
        String executeEvent = StepByStepEngine.executeEvent(step, androidSDKPath, appPackage, executionType);
        LogHelper.getInstance(
                "commands" + File.separator + this.getClass().getSimpleName() + File.separator + appPackage + "_"
                        + getIdExecution()).addLine(executeEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.semeru.android.testing.monkeylab.strategy.ILabStrategy#getNextStep
     * (java.lang .String, java.lang.String)
     */
    @Override
    public void checkForCrash(String appPackage, String mainActivity, String androidSDKPath) {
        // Check new state and update the list of components
        System.out.println("Checking for Crash...");
        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoNoCache(androidSDKPath, widthScreen,
                heightScreen, true, false);
        for (DynGuiComponentVO dynGuiComponent : nodes) {
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("has stopped.")) {
                System.out.println("CRASH");
                crash = true;
            }
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("Complete action using")) {
                StepByStepEngine.doKey(androidSDKPath, StepByStepEngine.BACK + "", appPackage);
            }
            if (dynGuiComponent.getName().endsWith("Button") && crash) {
                // Execute the Okay button to dismiss the crash dialog
                System.out.println("Dismissing the Crash Dialog");
                GUIEventVO eventFromComponent = getEventFromComponent(dynGuiComponent, StepByStepEngine.CLICK);
                StepByStepEngine.executeEvent(eventFromComponent, androidSDKPath, appPackage, null);
                StepByStepEngine.stopAPK(androidSDKPath, appPackage);
                StepByStepEngine.startAPK(androidSDKPath, appPackage, mainActivity);
            }
        }
    }

    protected GUIEventVO getEventFromComponent(DynGuiComponentVO hvComponentVO, int type) {
        GUIEventVO vo = new GUIEventVO();
        try {
            // deepClone will enter infinite loop if the parent and child information is included
            // in the DynGUIComponentVO. Therefore, we temporarily unset this information here to 
            // aviod this case.  We reset the information after in case we want to use the hierarchical
            // relationships in the future.
            DynGuiComponentVO tempParent = hvComponentVO.getParent();
            List<DynGuiComponentVO> tempChildren = hvComponentVO.getChildren();
            hvComponentVO.setChildren(null);
            hvComponentVO.setParent(null);
            vo.setHvInfoComponent((DynGuiComponentVO) ClonerHelper.deepClone(hvComponentVO));
            hvComponentVO.setChildren(tempChildren);
            hvComponentVO.setParent(tempParent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vo.setRealInitialX((hvComponentVO.getWidth() / 2) + hvComponentVO.getPositionX());
        vo.setRealInitialY((hvComponentVO.getHeight() / 2) + hvComponentVO.getPositionY());
        vo.setEventTypeId(type);
        if (type == StepByStepEngine.SWIPE_LEFT) {
            vo.setRealFinalX((hvComponentVO.getWidth() / 8) + hvComponentVO.getPositionX());
            vo.setRealFinalY((hvComponentVO.getHeight() / 2) + hvComponentVO.getPositionY());
            vo.setEventTypeId(StepByStepEngine.SWIPE);
        } else if (type == StepByStepEngine.SWIPE_RIGHT) {
            vo.setRealFinalX(hvComponentVO.getWidth() + hvComponentVO.getPositionX() - (hvComponentVO.getWidth() / 8));
            vo.setRealFinalY((hvComponentVO.getHeight() / 2) + hvComponentVO.getPositionY());
            vo.setEventTypeId(StepByStepEngine.SWIPE);
        } else {
            vo.setRealFinalX((hvComponentVO.getWidth() / 2) + hvComponentVO.getPositionX());
            vo.setRealFinalY((hvComponentVO.getHeight() / 2) + hvComponentVO.getPositionY());
        }
        return vo;
    }
    
    protected GUIEventVO getEventFromComponentSwipe(DynGuiComponentVO hvComponentVO, int type, int [] swipe) {
        GUIEventVO vo = new GUIEventVO();
        try {
            // deepClone will enter infinite loop if the parent and child information is included
            // in the DynGUIComponentVO. Therefore, we temporarily unset this information here to 
            // aviod this case.  We reset the information after in case we want to use the hierarchical
            // relationships in the future.
            DynGuiComponentVO tempParent = hvComponentVO.getParent();
            List<DynGuiComponentVO> tempChildren = hvComponentVO.getChildren();
            hvComponentVO.setChildren(null);
            hvComponentVO.setParent(null);
            vo.setHvInfoComponent((DynGuiComponentVO) ClonerHelper.deepClone(hvComponentVO));
            hvComponentVO.setChildren(tempChildren);
            hvComponentVO.setParent(tempParent);
        } catch (Exception e) {
            e.printStackTrace();
        }
       vo.setInitialX(swipe[0]);
       vo.setInitialY(swipe[1]);
       vo.setFinalX(swipe[2]);
       vo.setFinalY(swipe[3]);
        return vo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.semeru.android.testing.monkeylab.strategy.ILabStrategy#setUpApp()
     */
    @Override
    public void setUpApp(String androidSDKPath, String appPackage, String mainActivity) {
        // StepByStepEngine.startAPK(androidSDKPath, appPackage, mainActivity);
        // checkNewState(appPackage, mainActivity, null);
    }

}

