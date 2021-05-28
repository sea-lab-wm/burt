/**
 * Created by Kevin Moran on Aug 2, 2016
 */


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
package edu.semeru.android.testing.helpers;


import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.testing.helpers.UiAutoConnector.TypeDeviceEnum;
import edu.semeru.android.testing.helpers.UiAutomatorBridge;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.core.model.WindowVO;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.CmdProcessBuilder;
import edu.semeru.android.testing.helpers.TerminalHelper;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author KevinMoran
 * @Date 8/2/2016
 *
 *       Description: This class provides access to many different useful
 *       commands that interface with one or several Android Devices running on
 *       a single machine. There is an enumeration of the Device type that
 *       should be instantiated with an object of the class. This way the class
 *       is easily reusable and the device interface can be changed with a
 *       single parameter or line of code.
 *
 */
public class DeviceHelper {

    public final static int CLICK = 0;
    public final static int LONG_CLICK = 1;
    public final static int SWIPE = 2;
    public final static int SWIPE_UP = 20;
    public final static int SWIPE_RIGHT = 21;
    public final static int SWIPE_DOWN = 22;
    public final static int SWIPE_LEFT = 23;
    public final static int CLICK_TYPE = 3;
    public final static int BACK = 4;
    public final static int TYPE = 5;
    public final static int OPEN_APP = 6;
    public final static int ROTATION = 7;
    public final static int GPS = 8;
    public final static int NETWORK = 9;
    public final static int CRASH = 100;
    public static final int NOTHING = 99;
    public static final int KEYEVENT = 19;
    public static final int TYPE_RANDOM = 10;
    public static final int MENU_BTN = 11;
    public static final int DELETE_TEXT = 12;

    // check ->
    // http://stackoverflow.com/questions/7789826/adb-shell-input-events?answertab=votes#tab-top
    @Deprecated() // Use AndroidKeyEvents
    private final static String KEYCODE_MENU = "1";
    @Deprecated() // Use AndroidKeyEvents
    private final static String KEYCODE_HOME = "3";
    @Deprecated() // Use AndroidKeyEvents
    public final static String KEYCODE_BACK = "4";
    @Deprecated() // Use AndroidKeyEvents
    public final static String KEYCODE_MENU_BTN = "82";

    // for contextual menu and bottom menu
    private static final String MENU_BOTTOM_AND_CONTEXTUAL = "AtchDlg";
    private static final String POPUP = "PopupWindow";

    private String deviceCommand;
    private String adbPort;
    private String devicePort;

    private String androidSDKPath;

    private TypeDeviceEnum DEVICE_TYPE;

    // Added builder pattern
    public static class DeviceHelperBuilder {
        private String sdkPath;
        private String portOrName;
        //Default port
        private String adbPort = "5037";

        public DeviceHelperBuilder(final String sdkPath, final String portOrName) {
            this.sdkPath = sdkPath;
            this.portOrName = portOrName;
        }

        public DeviceHelperBuilder setAdbPort(String adbPort) {
            this.adbPort = adbPort;
            return this;
        }

        public DeviceHelper buildDevice() {
            return new DeviceHelper(TypeDeviceEnum.DEVICE, this.sdkPath, this.portOrName, this.adbPort);
        }

    }

    // TODO:Create a method build<TypeOfDevice> for all the devices because this method should be private
    @Deprecated
    public DeviceHelper(UiAutoConnector.TypeDeviceEnum deviceType, String sdkPath, String devicePort, String adbPort) {

        androidSDKPath = sdkPath;
        this.adbPort = adbPort;
        this.devicePort = devicePort;
        // Sets the deviceCommand to be used for all member methods of this
        // class.
        switch (deviceType) {
        case EMULATOR:
            deviceCommand = adbPort + " -s emulator-" + devicePort;
            DEVICE_TYPE = UiAutoConnector.TypeDeviceEnum.EMULATOR;
            break;
        case AVD:
            if (SystemUtils.IS_OS_MAC) {
                deviceCommand = adbPort + " -s 127.0.0.1:" + devicePort;
            } else {
                deviceCommand = adbPort + " -s localhost:" + devicePort;
            }
            DEVICE_TYPE = UiAutoConnector.TypeDeviceEnum.AVD;
            break;
        case GENY_MOTION:
            if (SystemUtils.IS_OS_MAC) {
                deviceCommand = adbPort + " -s " + devicePort;
            } else {
                deviceCommand = adbPort + " -s " + devicePort;
            }
            DEVICE_TYPE = UiAutoConnector.TypeDeviceEnum.GENY_MOTION;
            break;
        case DEVICE:
            deviceCommand = adbPort + " -s " + devicePort;
            DEVICE_TYPE = UiAutoConnector.TypeDeviceEnum.DEVICE;
            break;
        default:
            deviceCommand = adbPort + " -s emulator-" + devicePort;
            DEVICE_TYPE = UiAutoConnector.TypeDeviceEnum.DEVICE;
            break;
        }

    }

