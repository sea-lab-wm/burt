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
            //guiEventVO.setActivity(title);
            guiEventVO.getHvInfoComponent().setTitleWindow(window.getTitle());
            UiAutoConnector.setComponentAreas(guiEventVO, androidSDKPath, screenDims.get(0),
                    screenDims.get(1));
            // Extract screenshot, DO NOT change the order
            // of these lines
            //String imageName = appPackage + "_" + (i + 1) + ".jpg";

            System.out.println(guiEventVO.getHvInfoComponent());
            ArrayList<DynGuiComponentVO> screenInfoCache = UiAutoConnector
                    .getScreenInfoNoCache(androidSDKPath, screenDims.get(0),
                            screenDims.get(1), false, false);
            
            String textEntry = StepByStepEngine.executeEvent(guiEventVO, androidSDKPath, appPackage, null, false, device);

            Thread.sleep(1500);
            // Set screenshot
            //guiEventVO.getHvInfoComponent().setGuiScreenshot(imageName);
            
            int screenWidth = replayerFeatures.getWidthScreen();
            int screenHeight = replayerFeatures.getHeightScreen();
            String executionType = "User-Trace";
            
            Screen screen = new Screen();
            screen.setActivity(Utilities.getCurrentActivity(androidSDKPath));
            screen.setWindow(window.getWindow());
            screen.setDynGuiComponents(StepByStepEngine.getEntityFromVO(screenInfoCache));
                        
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
                            
                            DynGuiComponent component2 = StepByStepEngine.getEntityFromVO(vo.getHvInfoComponent());
                            
                            Step step = addStep(component2, textEntry,
                            		vo.getEventTypeId(), screen);
                          
                            takeScreenshot(appPackage, outputFolder, appVersion, screenWidth, screenHeight, step,
                                    executionType, executionCtr);
                            
                            String guiScreenshot = cropScreenshot(appPackage, androidSDKPath, outputFolder, appVersion, 
                            		screenWidth, screenHeight, step,
                            		component2, true,
                            		executionType, executionCtr, lastStepRotated);
                            
                            //If screenshots are enabled, set the cropped screenshot to the GUI component
                            if (component2 != null && guiScreenshot != null && !guiScreenshot.equals(null)) {
                                component2.setGuiScreenshot(guiScreenshot);
                            } else {
                                //System.out.println("Screenshots Disabled!"); //For debugging
                                component2.setGuiScreenshot("none");
                                
                            }
                            replayerFeatures.setCorrectAugScreen(false);
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
                
                DynGuiComponent component = StepByStepEngine.getEntityFromVO(guiEventVO.getHvInfoComponent());
                
                Step step = addStep(component, textEntry,
                		guiEventVO.getEventTypeId(), screen);
              
                takeScreenshot(appPackage, outputFolder, appVersion, screenWidth, screenHeight, step,
                        executionType, executionCtr);
                
                String guiScreenshot = cropScreenshot(appPackage, androidSDKPath, outputFolder, appVersion, 
                		screenWidth, screenHeight, step,
                		component, true,
                		executionType, executionCtr, lastStepRotated);
                
                //If screenshots are enabled, set the cropped screenshot to the GUI component
                if (component != null && guiScreenshot != null && !guiScreenshot.equals(null)) {
                    component.setGuiScreenshot(guiScreenshot);
                } else {
                    //System.out.println("Screenshots Disabled!"); //For debugging
                    component.setGuiScreenshot("none");
                    
                }
                replayerFeatures.setCorrectAugScreen(false);
                i++;
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
    
    /**
     * Description: Takes three types of screenshots and saves them to appropriate data objects.  First, this
     * method takes a normal screenshot and saves it to disk.  Then it generates an augmented screenshot and cropped
     * screenshot.  The augmented screenshot contains an annotation for the bounding box of the component that was 
     * interacted with in the Step object. The cropped screenshot is a cropped version of the full screenshot containing
     * just an image of the component that can later be used in the crash report.
     * 
     * @param appPackage
     * @param androidSDKPath
     * @param folderScreenshots
     * @param execution
     * @param dfs
     * @param step
     * @throws IOException
     */
    private void takeScreenshot(String appPackage, String folderScreenshots, String version, int screenWidth, int screenHeight, Step step,
            String executionType, int executionCtr) {
        //Check to see if screenshots are enabled
        if (takeScreenshots) {
            try {   // Try/catch for image processing
                String currstep = Integer.toString(sequence - 1);   // Here the current step is actually the current sequence minus 1

                String screenshot = appPackage + "_" + version + "_gnucash" + sequence + ".png";
                String currscreenshot = appPackage + "_" + version + "_gnucash" + currstep + ".png";

                // Check if the last executed step was a rotation, if so we need to roll back the 
                // sequences to get the proper existing screenshots
                if (replayerFeatures.isCorrectAugScreen() == true){
                    //System.out.println("last step rotated fixing GUI screenshot"); //For Debugging
                    String step1 = Integer.toString(sequence - 1);
                    String step2 = Integer.toString(sequence - 2);
                    screenshot = appPackage + "_" + version + "_gnucash" + step1 + ".png";
                    currscreenshot = appPackage + "_" + version + "_gnucash" + step2 + ".png";
                    replayerFeatures.setCorrectAugScreen(false);
                }else if (step != null && step.getAction() == 3){
                    // If the last step was a typing step, we need to reset the screenshot paths
                    //System.out.println("last step was typing"); //For debugging
                    String step1 = Integer.toString(sequence);
                    String step2 = Integer.toString(sequence);
                    screenshot = appPackage + "_" + version + "_gnucash" + step1 + ".png";
                    currscreenshot = appPackage + "_" + version + "_gnucash" + step2 + ".png";
                    replayerFeatures.setCorrectAugScreen(false);
                }

                System.out.println("Processing Screenshot for current Step " + sequence);

                Utilities.getAndPullScreenshot(androidSDKPath, folderScreenshots + File.separator
                        + "screenshots", appPackage + "."
                                + executionCtr + "." + screenshot);
                if (step != null) {
                    ScreenActionData data = null;
                    switch (step.getAction()) {
                    case StepByStepEngine.SWIPE:
                        data = new ScreenActionData(step.getAction(), screenWidth / 2, (int) (screenHeight * .1),
                                screenWidth / 2, (int) (screenHeight * .8), screenWidth, screenHeight);
                        ScreenshotModifier.augmentScreenShot(folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                + executionCtr + "." +currscreenshot, folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                         + executionCtr + "."
                                        + screenshot.replace(".png", "_augmented.png"), data);
                        step.setScreenshot(appPackage + "."
                                + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + screenshot.replace(".png", "_augmented.png"));
                        break;
                    default:
                        System.out.println("Augmenting the Previous Step # " + currstep + " Screenshot");
                        //System.out.println("--- Augmenting " + currscreenshot); //For debugging
                        if(step.getDynGuiComponent() != null){
                            data = new ScreenActionData(step.getAction(), (step.getDynGuiComponent().getWidth() / 2)
                                    + step.getDynGuiComponent().getPositionX(), (step.getDynGuiComponent().getHeight() / 2)
                                    + step.getDynGuiComponent().getPositionY(), screenWidth, screenHeight);

                            ScreenshotModifier.augmentScreenShot(folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                   + executionCtr + "." + currscreenshot, folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                           + executionCtr + "."
                                            + screenshot.replace(".png", "_augmented.png"), data);
                            step.setScreenshot(appPackage + "."
                            		+ executionCtr + "." + screenshot.replace(".png", "_augmented.png"));
                            break;
                        }else{
                            step.setScreenshot(appPackage + "."
                                    + executionCtr + "." + screenshot);
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(TraceReplayer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (step != null) {
            // Screenshots are off!
            step.setScreenshot("none");
        }
    }
    
 // This method is for cropping the screenshots to show only the GUI
    // component that was acted upon in the previous step.

    private String cropScreenshot(String appPackage, String androidSDKPath, String folderScreenshots, String version,
            int screenWidth, int screenHeight, Step step, DynGuiComponent component, boolean mainstep,
            String executionType, int executionCtr, boolean lastRotation) {
        if (takeScreenshots && mainstep) {
            String currstep = "0";
            String nextStep = "0";
            //System.out.println(lastRotation); //For debugging

            if(step != null && lastRotation == true && firststep == false){
                //System.out.println("Last Step was rotation, adjusting screenshot.");  //For Debugging
                currstep = Integer.toString(sequence - 2);
                nextStep = Integer.toString(sequence-1);
            }else if (step != null && firststep == true && lastRotation == true) {
                currstep = "1";
                nextStep = "1";
                firststep = false;
            }else if (step != null && lastRotation == false) {
                currstep = Integer.toString(sequence - 1);
                nextStep = Integer.toString(sequence);
            }
            String currscreenshot =  appPackage + "."
            		+ executionCtr + "." + appPackage + "_" + version + "_gnucash" + currstep + ".png";
            String guiscreenshot =  appPackage + "."
                    + executionCtr + "." + appPackage + "_" + version + "_gnucash" + nextStep + ".png";
            //System.out.println(currscreenshot);   //For Debugging
            //System.out.println(guiscreenshot);        //For Debugging
            String ss = "";

            File check = new File(folderScreenshots + File.separator + "screenshots" + File.separator + currscreenshot);

            //System.out.println("Target Screenshot File: " + check); //For Debugging

            if(check.exists()){

                try {
                    if (component != null) {
                        ScreenActionData data = null;
                        Image orig = null;
                        boolean ok = false;
                        do {
                            try {
                                //https://stackoverflow.com/questions/13796611/imageio-read-with-mac
                                orig = ImageIO.read(check);
                                ok = true;
                            } catch (IOException e) {
                                ok = false;
                                e.printStackTrace();
                            }
                        } while (!ok);

                        int x = component.getPositionX();
                        int y = component.getPositionY();
                        int w = component.getWidth();
                        int h = component.getHeight();

                        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        bi.getGraphics().drawImage(orig, 0, 0, w, h, x, y, x + w, y + h, null);

                        try {
                            ImageIO.write(bi, "png",
                                    new File( folderScreenshots + File.separator + "screenshots" + File.separator + guiscreenshot.replaceAll(".png", "_gui.png")));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        ss = guiscreenshot.replaceAll(".png", "_gui.png");

                    }
                } catch (Exception ex) {
                    Logger.getLogger(TraceReplayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            lastStepRotated = false;
            return ss;

        } else if (takeScreenshots && !mainstep) {
            String currstep = "0";

            if (step != null) {
                currstep = Integer.toString(sequence -1);
            }

            if(step!=null && step.getAction() == 3){
                System.out.println("Typing step!!");
                currstep = Integer.toString(sequence);
            }



            String currscreenshot =  appPackage + "."
                    + executionCtr + "." + appPackage + "_" + version + "_gnucash" + currstep + ".png";
            //System.out.println(currscreenshot);   //For Debugging
            String ss = "";
            try {
                if (component != null) {
                    ScreenActionData data = null;
                    Image orig = null;
                    boolean ok = false;
                    do {
                        try {
                            orig = ImageIO.read(new File( folderScreenshots + File.separator + "screenshots" + File.separator + currscreenshot));
                            ok = true;
                        } catch (IOException e) {
                            ok = false;
                            e.printStackTrace();
                        }
                    } while (!ok);

                    int x = component.getPositionX();
                    int y = component.getPositionY();
                    int w = component.getWidth();
                    int h = component.getHeight();

                    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    bi.getGraphics().drawImage(orig, 0, 0, w, h, x, y, x + w, y + h, null);
                    if (step != null) {
                        currstep = Integer.toString(sequence);
                        currscreenshot =  appPackage + "."
                                + executionCtr + "." + appPackage + "_" + version + "_gnucash" + currstep + ".png";
                    }
                    try {
                        ImageIO.write(
                                bi,
                                "png",
                                new File(folderScreenshots + File.separator + "screenshots" + File.separator +currscreenshot.replaceAll(".png", "_gui.png")));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ss = currscreenshot.replaceAll(".png", "_gui.png");

                }
            } catch (Exception ex) {
                Logger.getLogger(TraceReplayer.class.getName()).log(Level.SEVERE, null, ex);
            }

            return ss;
        }
        return null;
    }

}
