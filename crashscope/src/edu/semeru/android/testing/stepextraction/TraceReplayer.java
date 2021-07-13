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
package edu.semeru.android.testing.stepextraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import edu.semeru.android.testing.helpers.DeviceInfo;
import edu.semeru.android.testing.helpers.StepByStepEngine;
import edu.semeru.android.testing.helpers.Utilities;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.testing.helpers.UiAutoConnector.TypeDeviceEnum;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.core.model.WindowVO;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.EventsFormatter;

/**
 * 
 * This class generates takes as input a getevent file, and generates
 * a list of tuples that contain information about each step performed
 * on a device.
 *
 * @author Kevin Moran & Carlos Bernal
 * @since Feb 1, 2015
 */
public class TraceReplayer {

    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        String androidSDKPath = "/Applications/AndroidSDK/sdk";
        String pythonScriptsPath = "/Users/KevinMoran/Desktop/Replayer/AndroidTraceReplayer/lib/python-scripts";
        String device = ""; // If more than one emulator
        
        String appName = "6pm";
        String appPackage = "com.zappos.android.sixpmFlavor";
        String mainActivity = "com.zappos.android.activities.HomeActivity";
        String apkPath = "/Users/KevinMoran/Dropbox/Documents/My_Faculty_Work/SAGE/Android-Test-Case-Reuse.project/Data-Collection-Study/APKs/Shopping/6pm.apk";
        String geteventFile = "/Users/KevinMoran/Dropbox/Documents/My_Faculty_Work/SAGE/Android-Test-Case-Reuse.project/Data-Collection-Study/SWE-632-F20-Android-User-Study/p16/6pm/getevent-detail-1.log";
        String outputFolder = "/Users/KevinMoran/Desktop/test-output";


        boolean install = true;
        boolean screenshot = false;
        boolean keyboardActive = false;
        GUIEventVO oldComponent = null;

        ArrayList<Integer> screenDims = null;
        ArrayList<Integer> maxLinuxScreenDims = null;

            // Get dimensions
            screenDims = Utilities.getScreenDimensions(androidSDKPath, device);
            System.out.println(screenDims.toString());
            maxLinuxScreenDims = Utilities.getMaxScreenAbsValues(androidSDKPath, device);
            System.out.println(maxLinuxScreenDims.toString());
            // navBarDims = Utilities.getStatusBarDimensions();

               

                    new File(outputFolder + File.separator).mkdirs();
                    List<GUIEventVO> events = new ArrayList<GUIEventVO>();


                            File stepsFile = new File(outputFolder + File.separator + appName + "-" + String.valueOf(System.currentTimeMillis()) + "-trace.txt");
                            BufferedWriter writer = new BufferedWriter(new FileWriter(stepsFile));

                            System.out.println("- Setting up the app " + appName);
                            if (install) {
                                StepByStepEngine.unInstallAndInstallApp(androidSDKPath, apkPath, appPackage, device);
                            } else {
                                StepByStepEngine.clearAppData(androidSDKPath, appPackage, device);
                            }

                            System.out.println("- Getting step-by-step statements for file " + geteventFile);
                            ArrayList<GUIEventVO> guiEvents = StepByStepEngine.getStepByStepStatementsFromEventLog(
                                    geteventFile, pythonScriptsPath);

                            System.out.println("- Starting step-by-step execution (" + geteventFile + ") for " + appPackage
                                    + ", using ");

                           
                            StepByStepEngine.startAPK(androidSDKPath, appPackage, mainActivity, device);
                            

