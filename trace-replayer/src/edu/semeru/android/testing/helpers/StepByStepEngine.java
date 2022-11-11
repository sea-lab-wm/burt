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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.TerminalHelper;

/**
 * 
 * Class for handling steps execution using adb from Android SDK
 *
 * @author Mario Linares
 * @author Carlos Bernal
 * @since Aug 7, 2014
 */
public class StepByStepEngine {

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
    public final static int TYPE_RANDOM = 10;
    public final static int CRASH = 100;
    private static final int KEYEVENT = 19;
    private static final int NONE = 111;
    public static final int MENU_BTN = 11;

    public static final int END_APP = 50;
    
    // check ->
    // http://stackoverflow.com/questions/7789826/adb-shell-input-events?answertab=votes#tab-top
    private final static String KEYCODE_MENU = "1";
    private final static String KEYCODE_HOME = "3";
    public final static String KEYCODE_BACK = "4";

    public static void startAPK(String androidSDKPAth, String packageName, String mainActivity) {
        startAPK(androidSDKPAth, packageName, mainActivity, null);
    }

    public static void startAPK(String androidSDKPAth, String packageName, String mainActivity, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        try {
            String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";
            System.out.println("-- Cleaning logcat before starting APK");
            System.out.println(TerminalHelper
                    .executeCommand(androidToolsPath + File.separator + "adb " + device + "shell logcat -c"));
            System.out.println("-- Starting " + packageName + " on the device");
            TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb " + device + "shell am start -n "
                    + packageName + "/" + mainActivity);
            Thread.sleep(3000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void startInstumentedAPK(String androidSDKPAth, String packageName, String mainActivity,
            String coverageFile) {
        try {
            // System.out.println("-- Cleaning logcat before starting APK");
            String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Starting " + packageName + " on the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell logcat -c").waitFor();
            String command = androidToolsPath + File.separator + "adb shell am instrument -e coverageFile "
                    + coverageFile + " " + packageName + "/instrumentation.EmmaInstrumentation";
            // System.out.println(command);
            rt.exec(command).waitFor();

            Thread.sleep(2000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void stopInstumentedAPK(String androidSDKPAth, String packageName) {
        stopInstumentedAPK(androidSDKPAth, packageName, null);
    }

    public static void stopInstumentedAPK(String androidSDKPAth, String packageName, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        try {
            String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Stopping " + packageName + " on the device");
            String command = androidToolsPath + File.separator + "adb " + device
                    + "shell am broadcast -a com.instrumentation.STOP";
            // System.out.println(command);
            rt.exec(command).waitFor();

            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void stopAPK(String androidSDKPAth, String packageName) {
        stopAPK(androidSDKPAth, packageName, null);
    }

    public static void stopAPK(String androidSDKPAth, String packageName, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        try {
            String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Stopping " + packageName + " on the device");
            rt.exec(androidToolsPath + File.separator + "adb " + device + "shell am force-stop " + packageName)
                    .waitFor();

            Thread.sleep(2000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String getLabelFromEvent(int typeId) {
        String result = null;
        switch (typeId) {
        case CLICK:
            result = "CLICK";
            break;
        case LONG_CLICK:
            result = "LONG_CLICK";
            break;
        case BACK:
            result = "BACK";
            break;
        case ROTATION:
            result = "ROTATION";
            break;
        case TYPE:
        case CLICK_TYPE:
            result = "TYPE";
            break;
        case SWIPE:
        case SWIPE_UP:
        case SWIPE_RIGHT:
        case SWIPE_DOWN:
        case SWIPE_LEFT:
            result = "SWIPE";
            break;

        default:
            System.out.println(typeId);
            break;
        }
        return result;
    }

    private static GUIEventVO getGUIEventVOFromRawEvent(String rawEvent) {
        if (!rawEvent.startsWith("BACK")) {
            String[] data = rawEvent.split("#");
            int eventTypeId = Integer.parseInt(data[0]);
            String eventLabel = data[1];
            double duration = Double.parseDouble(data[3]);
            String[] initPosition = data[4].replace("(", "").replace(")", "").split(",");
            String[] finalPosition = data[5].replace("(", "").replace(")", "").split(",");

            String direction = getSwipeText(eventTypeId, Integer.parseInt(initPosition[0].trim()),
                    Integer.parseInt(initPosition[1].trim()), Integer.parseInt(finalPosition[0].trim()),
                    Integer.parseInt(finalPosition[1].trim()));

            return new GUIEventVO(duration, Integer.parseInt(initPosition[0].trim()),
                    Integer.parseInt(initPosition[1].trim()), Integer.parseInt(finalPosition[0].trim()),
                    Integer.parseInt(finalPosition[1].trim()), eventTypeId, eventLabel, direction);
        } else {
            return new GUIEventVO(BACK);
        }

    }

    /**
     * @param eventTypeId
     * @param initPosition
     * @param finalPosition
     * @param direction
     * @return
     */
    private static String getSwipeText(int eventTypeId, int initPositionX, int initPositionY, int finalPositionX,
            int finalPositionY) {
        String direction = "";
        if (eventTypeId == SWIPE) {
            int diffX = initPositionX - finalPositionX;
            int diffY = initPositionY - finalPositionY;

            String vertical = "UP";
            String horizontal = "LEFT";

            if (diffX > 0) {
                vertical = "DOWN";
            }
            if (diffY < 0) {
                horizontal = "RIGHT";
            }

            if (Math.abs(diffX) > Math.abs(diffY)) {
                direction = horizontal + "-" + vertical;
            } else {
                direction = vertical + "-" + horizontal;
            }
        }
        return direction;
    }

    public static ArrayList<GUIEventVO> getStepByStepStatementsFromEventLog(String eventLogPath,
            String pythonScriptsPath) {
        ArrayList<GUIEventVO> statements = new ArrayList<GUIEventVO>();

        try {
            Runtime rt = Runtime.getRuntime();
            String command = "python3 " + pythonScriptsPath + File.separator + "partition_events.py " + eventLogPath;
            System.out.println(command);
            Process proc = rt.exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                statements.add(getGUIEventVOFromRawEvent(s));
            }

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s2 = null;
            while ((s2 = stdError.readLine()) != null) {
                System.out.println(s2);
            }
            proc.waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        return statements;
    }

    public static String executeEvent(GUIEventVO vo, String androidRoot, String packageName, String executionType,
            boolean hideKeyboard) {
        return executeEvent(vo, androidRoot, packageName, executionType, hideKeyboard, null);
    }

    public static String executeEvent(GUIEventVO vo, String androidRoot, String packageName, String executionType) {
        return executeEvent(vo, androidRoot, packageName, executionType, true, null);
    }

    public static String executeEvent(GUIEventVO vo, String androidRoot, String packageName, String executionType,
            String device) {
        return executeEvent(vo, androidRoot, packageName, executionType, true, device);
    }

    public static String executeEvent(GUIEventVO vo, String androidRoot, String packageName, String executionType,
            boolean hideKeyboard, String device) {
    	if (Utilities.getAndroidVersion(androidRoot).equals("10")) {
        	String obscuringWindow = null;
        	do {
                System.out.println("-App State not Idle, waiting...");
                obscuringWindow = StepByStepEngine.getObscuringWindow(androidRoot);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Catch Thread Interrupted Exception
                    e.printStackTrace();
                }
            } while (obscuringWindow.equals("null"));
        } else {
	        String appTransitionState = null;
	        do {
	            System.out.println("-App State not Idle, waiting...");
	            appTransitionState = getAppTransitionState(androidRoot);
	            try {
	                Thread.sleep(500);
	            } catch (InterruptedException e) {
	                // Catch Thread Interrupted Exception
	                e.printStackTrace();
	            }
	
	        } while (!appTransitionState.equals("APP_STATE_IDLE"));
        }
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String command = null;
        switch (vo.getEventTypeId()) {
        case CLICK:
            command = "adb " + device + "shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY();
            break;
        case LONG_CLICK:
            command = "adb " + device + "shell input touchscreen swipe  " + vo.getRealInitialX() + " "
                    + vo.getRealInitialY() + " " + vo.getRealInitialX() + " " + vo.getRealInitialY() + " 2000";
            break;

        case SWIPE:
            command = "adb " + device + "shell input touchscreen swipe  " + vo.getRealInitialX() + " "
                    + vo.getRealInitialY() + " " + vo.getRealFinalX() + " " + vo.getRealFinalY() + " "
                    + (int) (vo.getDuration() * 1000);
            break;

        case CLICK_TYPE:
            command = "adb " + device + "shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY();
            break;

        case TYPE:
            command = "adb " + device + "shell input text " + vo.getText();
            break;

        case BACK:
            command = "adb " + device + "shell input keyevent " + KEYCODE_BACK;
            break;

        case TYPE_RANDOM:
            int inputType = InputHelper.checkInputType(androidRoot, device);
            String input = InputHelper.generateInput(inputType, executionType);
            vo.getHvInfoComponent().setText(input);
            vo.setText(input);
            command = "adb " + device + "shell input text " + vo.getText();
            break;

        }
        if (command != null) {
            try {
                String androidToolsPath = androidRoot + File.separator + "platform-tools";
                Runtime rt = Runtime.getRuntime();
                System.out.println("--- Executing GUI event " + command);
                rt.exec(androidToolsPath + File.separator + command).waitFor();
                if (vo != null && vo.getEventTypeId() == CLICK_TYPE) {
                    if (Utilities.isKeyboardActive(androidRoot) && hideKeyboard) {
                        Utilities.disposeKeyboard(androidRoot);
                    }
                    // System.out.println("Generating Input ;)");
                    int inputType = InputHelper.checkInputType(androidRoot, device);
                    String input = InputHelper.generateInput(inputType, executionType);
                    String back = "67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67"
                            + " 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67 67";
                    command = command + "\n";
                    command += doKey(androidRoot, back, packageName) + "\n";
                    command += doTypeCrashScope(androidRoot, input, packageName, executionType) + "\n";
                    System.out.println("Input: " + input);
                    return input;
                }

            } catch (Exception ex) {
                Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return command;
    }
    
    /***********************************************************************************************************
     * Method Name: getObscuringWindow
     * 
     * Description: This method gets the obscuring window.
     * TODO: Add possible obscuring window Strings returned
     * 
     ***********************************************************************************************************/

    public static String getObscuringWindow(String androidSDKPath) {

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("-Getting current Window Transition State...");
        // System.out.println(androidToolsPath + File.separator + "adb -P " +
        // adbPort + " -s " + avdAddress + " shell dumpsys window -a | grep
        // 'mAppTransitionState'");
        String terminalCommand = TerminalHelper.executeCommand(androidToolsPath + File.separator + "adb shell dumpsys window -a | grep 'mObscuringWindow'");

        terminalCommand = terminalCommand.substring(terminalCommand.indexOf("mObscuringWindow=") + 20,
                terminalCommand.length());

        // System.out.println(terminalCommand);

        return terminalCommand;

    }

    public static String getAppTransitionState(String androidSDKPath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        System.out.println("- Getting current Window Transition State...");
        String terminalCommand = TerminalHelper.executeCommand(
                androidToolsPath + File.separator + "adb shell dumpsys window -a | grep 'mAppTransitionState'");
        terminalCommand = terminalCommand.substring(terminalCommand.indexOf("mAppTransitionState=") + 20,
                terminalCommand.length());

        return terminalCommand;
    }

    public static void executeInputCommand(String command, String sdkPath) {
        Runtime rt = Runtime.getRuntime();
        String androidToolsPath = sdkPath + File.separator + "platform-tools";
        System.out.println("--- Executing GUI event " + command);
        try {
            rt.exec(androidToolsPath + File.separator + command).waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void clearAppData(String androidRoot, String packageName) {
        clearAppData(androidRoot, packageName, null);
    }

    public static void clearAppData(String androidRoot, String packageName, String device) {
        try {
            if (device == null || (device != null && device.isEmpty())) {
                device = "";
            } else {
                device = "-s " + device + " ";
            }
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            System.out.println("--- Clearing " + packageName + " data in the device");
            String executeCommand = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb " + device + "shell pm clear " + packageName);
            // System.out.println("executeCommand: " + executeCommand);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void installApp(String androidRoot, String apkPath, String packageName) {
        try {
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            rt.exec(androidToolsPath + File.separator + "adb install " + apkPath).waitFor();
            String executeCommand = TerminalHelper
                    .executeCommand(androidToolsPath + File.separator + "adb install " + apkPath);
            System.out.println("executeCommand: " + executeCommand);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void unInstallAndInstallApp(String androidRoot, String apkPath, String packageName) {
        unInstallAndInstallApp(androidRoot, apkPath, packageName, null);
    }

    public static void unInstallAndInstallApp(String androidRoot, String apkPath, String packageName, String device) {
        try {
            if (device == null || (device != null && device.isEmpty())) {
                device = "";
            } else {
                device = "-s " + device + " ";
            }
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Removing " + packageName + " from the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell pm uninstall " + packageName).waitFor();
            String executeCommand = TerminalHelper.executeCommand(
                    androidToolsPath + File.separator + "adb " + device + "shell pm uninstall " + packageName);
            // System.out.println("executeCommand: " + executeCommand);
            System.out.println("--- Installing " + packageName + " apk (" + new File(apkPath).getName() + ")");
            // rt.exec(androidToolsPath + File.separator + "adb install " +
            // apkPath).waitFor();
            executeCommand = TerminalHelper
                    .executeCommand(androidToolsPath + File.separator + "adb " + device + "install " + apkPath);
            // System.out.println("executeCommand: " + executeCommand);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void unInstallApp(String androidRoot, String apkPath, String packageName) {
        try {
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Removing " + packageName + " from the device");
            // rt.exec(androidToolsPath + File.separator +
            // "adb shell pm uninstall " + packageName).waitFor();
            String executeCommand = TerminalHelper
                    .executeCommand(androidToolsPath + File.separator + "adb shell pm uninstall " + packageName);
            System.out.println("executeCommand: " + executeCommand);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @deprecated
     * @param androidRoot
     * @param packageName
     */
    @Deprecated
    public static String doBack(String androidRoot, String packageName) {
        String command = "adb shell input keyevent " + KEYCODE_BACK;
        try {
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Executing GUI event" + command);
            rt.exec(androidToolsPath + File.separator + command).waitFor();
            // LogHelper.getInstance("inputs/" + packageName +
            // ".txt").addLine(command);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return command;
    }

    public static String doType(String androidRoot, String text, String packageName) {

        String command = "adb shell input text '" + text + "'";

        try {
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Executing GUI event " + command);
            rt.exec(androidToolsPath + File.separator + command).waitFor();
            // LogHelper.getInstance("inputs/" + packageName +
            // ".txt").addLine(command);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return command;

    }

    public static String doTypeCrashScope(String androidRoot, String text, String packageName, String executionType) {
        return doTypeCrashScope(androidRoot, text, packageName, executionType, null);
    }

    public static String doTypeCrashScope(String androidRoot, String text, String packageName, String executionType,
            String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String command = "adb " + device + "shell input text '" + text + "'";
        if (executionType.equals("no_text")) {
            System.out.println("--- Executing GUI event " + command);
            return command;
        } else {
            try {
                String androidToolsPath = androidRoot + File.separator + "platform-tools";
                Runtime rt = Runtime.getRuntime();
                System.out.println("--- Executing GUI event " + command);
                rt.exec(androidToolsPath + File.separator + command).waitFor();
                // LogHelper.getInstance("inputs/" + packageName +
                // ".txt").addLine(command);

            } catch (Exception ex) {
                Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            return command;
        }
    }

    public static String doKey(String androidRoot, String keyEvent, String packageName) {
        return doKey(androidRoot, keyEvent, packageName, null);
    }

    public static String doKey(String androidRoot, String keyEvent, String packageName, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String command = "adb " + device + "shell input keyevent '" + keyEvent + "'";
        // try {
        // String androidToolsPath = androidRoot + File.separator +
        // "platform-tools";
        // Runtime rt = Runtime.getRuntime();
        // System.out.println("--- Executing GUI event " + command);
        // rt.exec(androidToolsPath + File.separator + command).waitFor();
        // // LogHelper.getInstance("inputs/" + packageName +
        // // ".txt").addLine(command);
        //
        // } catch (Exception ex) {
        // Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE,
        // null, ex);
        // }
        String androidToolsPath = androidRoot + File.separator + "platform-tools";
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command);
        System.out.println("--- Executing GUI event " + command);
        return command;
    }

    /**
     * @param vo
     * @param click2
     */
    public static GUIEventVO getEventFromComponent(DynGuiComponentVO vo, int type) {
        GUIEventVO gui = new GUIEventVO();
        gui.setRealFinalX(vo.getPositionX() + vo.getWidth() / 2);
        gui.setRealFinalY(vo.getPositionY() + vo.getHeight() / 2);
        gui.setEventTypeId(type);
        gui.setHvInfoComponent(vo);
        if (type == LONG_CLICK) {
            gui.setRealInitialX(vo.getPositionX() + vo.getWidth() / 2);
            gui.setRealInitialY(vo.getPositionY() + vo.getHeight() / 2);
            gui.setRealFinalX(vo.getPositionX() + vo.getWidth() / 2);
            gui.setRealFinalY(vo.getPositionY() + vo.getHeight() / 2);
        } else if (type == TYPE || type == CLICK_TYPE) {
            gui.setText(vo.getText());
        }
        // if (vo.getName().contains("EditText")) {
        // gui.setEventTypeId(StepByStepEngine.CLICK_TYPE);
        // } else {
        gui.setEventTypeId(type);
        // }
        return gui;
    }

    /**
     * @param vo
     * @return
     */
    public static DynGuiComponentVO getVOFromEntity(DynGuiComponent entity) {
        return getVOFromEntity(entity, true);
    }
    
    public static DynGuiComponentVO getVOFromEntity(DynGuiComponent entity, boolean transformHierarchy) {
        if (entity != null) {
            DynGuiComponentVO vo = new DynGuiComponentVO();
            vo.setActivity(entity.getActivity());
            vo.setCalendarWindow(entity.isCalendarWindow());
            vo.setCheckable(entity.isCheckable());
            vo.setChecked(entity.isChecked());
            //
            if(transformHierarchy){
                for (DynGuiComponent item : entity.getChildren()) {
                    vo.getChildren().add(getVOFromEntity(item, transformHierarchy));
                }
            }

            vo.setClickable(entity.isClickable());
            vo.setComponentIndex(entity.getComponentIndex());
            vo.setComponentTotalIndex(entity.getComponentTotalIndex());
            vo.setContentDescription(entity.getContentDescription());
            vo.setCurrentWindow(entity.getCurrentWindow());
            vo.setDrawTime(entity.getDrawTime());
            vo.setEnabled(entity.isEnabled());
            vo.setFocusable(entity.isFocusable());
            vo.setFocused(entity.isFocused());
            vo.setGuiScreenshot(entity.getGuiScreenshot());
            vo.setHeight(entity.getHeight());
            vo.setId(entity.getId());
            vo.setIdText(entity.getIdText());
            vo.setIdXml(entity.getIdXml());
            vo.setItemList(entity.isItemList());
            vo.setLongClickable(entity.isLongClickable());
            vo.setName(entity.getName());
            vo.setOffset(entity.getOffset());
            //
            if(transformHierarchy){
                vo.setParent(getVOFromEntity(entity.getParent()));
            }
            vo.setPassword(entity.isPassword());
            vo.setPositionX(entity.getPositionX());
            vo.setPositionY(entity.getPositionY());
            vo.setProperties(entity.getProperties());
            vo.setRelativeLocation(entity.getRelativeLocation());
            vo.setScrollable(entity.isScrollable());
            vo.setSelected(entity.isSelected());
            //
            vo.setText(entity.getText());
            vo.setTitleWindow(entity.getTitleWindow());
            vo.setVisibility(entity.getVisibility());
            vo.setWidth(entity.getWidth());

            return vo;
        }
        return null;
    }

    public static Step getStepFromEvent(GUIEventVO event) {
        Step step = new Step();
        if(event.getEventTypeId()!=StepByStepEngine.SWIPE) {
	        step.setDynGuiComponent(getEntityFromVO(event.getHvInfoComponent()));
	        step.getDynGuiComponent().setCurrentWindow(event.getActivity());
        }
        // Set the appropriate action
        if (event.getEventTypeId() == StepByStepEngine.CLICK
                || (event.getEventLabel() != null && event.getEventLabel().equals("CLICK"))) {
            step.setAction(StepByStepEngine.CLICK);
        } else if (event.getEventTypeId() == StepByStepEngine.LONG_CLICK
                || (event.getEventLabel() != null && event.getEventLabel().equals("LONG_CLICK"))) {
            step.setAction(StepByStepEngine.LONG_CLICK);
        } else if (event.getEventTypeId() == StepByStepEngine.SWIPE
                || (event.getEventLabel() != null && event.getEventLabel().contains("SWIPE"))) {
            step.setAction(StepByStepEngine.SWIPE);
            step.setTextEntry(event.getDirection());
        } else if (event.getEventTypeId() == StepByStepEngine.TYPE
                || (event.getEventLabel() != null && event.getEventLabel().equals("TYPE"))) {
            step.setAction(StepByStepEngine.TYPE);
            step.setTextEntry("");
        } else if (event.getEventTypeId() == StepByStepEngine.CLICK_TYPE
                || (event.getEventLabel() != null && event.getEventLabel().equals("CLICK_TYPE"))) {
            step.setAction(StepByStepEngine.CLICK_TYPE);
        }

        return step;
    }

    public static DynGuiComponent getEntityFromVO(DynGuiComponentVO vo) {
        return getEntityFromVO(vo, true);
    }
    
    public static DynGuiComponent getEntityFromVO(DynGuiComponentVO vo, boolean transformHierarchy) {
        if (vo != null) {
            DynGuiComponent entity = new DynGuiComponent();
            entity.setActivity(vo.getActivity());
            entity.setCalendarWindow(vo.isCalendarWindow());
            entity.setCheckable(vo.isCheckable());
            entity.setChecked(vo.isChecked());
            //
            if(transformHierarchy){
                for (DynGuiComponentVO item : vo.getChildren()) {
                    DynGuiComponent temp = getEntityFromVO(item);
                    temp.setParent(entity);
                    entity.getChildren().add(temp);
                }
            }

            entity.setClickable(vo.isClickable());
            entity.setComponentIndex(vo.getComponentIndex());
            entity.setComponentTotalIndex(vo.getComponentTotalIndex());
            entity.setContentDescription(vo.getContentDescription());
            entity.setCurrentWindow(vo.getCurrentWindow());
            entity.setDrawTime(vo.getDrawTime());
            entity.setEnabled(vo.isEnabled());
            entity.setFocusable(vo.isFocusable());
            entity.setFocused(vo.isFocused());
            entity.setGuiScreenshot(vo.getGuiScreenshot());
            entity.setHeight(vo.getHeight());
            entity.setId(vo.getId());
            entity.setIdText(vo.getIdText());
            entity.setIdXml(vo.getIdXml());
            entity.setItemList(vo.isItemList());
            entity.setLongClickable(vo.isLongClickable());
            entity.setName(vo.getName());
            entity.setOffset(vo.getOffset());
            
            entity.setPassword(vo.isPassword());
            entity.setPositionX(vo.getPositionX());
            entity.setPositionY(vo.getPositionY());
            entity.setProperties(vo.getProperties());
            entity.setRelativeLocation(vo.getRelativeLocation());
            entity.setScrollable(vo.isScrollable());
            entity.setSelected(vo.isSelected());
            //
            entity.setText(vo.getText());
            entity.setTitleWindow(vo.getTitleWindow());
            entity.setVisibility(vo.getVisibility());
            entity.setWidth(vo.getWidth());

            return entity;
        }
        return null;
    }

    /**
     * @param screenVO
     * @return
     */
    public static List<DynGuiComponent> getEntityFromVO(List<DynGuiComponentVO> screenVO) {
        HashMap<Integer, DynGuiComponentVO> mapVO = new HashMap<>();
        HashMap<Integer, DynGuiComponent> mapEntity = new HashMap<>();
        List<DynGuiComponent> screenEntity = new ArrayList<DynGuiComponent>();
        
        for (DynGuiComponentVO component : screenVO) {
            mapVO.put(component.getSequenceHierarchy(), component);
            DynGuiComponent entity = getEntityFromVO(component, false);
            mapEntity.put(component.getSequenceHierarchy(), entity);
            screenEntity.add(entity);
        }
        
        for (DynGuiComponentVO component : screenVO) {
            if (component.getParent() != null) {
                DynGuiComponent child = mapEntity.get(component.getSequenceHierarchy());
                DynGuiComponent parent = mapEntity.get(component.getParent().getSequenceHierarchy());
                child.setParent(parent);
            }
        }
        
        return screenEntity;
    }

    /**
     * @param events
     * @param execution
     * @return
     * @throws Exception
     */
    public static List<Step> getStepsFromEvents(List<GUIEventVO> events, Execution execution) throws Exception {
        ArrayList<Step> list = new ArrayList<Step>();
        int sequence = 1;
        for (GUIEventVO event : events) {
            Step step = getStepFromEvent((GUIEventVO) ClonerHelper.deepClone(event));
            step.setSequenceStep(sequence++);
            step.setAction(event.getEventTypeId());
            step.setExecution(execution);
            step.setAreaEdit(event.getAreaEdit());
            step.setAreaList(event.getAreaList());
            step.setAreaView(event.getAreaView());
            step.setAreaSelect(event.getAreaSelect());
            // Set screenshot per step
            step.setScreenshot(event.getHvInfoComponent().getGuiScreenshot());
            step.getDynGuiComponent().setGuiScreenshot("");
            if (event.getEventTypeId() == StepByStepEngine.TYPE || event.getEventTypeId() == StepByStepEngine.CLICK_TYPE) {
                step.getDynGuiComponent().setText(event.getText());
                step.setTextEntry(event.getText());
            }
            list.add(step);
        }
        return list;
    }

    public static String getAction(Step step) {
        String label = "";
        switch (step.getAction()) {
        case CLICK:
            label = "Click on ";
            break;
        case LONG_CLICK:
            label = "Long click on ";
            break;
        case SWIPE:
            String temp = step.getTextEntry() != null ? step.getTextEntry().toLowerCase() : "";
            if (temp.contains("-")) {
                temp = temp.substring(0, temp.indexOf("-"));
            }
            label = "Swipe " + temp + " on ";
            break;
        case CLICK_TYPE:
            label = "Type ";
            break;
        case TYPE:
            label = "Type ";
            break;
        default:
            break;
        }
        return label;
    }

    public static void disableNetwork(String androidRoot) {
        try {
            String androidToolsPath = androidRoot + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Stopping Network on Emulator");
            String command = "echo 'gsm data unregistered' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);
            rt.exec(androidToolsPath + File.separator + "adb kill-server").waitFor();
            rt.exec(androidToolsPath + File.separator + "adb start-server").waitFor();
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void enableNetwork() {
        try {

            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Starting Network on Emulator");
            String command = "echo 'gsm data home' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);

            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void dupeGPS() {
        try {

            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Setting bogus GPS Coordinates");
            String command = "echo 'geo fix 1000 1000' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void correctGPS() {
        try {

            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Setting correct GPS Coordinates");
            String command = "echo 'geo fix 28.4158 81.2989' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 
     * @param sensor
     *            Possible Arguments: "accelerometer" "temperature"
     *            "magnetic-field"
     */
    public static void dupeSensor(String sensor) {
        try {

            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Setting correct GPS Coordinates");
            String command = "echo 'sensor set " + sensor + " 99999999' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void correctSensor(String sensor) {
        try {

            Runtime rt = Runtime.getRuntime();
            System.out.println("-- Setting correct " + sensor + " Sensor Value");
            String command = "echo 'sensor set " + sensor + " 0' | nc localhost 5554";
            String output = TerminalHelper.executeCommand(command);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void rotateScreen(String androidSDKPath) {
        System.out.println("--Rotating Screen");
        String rotateLandscape = androidSDKPath
                + "/platform-tools/adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1";
        String output = TerminalHelper.executeCommand(rotateLandscape);
        System.out.println(output);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String rotatePortrait = androidSDKPath
                + "/platform-tools/adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:0";
        String output2 = TerminalHelper.executeCommand(rotatePortrait);
        System.out.println(output2);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void getCoverageFile(String androidSDKPAth, String emmaFilePath, String emmaExtractionPath,
            String appPackage, String executionType) {

        System.out.println("-- Getting Coverage Files from device");
        String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";

        String commandMk = "mkdir " + emmaExtractionPath + File.separator + "coverageFiles" + File.separator
                + appPackage + "_" + executionType;

        TerminalHelper.executeCommand(commandMk);

        String command = androidToolsPath + File.separator + "adb pull " + emmaFilePath + " " + emmaExtractionPath
                + File.separator + "coverageFiles" + File.separator + appPackage + "_" + executionType;
        TerminalHelper.executeCommand(command);

    }

    public static void getAndrotestCoverageFile(String androidSDKPAth, String emmaFilePath, String emmaExtractionPath,
            String appPackage, String executionType, int executionCtr) {

        System.out.println("-- Getting Coverage Files from device");
        String androidToolsPath = androidSDKPAth + File.separator + "platform-tools";

        // String commandMk = "mkdir " + emmaExtractionPath + File.separator +
        // "coverageFiles" + File.separator + appPackage + "_" + executionType +
        // "_" + executionCtr;

        // TerminalHelper.executeCommand(commandMk);

        String command = androidToolsPath + File.separator
                + "adb shell am broadcast -a edu.gatech.m3.emma.COLLECT_COVERAGE";
        TerminalHelper.executeCommand(command);

        String command1 = androidToolsPath + File.separator + "adb pull " + emmaFilePath + " " + emmaExtractionPath
                + File.separator + "coverageFiles" + File.separator + appPackage + "_" + executionType + "_"
                + executionCtr + ".ec";
        TerminalHelper.executeCommand(command1);

    }

    public static GUIEventVO getTypeByAdb(String command) {
        GUIEventVO vo = new GUIEventVO();
        command = command.replaceAll("\\s+", " ");
        if (command.contains("tap")) {
            String[] split = command.split(" ");
            String x1 = split[4];
            String y1 = split[5];
            vo.setEventTypeId(CLICK);
            vo.setRealInitialX(Integer.parseInt(x1));
            vo.setRealInitialY(Integer.parseInt(y1));
            vo.setRealFinalX(Integer.parseInt(x1));
            vo.setRealFinalY(Integer.parseInt(y1));
        } else if (command.contains("swipe")) {
            // adb shell input touchscreen swipe
            String[] split = command.split(" ");
            String x1 = split[5];
            String y1 = split[6];
            String x2 = split[7];
            String y2 = split[8];
            if (x1.equals(x2) && y1.equals(y2)) {
                vo.setEventTypeId(LONG_CLICK);
            } else {
                vo.setEventTypeId(SWIPE);
            }
            vo.setRealInitialX(Integer.parseInt(x1));
            vo.setRealInitialY(Integer.parseInt(y1));
            vo.setRealFinalX(Integer.parseInt(x2));
            vo.setRealFinalY(Integer.parseInt(y2));
        } else if (command.contains("input text")) {
            String[] split = command.split(" ");
            String text = split[4];
            vo.setEventTypeId(TYPE);
            vo.setText(text);
        } else if (command.contains("input keyevent")) {
            String[] split = command.split(" ");
            String text = split[4];
            vo.setEventTypeId(KEYEVENT);
            vo.setText(text);
        } else if (command.contains("OPEN_APP")) {
            vo.setEventTypeId(OPEN_APP);
        }
        vo.setEventLabel(getLabelFromEvent(vo.getEventTypeId()));
        return vo;
    }

    /**
     * @param step
     */
    public static String getAdbByStep(Step step) {
        String command = null;
        // System.out.println(getLabelFromEvent(step.getAction()) + ":" +
        // step.getAction() + " : "
        // + step.getDynGuiComponent());
        DynGuiComponent component = step.getDynGuiComponent() == null ? new DynGuiComponent()
                : step.getDynGuiComponent();
        GUIEventVO vo = getEventFromComponent(getVOFromEntity(component, false), step.getAction());
        // -------
        switch (step.getAction()) {
        case CLICK:
            command = "adb shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY();
            break;
        case LONG_CLICK:
            command = "adb shell input touchscreen swipe  " + vo.getRealInitialX() + " " + vo.getRealInitialY() + " "
                    + vo.getRealInitialX() + " " + vo.getRealInitialY() + " 2000";
            break;

        case SWIPE:
            command = "adb shell input touchscreen swipe  " + vo.getRealInitialX() + " " + vo.getRealInitialY() + " "
                    + vo.getRealFinalX() + " " + vo.getRealFinalY() + " " + (int) (vo.getDuration() * 1000);
            break;

        case CLICK_TYPE:
            command = "adb shell input tap " + vo.getRealFinalX() + " " + vo.getRealFinalY() + "\n";
            command += "adb shell input text '" + (step.getTextEntry() == null ? "" : step.getTextEntry()) + "'";
            break;

        case TYPE:
            command = "adb shell input text '" + (step.getTextEntry() == null ? "" : step.getTextEntry()) + "'";
            break;

        case BACK:
            command = "adb shell input keyevent " + KEYCODE_BACK;
            break;

        case CRASH:
            command = "CRASH";
            break;

        case ROTATION:
            command = "ROTATION";
            break;
        }

        return command;
    }

    public static String getAdbBySteps(List<Step> steps) {
        String command = "";
        Step[] array = new Step[steps.size()];
        steps.toArray(array);
        Arrays.sort(array);
        String accelerometer = "";
        String gps = "";
        String magnetometer = "";
        String network = "";
        String temperature = "";

        for (Step step : array) {
            // Accelerometer
            if ((accelerometer.isEmpty() && step.isAcellerometer())
                    || (accelerometer.equals("OFF") && step.isAcellerometer())) {
                command += "ACCELEROMETER:ON\n";
                accelerometer = "ON";
            } else if ((accelerometer.isEmpty() && !step.isAcellerometer())
                    || (accelerometer.equals("ON") && !step.isAcellerometer())) {
                command += "ACCELEROMETER:OFF\n";
                accelerometer = "OFF";
            }
            // Gps
            if ((gps.isEmpty() && step.getGps()) || (gps.equals("OFF") && step.getGps())) {
                command += "GPS:ON\n";
                gps = "ON";
            } else if ((gps.isEmpty() && !step.getGps()) || (gps.equals("ON") && !step.getGps())) {
                command += "GPS:OFF\n";
                gps = "OFF";
            }
            // Magnetometer
            if ((magnetometer.isEmpty() && step.isMagentometer())
                    || (magnetometer.equals("OFF") && step.isMagentometer())) {
                command += "MAGNETOMETER:ON\n";
                magnetometer = "ON";
            } else if ((magnetometer.isEmpty() && !step.isMagentometer())
                    || (magnetometer.equals("ON") && !step.isMagentometer())) {
                command += "MAGNETOMETER:OFF\n";
                magnetometer = "OFF";
            }
            // Network
            if ((network.isEmpty() && step.getNetwork()) || (network.equals("OFF") && step.getNetwork())) {
                command += "NETWORK:ON\n";
                network = "ON";
            } else if ((network.isEmpty() && !step.getNetwork()) || (network.equals("ON") && !step.getNetwork())) {
                command += "NETWORK:OFF\n";
                network = "OFF";
            }
            // Temperature
            if ((temperature.isEmpty() && step.isTemperature())
                    || (temperature.equals("OFF") && step.isTemperature())) {
                command += "TEMPERATURE:ON\n";
                temperature = "ON";
            } else if ((temperature.isEmpty() && !step.isTemperature())
                    || (temperature.equals("ON") && !step.isTemperature())) {
                command += "TEMPERATURE:OFF\n";
                temperature = "OFF";
            }
            // -----------------------
            // System.out.println(step);
            command += StepByStepEngine.getAdbByStep(step) + "\n";
        }
        return command;
    }

}
