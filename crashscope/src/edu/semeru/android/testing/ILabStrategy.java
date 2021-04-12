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
 * ILabStrategy.java
 * 
 * Created on Aug 6, 2014, 2:14:18 PM
 */
package edu.semeru.android.testing;

import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.core.model.Transition;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 6, 2014
 */
public interface ILabStrategy {

    /**
     * Return the next step to be executed
     * 
     * @param appPackage
     * @param mainActivity
     * @return
     */
    public GUIEventVO getNextStep(String appPackage, String mainActivity, String executionType, int stepCounter,
            int executionCounter);

    /**
     * Analyze the state of the application and store all the data
     * 
     * @param appPackage
     * @param mainActivity
     */
    public Transition checkNewState(String appPackage, String mainActivity, DynGuiComponentVO gui, int eventType, String executionType, int executionCtr);

    /**
     * Executes the step using adb command
     * 
     * @param step
     * @param appPackage
     */
    // public DynGuiComponent executeStep(GUIEventVO step, String appPackage);
    public Step executeStep(GUIEventVO step, String appPackage, String executionType);

    /**
     * This method should start profiler, launch app, clean logcat, etc.
     * 
     * @param androidSDKPath
     * @param appPackage
     * @param mainActivity
     */
    public void setUpApp(String androidSDKPath, String appPackage, String mainActivity);

    public void checkForCrash(String appPackage, String mainActivity, String androidSDKPath);

}