                            int i = 0;
                            GUIEventVO vo = null;
                            for (GUIEventVO guiEventVO : guiEvents) {
                                // updated according to
                                // https://source.android.com/devices/input/touch-devices.html#touch-device-driver-requirements
                                long realInitialX = Math.round((guiEventVO.getInitialX())
                                        / (maxLinuxScreenDims.get(0) + 10d) * screenDims.get(0));
                                long realInitialY = Math.round((guiEventVO.getInitialY())
                                        / (maxLinuxScreenDims.get(1) + 10d) * screenDims.get(1));

                                long realFinalX = Math.round((guiEventVO.getFinalX())
                                        / (maxLinuxScreenDims.get(0) + 10d) * screenDims.get(0));
                                long realFinalY = Math.round((guiEventVO.getFinalY())
                                        / (maxLinuxScreenDims.get(1) + 10d) * screenDims.get(1));

                                guiEventVO.setRealInitialX((int) realInitialX);
                                guiEventVO.setRealInitialY((int) realInitialY);

                                guiEventVO.setRealFinalX((int) realFinalX);
                                guiEventVO.setRealFinalY((int) realFinalY);

                              
                                Thread.sleep(2000);
     
                                WindowVO window = Utilities.detectTypeofWindow(androidSDKPath, screenDims.get(0),
                                        screenDims.get(1), TypeDeviceEnum.DEVICE, device, null);
                                // System.out.println(window.getTitle());
                                // Set the best component matching
                                UiAutoConnector.getComponent(androidSDKPath, guiEventVO, screenDims.get(0),
                                        screenDims.get(1), device);
                                String title = ((window.getWindow() + (window.getTitle() != null
                                        && !window.getTitle().isEmpty() ? window.getTitle() : ""))).trim();
                                guiEventVO.setActivity(title);
                                guiEventVO.getHvInfoComponent().setTitleWindow(window.getTitle());
                                UiAutoConnector.setComponentAreas(guiEventVO, androidSDKPath, screenDims.get(0),
                                        screenDims.get(1));
                                // Extract screenshot, DO NOT change the order
                                // of these lines
                                String imageName = appPackage + "_" + (i + 1) + ".jpg";
                                if (screenshot) {
                                    Utilities.getAndPullScreenshot(androidSDKPath, outputFolder + File.separator
                                            + geteventFile.replace(".log", ""), imageName);
                                }
                                System.out.println(guiEventVO.getHvInfoComponent());
                                // Is Keyboard component?
                                if (guiEventVO != null
                                        && guiEventVO.getHvInfoComponent().getIdXml().equals("id/keyboard_view")) {
                                    if (!keyboardActive && oldComponent != null) {
                                        vo = (GUIEventVO) Utilities.cloneObject(oldComponent);
                                        events.add(events.size(), vo);
                                    }
                                    keyboardActive = true;
                                } else {
                                    if (keyboardActive) {
                                        ArrayList<DynGuiComponentVO> screenInfoCache = UiAutoConnector
                                                .getScreenInfoNoCache(androidSDKPath, screenDims.get(0),
                                                        screenDims.get(1), false, false);
                                        for (DynGuiComponentVO component : screenInfoCache) {
                                            if (component.getIdXml().equals(vo.getHvInfoComponent().getIdXml())) {
                                                DynGuiComponentVO deepClone = ClonerHelper
                                                        .deepClone(DeviceInfo.NEXUS5X_KEYBOARD);
                                                deepClone.setTitleWindow(window.getTitle());
                                                deepClone.setGuiScreenshot(appPackage + "_" + i + ".jpg");
                                                deepClone.setActivity(component.getActivity());
                                                deepClone.setText(component.getText());

                                                vo.setHvInfoComponent(deepClone);
                                                vo.setText(component.getText());

                                                vo.setEventLabel("TYPE");
                                                vo.setEventTypeId(StepByStepEngine.TYPE);
                                                writer.write(EventsFormatter.format4Steps(vo));
                                                writer.newLine();
                                                writer.flush();
                                                break;
                                            }
                                        }
                                        keyboardActive = false;
                                    }
                                }

                                StepByStepEngine.executeEvent(guiEventVO, androidSDKPath, appPackage, null, false, device);
                                Thread.sleep(1500);
                                // Set screenshot
                                guiEventVO.getHvInfoComponent().setGuiScreenshot(imageName);
   
                                System.out.println("Step: " + i);
                                i++;

                                
                                //Right now we are not recording keyboard events
                                if (!keyboardActive) {
                                    events.add(guiEventVO);
                                    oldComponent = guiEventVO;
                                    // writer.write(EventsFormatter.format4CollectorFile(guiEventVO));
                                    writer.write(EventsFormatter.format4Steps(guiEventVO));
                                    writer.newLine();
                                    writer.flush();
                                }

                            }



                            writer.close();

                            System.out.println("- Final steps (pulling files from device and stopping profiler/app)");


                                StepByStepEngine.stopAPK(androidSDKPath, appPackage, device);
  

                            System.out.println("- DONE");
                            System.out.println("----------------------------------------------------");



        }
    
}
