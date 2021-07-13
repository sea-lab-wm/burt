


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
 * DfsCrashScopeStrategy.java
 * 
 * Created on Jun 15, 2016
 */
package edu.semeru.android.testing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.ActivityFeature;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.DynamicTransition;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.entity.model.ContextualFeatures;
import edu.semeru.android.testing.helpers.DeviceHelper;
import edu.semeru.android.testing.helpers.StepByStepEngine;
import edu.semeru.android.testing.helpers.Utilities;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.testing.helpers.UiAutoConnector.TypeDeviceEnum;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.DynWindow;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.core.model.HVComponentVO;
import edu.semeru.android.core.model.Transition;
import edu.semeru.android.core.model.WindowVO;
import edu.semeru.android.core.service.PersistDataService;
import edu.semeru.android.testing.GeneralStrategy;
import edu.semeru.android.testing.helpers.LogHelper;
import edu.semeru.android.testing.helpers.ScreenActionData;
import edu.semeru.android.testing.helpers.ScreenshotModifier;
import edu.semeru.android.testing.helpers.TerminalHelper;



/**
 * @author KevinMoran
 *
 */
public class CrashScope extends GeneralStrategy {


    //Set up variables
    private int sequence = 0;   //Holds the current number of steps in the sequence
    private int maxNumEvents = 100;
    private TypeDeviceEnum deviceType;
    private String guiStrat = "";   // Defines the strategy for interacting with GUI components
    private String textStrat = "";  // Defines the text input generation strategy
    private String featStrat = "";  // Defines the strategy used for testing contextual features
    private boolean lastStepRotated;    // Determines whether the last step was a rotation
    private boolean correctAugScreen;   
    private boolean firststep;
    private boolean activityLevel = true;// Determines whether or not this is the first step of the execution
    private DynGuiComponentVO rootWindow = new DynGuiComponentVO();
    private List<Step> steps = new ArrayList<Step>();   // Holds the steps of the current execution
    private HashMap<String, DynWindow> windows = new HashMap<String, DynWindow>();  //HashMap of windows for determining transitions
    private List<ActivityFeature> featureList = new ArrayList<ActivityFeature>();   // List of Activity-level contextual features implemented in app
    private String uiDumpLocation;  // Location for storing uiautomator .xml files during execution
    private ContextualFeatures contextFeats = new ContextualFeatures(); // Object that stores the current state of contextual features during execution
    private final static String PERSIST_UNIT = "CrashScope-eBug";   // Persistance unit for MySQL
    private int executionCtr;   // Keeps track of the number of Executions (which represents one combination of strategies)
    private DeviceHelper deviceHelper;  // Provides APIs to interface with an Android device 
    private String dataFolder;  // Holds screenshots and uidumps
    private String apkPath; // Path to the AUT .apk file
    private String scriptsPath; //Path to Helper scripts for interacting with device
    private CrashScopeSettings strategy = new CrashScopeSettings(); // Holds the combination of strategy settings in a singelton for AndroidLS
    boolean gnuCash = false;

    /**
     * @author KevinMoran
     * 
     * Description: This is the default constructor for the CrashScope Testing Strategy.
     * 
     * @param avdPort
     * @param adbPort
     * @param firststep
     * @param logcatScriptsPath
     * @param uiDumpLocation
     * @param deviceHelper
     * @param csApp
     * @param dataFolder
     * @param apkPath
     * @param scriptsPath
     * @param strategy
     */
    public CrashScope(DeviceHelper deviceHelper, App csApp, String dataFolder, String apkPath,
            String scriptsPath, CrashScopeSettings strategy) {
        super();

        this.deviceHelper = deviceHelper;
        this.app = csApp;
        this.dataFolder = dataFolder;
        this.apkPath = apkPath;
        this.scriptsPath = scriptsPath;
        this.strategy = strategy;
        this.deviceType = deviceHelper.getDEVICE_TYPE();
    }