    public void unlockDevice() {
        System.out.println("---Unlocking device " + devicePort + " on adb server " + adbPort);
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        TerminalHelper.executeCommand(
                androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell input keyevent 82");
    }

    public static List<String> listDevices(String androidSDKPath) {
        List<String> devices = new ArrayList<>();
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String[] list = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb devices").split("\n");
        // 0932986c device
        for (int i = 1; i < list.length; i++) {
            String name = list[i].split("\t")[0];
            devices.add(name);
        }
        return devices;
    }
    
    /***********************************************************************************************************
     * Method Name: startAPK
     * 
     * Description: This method starts a running application on a target
     * emulator.
     * 
     * @param packageName:
     *            The package Name of the application to be stopped.
     * 
     * @param mainActivity:
     *            The mainActivity of the app to be started. This will be the
     *            activity started by this method.
     * 
     ***********************************************************************************************************/

    public void startAPK(String packageName, String mainActivity) {
        try {
            System.out.println("-- Cleaning logcat before starting APK");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
//          Runtime rt = Runtime.getRuntime();
            System.out.println("-- Starting " + packageName + " on the device");
            TerminalHelper
            .executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell logcat -c");
//          TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand
//                  + " shell am start -n " + packageName + "/" + mainActivity);
            System.out.println(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell monkey -p " + packageName + " -c android.intent.category.LAUNCHER 1");
            TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell monkey -p " + packageName + " -c android.intent.category.LAUNCHER 1");
            Thread.sleep(15000);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void clearLogcat() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
//      Runtime rt = Runtime.getRuntime();
        System.out.println("-- clearning Logcat");
        TerminalHelper
        .executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell logcat -c");
    }


    /***********************************************************************************************************
     * Method Name: startAPK
     * 
     * Description: This method determines whether an application can be started on a given device. It 
     * returns a boolean indicating whether or not the app could be started, true if yes, false if no.
     * 
     * @param packageName:
     *            The package Name of the application to be stopped.
     * 
     * @param mainActivity:
     *            The mainActivity of the app to be started. This will be the
     *            activity started by this method.
     * 
     ***********************************************************************************************************/

    public boolean checkifAPKLaunchable(UiAutomatorBridge bridge, String packageName, String mainActivity, int widthScreen, int heightScreen) {

        boolean launchable = false;
        boolean warning = false;

        try {
            System.out.println("-- Cleaning logcat before starting APK");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            System.out.println("-- Starting " + packageName + " on the device");
            TerminalHelper
            .executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell logcat -c");
            TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand
                    + " shell am start -n " + packageName + "/" + mainActivity);
            Thread.sleep(10000);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        String currPackage = getCurrentActivityImproved();

        //try{
        warning = checkForWarning(bridge, packageName, mainActivity, widthScreen, heightScreen);
        //}catch (Exception exp){
        //return false;
        //}

        if(currPackage.contains(packageName) && !warning){
            System.out.println("APK is launchable!!");
            launchable = true;
        }else{
            System.out.println("APK is not launchable!!");
        }

        return launchable;

    }

    /***********************************************************************************************************
     * Method Name: executeInputCommand
     * 
     * Description: This method executes a given command in the adb shell of a
     * target emulator.
     * 
     * @param command:
     *            The shell command to be executed.
     * 
     * @param waitForTrans:
     *            This boolean indicates whether or not the method should wait
     *            to return until the the device is no longer in a transition
     *            state, e.g. transitioning between screens.
     * 
     ***********************************************************************************************************/

    public void executeInputCommand(String command, boolean waitForTrans) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;
        String emuCommand = null;
        String appTransitionState = "";

        System.out.println("--- Executing GUI event ");

        emuCommand = command.substring(command.indexOf("shell"));

        emuCommand = androidToolsPath + "adb -P " + deviceCommand + " " + emuCommand;
        System.out.println(emuCommand);
        TerminalHelper.executeCommand(emuCommand);

        if (waitForTrans == true) {

            do {
                System.out.println("-App State not Idle, waiting...");
                appTransitionState = getAppTransitionState();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Catch Thread Interrupted Exception
                    e.printStackTrace();
                }

            } while (!appTransitionState.equals("APP_STATE_IDLE"));

            System.out.println("-App State Idle - Ready to Continue");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // End if statement to check App Idle State

    }

    /***********************************************************************************************************
     * Method Name: getCoverageFile
     * 
     * Description: This method pulls a specified emma coverage file (.ec) from
     * a device to a specified location on the local machine.
     * 
     * @param emmaFilePath:
     *            The path to the emma File located on the specified device.
     * 
     * @param emmaExtractionPath:
     *            The path to the location where the emma coverage file will be
     *            extracted.
     * 
     ***********************************************************************************************************/

    public void getCoverageFile(String emmaFilePath, String emmaExtractionPath) {
        try {
            System.out.println("-- Getting Coverage Files from device");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            rt.exec(androidToolsPath + File.separator + "adb -P " + deviceCommand + " pull " + emmaFilePath + " "
                    + emmaExtractionPath).waitFor();
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***********************************************************************************************************
     * Method Name: startInstrumentedAPK
     * 
     * Description: This method starts a running application that was
     * instrumented with Emma on a target emulator.
     * 
     * @param packageName:
     *            The package Name of the application to be stopped.
     * 
     * @param mainActivity:
     *            The mainActivity of the app to be started. This will be the
     *            activity started by this method.
     * 
     * @param coverageFile:
     *            The name of the coverage File to be generated on the device.
     *            This will be used later to pull the file off the device.
     * 
     ***********************************************************************************************************/

    public void startInstrumentedAPK(String packageName, String mainActivity, String coverageFile) {
        try {
            // System.out.println("-- Cleaning logcat before starting APK");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Starting " + packageName + " on the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell logcat -c").waitFor();
            System.out.println(androidToolsPath + File.separator + "adb -P " + deviceCommand
                    + " shell am instrument -e coverageFile " + coverageFile + " " + packageName
                    + "/instrumentation.EmmaInstrumentation");
            rt.exec(androidToolsPath + File.separator + "adb -P " + deviceCommand
                    + " shell am instrument -e coverageFile " + coverageFile + " " + packageName
                    + "/instrumentation.EmmaInstrumentation").waitFor();

            Thread.sleep(1500);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /***********************************************************************************************************
     * Method Name: stopInstrumentedAPK
     * 
     * Description: This method stops a running application that was
     * instrumented with Emma on a target emulator.
     * 
     * @param packageName:
     *            The package Name of the application to be stopped.
     * 
     ***********************************************************************************************************/

    public void stopInstumentedAPK(String packageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Stopping " + packageName + " on the device");
            rt.exec(androidToolsPath + File.separator + "adb -P " + deviceCommand
                    + " shell am broadcast -a com.instrumentation.STOP").waitFor();

            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /***********************************************************************************************************
     * Method Name: stopAPK
     * 
     * Description: This method stops a running application on a target
     * emulator.
     * 
     * @param packageName:
     *            The package Name of the application to be stopped.
     * 
     ***********************************************************************************************************/

    public void stopAPK(String packageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            System.out.println("-- Stopping " + packageName + " on the device");
            TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell am force-stop "
                    + packageName);
            Thread.sleep(2000);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /***********************************************************************************************************
     * Method Name: startLogcatCollection
     * 
     * Description: This method stops the Process started to collect the logcat
     * information from the emulator. This method should be called ONLY after a
     * logcat collection process from startLogcatCollection is started.
     * 
     * @param logFileName:
     *            This is the full path and file name of the logcat output to be
     *            saved.
     * 
     ***********************************************************************************************************/

    public Process startLogcatCollection(String logFileName) {
        Runtime rt = Runtime.getRuntime();
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        System.out.println("--- Starting Verbose Logcat Collection");
        String[] deviceCommandParsed = deviceCommand.split(" ");
        String[] logcatCommand = {androidToolsPath + File.separator + "adb","-P", deviceCommandParsed[0], deviceCommandParsed[1], deviceCommandParsed[2], "logcat", " > ", logFileName};
            
            try {
                Process logcat = CmdProcessBuilder.executeCommandReDirect(logcatCommand, logFileName);
                return logcat;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return null;
    }

    /***********************************************************************************************************
     * Method Name: stopLogcatCollection
     * 
     * Description: This method stops the Process started to collect the logcat
     * information from the emulator. This method should be called ONLY after a
     * logcat collection process from startLogcatCollection is started.
     * 
     * @param logcat:
     *            This is the Process object that was instantiated to start the
     *            logcat collection.
     * 
     ***********************************************************************************************************/

    public void stopLogcatCollection(Process logcat) {
        logcat.destroy();
    }

    /***********************************************************************************************************
     * Method Name: installApp
     * 
     * Description: This method uninstalls a target application from the target
     * emulator.
     * 
     * @param apkPath:
     *            The path to the .apk file of the application to be installed.
     * 
     * @param packageName:
     *            This is the package name of the app to be installed from the
     *            emulator.
     * 
     ************************************************************************************************************/

    public String installApp(String apkPath, String packageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
             System.out.println(androidToolsPath + File.separator + "adb -P "
             + deviceCommand + " install " + apkPath);
            String output = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb -P " + deviceCommand + " install " + apkPath);
            return output;

        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "EXCEPTION";
        }

    }

    /***********************************************************************************************************
     * Method Name: unInstallandInstallApp
     * 
     * Description: This method uninstalls and then reinstalls a target
     * application from the target emulator.
     * 
     * @param packageName:
     *            This is the package name of the app to be uninstalled and
     *            reinstalled from the emulator.
     * 
     * @param apkPath:
     *            the path to the .apk file to be installed.
     ************************************************************************************************************/

    public boolean unInstallAndInstallApp(String apkPath, String packageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            System.out.println("--- Removing " + packageName + " from the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell pm uninstall " + packageName).waitFor();
            System.out.println(androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell pm uninstall "
                    + packageName);
            String executeCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                    + deviceCommand + " shell pm uninstall " + packageName);
            System.out.println("executeCommand: " + executeCommand);
            System.out.println("--- Installing " + packageName + " apk (" + apkPath + ")");
            executeCommand = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb -P " + deviceCommand + " install " + apkPath);
            System.out.println("executeCommand: " + executeCommand);
            if (executeCommand.contains("FAILED")) {
                return false;
            }

        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }
    
    /***********************************************************************************************************
     * Method Name: unInstallandInstallApp
     * 
     * Description: This method uninstalls and then reinstalls a target
     * application from the target emulator.
     * 
     * @param packageName:
     *            This is the package name of the app to be uninstalled and
     *            reinstalled from the emulator.
     * 
     * @param apkPath:
     *            the path to the .apk file to be installed.
     ************************************************************************************************************/

    public void pushFile(String filePath, String devicePath) {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell pm uninstall " + packageName).waitFor();
            System.out.println("Pushing File: " + filePath);
            String executeCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                    + deviceCommand + " push " + filePath + " " + devicePath);
            System.out.println("executeCommand: " + executeCommand);

    }

    /***********************************************************************************************************
     * Method Name: getErrorsFromLogcat
     * 
     * Description: This method captures the errors from the lgocat for a
     * specific application running on a target emulator.
     * 
     * @param packageName:
     *            This is the package name of the app for which you want to
     *            capture the errors.
     * 
     * @param scriptsPath:
     *            The path to the folder containing the logcat error helper
     *            script.
     ************************************************************************************************************/

    public String getErrorsFromLogcat(String packageName, String scriptsPath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "";
        if (SystemUtils.IS_OS_MAC) {
            if(DEVICE_TYPE.equals(DEVICE_TYPE.GENY_MOTION) || DEVICE_TYPE.equals(DEVICE_TYPE.DEVICE)){
                command = scriptsPath + File.separator + "logcat_error_helper-genymotion-and-device.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }else if(DEVICE_TYPE.equals(DEVICE_TYPE.EMULATOR)){
                command = scriptsPath + File.separator + "logcat_error_helper-emulator.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }else{
                command = scriptsPath + File.separator + "logcat_error_helper-mac-avd.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }

        } else if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_WINDOWS) {
            if(DEVICE_TYPE.equals(DEVICE_TYPE.GENY_MOTION) || DEVICE_TYPE.equals(DEVICE_TYPE.DEVICE)){
                command = scriptsPath + File.separator + "logcat_error_helper-genymotion-and-device.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }else if(DEVICE_TYPE.equals(DEVICE_TYPE.EMULATOR)){
                command = scriptsPath + File.separator + "logcat_error_helper-emulator.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }else{
                command = scriptsPath + File.separator + "logcat_error_helper-linux-avd.sh " + packageName + " "
                        + androidToolsPath + File.separator + "adb " + devicePort + " " + adbPort;
            }
        }
        System.out.println(command);
        String error = TerminalHelper.executeCommand(command);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String clear_logcat = androidToolsPath + File.separator + "adb -P " + deviceCommand + " logcat -c";
        String output = TerminalHelper.executeCommand(clear_logcat);
        if (error != null && !error.isEmpty()) {
            System.out.println("Logcat Errors: " + error);
            return (error);
        }
        System.out.println("No exceptions");
        return (null);

    }

    /***********************************************************************************************************
     * Method Name: unInstallApp
     * 
     * Description: This method uninstalls a target application from the target
     * emulator.
     * 
     * @param packageName:
     *            This is the package name of the app to be uninstalled from the
     *            emulator.
     * 
     ************************************************************************************************************/

    public String unInstallApp(String packageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
//          Runtime rt = Runtime.getRuntime();
            System.out.println("--- Removing " + packageName + " from the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell pm uninstall " + packageName).waitFor();
            String executeCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                    + deviceCommand + " shell pm uninstall " + packageName);
            System.out.println("executeCommand: " + executeCommand);
            return executeCommand;
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "EXCEPTION";
        }

    }


    public synchronized void getAndPullScreenshot(String outputFolder, String name) {
        getAndPullScreenshot(outputFolder, name, false);
    }

    /***********************************************************************************************************
     * Method Name: getAndPullScreenshot
     *
     * Description: This method takes of screenshot of the current screen
     * displayed on the emulator and saves it to file on the local machine.
     *
     * @param outputFolder:
     *            This is the full path to the folder where the screenshot files
     *            will be stored.
     *
     * @param name:
     *            This is the filename of hte screenshot that will be saved in
     *            the output folder on the host machine.
     *
     * @param throwExceptions:
     *            when true, this method will throw RuntimeExceptions that may occur,
     *            otherwise, it will not, but will log these exceptions.
     *
     ************************************************************************************************************/

    public synchronized void getAndPullScreenshot(String outputFolder, String name, boolean throwExceptions) {

        try {

            // Make sure parent folder exists, otherwise you will get stuck in an infinite loop below
            File outFolder = new File(outputFolder);
            if (!outFolder.exists()) {
                boolean success = outFolder.mkdirs();
                if (!success && throwExceptions) {
                    throw new RuntimeException("Could not create the output folder: " + outputFolder);
                }
            }

            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            //System.out.println("--- Getting screenshot:" + name); //For Debugging
            File testImage = null;
            // do-while to counteract the emulator behavior where the screenshot
            // is sometimes not generated.
            // This will continually re-generate and pull the screenshot until
            // it is not empty according to file size.
            int checks = 0;
            do {
                checks++;
                String commandOutput = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " +
                        deviceCommand + " shell /system/bin/screencap -p /sdcard/screen.png");
                //Logger.getLogger("adb-output").log(Level.INFO, commandOutput);
                Thread.sleep(2000);
                //System.out.println("--- Pulling screenshot:" + name); //For Debugging
                commandOutput = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " +
                        deviceCommand + " pull /sdcard/screen.png \"" + outputFolder + File.separator + name + "\"");
                //Logger.getLogger("adb-output").log(Level.INFO, commandOutput);

                if (throwExceptions && commandOutput.contains("error:")) {
                    throw new RuntimeException("Could not create screenshot: " + commandOutput);
                }

                testImage = new File(outputFolder + File.separator + name);
            } while (testImage.length() == 0 && checks < 20);

            if (throwExceptions && testImage.length() == 0) {
                throw new RuntimeException("Could not create screenshot: " + testImage);
            }

        } catch (RuntimeException ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
            if (throwExceptions) throw ex;
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void getAndPullUIDump(String outputFolder, String name) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            //System.out.println("--- Getting screenshot:" + name); //For Debugging
            String terminal = "";
            String terminal2 = ""; 
            File testImage = null;
            // do-while to counteract the emulator behavior where the screenshot
            // is sometimes not generated.
            // This will continually re-generate and pull the screenshot until
            // it is not empty according to file size.
            do {
                terminal = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand
                        + " shell mkdir /sdcard/uimonkeyautomator");
                terminal2 = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand
                        + " shell /system/bin/uiautomator dump /sdcard/uimonkeyautomator/ui_dump.xml");
                //Logger.getLogger("adb-output").log(Level.INFO, terminal);
                Thread.sleep(2000);
                //System.out.println("--- Pulling screenshot:" + name); //For Debugging
                terminal = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P " + deviceCommand
                        + " pull /sdcard/uimonkeyautomator/ui_dump.xml '" + outputFolder + File.separator + name + "'");
                //Logger.getLogger("adb-output").log(Level.INFO, terminal);
                testImage = new File(outputFolder + File.separator + name);
            } while (testImage.length() == 0);

        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***********************************************************************************************************
     * Method Name: createAVD
     * 
     * Description: The purpose of this method is to create an avd based off a
     * pre-defined avd on the host machine. The host machine should have several
     * avds that represent physical devices already created through the android
     * AVD manager, and these follow the naming scheme
     * "DeviceName-AndroidPlatformNumber" e.g. Nexus7-19. This method will clone
     * the pre-existing under a new name and create a new instance of a bootable
     * avd.
     * 
     * @param virtualBoxPath:
     *            This is the path to the VBoxManage executable.
     * 
     * @param avdName:
     *            This is the desired name of the new avd being created.
     * 
     * @param deviceType:
     *            This is the name of the target pre-configured avd.
     ************************************************************************************************************/

