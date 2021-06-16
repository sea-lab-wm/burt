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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import edu.semeru.android.testing.helpers.DeviceInfo;
import edu.semeru.android.testing.helpers.StepByStepEngine;
import edu.semeru.android.testing.helpers.Utilities;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.testing.helpers.UiAutoConnector.TypeDeviceEnum;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.core.model.Transition;
import edu.semeru.android.core.model.WindowVO;
import edu.semeru.android.testing.CrashScope;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.DeviceHelper;
import edu.semeru.android.testing.helpers.EventsFormatter;
import edu.semeru.android.testing.helpers.ScreenActionData;
import edu.semeru.android.testing.helpers.ScreenshotModifier;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.DynamicTransition;
import edu.semeru.android.core.entity.model.fusion.Screen;

import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.entity.model.fusion.Execution;


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
	private List<Step> steps = new ArrayList<Step>();   // Holds the steps of the current execution
    private DeviceHelper deviceHelper;  // Provides APIs to interface with an Android device 
    private int sequence = 0;
    public boolean takeScreenshots = false;
    private String guiStrat = "";   // Defines the strategy for interacting with GUI components
    private String textStrat = "";  // Defines the text input generation strategy
    private String featStrat = "";
    private int executionCtr = 1;
    private boolean firststep;
    private boolean lastStepRotated;
    private ReplayerFeatures replayerFeatures;
    private String androidSDKPath;
//    private String pythonScriptsPath;
//    private String appName;
//    private String appPackage;
//    private String mainActivity;
//    private String apkPath;
//    private String geteventFile;
//    private String outputFolder;
    
    
    public TraceReplayer() {
    	
    }
    