    public static void runCrashScopeLocal(App testApp) throws JsonIOException, IOException {

        CrashScopeSettings strategy = new CrashScopeSettings();
        strategy.setTopDown(true);
        strategy.setBottomUp(true);
        strategy.setContextFeatsEnabled(false);
        strategy.setContextFeatsDisabled(true);
        strategy.setUnexpectedText(false);
        strategy.setExpectedText(true);
        strategy.setNoText(false);

//      String adbPort = "5037";
//      String avdPort = "0932890b";
//      String uiDumpLocation = "/Volumes/Macintosh_HD_3/Research-Files/Bug-Reproduction-CrashScope-Workspace/ui-dumps/";
//      String apkPath = "/Users/KevinMoran/Dropbox/Documents/My_Graduate_School_Work/SEMERU/git_src_code/gitlab-code/Android-Bug-Report-Reproduction/Data/FUSION-Data/Apks/mileage.apk";
//      String androidSDKPath = "/Users/semeru/Applications/android-sdk";
//      String dataFolder = "/Users/semeru/Documents/SEMERU/CrashScope/output/";
//      String scriptsPath = "/Users/KevinMoran/Dropbox/Documents/My_Graduate_School_Work/SEMERU/git_src_code/gitlab-code/Android-Core/scripts/";
        
        
        String scriptsPath = "/Users/KevinMoran/Dropbox/Documents/My_Faculty_Work/SAGE/git-src-code/BURT/crashscope/scripts";
        String dataFolder = "/Users/KevinMoran/Desktop/CrashScope-Data-Evolving-GUIs";
        String androidSDKPath = "/Applications/AndroidSDK/sdk";
              

        String avdPort = "5554";
        String adbPort = "5037";
        TypeDeviceEnum deviceType = UiAutoConnector.TypeDeviceEnum.EMULATOR;
        DeviceHelper deviceHelper = new DeviceHelper(deviceType, androidSDKPath, avdPort, adbPort);

        
        HashMap<String, String> dpProperties = new HashMap<String, String>();
        
//        App dbApp = PersistDataService.getAppByPackageAndVersion(testApp.getPackageName(), testApp.getVersion(), PERSIST_UNIT, dpProperties);
        
        CrashScope crashScope = new CrashScope(deviceHelper, testApp, dataFolder, testApp.getApkPath(), scriptsPath, strategy);

        //      deviceHelper.unInstallAndInstallApp(app.getApkPath(), app.getPackageName());
        //      deviceHelper.unInstallApp(app.getPackageName());
        //      deviceHelper.startAPK(app.getPackageName(), app.getMainActivity());

        try {
            crashScope.executeDFS();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException,
    ClassNotFoundException, SQLException, JsonIOException, IOException {

        ArrayList<App> bugRepApps = generateBugRepData("/Users/KevinMoran/Dropbox/Documents/My_Faculty_Work/SAGE/git-src-code/BURT/crashscope/test-apks/apps.txt","/Applications/AndroidSDK/sdk/build-tools/25.0.1");

        Gson gson = new Gson();
        
//        JsonReader reader = new JsonReader(new FileReader("/Users/KevinMoran/Desktop/CrashsCope-Data/Execution-1.json"));
        
//        Execution exec = gson.fromJson(reader, Execution.class);
        
//        System.out.println("Execution App: " + exec.getApp().getMainActivity());
        
        for(App currApp: bugRepApps) {

            System.out.println("Running Crashscope on App:" + currApp.getName());
            runCrashScopeLocal(currApp);

        }

    }

    public List<Execution> executeDFS()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JsonIOException, IOException {
        return executeDFS(true);
    }
    
    /**
     * Method Name: executeDFS
     * 
     * Purpose: This method will execute several "runs" of testing executions using one of 12 possible configurations 
     * currently possible using the CrashScope strategies.  Each "run" is encapsulted within an Execution Object
     * which contains several sub-objects that hold all of the information relevant for a particular run, including 
     * GUI-Component Information and screenshots paths.
     * 
     * @param testContextFeats: Enable or Disable Testing of Contextual Features
     * @param takeScreenshots: Enable or Disable Taking Screenshots
     *
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException 
     * @throws JsonIOException 
     */
    public List<Execution> executeDFS(boolean storeInDb)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JsonIOException, IOException {

        long appStartTime = System.currentTimeMillis();
        
        // Set up initial parameters for experiments

        List<String> guiStrats = getGUIStrategies();    // Get GUI exploration strategies from CrashScope Settings and save in list
        List<String> textStrats = getTextStrategies();  // Get text input generation strategies from CrashScope Settings and save in list
        List<String> featStrats = getFeatureStrategies(); // Get GUI exploration strategies from CrashScope Settings and save in list

        // Print out the strategies to be executed before starting.

        System.out.println("Strategies to be Executed: ");
        // For loop for iterating through all GUI-Traversal Strategies 
        for(String guiStrat: guiStrats){
            // For loop for Iterating through all Text-Input Strategies
            for(String textStrat: textStrats){
                //For loop for iterating through testing with Contextual Features enabled or disabled
                for(String featStrat:featStrats){
                    System.out.println("Gui-Strategy: " + guiStrat + " Text Strategy " + textStrat + " Contextual Feat Strat: " + featStrat);
                }}}

        // Print out general information about app before starting execution.

        System.out.println("Beginning testing for " + app.getName());   //Print the name of the app under test (AUT), and an initial status message.
        System.out.println("App Package: " + app.getPackageName()); //Print the package name for the current AUT
        System.out.println("App MainActivity: " + app.getMainActivity());   //Print the Main Activity for the current AUT

        // Perform some setup before the execution begins

        takeScreenshots = true; // This variable determines whether or not screenshots are recorded for all executions
        int executionCtr = 1; // The current Execution
        List<Execution> csExecutions = new ArrayList<Execution>();  // ArrayList to hold all of the CrashScope Executions. This will be returned at the end of the method


        //Set up the database information for the executions
        HashMap<String, String> dpProperties = new HashMap<String, String>();
        //dpProperties.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/ml-mileage");
        // Below is the database information for Derby
        //      HashMap<String,String> dpProperties = new HashMap<String,String>();
        //      dpProperties.put("javax.persistence.jdbc.url", "jdbc:derby:"+ dataFolder+ File.separator + "derby_db");


        // If we are going to use contextual features, extract them from the database
        if(featStrats.contains("Enabled")){
            featureList = PersistDataService.getActivityFeatsByAppId(app.getId(), PERSIST_UNIT, dpProperties);
            System.out.println("Printing Activity Features:");
            for (ActivityFeature act : featureList){
                //              System.out.println("Activity: " + act.getActivity() + " Feature: " + act.getFeature()); For debugging
            }// End for loop for printing Activity-Level features
        }// End conditional for checking whether testing contextual features is enabled

        // Next execution begins, execution proceeds by looping through all of the available strategies.
        // Using multiple nested loops, with the GUI-traversal strategy at the top of the hierarchy

        deviceHelper.clearLogcat();
        
        Process logcat = deviceHelper.startLogcatCollection(dataFolder + File.separator + app.getPackageName() + "-" + app.getVersion() + ".log");
        
        // For loop for iterating through all GUI-Traversal Strategies 
        for(String guiStrat: guiStrats){
            // For loop for Iterating through all Text-Input Strategies
            for(String textStrat: textStrats){
                //For loop for iterating through testing with Contextual Features enabled or disabled
//              for(String featStrat:featStrats){

                    //Set Global Variables to state of current execution

                    setGuiStrat(guiStrat);
//                  setFeatStrat(featStrat);
                    setTextStrat(textStrat);

                    sequence = 0;
                    getAvailableStackSteps().clear();
                    getAvailableStack().clear();
                    getVisitedStack().clear();
                    getVisitedStackSteps().clear();
                    getTransitions().clear();
                    getTransitionsIds().clear();
                    getComponents().clear();
                    steps.clear();
                    windows.clear();
                    rootWindow = new DynGuiComponentVO();

                    long startTime = System.currentTimeMillis(); //Start time for the current execution
                    Execution execution = new Execution();  // Create the Execution object that will hold the information during automated testing.

                    // Here we need to set up the Execution Object, which will hold identifying information about 
                    // the current strategy.

                    // Set Device Information
                    execution.setDeviceDimensions(deviceHelper.getScreenSize());
                    execution.setOrientation(deviceHelper.getOrientation());
                    execution.setAndroidVersion(deviceHelper.getAndroidVersion());
                    execution.setDeviceName(deviceHelper.getDeviceVersion());
                    execution.setExecutionType(getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat());
                    widthScreen = Integer.parseInt(execution.getDeviceDimensions().split("x")[0]);
                    heightScreen = Integer.parseInt(execution.getDeviceDimensions().split("x")[1]);

                    // Associate the execution to app so it is linked in the database
                    // TODO: @Carlos, why is it necessary to do this both ways?
                    app.addExecution(execution);
                    execution.setApp(app);

                    // Setup misc settings for the execution
                    execution.setExecutionNum(executionCtr);
                    execution.setDate(Calendar.getInstance().getTime());
                    execution.setMainActivity(app.getMainActivity());
                    setExecutionStrategies(execution, guiStrat, textStrat, featStrat);

                    // Set paths for screenshots and uiautomator dumps 
                    folderScreenshots = dataFolder;
                    setUiDumpLocation(dataFolder + File.separator + app.getPackageName() + "-" + app.getVersion() + "-" + executionCtr);

                    if(featStrat.equals("Enabled")){
                        checkAndActivateAppFeats(app.getPackageName());
                    }

                    /**
                     * Here we start the testing strategy. First, we install the application and start it.
                     * Next, the number of available components which are capable of receiving input are 
                     * initialized.  Then any contextual features are activated before entering into a while 
                     * loop that will control the automated input generation
                     */

                    // Here the execution of the strategy starts by installing and starting the apk
                    deviceHelper.unInstallAndInstallApp(apkPath, app.getPackageName());
//                  deviceHelper.pushFile("/Users/KevinMoran/Dropbox/Documents/My_Graduate_School_Work/SEMERU/git_src_code/gitlab-code/Android-Bug-Report-Reproduction/Data/Apks/extra-files/dictionary1.aar", "/sdcard/");
//                  deviceHelper.pushFile("/Users/KevinMoran/Dropbox/Documents/My_Graduate_School_Work/SEMERU/git_src_code/gitlab-code/Android-Bug-Report-Reproduction/Data/Apks/extra-files/dictionary2.aar", "/sdcard/");
//                  deviceHelper.pushFile("/Users/KevinMoran/Dropbox/Documents/My_Graduate_School_Work/SEMERU/git_src_code/gitlab-code/Android-Bug-Report-Reproduction/Data/Apks/extra-files/pdf_file.pdf", "/sdcard/");
                    deviceHelper.startAPK(app.getPackageName(), app.getMainActivity());
                    
                    // Disable user adaptable accelerometer so that CrashScope can rotate the app programmatically
                    // Rotate into Portrait mode for beginning of the execution
                    deviceHelper.disableAccelerometer();
                    deviceHelper.rotateDevice("portrait");
                    if(deviceHelper.isKeyboardActive()) {
                        deviceHelper.disposeKeyboard();
                    }
                    Step step = null;
                    setupGNUCash();
                    
                    // Update the initial stack of components that can recieve input, and get the current activity and window
                    updateAvailableStack(app.getPackageName(), deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), false);
                    currentActivity = deviceHelper.getCurrentActivityImproved();
                    //                  WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
                    //                  currentWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
                    //                          .getTitle() : ""))).trim();

                    currentWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), false);

                    currentScreen = getCurrentScreen();

                        // Create inital step for execution
                    //Check to see if there are contextual features to active in the initial activity of the application
                    // TODO: fix this
//                  if(activateActFeats(app.getMainActivity(), app.getPackageName(), app.getMainActivity())){
//                      String exceptions = deviceHelper.getErrorsFromLogcat(app.getPackageName(), scriptsPath);
//                      step = addStep(null, 7, null, "null", exceptions, currentScreen);
//                      crash = deviceHelper.checkForCrash(app.getPackageName(), app.getMainActivity(), getWidthScreen(), getHeightScreen(), getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, true);
//                  }// End conditional to check for MainActivity contextual features


                    /**
                     * The following do-while loop contains most of the logic for the testing.  Essentially, CrashScope will iterate through
                     * this loop until there are no more components that accept inputs that have not already been tested, or if until
                     * a crash is encountered.  In order to perform the systematic execution, CrashScope keeps a stack of "unvisited" components
                     * representing those that have not yet been sent an GUI/Text input. If a crash is encountered, it will save the execution 
                     * information and then begin a new execution with the same number of components on the stack. If the strategy finishes
                     * (e.g., there are no more components on the stack that can be reached) then a either a new execution with next combination 
                     * of strategies starts, or execution finishes, if there are no more strategies to iterate through.
                     */

                    sysExplorationLoop:do {
                        System.out.println("--Step: " + sequence + "--");
                        //System.out.println("Remaining Components:");
                        //printAvailableStack();    // Prints the current stack of components, used for debugging purposes

                        // take a screenshot before executing the next action
                        takeScreenshot(app.getPackageName(), dataFolder, getApp().getVersion(), getWidthScreen(), getHeightScreen(), step, getTextStrat(), executionCtr);

                        //Get the next input event from the stack of unvisited component-action pairs
                        GUIEventVO event = getNextStep(app.getPackageName(), app.getMainActivity(), getTextStrat(), 0, executionCtr);

                        // If the event is null, something went wrong, exit the execution
                        //if (event == null) {
                        //  break;
                        //}// End conditional to check for null event


                        // This conditional checks to determine if there was a crash in the last step or the number of events has exceeded the maximum
                        // threshold specified
                        if (!crash && sequence <= maxNumEvents && !(event == null)) {

                            //If not in the app, launch the app again before the next step.
                            String activityTemp = deviceHelper.getCurrentActivityImproved();
                            if (!activityTemp.contains(app.getPackageName())) {
                                // chimp.setUpApp(androidSDKPath, app.getPackageName(), app.getMainActivity());
                                deviceHelper.startAPK(app.getPackageName(), app.getMainActivity());
                            }// End check for device state being outside of application 

                            //Update the new Screen state

                            currentScreen = getCurrentScreen();

                            // Here we execute the step derived from the call to getNextStep above
                            Step component = executeStep(event, app.getPackageName(), getTextStrat());

                            // Next we extract the GUI component that was interacted with, check for any thrown exceptions
                            // and check whether the event caused a transition to a new state in the application
                            DynGuiComponent dynComponent = (component == null ? null : component.getDynGuiComponent());

                            //Get Logcat Errors/Exceptions
                            String exceptions = deviceHelper.getErrorsFromLogcat(app.getPackageName(), getScriptsPath());

                            //Check new app state for transition from previous state
                            Transition transition = checkNewState(app.getPackageName(), app.getMainActivity(), StepByStepEngine.getVOFromEntity(dynComponent, false),
                                    event.getEventTypeId(),"executionType", executionCtr);


                            // Add the completed step to the list of steps for the execution
                            step = addStep(dynComponent, event.getEventTypeId(), transition, "none", exceptions, currentScreen);

                            // Take a screenshot of the resulting state after executing the event
                            String guiScreenshot = cropScreenshot(app.getPackageName(), deviceHelper.getAndroidSDKPath(), dataFolder, getApp()
                                    .getVersion(), getWidthScreen(), getHeightScreen(), step, dynComponent, true,
                                    getTextStrat(), executionCtr, isLastStepRotated());

                            //If screenshots are enabled, set the cropped screenshot to the GUI component
                            if (dynComponent != null && guiScreenshot != null && !guiScreenshot.equals(null)) {
                                dynComponent.setGuiScreenshot(guiScreenshot);
                            } else {
                                //System.out.println("Screenshots Disabled!"); //For debugging
                                dynComponent.setGuiScreenshot("none");
                                //System.out.println(dynComponent.getGuiScreenshot());
                            }// End conditional to check and see if screenshots are disabled

                            // Check for crash after executing the action 
                            if (crash) {
                                sequence ++;
                                //Take screenshot for crash step
                                takeScreenshot(app.getPackageName(), dataFolder, getApp().getVersion(),
                                        getWidthScreen(), getHeightScreen(), step, getTextStrat(), executionCtr);
                            }// End check for crash after executing an action

                        } else {
                            int eventType;
                            if(event == null) {
                                eventType = 111;
                            }else {
                                eventType = event.getEventTypeId();
                            }
                            checkNewState(app.getPackageName(), app.getMainActivity(), null, eventType, getTextStrat(), executionCtr);
                            if(sequence >= maxNumEvents){
                                break sysExplorationLoop;
                            }// End check for if CrashScope has exceeded the maximum number of events
                        }//End check for crash or exceeding max number of events

                        // If there was a crash restart the execution from the current step.
                        if (crash) {

                            System.out.println("Collecting and Saving Execution Data to JSON");

                            setExecutionCtr(getExecutionCtr()+1);   // Increment the Execution Counter
                            addStep(null, DeviceHelper.BACK, null, "none", null, null); // Add step for crash

                            // Set crash information and steps to execution and persist to database
                            execution.setCrash(true);
                            execution.setSteps(getSteps());
                            for (Step listStep : getSteps()) {
                                listStep.setExecution(execution);
                            }
                            csExecutions.add(execution);
                            
                            Gson gson = new Gson();
                            
                            try {
                                gson.toJson(execution, new FileWriter(dataFolder + File.separator + "Execution-" + executionCtr + ".json"));
                            } catch (JsonIOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            
                           
                            
//                            if(storeInDb){
//                                PersistDataService.saveExecutionData(execution, PERSIST_UNIT, dpProperties);
//                            }


                            //Set up new execution to continue from point where the app crashed
                            Execution execution_crash = new Execution();
                            execution_crash.setDeviceDimensions(deviceHelper.getScreenSize());
                            execution_crash.setOrientation(deviceHelper.getOrientation());
                            execution_crash.setDate(Calendar.getInstance().getTime());
                            execution_crash.setApp(app);
                            app.addExecution(execution_crash);
                            execution_crash.setMainActivity(app.getMainActivity());
                            execution_crash.setAndroidVersion(deviceHelper.getAndroidVersion());
                            execution_crash.setExecutionNum(executionCtr);
                            execution_crash.setDeviceName(deviceHelper.getDeviceVersion());
                            execution_crash.setSteps(new ArrayList<Step>());
                            setExecutionStrategies(execution_crash, guiStrat, textStrat, featStrat);
                            execution = execution_crash;
                            //System.out.println("--Execution: " + execution.getSteps());
                            setSteps(new ArrayList<Step>());
                            step = null;
                            // checkNewState(app.getPackageName(), app.getMainActivity(), dynComponent,
                            // event.getEventTypeId());
                            //coverageCtr = 1;
                            crash = false;

                        }// End conditional to check for crash if 

                    } while (!getAvailableStackSteps().isEmpty());  // End do while loop to process the current execution

                    // printComponents();   // For Debugging purposes
                    // printTransitions();  // For Debugging purposes

                    // If the program reaches this point, it means the current execution strategy finished without crashing, or exceeded 
                    // the current number of maximum allowable steps

                    if(steps.isEmpty()) {
                        continue;
                    }

                    // Because we only set one Screen object per step, we need to add an extra screen at the end of the execution
                    // to capture the final state of the application.
                    currentScreen = getCurrentScreen();
                    addStep(null, DeviceHelper.NOTHING, null, null, null, currentScreen);

                    // Record execution time and print message to user
                    long endTime = System.currentTimeMillis();
                    execution.setElapsedTime(endTime - startTime);
                    System.out.println("Execution strategy for " + execution.getApp().getName() + " with strategy: " + getGuiStrat() + getTextStrat() + getFeatStrat() + " is complete." +
                            "Execution took: " + execution.getElapsedTime()/1000);

                    deviceHelper.stopAPK(app.getPackageName()); // Stop the current app
                    //Set execution information and steps to execution and persist to database
                    execution.setCrash(false);
                    execution.setSteps(getSteps());
                    for (Step listStep : getSteps()) {
                        listStep.setExecution(execution);
                    }
//                    if(storeInDb){
//                        PersistDataService.saveExecutionData(execution, PERSIST_UNIT , dpProperties);
//                    }
                    
                    Gson gson = new Gson();
                    String json = gson.toJson(execution);
                    PrintWriter writer = new PrintWriter(dataFolder + File.separator + "Execution-" + System.currentTimeMillis() + ".json");
                    writer.println(json);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    writer.close();
                    
                    System.out.println("Execution took " + (endTime - startTime) + " milliseconds");
                    csExecutions.add(execution);

                    //TODO: Implement these features for the AVD

                    //                  StepByStepEngine.correctGPS();  
                    //                  StepByStepEngine.enableNetwork();
                    //                  StepByStepEngine.correctSensor("acceleration");
                    //                  StepByStepEngine.correctSensor("magnetic-field");
                    //                  StepByStepEngine.correctSensor("temperature");

//              }// End loop for Feature Strategies
            }// End for loop for Text Strategies
        } // End for loop for GUI-Traversal Strategy
        deviceHelper.stopLogcatCollection(logcat);
        deviceHelper.unInstallApp(app.getPackageName());
        
        long appEndTime = System.currentTimeMillis();
        long appElapsedTime = appEndTime - appStartTime;
        
        PrintWriter bw;
        try {
            bw = new PrintWriter(new FileWriter(dataFolder + File.separator + app.getPackageName() + "-" + app.getVersion() + ".log", true));
            bw.println("Elapsed Time: " + appElapsedTime);
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return csExecutions;

    }// End executeDFS()

    private Step setupGNUCash() {
        
        gnuCash = true;
        
        DynGuiComponentVO gnuComp1 = new DynGuiComponentVO();
        gnuComp1.setActivity("");
        gnuComp1.setPositionX(600);
        gnuComp1.setPositionY(1728);
        gnuComp1.setClickable(true);
        gnuComp1.setComponentIndex(1);
        gnuComp1.setEnabled(true);
        gnuComp1.setFocusable(false);
        gnuComp1.setHeight(96);
        gnuComp1.setWidth(600);
        gnuComp1.setIdXml("org.gnucash.android:id/btn_save");
        gnuComp1.setLongClickable(false);
        gnuComp1.setName("android.widget.Button");
        gnuComp1.setRelativeLocation("Bottom-Right");
        gnuComp1.setScrollable(false);
        gnuComp1.setSelected(false);
        gnuComp1.setText("Next");
        
        DynGuiComponentVO gnuComp2 = new DynGuiComponentVO();
        gnuComp2.setActivity(deviceHelper.getCurrentActivityImproved());
        gnuComp2.setPositionX(32);
        gnuComp2.setPositionY(443);
        gnuComp2.setClickable(true);
        gnuComp2.setComponentIndex(1);
        gnuComp2.setEnabled(true);
        gnuComp2.setFocusable(false);
        gnuComp2.setHeight(96);
        gnuComp2.setWidth(1116);
        gnuComp2.setIdXml("android:id/text1");
        gnuComp2.setLongClickable(false);
        gnuComp2.setName("android.widget.CheckedTextView");
        gnuComp2.setRelativeLocation("Top");
        gnuComp2.setScrollable(false);
        gnuComp2.setSelected(false);
        gnuComp2.setText("Disable crash reports");
        Step gnuStep = null;
        
        for (int i =0; i < 6; i++) {

            if(i==3) {
                takeScreenshot(app.getPackageName(), dataFolder, getApp().getVersion(), getWidthScreen(), getHeightScreen(), gnuStep, getTextStrat(), executionCtr);
                currentActivity = deviceHelper.getCurrentActivityImproved();
                currentWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true);
                gnuComp2.setActivity(currentActivity);
                
                GUIEventVO gnuEvent = getEventFromComponent(gnuComp2, StepByStepEngine.CLICK);
                currentScreen = getCurrentScreen();

                deviceHelper.executeEvent(gnuEvent, getTextStrat(), true);
                String exceptions = deviceHelper.getErrorsFromLogcat(app.getPackageName(), getScriptsPath());
                Transition transition = checkNewState(app.getPackageName(), app.getMainActivity(), gnuComp2,
                        gnuEvent.getEventTypeId(),"executionType", executionCtr);
                DynGuiComponent gnuComp = StepByStepEngine.getEntityFromVO(gnuComp2, false);
                gnuStep = addStep(gnuComp, gnuEvent.getEventTypeId(), transition, "none", exceptions, currentScreen);

                // Take a screenshot of the resulting state after executing the event
                String guiScreenshot = cropScreenshot(app.getPackageName(), deviceHelper.getAndroidSDKPath(), dataFolder, getApp()
                        .getVersion(), getWidthScreen(), getHeightScreen(), gnuStep, gnuComp, true,
                        getTextStrat(), executionCtr, isLastStepRotated());
                if (gnuComp != null && guiScreenshot != null && !guiScreenshot.equals(null)) {
                    gnuComp.setGuiScreenshot(guiScreenshot);
                } else {
                    //System.out.println("Screenshots Disabled!"); //For debugging
                    gnuComp.setGuiScreenshot("none");
                    //System.out.println(dynComponent.getGuiScreenshot());
                }// End conditional to check and see if screenshots are disabled

            }else {
                takeScreenshot(app.getPackageName(), dataFolder, getApp().getVersion(), getWidthScreen(), getHeightScreen(), gnuStep, getTextStrat(), executionCtr);
                currentActivity = deviceHelper.getCurrentActivityImproved();
                gnuComp1.setActivity(currentActivity);
                currentWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true);

                GUIEventVO gnuEvent = getEventFromComponent(gnuComp1, StepByStepEngine.CLICK);
                currentScreen = getCurrentScreen();

                deviceHelper.executeEvent(gnuEvent, getTextStrat(), true);
                String exceptions = deviceHelper.getErrorsFromLogcat(app.getPackageName(), getScriptsPath());
                Transition transition = checkNewState(app.getPackageName(), app.getMainActivity(), gnuComp1,
                        gnuEvent.getEventTypeId(),"executionType", executionCtr);
                DynGuiComponent gnuComp = StepByStepEngine.getEntityFromVO(gnuComp1, false);
                gnuStep = addStep(gnuComp, gnuEvent.getEventTypeId(), transition, "none", exceptions, currentScreen);

                // Take a screenshot of the resulting state after executing the event
                String guiScreenshot = cropScreenshot(app.getPackageName(), deviceHelper.getAndroidSDKPath(), dataFolder, getApp()
                        .getVersion(), getWidthScreen(), getHeightScreen(), gnuStep, gnuComp, true,
                        getTextStrat(), executionCtr, isLastStepRotated());
                if (gnuComp != null && guiScreenshot != null && !guiScreenshot.equals(null)) {
                    gnuComp.setGuiScreenshot(guiScreenshot);
                } else {
                    //System.out.println("Screenshots Disabled!"); //For debugging
                    gnuComp.setGuiScreenshot("none");
                    //System.out.println(dynComponent.getGuiScreenshot());
                }// End conditional to check and see if screenshots are disabled

            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        takeScreenshot(app.getPackageName(), dataFolder, getApp().getVersion(), getWidthScreen(), getHeightScreen(), gnuStep, getTextStrat(), executionCtr);
        gnuCash = false;
        return gnuStep;

    }
    
    
    /**
     * @return
     */
    private Screen getCurrentScreen() {
        // Get all the components with hierarchy information
        List<DynGuiComponentVO> components = UiAutoConnector.getScreenInfoGeneric(deviceHelper.getAndroidSDKPath(), new StringBuilder(), getWidthScreen(),
                getHeightScreen(), true, false, true, deviceHelper.getDevicePort(), deviceHelper.getAdbPort(),
                getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true).replaceAll(" ", "").replaceAll("'","") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType, UiAutoConnector.GENERIC_STRATEGY);

        // Remove all children
        for (DynGuiComponentVO component : components) {
            component.setChildren(null);
        }
        // Create screen with required data
        Screen screen = new Screen();
        screen.setActivity(currentActivity);
        screen.setWindow(currentWindow);
        //Set both ways for JPA
        screen.setDynGuiComponents(StepByStepEngine.getEntityFromVO(components));
        for(DynGuiComponent currComp: screen.getDynGuiComponents()){
            currComp.setScreen(screen);
        }
        return screen;
    }