    public void createAVD(String virtualBoxPath, String avdName, String deviceType) {
        // String androidToolsPath = androidAVDPath;

        System.out.println("---Creating new Emulator " + avdName + " by cloning " + deviceType);

        // Clone the target device to the new name.

        String terminaloutput = TerminalHelper.executeCommand(virtualBoxPath + File.separator + "VBoxManage clonevm \""
                + deviceType + "\" --name " + avdName + " --register");

    }

    /***********************************************************************************************************
     * Method Name: startAVD
     * 
     * Description: Starts an emulator of a given avd name on a specific avd
     * server (specified by the port #) and emulator port.
     * 
     * @param androidSDKPath:
     *            Path to the local install of the Android SDK.
     * 
     * @param avdPort:
     *            Port number of the emulator that you wish to start and connect
     *            to.
     * 
     * @param adbPort:
     *            The port of the adb server the target emulator is connected
     *            to.
     * 
     * @param avdName:
     *            The name of the android virtual device to started.
     * 
     * @param gpu:
     *            Specifies wether or not GPU acceleration should be enabled.
     *            Takes a boolean argument true=on false=off.
     ***********************************************************************************************************/

    public void startAndConnectAVD(String virtualBoxPath, String avdPort, String adbPort, String avdName,
            boolean headless) {

        System.out.println("---Starting " + avdPort + " on adb server " + adbPort);
        String bootcompleted = "0";
        String gui = "";

        // if statement to set whether the machine should be headless or not.

        if (headless) {
            gui = "--type headless";
        } else {
            gui = "";
        }

        String androidToolsPath = androidSDKPath + File.separator + "tools";
        String androidPlatformToolsPath = androidSDKPath + File.separator + "platform-tools";
        String[] commands = new String[2];
        commands[0] = "export DISPLAY=:1";
        commands[1] = virtualBoxPath + File.separator + "VBoxManage startvm \"" + avdName + "\" " + gui;
        // String command = virtualBoxPath + File.separator + "VBoxManage
        // startvm \"" + avdName + "\" " + gui;
        // System.out.println(command);
        TerminalHelper.executeCommands(commands);
        // String terminaloutput = TerminalHelper.executeCommand("export
        // DISPLAY=:1 " + virtualBoxPath + File.separator +"VBoxManage startvm
        // \"" + avdName + "\" " + gui);

        // System.out.println(terminalOutput);

        // Wait for the Virtual Machine to boot.

        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String terminaloutput1 = "";
        // Connect the virtual machine to the specified adb port.
        if (SystemUtils.IS_OS_MAC) {
            terminaloutput1 = TerminalHelper.executeCommand(
                    androidPlatformToolsPath + File.separator + "adb -P " + adbPort + " connect 127.0.0.1:" + avdPort);
        } else {
            terminaloutput1 = TerminalHelper.executeCommand(
                    androidPlatformToolsPath + File.separator + "adb -P " + adbPort + " connect localhost:" + avdPort);
        }
        System.out.println(terminaloutput1);

    }