//	public TraceReplayer(String pythonScriptsPath, String appName, String appPackage, 
//			String mainActivity, String apkPath, String geteventFile, String outputFolder) {
//		super();
//		
//		this.pythonScriptsPath = pythonScriptsPath;
//		this.appName = appName;
//		this.appPackage = appPackage;
//		this.mainActivity = mainActivity;
//		this.apkPath = apkPath;
//		this.geteventFile = geteventFile;
//		this.outputFolder = outputFolder;
//	}
	
	
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
        TraceReplayer tracereplayer = new TraceReplayer();
        tracereplayer.runTraceReplayer();

    }
    
    public void runTraceReplayer() throws Exception{
    	androidSDKPath = "/Users/junayed/Library/Android/sdk";
        String pythonScriptsPath = "/Users/junayed/Documents/NecessaryDocs/GeorgeMasonUniversity/KevinMoran/BugReporting/CrashScope/burt/crashscope/lib/python-scripts";
        String scriptsPath = "/Users/junayed/Documents/NecessaryDocs/GeorgeMasonUniversity/KevinMoran/BugReporting/CrashScope/burt/crashscope/scripts";
        String device = ""; // If more than one emulator
        
        String appName = "6pm";
        String appPackage = "com.zappos.android.sixpmFlavor";
        String appVersion = "";
        String mainActivity = "com.zappos.android.activities.HomeActivity";
        String apkPath = "/Users/junayed/Documents/NecessaryDocs/GeorgeMasonUniversity/KevinMoran/BugReporting/UserData/participant-16/APKs/6pm.apk";
        String geteventFile = "/Users/junayed/Documents/NecessaryDocs/GeorgeMasonUniversity/KevinMoran/BugReporting/UserData/participant-16/6pm/getevent-detail-1.log";
        String outputFolder = "/Users/junayed/Documents/NecessaryDocs/GeorgeMasonUniversity/KevinMoran/BugReporting/test-output";
        
        String avdPort = "5554";
        String adbPort = "5037";
        TypeDeviceEnum deviceType = UiAutoConnector.TypeDeviceEnum.EMULATOR;
        DeviceHelper deviceHelper = new DeviceHelper(deviceType, androidSDKPath, avdPort, adbPort);
    	steps.clear();
        boolean install = true;
        
        boolean keyboardActive = false;
        lastStepRotated = false;
        takeScreenshots = true;
        GUIEventVO oldComponent = null;

        ArrayList<Integer> screenDims = null;
        ArrayList<Integer> maxLinuxScreenDims = null;
        
        Execution execution = new Execution();
        App app = new App();
        app.setName(appName);
        app.setPackageName(appPackage);
        app.setMainActivity(mainActivity);
        app.setApkPath(apkPath);
        
        execution.setApp(app);
        execution.setDeviceDimensions(Utilities.getScreenSize(androidSDKPath));
        execution.setOrientation(Utilities.getOrientation(androidSDKPath));
        execution.setAndroidVersion(Utilities.getAndroidVersion(androidSDKPath));
        execution.setDeviceName(Utilities.getDeviceVersion(androidSDKPath));
        execution.setMainActivity(app.getMainActivity());
        execution.setExecutionType("User-Trace");
        
        replayerFeatures = new ReplayerFeatures();
        
        replayerFeatures.setWidthScreen(Integer.parseInt(execution.getDeviceDimensions().split("x")[0]));
        replayerFeatures.setHeightScreen(Integer.parseInt(execution.getDeviceDimensions().split("x")[1]));
        replayerFeatures.setUiDumpLocation(outputFolder + File.separator + app.getPackageName() + "-" + executionCtr);

        // Get dimensions
        screenDims = Utilities.getScreenDimensions(androidSDKPath, device);
        System.out.println(screenDims.toString());
        maxLinuxScreenDims = Utilities.getMaxScreenAbsValues(androidSDKPath, device);
        System.out.println(maxLinuxScreenDims.toString());
        // navBarDims = Utilities.getStatusBarDimensions();
        
        long startTime = System.currentTimeMillis();

       
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
        
        if(takeScreenshots) {
        	String screenshot = appPackage + "_" + appVersion + "_gnucash" + sequence + ".png";        
            Utilities.getAndPullScreenshot(androidSDKPath, outputFolder + File.separator
                    + "screenshots", appPackage + "."
                            + executionCtr + "." + screenshot);
        }
        
        int screenWidth = replayerFeatures.getWidthScreen();
        int screenHeight = replayerFeatures.getHeightScreen();
        String executionType = "User-Trace";

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

                          
            //Thread.sleep(5000);
            
            WindowVO window = Utilities.detectTypeofWindow(androidSDKPath, screenDims.get(0),
                                    screenDims.get(1), TypeDeviceEnum.DEVICE, device, null);
            // System.out.println(window.getTitle());
            // Set the best component matching
            UiAutoConnector.getComponent(androidSDKPath, guiEventVO, screenDims.get(0),
                    screenDims.get(1), device, replayerFeatures.getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + sequence);
            String title = ((window.getWindow() + (window.getTitle() != null
                    && !window.getTitle().isEmpty() ? window.getTitle() : ""))).trim();
            //guiEventVO.setActivity(title);
            guiEventVO.getHvInfoComponent().setTitleWindow(window.getTitle());
            UiAutoConnector.setComponentAreas(guiEventVO, androidSDKPath, screenDims.get(0),
                    screenDims.get(1));
            // Extract screenshot, DO NOT change the order
            // of these lines
            //String imageName = appPackage + "_" + (i + 1) + ".jpg";

            System.out.println(guiEventVO.getHvInfoComponent());

            
            

            
            
            replayerFeatures.setCorrectAugScreen(false);
            
            String textEntry = StepByStepEngine.executeEvent(guiEventVO, androidSDKPath, appPackage, null, false, device);
            Thread.sleep(3000);
            

            
            //Thread.sleep(1500);
            // Set screenshot
            //guiEventVO.getHvInfoComponent().setGuiScreenshot(imageName);
            

            
//            Screen screen = new Screen();
//            screen.setActivity(Utilities.getCurrentActivity(androidSDKPath));
//            screen.setWindow(window.getWindow());
//            screen.setDynGuiComponents(StepByStepEngine.getEntityFromVO(screenInfoCache));
                        
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
                                    screenDims.get(1), true, false);
                    ArrayList<DynGuiComponentVO> screenInfoEmulator = UiAutoConnector.getScreenInfoEmulator(androidSDKPath, screenWidth,
                            screenHeight, true, false, false, avdPort,
                            adbPort, replayerFeatures.getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + sequence);
                    
                    for (DynGuiComponentVO component : screenInfoEmulator) {
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
                            
                            
                            i++;
                            break;
                        }
                    }
                    keyboardActive = false;
                    
                    
                }
            }


            
            
            System.out.println("Step: " + (i+1));
            
            
            
            
            
            //Right now we are not recording keyboard events
            if (!keyboardActive) {
                events.add(guiEventVO);
                oldComponent = guiEventVO;
                // writer.write(EventsFormatter.format4CollectorFile(guiEventVO));
                writer.write(EventsFormatter.format4Steps(guiEventVO));
                writer.newLine();
                writer.flush();
                

                
                i++;
            }
            
            sequence++;
            
            Step step = StepByStepEngine.getStepFromEvent(guiEventVO);
            
            String screenshot = appPackage + "_" + appVersion + "_gnucash" + sequence + ".png";     
            
            String currstep = Integer.toString(sequence - 1);   // Here the current step is actually the current sequence minus 1

            String currscreenshot = appPackage + "_" + appVersion + "_gnucash" + currstep + ".png";

            Utilities.getAndPullScreenshot(androidSDKPath, outputFolder + File.separator
                    + "screenshots", appPackage + "."
                            + executionCtr + "." + screenshot);
            
            if (step != null) {
                ScreenActionData data = null;
                switch (step.getAction()) {
	                case StepByStepEngine.SWIPE:
	                    data = new ScreenActionData(step.getAction(), screenWidth / 2, (int) (screenHeight * .1),
	                            screenWidth / 2, (int) (screenHeight * .8), screenWidth, screenHeight);
	                    ScreenshotModifier.augmentScreenShot(outputFolder + File.separator + "screenshots" + File.separator + appPackage + "."
	                            + executionCtr + "." + currscreenshot, outputFolder + File.separator + "screenshots" + File.separator + appPackage + "."
	                                     + executionCtr + "."
	                                    + screenshot.replace(".png", "_augmented.png"), data);
	                    step.setScreenshot(appPackage + "."
	                            + executionCtr + "." + screenshot.replace(".png", "_augmented.png"));
	                    break;
	                default:
	                    System.out.println("Augmenting the Previous Step # " + sequence + " Screenshot");
	                    //System.out.println("--- Augmenting " + currscreenshot); //For debugging
	                    if(step.getDynGuiComponent() != null){
	                        data = new ScreenActionData(step.getAction(), (step.getDynGuiComponent().getWidth() / 2)
	                                + step.getDynGuiComponent().getPositionX(), (step.getDynGuiComponent().getHeight() / 2)
	                                + step.getDynGuiComponent().getPositionY(), screenWidth, screenHeight);
	
	                        ScreenshotModifier.augmentScreenShot(outputFolder + File.separator + "screenshots" + File.separator + appPackage + "."
	                               + executionCtr + "." + currscreenshot, outputFolder + File.separator + "screenshots" + File.separator + appPackage + "."
	                                       + executionCtr + "."
	                                        + screenshot.replace(".png", "_augmented.png"), data);
	                        step.setScreenshot(appPackage + "."
	                        		+ executionCtr + "." + screenshot.replace(".png", "_augmented.png"));
	                        break;
	                    } else{
	                        step.setScreenshot(appPackage + "."
	                                + executionCtr + "." + screenshot);
	                    }
                }
            }
            
            DynGuiComponent dynGuiComponent = step.getDynGuiComponent();
            
            String guiScreenShot = null;
            
            if (dynGuiComponent != null) {
                ScreenActionData data = null;
                Image orig = null;
                boolean ok = false;
                do {
                    try {
                        //https://stackoverflow.com/questions/13796611/imageio-read-with-mac
                    	System.out.println(outputFolder + File.separator + "screenshots" + File.separator + screenshot);
                        orig = ImageIO.read(new File(outputFolder + File.separator + "screenshots" + File.separator +  appPackage + "."
                                + executionCtr + "." + currscreenshot));
                        ok = true;
                    } catch (IOException e) {
                        ok = false;
                        e.printStackTrace();
                    }
                } while (!ok);

                int x = dynGuiComponent.getPositionX();
                int y = dynGuiComponent.getPositionY();
                int w = dynGuiComponent.getWidth();
                int h = dynGuiComponent.getHeight();

                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                bi.getGraphics().drawImage(orig, 0, 0, w, h, x, y, x + w, y + h, null);

                try {
                    ImageIO.write(bi, "png",
                            new File( outputFolder + File.separator + "screenshots" + File.separator + appPackage + "."
                                    + executionCtr + "."+  screenshot.replaceAll(".png", "_gui.png")));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                guiScreenShot = screenshot.replaceAll(".png", "_gui.png");

            }

        }
        
        



        writer.close();

        System.out.println("- Final steps (pulling files from device and stopping profiler/app)");

        
            

        execution.setSteps(getSteps());
        execution.setCrash(false);
        
        long endTime = System.currentTimeMillis();
        execution.setElapsedTime(endTime - startTime);
        
        System.out.println(execution);
        
        Gson gson = new Gson();
        String json = gson.toJson(execution);
        PrintWriter gsonWriter = new PrintWriter(outputFolder + File.separator + "Execution-" + "3" + ".json");
        gsonWriter.println(json);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        gsonWriter.close();
        
        StepByStepEngine.stopAPK(androidSDKPath, appPackage, device);

        System.out.println("- DONE");
        System.out.println("----------------------------------------------------");


    }
    
    public Step addStep(DynGuiComponent component, String textEntry, int action, Screen screen) {
    	sequence++;
    	Step step = new Step();

        step.setSequenceStep(sequence);
        step.setDynGuiComponent(component);
        step.setAction(action);
        step.setTextEntry(textEntry);
        
        if(screen!= null){
            step.setScreen(screen);
            screen.setStep(step);
        }

        steps.add(step);
        return step;
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

}