    /**
     * @param devicePort
     * @param adbPort
     * @param all
     * @return
     */
    private String getcurrentWindow(String devicePort, String adbPort, Boolean all) {

        StringBuilder hash = new StringBuilder();
        String currentWindow = null;

        if(activityLevel) {
            WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
            //This is Id for generic (Activity version)
            currentWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
                    .getTitle() : ""))).trim();
        }else {
            WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
            currentWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
                    .getTitle() : ""))).trim();
            ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoActionableComponents(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), all, false, true, devicePort, adbPort, getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(devicePort, adbPort, all).replaceAll(" ", "") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType);
            currentWindow += "->" + hash.toString().hashCode() + "";
        }

        System.out.println("Window:" + currentWindow);
        return currentWindow;

    }

    /**
     * @param windowOne
     * @param windowTwo
     * @return
     */
    private Boolean compareWindows(String windowOne, String windowTwo) {
        boolean windowsEqual;

        String hashOne = null;
        String hashTwo = null;
        
        if(windowOne.contains("->")) {
        hashOne = windowOne.substring(windowOne.indexOf("->")+2, windowOne.length());
        }else {
            hashOne = windowOne;
        }
        if(windowTwo.contains("->")) {
            hashTwo = windowTwo.substring(windowTwo.indexOf("->")+2, windowTwo.length());
        }else {
            hashTwo = windowTwo;
        }

        if(activityLevel){
            System.out.println("Comparing Old Activity: " + windowOne);
            System.out.println("to New Activity: " + windowTwo);
            if(windowOne.equals(windowTwo)) {
                windowsEqual = true;
            }else {
                windowsEqual = false;
            }

        }else{
            System.out.println("Comparing Old Activity: " + hashOne);
            System.out.println("to New Activity: " + hashTwo);
            if(hashOne.equals(hashTwo)) {
                windowsEqual = true;
            }else {
                windowsEqual = false;
            }

        }

        return windowsEqual;
    }


    /**
     * Description: Takes the current configuration for a single CrashScope strategy as strings
     * representing each parameter, and saves it to an execution object.
     * 
     * @param execution
     * @param guiStrat
     * @param textStrat
     * @param featStrat
     */
    private void setExecutionStrategies(Execution execution, String guiStrat, String textStrat, String featStrat){

        execution.setStratsFalse();

        if (guiStrat.equals("Top_Down")){
            execution.setTopDown(true);
        } if(guiStrat.equals("Bottom_Up")){
            execution.setBottomUp(true);
        } if(textStrat.equals("Expected")){
            execution.setExpectedText(true);
        } if(textStrat.equals("Unexpected")){
            execution.setUnexpectedText(true);
        } if(textStrat.equals("No_Text")){
            execution.setNoText(true);
        } if(featStrat.equals("Enabled")){
            execution.setContextFeatsEnabled(true);
        }
    }

    /**
     * Description: Returns the list of text strategies to be used during execution from
     * the CrashScopeSettings object.
     * 
     * @return
     */
    private List<String> getTextStrategies(){

        List<String> textStrats = new ArrayList<>();
        if(strategy.isExpectedText()){
            textStrats.add("Expected");
        }if(strategy.isUnexpectedText()){
            textStrats.add("Unexpected");
        }if(strategy.isNoText()){
            textStrats.add("No_Text");
        }

        return textStrats;

    }// End getTextStrategies() 

    /**
     * Description: Returns the list of GUI exploration strategies to be used during 
     * execution from the CrashScopeSettings object
     * 
     * @return
     */
    private List<String> getGUIStrategies(){

        List<String> guiStrats = new ArrayList<>();
        if(strategy.isTopDown()){
            guiStrats.add("Top_Down");
        }if(strategy.isBottomUp()){
            guiStrats.add("Bottom_Up");
        }

        return guiStrats;

    }// End getGUIStrategies()

    /**
     * Description: Returns the list of Contextual Feature strategies to be used during
     * execution from the CrashScope Settings object
     * 
     * @return
     */
    private List<String> getFeatureStrategies(){

        List<String> featStrats = new ArrayList<>();
        if(strategy.isContextFeatsEnabled()){
            featStrats.add("Enabled");
        }if(strategy.isContextFeatsDisabled()){
            featStrats.add("Disabled");
        }

        return featStrats;

    }// End getFeatureStrategies()



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

                String screenshot = appPackage + "_" + version + "_" + sequence + ".png";
                String currscreenshot = appPackage + "_" + version + "_" + currstep + ".png";

                // Check if the last executed step was a rotation, if so we need to roll back the 
                // sequences to get the proper existing screenshots
                if (isCorrectAugScreen() == true){
                    //System.out.println("last step rotated fixing GUI screenshot"); //For Debugging
                    String step1 = Integer.toString(sequence - 1);
                    String step2 = Integer.toString(sequence - 2);
                    screenshot = appPackage + "_" + version + "_" + step1 + ".png";
                    currscreenshot = appPackage + "_" + version + "_" + step2 + ".png";
                    correctAugScreen = false;
                }else if (step != null && step.getAction() == 3){
                    // If the last step was a typing step, we need to reset the screenshot paths
                    //System.out.println("last step was typing"); //For debugging
                    String step1 = Integer.toString(sequence);
                    String step2 = Integer.toString(sequence);
                    screenshot = appPackage + "_" + version + "_" + step1 + ".png";
                    currscreenshot = appPackage + "_" + version + "_" + step2 + ".png";
                    correctAugScreen = false;
                }

                System.out.println("Processing Screenshot for current Step " + sequence);

                deviceHelper.getAndPullScreenshot(folderScreenshots + File.separator + "screenshots", appPackage + "."
                        + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + screenshot);
                if (step != null) {
                    ScreenActionData data = null;
                    switch (step.getAction()) {
                    case StepByStepEngine.SWIPE:
                        data = new ScreenActionData(step.getAction(), screenWidth / 2, (int) (screenHeight * .1),
                                screenWidth / 2, (int) (screenHeight * .8), screenWidth, screenHeight);
                        ScreenshotModifier.augmentScreenShot(folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." +currscreenshot, folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                        + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "."
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
                                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + currscreenshot, folderScreenshots + File.separator + "screenshots" + File.separator + appPackage + "."
                                            + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "."
                                            + screenshot.replace(".png", "_augmented.png"), data);
                            step.setScreenshot(appPackage + "."
                                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + screenshot.replace(".png", "_augmented.png"));
                            break;
                        }else{
                            step.setScreenshot(appPackage + "."
                                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + screenshot);
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(CrashScope.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (step != null) {
            // Screenshots are off!
            step.setScreenshot("none");
        }
    }

    public boolean activateActFeats(String currentActivity, String appPackage, String mainActivity){

        String activity = null;
        String feature = null;
        boolean rotated = false;

        System.out.println("---Checking Features of the current Activity");

        for (ActivityFeature check : featureList){

            activity = check.getActivity();
            feature = check.getFeature();

            if (activity.equals(currentActivity) && (getFeatStrat().equals("Enabled"))){

                if(feature.equals("Rotatable")){
                    deviceHelper.rotateDevice("Landscape-Left");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    deviceHelper.rotateDevice("Portrait");
                    rotated = true;

                    setLastStepRotated(true);


                    if (deviceHelper.isKeyboardActive()) {
                        deviceHelper.disposeKeyboard();

                        // Pause to allow keyboard animation to
                        // complete before continuing.

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                } 

                //TODO: Refactor this method to more effectively activate features at the activity level.

                //                                      if(feature.contains("SensorManager") || feature.contains("Sensor") || feature.contains("SensorEventListener") || feature.contains("SensorEvent")){
                //                                          
                //                                          if(feature.contains("accelerometer") && appAccel == true){
                //                                              StepByStepEngine.dupeSensor("acceleration");
                //                                              accelNormal = false;
                //                                          }else if(appAccel == true){
                //                                              accelNormal = true;
                //                                              StepByStepEngine.correctSensor("acceleration");
                //                                          }if(feature.contains("magnetic-field") && appMagnet == true){
                //                                              StepByStepEngine.dupeSensor("magnetic-field");
                //                                              magnetNormal = false;
                //                                          }else if(appMagnet == true){
                //                                              StepByStepEngine.correctSensor("magnetic-field");
                //                                              magnetNormal = true;
                //                                          }if(feature.contains("temperature") && appTemp == true){
                //                                              StepByStepEngine.dupeSensor("temperature");
                //                                              tempNormal = false;
                //                                          }else if(appTemp == true){
                //                                              StepByStepEngine.correctSensor("temperature");
                //                                              tempNormal = true;
                //                                          }
                //
                //
                //                                      }else if(appAccel == true) {
                //                                          StepByStepEngine.correctSensor("acceleration");
                //                                          accelNormal = true;
                //                                      } if(appMagnet == true){
                //                                          StepByStepEngine.correctSensor("magnetic-field");
                //                                          magnetNormal = true;
                //                                      } if(appTemp == true){
                //                                          StepByStepEngine.correctSensor("temperature");
                //                                          tempNormal = true;
                //                                      }
                //                                      if((feature.contains("ConnectivityManager") || feature.contains("WifiManager") || feature.contains("HttpURLConnection") || feature.contains("HttpClient")) && appNetwork == true){
                //                                          StepByStepEngine.disableNetwork(androidSDKPath);
                //                                          network = false;
                //                                      }else if(appNetwork == true){
                //                                          StepByStepEngine.enableNetwork();   
                //                                          network = true;
                //                                      }
                //                                      if(feature.contains("LocationManager") && appGPS == true){
                //                                          StepByStepEngine.dupeGPS();
                //                                          gps = false;
                //                                      }else if(appGPS == true){
                //                                          StepByStepEngine.correctGPS();
                //                                          gps = true;
                //                                      }
                //                                      if(feature.contains("MediaRecorder")){
                //                                          //audio functionality 
                //                                      }

            }// End if loop for checking current Activity

        }

        return rotated;

    }


    public void checkAndActivateAppFeats(String appPackage){

        String activity = null;
        String feature = null;


        System.out.println("---Checking Features of at the App-Level");

        for (ActivityFeature check : featureList){

            activity = check.getActivity();
            feature = check.getFeature();

            //System.out.println("@@@@@Checking if " + activity + " = " + currentActivity);

            if (activity.equals("app")){

                if(feature.contains("SensorManager") || feature.contains("Sensor") || feature.contains("SensorEventListener") 
                        || feature.contains("SensorEvent")){

                    if(feature.contains("accelerometer") && getContextFeats().isAppAccel()){
                        System.out.println("---Duping Accelerometer at the App-Level");
                        //StepByStepEngine.dupeSensor("acceleration");  //TODO: Implement feature on AVD
                        getContextFeats().setAppAccel(false);
                    }if(feature.contains("magnetic-field") && getContextFeats().isAppMagnet()){
                        System.out.println("---Duping Magnetometer at the App-Level");
                        //StepByStepEngine.dupeSensor("magnetic-field");    //TODO: Implement Feature on AVD
                        getContextFeats().setAppMagnet(false);
                    }if(feature.contains("temperature")&& getContextFeats().isAppTemp()){
                        System.out.println("---Duping Temperature at the App-Level");
                        //StepByStepEngine.dupeSensor("temperature");   //TODO: Implement Feature on AVD
                        getContextFeats().setAppTemp(false);
                    }

                }
                if((feature.contains("ConnectivityManager") || feature.contains("WifiManager") || feature.contains("HttpURLConnection") 
                        || feature.contains("HttpClient")) && getContextFeats().isAppNetwork()){

                    System.out.println("---Disabling Network at the App-Level");
                    //StepByStepEngine.disableNetwork(androidSDKPath);  //TODO: Implement Feature on AVD
                    getContextFeats().setAppNetwork(false);
                }
                if(feature.contains("LocationManager") && getContextFeats().isAppGPS()){
                    System.out.println("---Disabling GPS at the App-Level");
                    //StepByStepEngine.dupeGPS();   //TODO: Implement feature for AVD
                    getContextFeats().setAppGPS(false);
                }


            }// End if loop for checking current Activity

        }

    }

    public Step addStep(DynGuiComponent component, int action, Transition transition, String textEntry,
            String exceptions, Screen currScreen) {
        // Update sequence, create step, and add step
        sequence++;
        Step step = new Step();
        step.setSequenceStep(sequence);
        step.setDynGuiComponent(component);
        step.setAction(action);
        step.setTextEntry(textEntry);
        step.setExceptions(exceptions);

        // This is mandatory for JPA
        if(currScreen!= null){
            step.setScreen(currScreen);
            currScreen.setStep(step);
        }
        // System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        // System.out.println( network || appNetwork);
        step.setNetwork(getContextFeats().isNetwork() || getContextFeats().isAppNetwork());
        step.setGps(getContextFeats().isGps() || getContextFeats().isAppGPS());
        step.setAcellerometer(getContextFeats().isAccelNormal() || getContextFeats().isAppAccel());
        step.setMagentometer(getContextFeats().isMagnetNormal() || getContextFeats().isAppMagnet());
        step.setTemperature(getContextFeats().isTempNormal() || getContextFeats().isAppTemp());

        // Set relationships
        /*  if (component != null) {
                    component.getSteps().add(step);
                }*/
        if (transition != null) {
            DynamicTransition dynTransition = new DynamicTransition();
            dynTransition.setSourceActicity(transition.getSourceActivity());
            dynTransition.setSourceWindow(transition.getSourceWindow());
            dynTransition.setTargetActivity(transition.getTargetActivity());
            dynTransition.setTargetWindow(transition.getTargetWindow());
            dynTransition.setStep(step);
            // Prepare source screen info
            step.setDynamicTransition(dynTransition);
        }
        steps.add(step);
        return step;
    }




    public void generateTextInputs(String appPackage, String mainActivity, String androidSDKPath, String textStrat,
            int executionCtr) {
        System.out.println("Checking for Text Entry Possibilities...");

        //get components for typing
        StringBuilder temp = new StringBuilder();
        ArrayList<DynGuiComponentVO> typingComponents = UiAutoConnector.getScreenInfoGeneric(deviceHelper.getAndroidSDKPath(), temp, getWidthScreen(), getHeightScreen(), false, false, true, deviceHelper.getDevicePort(), 
                deviceHelper.getAdbPort(), getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true).replaceAll(" ", "").replaceAll("'","") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence,deviceType);

        for (DynGuiComponentVO inputComponent : typingComponents) {
            //System.out.println(inputComponent);
            // Filter by EditText
            if (inputComponent.getName().endsWith("EditText") && !crash) {

                //Update current Screen
                currentScreen = getCurrentScreen();

                String input = deviceHelper.executeEvent(getEventFromComponent(inputComponent, DeviceHelper.CLICK_TYPE),
                        getTextStrat(), true);
                String exceptions = deviceHelper.getErrorsFromLogcat(appPackage, getScriptsPath());
                Step lastStep = addStep(StepByStepEngine.getEntityFromVO(inputComponent, false), DeviceHelper.CLICK_TYPE, null, input, exceptions, currentScreen);
                if (takeScreenshots) {
                    takeScreenshot(appPackage, folderScreenshots, getApp().getVersion(),
                            getWidthScreen(), getHeightScreen(), lastStep, textStrat, executionCtr);
                    String guiScreenshot = cropScreenshot(appPackage, androidSDKPath, folderScreenshots, getApp()
                            .getVersion(), getWidthScreen(), getHeightScreen(), lastStep, StepByStepEngine.getEntityFromVO(inputComponent, false), false,
                            textStrat, executionCtr, lastStepRotated);
                    //System.out.println(guiScreenshot); //For debugging
                    if (inputComponent != null && guiScreenshot != null) {
                        //System.out.println("--Setting Gui Screenshot!"); //For debugging
                        lastStep.getDynGuiComponent().setGuiScreenshot(guiScreenshot);
                        //inputComponent.setGuiScreenshot(guiScreenshot);
                    }
                }
                crash = deviceHelper.checkForCrash(appPackage, mainActivity, getWidthScreen(), getHeightScreen(), getUiDumpLocation()+ "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, true);
            }

        }

    }

    public boolean checkIfStepFeasible(DynGuiComponentVO stepToVerify) {

        //If it is a menu button we assume that it exists because we checked for it in updateAvilableStack()
        if(stepToVerify.getName().equals("MENU-BUTTON")){   
            return true;
        }

        StringBuilder hash = null;

        boolean stepFeasible = true;

        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoLayout(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), true, false, true, deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true).replaceAll(" ", "").replaceAll("'","") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType);

        if(!nodes.contains(stepToVerify)) {
            stepFeasible = false;
        }

        return stepFeasible;

    }

    public GUIEventVO getNextStep(String appPackage, String mainActivity, String textStrat, int stepCounter,
            int executionCtr) {

        System.out.println("--Getting the next Step to Execute");

        // First we need to check if there are any remaining actions or if 
        // we are over the maximum number of actions
        if (!getAvailableStackSteps().isEmpty() && sequence < maxNumEvents) {

            //If there are remaining actions, let's check the one that is on the top of the stack
            Step peek = getAvailableStackSteps().peek();
            boolean doBack = false;

            //Now that we have an potential Step, we need to check whether or not this step is feasible.
            //To do this, we first get the current window (which can mean activity, layout, etc... granularity)
            //then we check if the window for the component matches the current window

            //System.out.println(peek.getDynGuiComponent().getCurrentWindow()); //For debugging
            //System.out.println(currentWindow);    //For debugging

            //Here we do the check for whether or not the component is executable from the current app state.
            if (!compareWindows(peek.getDynGuiComponent().getCurrentWindow(), currentWindow)) {

                //If CrashScope finds that the component is not in the current activity, 
                //it will try to find a component that is by iterating over the stack
                //of potential components to be exercised.
                Step tempStep = null;
                for (Step iStep : getAvailableStackSteps()) {
                    if (compareWindows(iStep.getDynGuiComponent().getCurrentWindow(),currentWindow)) {
                        tempStep = iStep;
                    }//End if statement to check for executable components in the current screen state.
                }//End for loop to iterate over the available steps (e.g. potential executable components)

                //Next CrashScope checks whether it found a component that can be exercised from the 
                //current screen state
                if (tempStep != null) {

                    //If there is an available action, then we add it to the top of the stack and
                    //return this as the step to be executed by saving it to peek and returning it at the 
                    //end of the function
                    getAvailableStackSteps().remove(tempStep);
                    addAvailableStep(StepByStepEngine.getVOFromEntity(tempStep.getDynGuiComponent()));
                    peek = getAvailableStackSteps().peek();

                } else {

                    //If there are no components available in the current screen state, then we can
                    //try to use known transitions to get to that component.

                    String key = null;

                    if(!activityLevel) {
                        String tempCurrentWindow = currentWindow.substring(currentWindow.indexOf("->")+2, currentWindow.length());
                        String tempTargetWindow = peek.getDynGuiComponent().getCurrentWindow().substring(peek.getDynGuiComponent().getCurrentWindow().indexOf("->")+2,
                                peek.getDynGuiComponent().getCurrentWindow().length());
                        key =  tempCurrentWindow + "@"
                                + tempTargetWindow;
                    }else {

                        //Get the transition key
                        key =  currentWindow + "@"
                                + peek.getDynGuiComponent().getCurrentWindow();
                    }

                    System.out.println("No steps for the current screen available, trying to use transitions... ");
                    System.out.println("TRANSITION_KEY: " + key);

                    //Use a graph-search (in getTransitions()) to try and find a sequence of single action transitions that 
                    //will bring us to our desired state.
                    List<Transition> steps = null;
                    try {
                        steps = Utilities.getTransitions(getTransitions(), key, getTransitionsIds());
                    } catch (java.util.NoSuchElementException e) {
                        e.printStackTrace();
                    }

                    //If we didn't find any possible transition paths between the desired states, we execute
                    //a back command to try and get the state of the app into one with a potential transition.
                    if (steps == null) {
                        doBack = true;
                    } else {
                        //If we did find a potential transition, we try to execute it to reach the desired state.
                        //Note that here the 
                        int translimit = 0;
                        System.out.println(".::# Beginning Transitions Execution #::.");
                        transloop:for (Transition transition : steps) {
                            translimit ++;
                            if(translimit >= 50){
                                break transloop;
                            }
                            // Transform component to an event in order to reach the
                            // component we just removed from stack
                            GUIEventVO event = new GUIEventVO();
                            if (transition.getComponent() == null) {
                                event.setRealFinalX(0);
                                event.setRealFinalY(0);
                                event.setEventTypeId(StepByStepEngine.BACK);
                            } else {
                                event = getEventFromComponent(transition.getComponent(), transition.getAction());
                            }
                            // execute back if I'm not in home activity
                            String substring = mainActivity.substring(mainActivity.lastIndexOf("."),
                                    mainActivity.length());
                            System.out.println("substring: " + substring);
                            if (!(event.getEventTypeId() == StepByStepEngine.BACK && getApp().getExecutions().get(0)
                                    // .getMainActivity().contains("Emma"))) {
                                    .getMainActivity().contains(substring))) {
                                //Update Current Screen

                                currentScreen = getCurrentScreen();

                                // Execute event transition
                                System.out.println("Executing Transition Event # " + translimit);

                                if(!checkIfStepFeasible(StepByStepEngine.getVOFromEntity(StepByStepEngine.getEntityFromVO(event.getHvInfoComponent(), false)))) {
                                    System.out.println("Removing target step from stack becuase we cannot transition to it.");
                                    getAvailableStackSteps().pop();
                                    return getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
                                }

                                generateTextInputs(appPackage, mainActivity, deviceHelper.getAndroidSDKPath(), textStrat,
                                        executionCtr);

                                if(!checkIfStepFeasible(StepByStepEngine.getVOFromEntity(StepByStepEngine.getEntityFromVO(event.getHvInfoComponent(), false)))) {
                                    System.out.println("Removing target step from stack becuase we cannot transition to it.");
                                    getAvailableStackSteps().pop();
                                    return getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
                                }
                                deviceHelper.executeEvent(event, textStrat, true);

                                if (deviceHelper.isKeyboardActive()) {
                                    deviceHelper.disposeKeyboard();

                                    // Pause to allow keyboard animation to
                                    // complete before continuing.

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                updateAvailableStack(appPackage, deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), false);
                                String checkActivity = deviceHelper.getCurrentActivityImproved();
                                //                              WindowVO checkWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation+ "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
                                //                              String checkWindow = ((checkWindowTitle.getWindow() + (checkWindowTitle.getTitle() != null && !checkWindowTitle.getTitle().isEmpty() ? checkWindowTitle
                                //                                      .getTitle() : ""))).trim();
                                String checkWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), false);

                                String checkCombined = checkWindow;
                                //System.out.println("!!! Comparing :" + checkCombined + " to "
                                //+ transition.getTargetWindow());
                                //Here we need to check if the transition matches what we expect.
                                //If the transitions do not match, it means that we cannot execute the
                                //planned step that was derived at the beginning of the method
                                System.out.println("Checking if trasition event moved to proper state...");
                                if (!compareWindows(checkCombined, transition.getTargetWindow())) {
                                    if (!getAvailableStackSteps().isEmpty()) {
                                        //Pop the step from the stack ??
                                        System.out.println("Removing target step from stack becuase we cannot transition to it.");
                                        getAvailableStackSteps().pop();
                                    }
                                    //Since we couldn't execute the last step due to an infeasible transition
                                    //lets recurse into the getNextStep method to see if we find a feasible one.
                                    return getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
                                }

                                System.out.println("Transition Succeeded!! Continuing transitions");
                                //At this point CrashScope was able to successfully transition to the proper state, so here we have to 
                                //check the new state to see if we exited the app, had a crash, etc..

                                checkNewState(appPackage, mainActivity, transition.getComponent(),event.getEventTypeId(), textStrat, executionCtr);

                                //Check for any thrown exceptions

                                String exceptions = deviceHelper.getErrorsFromLogcat( appPackage, getScriptsPath());

                                Step stepTransition = addStep(StepByStepEngine.getEntityFromVO(transition.getComponent()), event.getEventTypeId(), transition, "none", exceptions, currentScreen);

                                takeScreenshot(appPackage, getFolderScreenshots(), getApp().getVersion(), getWidthScreen(), getHeightScreen(), stepTransition,
                                        textStrat, executionCtr);

                                //printAvailableStack();        //For debugging
                            }
                        }
                        updateAvailableStack(appPackage, deviceHelper.getDevicePort(), deviceHelper.getAdbPort(),false);
                        currentActivity = deviceHelper.getCurrentActivityImproved();
                        //                      WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
                        //                      currentWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
                        //                              .getTitle() : ""))).trim();
                        currentWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), false);
                        System.out.println(".::# Executing Transitions End #::.");

                    }// End conditional to check for potential transition steps
                    // printTransitions(); // For debugging
                }
            }

            if(!checkIfStepFeasible(StepByStepEngine.getVOFromEntity(peek.getDynGuiComponent(), false))) {
                System.out.println("Component Not Avilable to Execute, skipping this step.");
                getAvailableStackSteps().pop();
                return getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
            }

            String substring = mainActivity.substring(mainActivity.lastIndexOf("."), mainActivity.length());
            if (doBack && !getApp().getExecutions().get(0).getMainActivity().contains(substring)) {
                // if (doBack &&
                // !getApp().getExecution().getMainActivity().contains("Emma"))
                // {
                return new GUIEventVO(StepByStepEngine.BACK);
            } else if (doBack) {
                // I'm in the main activity and there is no possible transition,
                // just skip the step and get a new one
                if (!getAvailableStackSteps().isEmpty()) {
                    getAvailableStackSteps().pop();
                }
                if (!getAvailableStackSteps().isEmpty()) {
                    getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
                }
                return null;
            }

            generateTextInputs(appPackage, mainActivity, deviceHelper.getAndroidSDKPath(), textStrat, executionCtr);

            if(!checkIfStepFeasible(StepByStepEngine.getVOFromEntity(peek.getDynGuiComponent(), false))) {
                System.out.println("Component Not Avilable to Execute after Text was entered, skipping this step.");
                getAvailableStackSteps().pop();
                return getNextStep(appPackage, mainActivity, textStrat, stepCounter, executionCtr);
            }

            System.out.println("About to interact with component: " + peek.getDynGuiComponent());
            return getEventFromComponent(StepByStepEngine.getVOFromEntity(peek.getDynGuiComponent()), peek.getAction());

        }
        return null;

    }

    /**
     * 
     * @param appPackage
     * @param all
     *            : True=get all types of components; False = get only
     *            clickable, long clickable and typeable as well as frame layout
     *            components.
     * @return
     */
    private ArrayList<DynGuiComponentVO> updateAvailableStack(String appPackage, String devicePort, String adbPort, boolean all) {

        StringBuilder hash = new StringBuilder();
        // TODO #1 Kevin here you use the one you need
        // Activity 
        System.out.println("Name: " + getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(devicePort, adbPort, all).replaceAll(" ", "").replaceAll("'","") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoGeneric(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), all, false, true, devicePort, adbPort, getUiDumpLocation() + "--" + deviceHelper.getCurrentActivityImproved() + "--" + getcurrentWindow(devicePort, adbPort, all).replaceAll(" ", "").replaceAll("'","") + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence,deviceType);
        // Layout
        //ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoLayout(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), all, false, true, devicePort, adbPort, getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType);
        // Actionable components
         //ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoActionableComponents(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), all, false, true, devicePort, adbPort, getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType);
        // Components + Properties
        // ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoPropertiesComponents(deviceHelper.getAndroidSDKPath(), hash, getWidthScreen(), getHeightScreen(), all, false, true, devicePort, adbPort, getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, deviceType);

        ArrayList<DynGuiComponentVO> prelimComps = new ArrayList<DynGuiComponentVO>();

        boolean root = true;

        // We need to reverse the order of nodes if the strategy is top-down
        if(guiStrat.equals("Top_Down")){
            Collections.reverse(nodes);
        }

        for (DynGuiComponentVO hvComponentVO : nodes) {
            if ((hvComponentVO.isClickable() || hvComponentVO.isLongClickable() || hvComponentVO.isCheckable())) {
                if (!hvComponentVO.getName().endsWith("EditText")) {
                    prelimComps.add(hvComponentVO);
                    //addAvailableStep(hvComponentVO);
                }
            }
            // In the case we have more frameLayaout in the same .xml
            //System.out.println(hvComponentVO.getName());
            if (hvComponentVO.getName().equals("android.widget.FrameLayout") && root) {
                setRootWindow(hvComponentVO);
                //System.out.println("!!!!!!!!!!!!!!!!!!rootWindow: " + hvComponentVO.getHeight() + "x" + hvComponentVO.getWidth());
                root = false;
            }
        }

        String currentActivity = deviceHelper.getCurrentActivityImproved();
        //String currentWindow = Utilities.getCurrentWindowIdFromComponent(deviceHelper.getAndroidSDKPath(), getRootWindow());

        //WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
        // This is Id for generic (Activity version)
        //String currentWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
        //      .getTitle() : ""))).trim();
        // TODO #1 Kevin here use the hash
        //      String currentWindow = hash.toString().hashCode() + "";

        String currentWindow = getcurrentWindow(devicePort, adbPort, all);


        DynWindow window = getWindows().get(currentWindow);
        if (window == null) {
            window = new DynWindow();
            window.setWindow(currentWindow);
        }

        if (currentWindow != null) {
            // update window of elements
            for (DynGuiComponentVO compVo : nodes) {
                window.addComponent(compVo);
                //System.out.println(currentActivity + "#" + currentWindow);
                compVo.setCurrentWindow(currentWindow);
                // System.out.println(currentActivity);
                compVo.setActivity(currentActivity);
            }
        }
        window.setWindow(currentWindow);
        getWindows().put(window.getWindow(), window);

        if(deviceHelper.isMenuButtonAvailable()) {
            DynGuiComponentVO menuBtn = new DynGuiComponentVO();
            menuBtn.setActivity(currentActivity);
            menuBtn.setCurrentWindow(currentWindow);
            menuBtn.setName("MENU-BUTTON");
            menuBtn.setClickable(true);
            window.addComponent(menuBtn);
            addAvailableStep(menuBtn);
        }

        for(DynGuiComponentVO setCompVO: prelimComps){
            addAvailableStep(setCompVO);
        }

        return nodes;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.semeru.android.testing.monkeylab.strategy.ILabStrategy#checkNewState
     * (java.lang .String, java.lang.String)
     */

    @Override
    public Transition checkNewState(String appPackage, String mainActivity, DynGuiComponentVO component, int eventType, String executionType, int executionCtr) {

        System.out.println("Checking New State of App after last Action...");

        ArrayList<DynGuiComponentVO> nodes = new ArrayList<DynGuiComponentVO>();
        
        // Dispose keyboard
        if (deviceHelper.isKeyboardActive()) {
            deviceHelper.disposeKeyboard();

            // Pause to allow keyboard animation to complete before continuing.

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Transition transition = null;
        // Check whether I'm in the activity of the app
        System.out.println("Checking if App has been closed!");
        String activityTemp = deviceHelper.getCurrentActivityImproved();
        if (!activityTemp.contains(appPackage) && !activityTemp.contains("ResolverActivity")) {
            deviceHelper.startAPK(appPackage, mainActivity);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (activityTemp.contains("ResolverActivity")) {
            deviceHelper.doKey("4");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!crash) {
            crash = deviceHelper.checkForCrash(appPackage, mainActivity, getWidthScreen(), getHeightScreen(), getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, true);
        }

        // Check new state and update the list of components
        if(!gnuCash) { //This checks to see if we are doing the pre-defined GNU-Cash Steps
            nodes = updateAvailableStack(appPackage, deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true);
        }

        String newActivity = "";
        String newWindow = "";

        newActivity = deviceHelper.getCurrentActivityImproved();
        //      WindowVO currentWindowTitle = deviceHelper.detectTypeofWindow(widthScreen, heightScreen, uiDumpLocation + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence);
        //      newWindow = ((currentWindowTitle.getWindow() + (currentWindowTitle.getTitle() != null && !currentWindowTitle.getTitle().isEmpty() ? currentWindowTitle
        //              .getTitle() : ""))).trim();

        newWindow = getcurrentWindow(deviceHelper.getDevicePort(), deviceHelper.getAdbPort(), true);


        // Below are the new semantics for the window hash which uses the number
        // of GUI components and the activity hashed together.

        // Are we in a new state (window)? and we didnt send a back
        if (!compareWindows(newWindow, currentWindow)) {
            String source = null;
            String target = null;
            if(!activityLevel) {
                source = currentWindow.substring(currentWindow.indexOf("->")+2,currentWindow.length());
                target = newWindow.substring(newWindow.indexOf("->")+2,newWindow.length());
            }else {
                source=currentWindow;
                target=newWindow;       
            }
            String transitionId = source + "@" + target;
            System.out.println(source + "@" + target);
            transition = new Transition(source, target, currentActivity, newActivity, component);
            transition.setAction(eventType);
            System.out.println("New Transition Found - ID:" + transitionId);
            getTransitions().put(transitionId, transition);
            // Add nodes
            getTransitionsIds().add(source);
            getTransitionsIds().add(target);
            if(!crash){
                if(activateActFeats(newActivity, appPackage, mainActivity) == true){
                    //Update currentScreen
                    currentScreen = getCurrentScreen();

                    String exceptions = deviceHelper.getErrorsFromLogcat( appPackage, getScriptsPath());
                    Step temp_step = addStep(null, 7, null, "null", exceptions, currentScreen);
                    takeScreenshot(appPackage, getFolderScreenshots(), getApp().getVersion(), getWidthScreen(),
                            getHeightScreen(), temp_step, executionType, executionCtr);
                    setCorrectAugScreen(true);
                    if (!crash){
                        crash = deviceHelper.checkForCrash(appPackage, mainActivity, getWidthScreen(), getWidthScreen(), getUiDumpLocation() + "-" + getTextStrat() + "-" + getGuiStrat() + "-" + getFeatStrat() + sequence, true);
                    }
                }
            }
        }
        // Update windows and activities
        currentWindow = newWindow;
        currentActivity = newActivity;
        // update window of elements
        if(!gnuCash) {
        for (DynGuiComponentVO hvComponentVO : nodes) {
            hvComponentVO.setCurrentWindow(currentWindow);
        }
        }
        return transition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.semeru.android.testing.monkeylab.strategy.ILabStrategy#executeStep
     * (edu.semeru .android.testing.model.GUIEventVO, java.lang.String)
     */
    @Override
    public Step executeStep(GUIEventVO step, String appPackage, String executionType) {
        deviceHelper.executeEvent(step, executionType, true);
        // Thread sleep after execute event in order to allow the action to
        // complete.

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (step.getEventTypeId() != StepByStepEngine.BACK && step.getEventTypeId() != StepByStepEngine.CLICK_TYPE) {
            Step pop = getAvailableStackSteps().pop();
//          System.out.println("Adding the following step to the visited stack" + pop);
            getVisitedStackSteps().push(pop);
            return pop;
        } else {
            return null;
        }

    }

    public DynGuiComponent getComponent(HVComponentVO vo) {
        DynGuiComponent gui = new DynGuiComponent();

        // gui.setActivity(vo.getAc);
        gui.setCheckable(vo.isCheckable());
        gui.setChecked(vo.isChecked());
        gui.setClickable(vo.isClickable());
        // gui.setComponentClass(vo.getName());
        gui.setComponentIndex(vo.getIndex());
        gui.setCurrentWindow(vo.getCurrentWindow());
        gui.setDrawTime(vo.getDrawTime());
        gui.setEnabled(vo.isEnabled());
        gui.setFocusable(vo.isFocusable());
        gui.setFocused(vo.isFocused());
        gui.setHeight(vo.getHeight());
        gui.setId(vo.getIdDb());
        gui.setIdXml(vo.getId());
        gui.setLongClickable(vo.isLongClickable());
        gui.setName(vo.getName());
        gui.setPassword(vo.isPassword());
        gui.setPositionX(vo.getPositionX());
        gui.setPositionY(vo.getPositionY());
        gui.setScrollable(vo.isScrollable());
        gui.setSelected(vo.isSelected());
        gui.setText(vo.getText());

        return gui;
    }

    public LogHelper getLogger() {
        return LogHelper.getInstance("log" + File.separator + "uiautomator_steps_" + getIdExecution() + ".txt");
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
                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + appPackage + "_" + version + "_" + currstep + ".png";
            String guiscreenshot =  appPackage + "."
                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + appPackage + "_" + version + "_" + nextStep + ".png";
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
                    Logger.getLogger(CrashScope.class.getName()).log(Level.SEVERE, null, ex);
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
                    + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + appPackage + "_" + version + "_" + currstep + ".png";
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
                                + guiStrat + "." + textStrat + "." + featStrat + "." + executionCtr + "." + appPackage + "_" + version + "_" + currstep + ".png";
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
                Logger.getLogger(CrashScope.class.getName()).log(Level.SEVERE, null, ex);
            }

            return ss;
        }
        return null;
    }

    /**
     * @author KevinMoran
     * 
     * Description: This sets the appropriate strategies to be used during DFS execution
     * 
     * @param topDown
     * @param bottomUp
     * @param noText
     * @param expectedText
     * @param unexpectedText
     * @param contextFeatsEnabled
     * @param contextFeatsDisabled
     */
    public void setCrashScopeStrategy(boolean topDown, boolean bottomUp, boolean noText, boolean expectedText, 
            boolean unexpectedText, boolean contextFeatsEnabled, boolean contextFeatsDisabled){

        strategy.setTopDown(topDown);
        strategy.setBottomUp(bottomUp);
        strategy.setNoText(noText);
        strategy.setExpectedText(unexpectedText);
        strategy.setUnexpectedText(unexpectedText);
        strategy.setContextFeatsEnabled(contextFeatsEnabled);
        strategy.setContextFeatsDisabled(contextFeatsDisabled);

    }


    public static ArrayList<App> generateBugRepData(String appList, String pathToAAPT){


        // Read in the apk File paths from a file
        ArrayList<String> apkFiles = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(appList))) {
            String line;
            while ((line = br.readLine()) != null) {
                apkFiles.add(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Create a list of apps

        String aaptCommand = "";
        String appInfo = "";
        String appName = "";
        String appPackageName = "";
        String appMainActivity = "";
        String appVersion = "";

        ArrayList<App> bugRepApps = new ArrayList<App>();
        App app = new App();
        long appId = 1;

        for(String currFile : apkFiles) {

            System.out.println("Current File: " + currFile);
            System.out.println();

            //Get the application info from aapt
            aaptCommand = pathToAAPT + File.separator + "aapt dump badging " + currFile;
            appInfo = TerminalHelper.executeCommand(aaptCommand);
            System.out.println(appInfo);
            //Parse the application 
            appPackageName = appInfo.substring(appInfo.indexOf("name=")+6, appInfo.indexOf("versionCode")-2);
            System.out.println("Package Name: " + appPackageName);
            appName = appInfo.substring(appInfo.indexOf("application: label='")+20, appInfo.indexOf("icon=")-2);
            System.out.println("App Name: " + appName);
            appMainActivity = appInfo.substring(appInfo.indexOf("launchable-activity: name='")+27);
            appMainActivity = appMainActivity.substring(0, appMainActivity.indexOf(' ')-1);
            System.out.println("App Main Activity: " + appMainActivity);
            appVersion = appInfo.substring(appInfo.indexOf("versionName='")+13);
            appVersion = appVersion.substring(0,appVersion.indexOf(' ')-1);
            System.out.println("App Version: " + appVersion);
            System.out.println();

            app = new App();
            app.setName(appName);
            app.setPackageName(appPackageName);
            app.setMainActivity(appMainActivity);
            app.setVersion(appVersion);
            app.setApkPath(currFile);
            appId++;
            bugRepApps.add(app);

        }

        for(App persistApp : bugRepApps) {
           // PersistDataService.saveAppData(persistApp, PERSIST_UNIT);
        }

        return bugRepApps;
    }

    /**
     * @return the windows
     */
    public HashMap<String, DynWindow> getWindows() {
        return windows;
    }

    /**
     * @param windows
     *            the windows to set
     */
    public void setWindows(HashMap<String, DynWindow> windows) {
        this.windows = windows;
    }


    /**
     * @return the deviceHelper
     */
    public DeviceHelper getDeviceHelper() {
        return deviceHelper;
    }

    /**
     * @param deviceHelper the deviceHelper to set
     */
    public void setDeviceHelper(DeviceHelper deviceHelper) {
        this.deviceHelper = deviceHelper;
    }

    /**
     * @return the dataFolder
     */
    public String getDataFolder() {
        return dataFolder;
    }

    /**
     * @param dataFolder the dataFolder to set
     */
    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * @return the apkPath
     */
    public String getApkPath() {
        return apkPath;
    }

    /**
     * @param apkPath the apkPath to set
     */
    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    /**
     * @return the scriptsPath
     */
    public String getScriptsPath() {
        return scriptsPath;
    }

    /**
     * @param scriptsPath the scriptsPath to set
     */
    public void setScriptsPath(String scriptsPath) {
        this.scriptsPath = scriptsPath;
    }

    /**
     * @return the uiDumpLocation
     */
    public String getUiDumpLocation() {
        return uiDumpLocation;
    }

    /**
     * @param uiDumpLocation the uiDumpLocation to set
     */
    public void setUiDumpLocation(String uiDumpLocation) {
        this.uiDumpLocation = uiDumpLocation;
    }

    /**
     * @return the contextFeats
     */
    public ContextualFeatures getContextFeats() {
        return contextFeats;
    }

    /**
     * @param contextFeats the contextFeats to set
     */
    public void setContextFeats(ContextualFeatures contextFeats) {
        this.contextFeats = contextFeats;
    }

    /**
     * @return the rootWindow
     */
    public DynGuiComponentVO getRootWindow() {
        return rootWindow;
    }

    /**
     * @param rootWindow the rootWindow to set
     */
    public void setRootWindow(DynGuiComponentVO rootWindow) {
        this.rootWindow = rootWindow;
    }

    /**
     * @return the guiStrat
     */
    public String getGuiStrat() {
        return guiStrat;
    }

    /**
     * @param guiStrat the guiStrat to set
     */
    public void setGuiStrat(String guiStrat) {
        this.guiStrat = guiStrat;
    }

    /**
     * @return the featStrat
     */
    public String getFeatStrat() {
        return featStrat;
    }

    /**
     * @param featStrat the featStrat to set
     */
    public void setFeatStrat(String featStrat) {
        this.featStrat = featStrat;
    }

    /**
     * @return the textStrat
     */
    public String getTextStrat() {
        return textStrat;
    }

    /**
     * @param textStrat the textStrat to set
     */
    public void setTextStrat(String textStrat) {
        this.textStrat = textStrat;
    }

    /**
     * @return the lastStepRotated
     */
    public boolean isLastStepRotated() {
        return lastStepRotated;
    }

    /**
     * @param lastStepRotated the lastStepRotated to set
     */
    public void setLastStepRotated(boolean lastStepRotated) {
        this.lastStepRotated = lastStepRotated;
    }

    /**
     * @return the correctAugScreen
     */
    public boolean isCorrectAugScreen() {
        return correctAugScreen;
    }

    /**
     * @param correctAugScreen the correctAugScreen to set
     */
    public void setCorrectAugScreen(boolean correctAugScreen) {
        this.correctAugScreen = correctAugScreen;
    }

    /**
     * @return the firststep
     */
    public boolean isFirststep() {
        return firststep;
    }

    /**
     * @param firststep the firststep to set
     */
    public void setFirststep(boolean firststep) {
        this.firststep = firststep;
    }

    /**
     * @return the executionCtr
     */
    public int getExecutionCtr() {
        return executionCtr;
    }

    /**
     * @param executionCtr the executionCtr to set
     */
    public void setExecutionCtr(int executionCtr) {
        this.executionCtr = executionCtr;
    }

}