    public void startAndConnectAVD(String virtualBoxPath, String avdPort, String adbPort, String avdName,
            boolean headless, int timeout) {

        System.out.println("---Starting " + avdPort + " on adb server " + adbPort);
        String bootcompleted = "0";
        String gui = "";

        // if statement to set whether the machine should be headless or not.

        if (headless) {
            gui = "--type headless";
        } else {
            gui = "";
        }

        String androidToolsPath = androidSDKPath + File.separator + "tools";
        String androidPlatformToolsPath = androidSDKPath + File.separator + "platform-tools";
        String[] commands = new String[2];

        if (SystemUtils.IS_OS_MAC) {
            String command = virtualBoxPath + File.separator + "VBoxManage startvm \"" + avdName + "\" " + gui;
            TerminalHelper.executeCommand(command);
        } else {
            commands[0] = "export DISPLAY=:0";
            commands[1] = virtualBoxPath + File.separator + "VBoxManage startvm \"" + avdName + "\" " + gui;
            TerminalHelper.executeCommands(commands);
            System.out.println(commands[0] + " " + commands[1]);
        }

        // Wait for the Virtual Machine to boot.
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String terminaloutput1 = "";

        // Connect the virtual machine to the specified adb port.
        if (SystemUtils.IS_OS_MAC) {
            terminaloutput1 = TerminalHelper.executeCommand(
                    androidPlatformToolsPath + File.separator + "adb -P " + adbPort + " connect 127.0.0.1:" + avdPort);
        } else {
            terminaloutput1 = TerminalHelper.executeCommand(
                    androidPlatformToolsPath + File.separator + "adb -P " + adbPort + " connect localhost:" + avdPort);
        }
        System.out.println(terminaloutput1);

    }

    /***********************************************************************************************************
     * Method Name: killAVD
     * 
     * Description: This method stops a currently running emulator via an adb
     * command.
     * 
     * @param virtualBoxPath:
     *            The path to the VBoxManage executable.
     * 
     * @param avdName:
     *            The name of the avd you wish to kill.
     * 
     * @param adbPort:
     *            The port of the adb server the emulator is attached to.
     * 
     ***********************************************************************************************************/

    public void killAVD(String virtualBoxPath, String avdName) {

        System.out.println("---Killing AVD called: " + avdName + "---");

        String terminalCommand1 = TerminalHelper
                .executeCommand(virtualBoxPath + "VBoxManage controlvm \"" + avdName + "\" poweroff");
        System.out.println(terminalCommand1);

    }

    /***********************************************************************************************************
     * Method Name: startAppByPackage
     * 
     * Description: This method starts an application by using only it's
     * package, useful for when the main activity cannot be identified through
     * apkTool or through aapt dump.
     * 
     * @param appPackageName:
     *            This is the name of the app's package that you wish to start
     * 
     ***********************************************************************************************************/

    public void startAppByPackage(String appPackageName) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        String terminalCommand1 = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                + deviceCommand + " shell monkey -p " + appPackageName + " -c android.intent.category.LAUNCHER 1");

    }

    /***********************************************************************************************************
     * Method Name: enableVirtualKeyboardNexus7
     * 
     * Description: This method starts an application by using only it's
     * package, useful for when the main activity cannot be identified through
     * apkTool or through aapt dump.
     * 
     * @param appPackageName:
     *            This is the name of the app's package that you wish to start
     * 
     ***********************************************************************************************************/

    public void enableVirtualKeyboardNexus7() {

        // Server&Legacy-572 1677 Group-306 1570
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("--- Enabling Virtual Keyboard...");

        String commands[] = { "input tap 609 1864", "input touchscreen swipe 917 0 917 917 1131", "input tap 1008 142",
                "input tap 306 1570", "input tap 554 657", "input tap 1048 865", "input tap 609 1864" };

        for (int i = 0; i <= 6; i++) {

            System.out.println("Executing command: " + commands[i]);

            String terminalCommand = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell " + commands[i]);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    
    /***********************************************************************************************************
     * Method Name: configureGNUCash
     * 
     * Description: This method starts an application by using only it's
     * package, useful for when the main activity cannot be identified through
     * apkTool or through aapt dump.
     * 
     * @param appPackageName:
     *            This is the name of the app's package that you wish to start
     * 
     ***********************************************************************************************************/

    public void configureGNUCash() {

        // Server&Legacy-572 1677 Group-306 1570
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("--- Enabling Virtual Keyboard...");

        String commands[] = { "input tap 910 1776", "input tap 910 1776",
                "input tap 910 1776", "input tap 1100 490", "input tap 910 1776"};

        for (int i = 0; i <= 6; i++) {

            System.out.println("Executing command: " + commands[i]);

            String terminalCommand = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell " + commands[i]);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /***********************************************************************************************************
     * Method Name: getAppTransitionState
     * 
     * Description: This method gets the current animation transition state.
     * TODO: Add possible state Strings returned
     * 
     ***********************************************************************************************************/

    public String getAppTransitionState() {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("-Getting current Window Transition State...");
        // System.out.println(androidToolsPath + File.separator + "adb -P " +
        // adbPort + " -s " + avdAddress + " shell dumpsys window -a | grep
        // 'mAppTransitionState'");
        String terminalCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                + deviceCommand + " shell dumpsys window -a | grep 'mAppTransitionState'");

        terminalCommand = terminalCommand.substring(terminalCommand.indexOf("mAppTransitionState=") + 20,
                terminalCommand.length());

        // System.out.println(terminalCommand);

        return terminalCommand;

    }

    /***********************************************************************************************************
     * Method Name: removeAppData
     * 
     * Description: This removes target external application data (e.g. saved on
     * sdcard) on an Android device.
     * 
     * @param pathToData:
     *            This is the path of the data that you wish to delete on the
     *            device
     * 
     ***********************************************************************************************************/

    public void removeAppData(String pathToData) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("Delteing Stale Application Data at: " + pathToData);
        System.out.println(
                androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell rm -rf '" + pathToData + "'");
        String terminalCommand = TerminalHelper.executeCommand(
                androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell rm -rf '" + pathToData + "'");

        System.out.println(terminalCommand);

    }
    

    /***********************************************************************************************************
     * Method Name: getCurrentActivityAVD
     * 
     * Description: This method returns the current focused activity for an
     * Android device.
     * 
     ***********************************************************************************************************/

    public String getCurrentActivityImproved() {

        String emulatorConnect = "";

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        // System.out.println(activity);

        if (devicePort != null && !devicePort.isEmpty()) {
            emulatorConnect = "adb -P " + deviceCommand + " ";
        }

        String command = androidToolsPath + File.separator + emulatorConnect
                + " shell dumpsys window windows";
        String test = TerminalHelper.executeCommand(command).trim();
        
        String[] lines = test.split("\n");
        String activityLine = "";
        
        for (String activityTest: lines) {
            if(activityTest.contains("mCurrentFocus") ) {
                activityLine = activityTest;
            }else if(activityTest.contains("mFocusedApp")) {
                activityLine = activityLine + "\n" + activityTest;
            }
        }

        // System.out.println(activityLine);
        String[] result = activityLine.split("\n");
        String activity1 = null;
        String activity2 = null;
        try {
            activity2 = result[1].trim().split(" ")[4];
        } catch (Exception e) {
            return "com.android.launcher2.Launcher";
        }
        String packageName = activity2.substring(0, activity2.indexOf("/"));
        activity2 = packageName + activity2.replace(packageName + "/", "").replace("..", ".");

        // If current window is null then take app focused activity, this
        // happens when asking for the current window during a transition
        if (result[0].contains("mCurrentFocus=null")) {
            return activity2;
        }

        activity1 = result[0].trim().split(" ")[2].replace("}", "");

        // does it contain PopupWindow?
        if (activity1.startsWith("PopupWindow")) {
            return activity2;
        } else if (activity1.startsWith("AtchDlg:")) {
            // does it contain AtchDlg?
            activity1 = activity1.replace("AtchDlg:", "");
        }

        activity1 = packageName + activity1.replace(packageName, "").replace("/", "").replace("..", ".");

        if (!activity1.equals(activity2)) {
            System.out.println(activity1);
            return activity1;
        } else {
            System.out.println(activity2);
            return activity2;
        }
    }

    /***********************************************************************************************************
     * Method Name: pullFilesFromDeviceFolder
     * 
     * Description: This method pulls all the files from a folder on a target
     * device.
     * 
     * @param devicePath:
     *            The path to folder on the device from which you want to pull
     *            the contents.
     * 
     * @param targetPath:
     *            The path to location you would like the files extracted on the
     *            home machine.
     * 
     ***********************************************************************************************************/

    public void pullFilesFromDeviceFolder(String devicePath, String targetPath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " pull " + devicePath + " '"
                + targetPath + "'";
        System.out.println(command);
        System.out.println(TerminalHelper.executeCommand(command));
    }

    /***********************************************************************************************************
     * Method Name: createDeviceFolder
     * 
     * Description: This method creates a Folder with a specified path on the
     * target device.
     * 
     * @param devicePath:
     *            The path to folder on the device that you want to create.
     * 
     ***********************************************************************************************************/

    public void createDeviceFolder(String devicePath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell mkdir -p "
                + devicePath;
        System.out.println(command);
        System.out.println(TerminalHelper.executeCommand(command));
    }

    /***********************************************************************************************************
     * Method Name: connectAVDtoADB
     * 
     * Description: This method connects a currently running android device to a
     * running adb server at a specified port.
     * 
     * @param androidSDKPath:
     *            Path to the local install of the Android SDK.
     * 
     * @param avdPort:
     *            The port of the emulator that you wish to connect to adb.
     * 
     * @param adbPort:
     *            The port of the adb server the emulator is attached to.
     * 
     ***********************************************************************************************************/

    public void connectAVDtoABD(String avdPort, String adbPort) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "";
        if (SystemUtils.IS_OS_MAC) {
            command = androidToolsPath + File.separator + "adb -P " + adbPort + " connect 127.0.0.1:" + avdPort + "";
        } else {
            command = androidToolsPath + File.separator + "adb -P " + adbPort + " connect localhost:" + avdPort + "";
        }
        System.out.println(TerminalHelper.executeCommand(command));
    }

    /***********************************************************************************************************
     * Method Name: checkForCrash
     * 
     * Description: This method checks to see whether the currently running
     * application on a target device has crashed by detecting the Crash Dialog
     * GUI object.
     * 
     * @param appPackage:
     *            The package of the currently running application that you wish
     *            to check the crash status.
     * @param mainActivity:
     *            The main activity of the app in question
     * @param widthScreen:
     *            The current width of the screen in pixels
     * @param hieghtScreen:
     *            The current height of the screen in pixels.
     * @param uiDumpName:
     *            The name of the UI xml file to be collected off the device
     * @param close:
     *            boolean to signal whether or not the crash dialog should be
     *            closed.
     * 
     ***********************************************************************************************************/

    public boolean checkForCrash(String appPackage, String mainActivity, int widthScreen, int heightScreen,
            String uiDumpName, boolean close) {
        // Check for Crash
        boolean crash = false;
        System.out.println("Checking for Crash...");
        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoGeneric(androidSDKPath, widthScreen,
                heightScreen, true, false, false, devicePort, adbPort, uiDumpName, DEVICE_TYPE);
        for (DynGuiComponentVO dynGuiComponent : nodes) {
            // System.out.println(dynGuiComponent.getText());
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("has stopped.")) {
                System.out.println("CRASH");
                crash = true;
            }
            if (dynGuiComponent.getName().endsWith("Button") && crash && close) {
                // Execute the Okay button to dismiss the crash dialog
                System.out.println("Dismissing the Crash Dialog");
                GUIEventVO eventFromComponent = UiAutoConnector.getEventFromComponent(dynGuiComponent,
                        StepByStepEngine.CLICK);
                executeEvent(eventFromComponent, "", true);
                stopAPK(appPackage);
                startAPK(appPackage, mainActivity);
            }
        }
        return crash; // Returns false if their is a crash!
    }

    /***********************************************************************************************************
     * Method Name: checkForCrash
     * 
     * Description: This method checks to see whether the currently running
     * application on a target device has crashed by detecting the Crash Dialog
     * GUI object.
     * 
     * @param appPackage:
     *            The package of the currently running application that you wish
     *            to check the crash status.
     * @param mainActivity:
     *            The main activity of the app in question
     * @param widthScreen:
     *            The current width of the screen in pixels
     * @param hieghtScreen:
     *            The current height of the screen in pixels.
     * @param uiDumpName:
     *            The name of the UI xml file to be collected off the device
     * @param close:
     *            boolean to signal whether or not the crash dialog should be
     *            closed.
     * 
     ***********************************************************************************************************/

    public boolean checkForWarning(UiAutomatorBridge bridge, String appPackage, String mainActivity, int widthScreen, int heightScreen) {
        // Check for Crash
        boolean crash = false;
        System.out.println("Checking for Warning...");
        bridge.updateTree();
        ArrayList<DynGuiComponentVO> nodes = bridge.getScreenInfo(widthScreen, heightScreen, true, false);
        for (DynGuiComponentVO dynGuiComponent : nodes) {
            // System.out.println(dynGuiComponent.getText());
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("Your device does not")) {
                System.out.println("WARNING DIALOG");
                crash = true;
            }
        }
        return crash; 
    }

    /***********************************************************************************************************
     * Method Name: setAVDPortNumber
     * 
     * Description: This method checks to see whether the currently running
     * application on a target device has crashed.
     * 
     * @param virtualBoxPath:
     *            Path to the VBoxManage executable
     * 
     * @param avdName:
     *            The name of the AVD for which you would like to set the port
     *            numbers
     * 
     * @param adbPort:
     *            The desired port number to connect to adb for this avd on the
     *            host machine
     * 
     * @param consolePort:
     *            The desired number of the console port of the avd on the host
     *            machine.
     * 
     ***********************************************************************************************************/

    public void setAVDPortNumber(String virtualBoxPath, String avdName, String adbPort, String consolePort) {

        String adbPortCommand = virtualBoxPath + File.separator + "VBoxManage modifyvm \"" + avdName + "\" --natpf1 "
                + "\"adb1,tcp,," + adbPort + ",,5555\"";
        TerminalHelper.executeCommand(adbPortCommand);
        String consolePortCommand = virtualBoxPath + File.separator + "VBoxManage modifyvm \"" + avdName
                + "\" --natpf1 " + "\"adb1,tcp,," + consolePort + ",,5554\"";
        TerminalHelper.executeCommand(consolePortCommand);

    }

    /***********************************************************************************************************
     * Method Name: generateCoverageFiles
     * 
     * Description: This method sends the braodcast Intent for the SEMERU Emma
     * Code coverage collection tool to generate the coverage files on the
     * device (e.g. the .ec files).
     * 
     ***********************************************************************************************************/

    public void generateCoverageFiles() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String coverageCommand = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell am broadcast -a edu.semeru.android.emma.COLLECT_COVERAGE";
        String coverageOutput = TerminalHelper.executeCommand(coverageCommand);
        System.out.println(coverageOutput);

    }

    /***********************************************************************************************************
     * Method Name: getAppVersionAdb
     * 
     * Description: Gets the version of an app installed on a device by package
     * name.
     * 
     * @param appPackage:
     *            The package of the app for which you want to know the version.
     * 
     ***********************************************************************************************************/

    public String getAppVersionAdb(String appPackage) {
        String result = "";
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;
            String command = androidToolsPath + "adb -P " + deviceCommand + " shell dumpsys package " + appPackage
                    + " | grep versionName";
            String version = TerminalHelper.executeCommand(command);
            return version.substring(version.indexOf("=") + 1, version.length()).trim();
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /***********************************************************************************************************
     * Method Name: getOrientation
     * 
     * Description: Gets the current orientation of the device screen.
     * 
     ***********************************************************************************************************/

    public int getOrientation() {
        String line = null;
        String adb = androidSDKPath + File.separator + "platform-tools" + File.separator + "adb";
        try {
            String command = adb + " -P " + deviceCommand
                    + " shell dumpsys input | grep 'SurfaceOrientation' |  awk '{ print $2 }' | head -n 1";

            line = TerminalHelper.executeCommand(command);
            if (line == null || (line != null && line.isEmpty())) {
                return 0;
            }

        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.parseInt(line);
    }

    /***********************************************************************************************************
     * Method Name: getScreenSize
     * 
     * Description: Gets the current screen size of a given device
     * 
     ***********************************************************************************************************/

    public String getScreenSize() {
        String result = "";
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;

            String command = androidToolsPath + "adb -P " + deviceCommand
                    + " shell dumpsys window | grep \"mUnrestrictedScreen\"|  awk '{ print $2 }'";
            return TerminalHelper.executeCommand(command);
        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * This method returns an array with the size of the screen of a current device
     * 
     * @return an array of length in which position 0 is width and position 1 is height
     */
    public int[] getScreenSizeArray() {
        String[] screenSize = getScreenSize().split("x");
        int width = Integer.parseInt(screenSize[0]);
        int height = Integer.parseInt(screenSize[1]);
        return new int[] { width, height };
    }

    /***********************************************************************************************************
     * Method Name: restartADBServer
     * 
     * Description: Restarts the current ADB server.
     * 
     ***********************************************************************************************************/

    public void restartADBServer() {
        String result = "";
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;
            String command = androidToolsPath + "adb -P kill-server";
            TerminalHelper.executeCommand(command);
            Thread.sleep(3000);
            command = androidToolsPath + "adb -P " + adbPort + " devices";
            TerminalHelper.executeCommand(command);
            Thread.sleep(3000);

        } catch (Exception ex) {
            Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /***********************************************************************************************************
     * Method Name: cleanUiAutomator
     * 
     * Description: Removes the most recent ui-dump file from the default SEMERU
     * tools location.
     * 
     ***********************************************************************************************************/

    public void cleanUiAutomator() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        String terminalCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb -P "
                + deviceCommand + " shell rm /sdcard/uimonkeyautomator/ui_dump.xml");

    }

    /***********************************************************************************************************
     * Method Name: doType
     * 
     * Description: Enters a given text string on a device.
     * 
     * @param text:
     *            The text to be entered on the device.
     * 
     ***********************************************************************************************************/

    public String doType(String text) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "adb -P " + deviceCommand + " shell input text \"" + text + "\"";
        System.out.println("--- Executing GUI event " + command);
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command);
        return command;

    }

    /***********************************************************************************************************
     * Method Name: doKey
     * 
     * Description: Performs a given input event on a device.
     * 
     * @param keyEvent:
     *            The adb input event to be executed.
     * 
     ***********************************************************************************************************/

    public String doKey(String keyEvent) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "adb -P " + deviceCommand + " shell input keyevent '" + keyEvent + "'";

        System.out.println("--- Executing GUI event " + command);
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command);
        return command;
    }


    public String executeEvent(GUIEventVO vo, String textStrategy, boolean hideKeyboard) {
        return executeEvent(vo, textStrategy, hideKeyboard, 500);
    }

    /***********************************************************************************************************
     * Method Name: executeEvent
     * 
     * Description: This method checks to see whether the currently running
     * application on a target device has crashed.
     * 
     * @param vo:
     *            the GUIEvent Value Object to be executed.
     * 
     * @param executionType:
     *            The type of text execution (e.g. Expected, Unexpected, No
     *            Text).
     * 
     * @param hideKeyboard:
     *            The desired number of the console port of the avd on the host
     *            machine.
     * 
     ***********************************************************************************************************/

    public String executeEvent(GUIEventVO vo, String textStrategy, boolean hideKeyboard, long waitMillis) {
        String command = null;

        //determine the ADB command that needs to be executed
        switch (vo.getEventTypeId()) {
        case DeviceHelper.CLICK:
            command = "adb -P " + deviceCommand + " shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY();
            break;
        case DeviceHelper.LONG_CLICK:
            command = "adb -P " + deviceCommand + " shell input touchscreen swipe  " + vo.getRealInitialX() + " "
                    + vo.getRealInitialY() + " " + vo.getRealInitialX() + " " + vo.getRealInitialY() + " 2000";
            break;

        case DeviceHelper.SWIPE:
            command = "adb -P " + deviceCommand + " shell input touchscreen swipe  " + vo.getRealInitialX() + " "
                    + vo.getRealInitialY() + " " + vo.getRealFinalX() + " " + vo.getRealFinalY() + " "
                    + (int) (vo.getDuration() * 1000);
            break;

        case DeviceHelper.CLICK_TYPE:
            command = "adb -P " + deviceCommand + " shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY();
            break;

        case DeviceHelper.TYPE:
            command = "adb -P " + deviceCommand + " shell input text " + vo.getText();
            break;

        case DeviceHelper.DELETE_TEXT:
            //this method call already executes the event(s)!
            command = deleteText();
            break;

        case DeviceHelper.KEYEVENT:
            command = "adb -P " + deviceCommand + " shell input keyevent " + vo.getText();
            break;

        case DeviceHelper.BACK:
            command = "adb -P " + deviceCommand + " shell input keyevent " + AndroidKeyEvents.getKeyEvent(KeyCode.BACK);
            break;

        case DeviceHelper.MENU_BTN:
            command = "adb -P " + deviceCommand + " shell input keyevent " +  AndroidKeyEvents.getKeyEvent(KeyCode.MENU);
            break;

        case DeviceHelper.TYPE_RANDOM:
            int inputType = InputHelper.checkInputType(androidSDKPath, deviceCommand);
            String input = InputHelper.generateInput(inputType, textStrategy);
            vo.getHvInfoComponent().setText(input);
            vo.setText(input);
            command = "adb -P " + deviceCommand + " shell input text " + vo.getText();
            break;

        }

        //FIXME: refactor this method to return a list of commands? DELETE_TEXT needs more than one command
        if (command != null
                //DELETE_TEXT was already executed!
                && vo.getEventTypeId() != DeviceHelper.DELETE_TEXT) {

            try {

                //execute the command
                String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
                Runtime rt = Runtime.getRuntime();
                System.out.println("--- Executing GUI event " + command);
                rt.exec(androidToolsPath + File.separator + command).waitFor();

                //handle the CLICK_TYPE case
                if (vo != null && vo.getEventTypeId() == DeviceHelper.CLICK_TYPE) {
                    if (isKeyboardActive() && hideKeyboard) {
                        disposeKeyboard();
                    }
                    System.out.println("Generating Input...");
                    int inputType = InputHelper.checkInputType(androidSDKPath, deviceCommand);
                    String input = InputHelper.generateInput(inputType, textStrategy);
                    command += "\n";
                    command += deleteText();
                    command += doType(input);
                    // New keyboard version requires this
                    if (isKeyboardActive() && hideKeyboard) {
                        disposeKeyboard();
                    }
                    System.out.println("Input: " + input);
                    vo.setText(input);
                    return command;
                }

            } catch (Exception ex) {
                Logger.getLogger(DeviceHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //We need to make sure the screen is ready for executing the next event
        // (e.g., when there are transitions between different screens)
        String appTransitionState = null;

        long  startingTime = new Date().getTime();
        long currentTime = -1;
        final int TIME_LIMIT = 20000; //20 seconds
        do {
            System.out.println("-App State not Idle, waiting...");
            appTransitionState = getAppTransitionState();
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                // Catch Thread Interrupted Exception
                e.printStackTrace();
            }
            currentTime = new Date().getTime();
        } while (!appTransitionState.contains("APP_STATE_IDLE") && (currentTime - startingTime) <= TIME_LIMIT);

        return command;
    }

    private String deleteText() {

        // Move to the end of the line
        String moveEndKey = AndroidKeyEvents.getStringKeyEvent(KeyCode.MOVE_END);
        String command = doKey(moveEndKey) + "\n";

        // Do the deletes (50)
        String delKey = AndroidKeyEvents.getStringKeyEvent(KeyCode.DEL);
        String backCommand = String.join(" ", Collections.nCopies(50, delKey));
        command += doKey(backCommand) + "\n";

        return command;
    }

    /***********************************************************************************************************
     * Method Name: disposeKeyboard
     * 
     * Description: Disposes of the Android virtual keyboard if the view is
     * active on the screen.
     * 
     ***********************************************************************************************************/

    public void disposeKeyboard() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell input keyevent 111";
        TerminalHelper.executeCommand(command);
        String appTransitionState = "";
        do {
            System.out.println("-App State not Idle, waiting...");
            appTransitionState = getAppTransitionState();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Catch Thread Interrupted Exception
                e.printStackTrace();
            }

        } while (!appTransitionState.equals("APP_STATE_IDLE"));

        System.out.println("-App State Idle - Ready to Continue");
    }

    /***********************************************************************************************************
     * Method Name: isKeyboardActive
     * 
     * Description: Checks to see whether or not the Android virtual keyboard is
     * visible on a device screen.
     * 
     ***********************************************************************************************************/

    public boolean isKeyboardActive() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell dumpsys input_method|grep mInputShown| awk '{print $4 }'";
        String line = TerminalHelper.executeCommand(command);
        if (line != null && !line.isEmpty() && line.contains("true")) {
            return true;
        }
        return false;
    }

    /***********************************************************************************************************
     * Method Name: isMenuButtonAvailable
     * 
     * Description: Checks to see whether the menu button is available to interact with
     * 
     ***********************************************************************************************************/
    
    public boolean isMenuButtonAvailable() {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell dumpsys window -a | grep 'mLastFocusNeedsMenu'";
        String line = TerminalHelper.executeCommand(command);
        if (line != null && !line.isEmpty()) {
            return true;
        }else {
            return false;
        }

    }

    /***********************************************************************************************************
     * Method Name: disableAccelerometer
     * 
     * Description: Disables the tilt to rotate feature on a device. Allows for
     * programmatically changing the device orientation through adb, and the
     * SEMERU android tools.
     * 
     ***********************************************************************************************************/

    public void disableAccelerometer() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0";
        TerminalHelper.executeCommand(command);
    }

    /***********************************************************************************************************
     * Method Name: rotateDevice
     * 
     * Description: Rotates the device orientation to a given position.
     * 
     * @param orientation:
     *            The device orientation to be rotated.
     * 
     ***********************************************************************************************************/

    public void rotateDevice(String orientation) {
        int orientationValue = 0;
        if (orientation.equals("Portrait")) {
            orientationValue = 0;
        } else if (orientation.equals("Portrait-U")) {
            orientationValue = 2;
        } else if (orientation.equals("Landscape-Left")) {
            orientationValue = 1;
        } else if (orientation.equals("Landscape-Right")) {
            orientationValue = 3;
        }
        rotateDevice(orientationValue);
    }
    
    public void rotateDevice(int orientation) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:"
                + orientation;
        TerminalHelper.executeCommand(command);
    }

    /***********************************************************************************************************
     * Method Name: getAndroidVersion
     * 
     * Description: Retrieves the Android version of a device
     * 
     ***********************************************************************************************************/

    public String getAndroidVersion() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell getprop ro.build.version.release";
        String result = TerminalHelper.executeCommand(command);

        return result;

    }

    /***********************************************************************************************************
     * Method Name: getDeviceVersion
     * 
     * Description: Retrieves the Device model of a given device.
     * 
     ***********************************************************************************************************/

    public String getDeviceVersion() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell getprop ro.product.model";
        return TerminalHelper.executeCommand(command).trim();
    }
    
    /**
     * Checks whether an application is installed on the device
     * 
     * @param packageName
     * @return true if the application is installed
     */
    public boolean isAppInstalled(String packageName) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell pm list packages";
        String packages = TerminalHelper.executeCommand(command).trim();
        if (!packages.isEmpty()) {
            String[] split = packages.split("\n");
            for (String app : split) {
                return app.contains(packageName);
            }
        }
        return false;
    }

    /***********************************************************************************************************
     * Method Name: clearAppData
     * 
     * Description: Clears a specified App's Data
     * 
     ***********************************************************************************************************/

    public void clearAppData(String packageName) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        System.out.println("--- Clearing " + packageName + " data in the device");
        String executeCommand = TerminalHelper.executeCommand(
                androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell pm clear " + packageName);
        // System.out.println("executeCommand: " + executeCommand);

    }
    /***********************************************************************************************************
     * Method Name: detectTypeofWindow
     * 
     * Description: overload for reverse-compatibility
     * 
     ***********************************************************************************************************/
    public WindowVO detectTypeofWindow(int widthScreen, int heightScreen, String name) {
        return detectTypeofWindow(widthScreen, heightScreen, name, null);
    }

    /***********************************************************************************************************
     * Method Name: detectTypeofWindow
     * 
     * Description: return type of currently active window
     * 
     ***********************************************************************************************************/

    public WindowVO detectTypeofWindow(int widthScreen, int heightScreen, String name, UiAutomatorBridge bridge) {
        String appTransitionState = null;
        do {
            System.out.println("-App State not Idle, waiting...");
            appTransitionState = getAppTransitionState();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Catch Thread Interrupted Exception
                e.printStackTrace();
            }

        } while (!appTransitionState.equals("APP_STATE_IDLE"));

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        WindowVO vo = new WindowVO();
        // System.out.println(activity);
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand
                + " shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp' | awk '{print $3 }'";

        String result = TerminalHelper.executeCommand(command).split("\n")[0];
        String window = "";
        String activity = "";
        if(result.length() > 0){
            result = result.substring(0, result.length() - 1);
            activity = getCurrentActivityImproved();
            if (result.contains(MENU_BOTTOM_AND_CONTEXTUAL)) {
                // It's contextual menu or it's in the bottom
                window += "MENU:";
            } else if (result.contains(POPUP)) {
                // It's option menu in the top, popup windows from spinners and
                // other android components
                window += "POPUP:";
            }
        }

        boolean isAlert = false;
        // It's a normal activity or an AlertDialog

        DynGuiComponentVO root;
        if (bridge == null) {
            root = UiAutoConnector.getScreenInfoHierarchyGeneric(androidSDKPath, new StringBuilder(), widthScreen,
                    heightScreen, false, devicePort, adbPort, name, DEVICE_TYPE, UiAutoConnector.GENERIC_STRATEGY)
                    .getChildren().get(0);
        }
        else {
            bridge.updateTree();
            root = bridge.getScreenInfoHierarchy(androidSDKPath, new StringBuilder(), widthScreen, heightScreen).getChildren().get(0);
        }
        DynGuiComponentVO title = UiAutoConnector.getComponentByIdAndType("id/title", "TextView", root);
        if (title != null && title.getParent() != null && title.getParent().getName().endsWith("LinearLayout")
                && title.getParent().getIdXml().isEmpty()) {
            isAlert = true;
        } else {
            title = UiAutoConnector.getComponentByIdAndType("id/alertTitle", "TextView", root);
            if (title != null) {
                isAlert = true;
            }
        }

        if (isAlert) {
            window += "ALERT:";
        } else {
            title = UiAutoConnector.getComponentByIdAndType("id/action_bar_title", "TextView", root);
            if (title == null) {
                title = UiAutoConnector.getComponentByIdAndType("id/title", "TextView", root);
            }
        }

        String fragment = getCurrentFragment(activity);
        if (!fragment.isEmpty()) {
            window += "FRAGMENT:" + fragment;
        } else {
            window += "ACTIVITY:" + activity;
        }
        vo.setWindow(window);
        if (!window.contains("MENU:") && title != null) {
            vo.setTitle(title.getText());
        }
        return vo;
    }

    public static Object cloneObject(Object obj) {
        try {
            return ClonerHelper.deepClone(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***********************************************************************************************************
     * Method Name: getCurrentFragment
     * 
     * Description: Gets the current Fragment from a device
     * 
     ***********************************************************************************************************/

    public String getCurrentFragment(String currentActivity) {

        String result = "";
        if (!currentActivity.isEmpty()) {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            // System.out.println(activity);
            String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell dumpsys activity "
                    + currentActivity;
            int max = -1;

            String[] executeCommand = TerminalHelper.executeCommand(command).split("\n");

            for (String line : executeCommand) {
                if (line.startsWith("      #") && !line.contains("null") && line.contains("{")) {
                    String temp = line.trim();
                    String key = temp.substring(temp.indexOf("#") + 1, temp.indexOf(":"));
                    String fragment = temp.substring(temp.indexOf(":"), temp.indexOf("{")).trim();
                    int value = Integer.valueOf(key);
                    if (max < value) {
                        max = value;
                        // result = key + "#" + fragment;
                        result = fragment.replace(": ", "").trim();
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * This methods returns the package name of the app that is currently
     * running
     * 
     * @return package name of an application
     */
    public String getPackageCurrentRunningApp() {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell dumpsys activity";
        String[] executeCommand = TerminalHelper.executeCommand(command).split("\n");

        String line = null;
        for (int i = 0; i < executeCommand.length; i++) {
            line = executeCommand[executeCommand.length - 1 - i];
            if (line.contains("mFocusedApp")) {
                int begin = line.lastIndexOf('{');
                int end = line.indexOf('}');
                String[] split = line.substring(begin, end).split(" ");
                // clean result : package/activity
                return split[2].substring(0, split[2].indexOf('/'));
            }
        }

        return null;
    }

    /***********************************************************************************************************
     * Method Name: setDeviceDateAndTime
     * 
     * Description: Sets the current date of the device
     * 
     ***********************************************************************************************************/

    public void setDeviceDateAndTime(String time) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        // System.out.println(activity);
        String command = androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell 'su 0 date -s " + time
                + "' ";
        String executeCommand = TerminalHelper.executeCommand(command);
        System.out.println("executeCommand: " + executeCommand);

    }

    /***********************************************************************************************************
     * Method Name: forceRemoveAppData
     * 
     * Description: Sets the current date of the device
     * 
     ***********************************************************************************************************/

    public void forceRemoveAppData(String packageName) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String[] commands = { "su", "rm -rf /data/data/" + packageName + "/", "exit" };
        TerminalHelper.runAsRoot(commands, androidToolsPath + File.separator + "adb -P " + deviceCommand + " shell", true);
    }

    /**
     * @return the adbPort
     */
    public String getAdbPort() {
        return adbPort;
    }

    /**
     * @param adbPort
     *            the adbPort to set
     */
    public void setAdbPort(String adbPort) {
        this.adbPort = adbPort;
    }

    /**
     * @return the devicePort
     */
    public String getDevicePort() {
        return devicePort;
    }

    /**
     * @param devicePort
     *            the devicePort to set
     */
    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    /**
     * @return the dEVICE_TYPE
     */
    public UiAutoConnector.TypeDeviceEnum getDEVICE_TYPE() {
        return DEVICE_TYPE;
    }

    /**
     * @param dEVICE_TYPE
     *            the dEVICE_TYPE to set
     */
    public void setDEVICE_TYPE(UiAutoConnector.TypeDeviceEnum dEVICE_TYPE) {
        DEVICE_TYPE = dEVICE_TYPE;
    }

    /**
     * @return the androidSDKPath
     */
    public String getAndroidSDKPath() {
        return androidSDKPath;
    }

    /**
     * @param androidSDKPath
     *            the androidSDKPath to set
     */
    public void setAndroidSDKPath(String androidSDKPath) {
        this.androidSDKPath = androidSDKPath;
    }

    /**
     * @return the deviceCommand
     */
    public String getDeviceCommand() {
        return deviceCommand;
    }

    /**
     * @param deviceCommand
     *            the deviceCommand to set
     */
    public void setDeviceCommand(String deviceCommand) {
        this.deviceCommand = deviceCommand;
    }

}


