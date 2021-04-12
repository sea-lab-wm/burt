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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import se.vidstige.jadb.*;

import com.android.hierarchyviewerlib.ui.util.DrawableViewNode;
import com.android.uiautomator.tree.AttributePair;
import com.android.uiautomator.tree.BasicTreeNode;
import com.google.gson.Gson;

import edu.semeru.android.core.entity.model.Coords;
import edu.semeru.android.core.entity.model.Event;
import edu.semeru.android.testing.helpers.Constants;
import edu.semeru.android.testing.helpers.DataExtractor;
import edu.semeru.android.testing.helpers.UiAutoConnector;
import edu.semeru.android.testing.helpers.UiAutoConnector.TypeDeviceEnum;
import edu.semeru.android.core.model.CoverageValuesVO;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.Transition;
import edu.semeru.android.core.model.WindowVO;
import edu.wm.cs.semeru.core.jcie.AppSignatureGenerator;
import edu.wm.cs.semeru.core.jcie.model.ClassInformationVO;

public class Utilities {

    public static JadbConnection jadb;

    // consts for determining click size or long clicks
    public static double LONG_CLICK_DURATION = 0.5;
    public static int CLICK_RING = 20;

    public static void main(String[] args) throws IOException, JadbException, InterruptedException {
        try {
            new AdbServerLauncher(new Subprocess(), System.getenv()).launch();
            System.out.println("Started adb-server...");
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not start adb-server");
        }

        jadb = new JadbConnection();
        System.out.println(jadb.getHostVersion());

        List<Event> test_events = getEvents("data/youtube.log");
//        for (Event event : test_events) {
//        	System.out.println(event);
//        }

        replayEvents(test_events);
        // String androidSDKPath = "/Users/charlyb07/Applications/android-sdk-macosx/";
        // String command = androidSDKPath + "platform-tools/"
        // + "adb shell dumpsys activity org.dmfs.tasks/.TaskListActivity";
        // // int orientation = Utilities.getOrientation(pathAdb);
        // // System.out.println(orientation);
        // // Utilities.setOrientationLandscape(androidSDKPath);
        // // System.out.println(Utilities.getCurrentFragment(androidSDKPath));
        // // System.out.println(Utilities.detectTypeofWindow(androidSDKPath));
        //
        // int heightScreen = DeviceInfo.NEXUS_7_HEIGHT;
        // int widthScreen = DeviceInfo.NEXUS_7_WIDTH;
        // for (int i = 0; i < 20; i++) {
        // // System.out.println(Utilities.detectTypeofWindow(androidSDKPath,
        // // widthScreen, heightScreen));
        // // System.out.println(Utilities.getCurrentFragment(androidSDKPath,
        // // getCurrentActivityImproved(androidSDKPath, "")));
        // // System.out.println(Utilities.getCurrentActivityImproved(androidSDKPath,
        // // ""));
        // try {
        // Thread.sleep(4 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
    }

    public static List<Event> getEvents(String filePath) throws FileNotFoundException {
    	System.out.println("Test");
        File file = new File(filePath);
        Scanner input = new Scanner(file);
        List<Event> events = new ArrayList<Event>();
        List<String> list = new ArrayList<String>();

        long x = 0;
        long y = 0;
        boolean was_finger_down = false;
        boolean finger_down = false;
        double start_time = 0;
        double duration = 0;
        double last_event = 0;
        List<Coords> coords = new ArrayList<Coords>();

        Pattern pattern = Pattern.compile("\\[\\s*(\\d*\\.\\d*)\\] /dev/input/event(\\d): (\\w*) (\\w*)\\s*(\\w*)");
        // Constants.
        while (input.hasNextLine()) {
            String line = input.nextLine();
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                Double time = Double.parseDouble(m.group(1));
                String device = m.group(2);
                long type = Long.parseLong(m.group(3), 16);
                long code = Long.parseLong(m.group(4), 16);
                long value = Long.parseLong(m.group(5), 16);
//                 System.out.println("Line " + time + " " + device + " " + type + " " + code +
//                 " " + value);
                line = line.split("] ")[1];
                line = correctEvent(line);

                list.add(line);

                if (type == Constants.EV_KEY) {
//                    System.out.println("EV_KEY");
                    if (code == Constants.BTN_TOUCH) { // Check if screen has been touched
                    	if (value == 1)
                    		finger_down = true;
                    	else finger_down = false;
                    }
                    else if (value == 0) { // Event completed reset everything
                        duration = time - start_time;
                        System.out.println(list);
                        list = new ArrayList<String>();
                    } else if (value == 1) // Starting new event recording
                        start_time = time;
                } else if (type == Constants.EV_ABS) { // Absolute coordinates from a touchscreen.
//                     System.out.println("EV_ABS");
                    if (code == Constants.ABS_X || code == Constants.ABS_MT_POSITION_X)
                        x = value;
                    else if (code == Constants.ABS_Y || code == Constants.ABS_MT_POSITION_Y)
                        y = value;
                    else if (code == Constants.ABS_MT_TRACKING_ID) {
                        finger_down = (value != Long.parseLong("ffffffff", 16));
//                        System.out.println("Tracking " + finger_down);
                    }
                } else if (type == Constants.EV_SYN && code == Constants.SYN_REPORT) {
                    // If the finger has changed:
//                     System.out.println("Finger changed " + finger_down + " from " + was_finger_down);
                    if (finger_down != was_finger_down) {
                        // Restart the coordinate list.
                        if (finger_down) {
                            start_time = time;

                            coords.clear();
                            coords.add(new Coords(x, y));
                        } else {
                            duration = time - start_time;
                            Coords start_location = coords.get(0);
                            Coords end_location = coords.get(coords.size() - 1);
                            double pause_duration = time - ((last_event > 0) ? last_event : time);
                            last_event = time;
                            String event_label = "";

                            double distance = Math.sqrt(Math.pow(start_location.x - end_location.x, 2)
                                    + Math.pow(start_location.y - end_location.y, 2));

                            // Determine if event was click, long click, or swipe
                            if (duration >= LONG_CLICK_DURATION) {
                                event_label = "LONG_CLICK";
                                if (distance > CLICK_RING)
                                    event_label = "SWIPE";
                            } else {
                                event_label = "CLICK";
                                if (distance > CLICK_RING)
                                    event_label = "SWIPE";
                            }

                            Event delay = new Event(pause_duration);
                            events.add(delay);
                            Event event = new Event(start_location, end_location, last_event, event_label, 0.0);
                            event.setRaw_commands(new ArrayList(list));
                            events.add(event);

                            list.clear();
                        }

                        was_finger_down = finger_down;
                    } else
                        coords.add(new Coords(x, y));
                }
            }
        }

        return events;
    }

    public static String correctEvent(String event) {
        String[] result = event.split(" ");
        result[0] = result[0].substring(0, result[0].length() - 1);
        result[1] = "" + Long.parseLong(result[1], 16);
        result[2] = "" + Long.parseLong(result[2], 16);
        result[3] = "" + Long.parseLong(result[3], 16);

        return String.join(" ", result);
    }

    @SuppressWarnings("deprecation")
    public static void replayEvents(List<Event> events) throws IOException, JadbException, InterruptedException {

        JadbDevice device = jadb.getAnyDevice();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (Event event : events) {
            System.out.println(event);
            List<String> commands = event.getRaw_commands();
            double wait = event.getWait_per_command();
            for (String command : commands) {
//            	Thread.sleep((long) wait);
            	TimeUnit.MICROSECONDS.sleep((long) wait);
                System.out.println(command);
                if (event.is_delay())
                    device.executeShell(bout, command);
                else
                    device.executeShell(bout, "sendevent " + command);
            }
        }
        System.out.write(bout.toByteArray());

    }

    // -------------------------------------------------------------------------------------------
    // MISC TASKS
    // -------------------------------------------------------------------------------------------

    public static void runSDKMonkeyEvent(String androidSDKPath, String appPackage, int events, long delay) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Calling Monkey");
            rt.exec(androidToolsPath + File.separator + "adb shell monkey -p " + appPackage
                    + " --pct-trackball 0 --pct-nav 0 --pct-syskeys 0 --pct-appswitch 0 --pct-touch 100 --ignore-security-exceptions --throttle "
                    + delay + " -v " + events).waitFor();
            // Thread.sleep(delay);

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getAndPullScreenshot(String androidSDKPath, String outputFolder, String name) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Getting screenshot:" + name);
            rt.exec(androidToolsPath + File.separator + "adb shell /system/bin/screencap -p /sdcard/screen.png")
                    .waitFor();
            Thread.sleep(2000);
            System.out.println("--- Pulling screenshot:" + name);
            rt.exec(androidToolsPath + File.separator + "adb pull /sdcard/screen.png " + outputFolder + File.separator
                    + name).waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void clearLogcat(String androidSDKPath) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();
            System.out.println("--- Clearing logcat");
            rt.exec(androidToolsPath + File.separator + "adb shell logcat -c").waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Set<String> getLogcatAndExtractAppMethods(String androidSDKPath, String packageName) {

        Set<String> methods = new HashSet<String>();
        try {
            Runtime rt = Runtime.getRuntime();

            System.out.println("-- Getting logcat");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            Process proc = rt.exec(new String[] { "/bin/sh", "-c", androidToolsPath + File.separator
                    + "adb shell logcat -d  | grep 'SOOT:<' | grep '" + packageName + "'" });

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                methods.add(line.substring(line.indexOf('<'), line.indexOf('>')));
            }

            proc.waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return methods;

    }

    public static String getCurrentActivity(String androidSDKPath) {
        String activity = null;
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(new String[] { "/bin/sh", "-c", androidToolsPath + File.separator
                    + "adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'" });

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            String window = null;

            while ((line = stdInput.readLine()) != null) {

                if (line.trim().startsWith("mCurrentFocus")) {
                    window = line.substring(line.indexOf("u0") + 3, line.lastIndexOf("}"));
                    if (window.contains("PopupWindow:")) {
                        window = "Popup Menu";
                    } else if (window.contains("/")) {
                        window = "Main";
                    }

                }

                else if (line.trim().startsWith("mFocusedApp")) {
                    activity = line.substring(line.indexOf("/") + 1, line.length());
                    activity = activity.substring(0, activity.indexOf(" "));
                    if (activity.startsWith(".")) {
                        activity = activity.substring(1, activity.length());
                    }
                }
            }
            proc.waitFor();

            activity += "(Window=" + window + ")";

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return activity;
    }

    // -------------------------------------------------------------------------------------------
    // GUI EVENTS-RELATED TASKS
    // -------------------------------------------------------------------------------------------

    public static ArrayList<Integer> getScreenDimensions(String androidSDKPath) {
        return getScreenDimensions(androidSDKPath, null);
    }

    /**
     * Real dimensions in pixels
     * 
     * @param androidSDKPath
     * @return
     */
    public static ArrayList<Integer> getScreenDimensions(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        ArrayList<Integer> dimensions = null;

        String command = androidToolsPath + File.separator + "adb " + device + "shell dumpsys window | "
                + "grep 'mUnrestrictedScreen' ";
        String line = TerminalHelper.executeCommand(command);

        line = line.substring(line.indexOf(")") + 2);
        String[] temp = line.split("x");
        dimensions = new ArrayList<Integer>();
        dimensions.add(new Integer(temp[0]));
        dimensions.add(new Integer(temp[1]));

        return dimensions;

    }

    public static ArrayList<Integer> getMaxScreenAbsValues(String androidSDKPath) {
        return getMaxScreenAbsValues(androidSDKPath, null);
    }

    /**
     * Max dimensions in pixels used by the linux input/touchscreen drivers
     * 
     * @param androidSDKPath
     * @return
     */
    public static ArrayList<Integer> getMaxScreenAbsValues(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        ArrayList<Integer> dimensions = new ArrayList<Integer>();
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(androidToolsPath + File.separator + "adb " + device + "shell getevent -p");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            String temp = null;
            while ((line = stdInput.readLine()) != null) {
                if (line.trim().startsWith("0035")) { // X-dimension

                    temp = line.substring(line.indexOf("max") + 4, line.length());
                    temp = temp.substring(0, temp.indexOf(','));

                    dimensions.add(new Integer(temp));
                } else if (line.trim().startsWith("0036")) { // Y-dimension
                    temp = line.substring(line.indexOf("max") + 4, line.length());
                    temp = temp.substring(0, temp.indexOf(','));
                    dimensions.add(new Integer(temp));
                    break;
                }
            }
            proc.waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dimensions;

    }

    // -------------------------------------------------------------------------------------------
    // PROFILER-RELATED TASKS
    // -------------------------------------------------------------------------------------------

    public static void startProfiler(String androidSDKPath, String appPackageName) {
        try {
            System.out.println("-- Starting profiler");
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();

            rt.exec(androidToolsPath + File.separator + "adb shell am profile start " + appPackageName
                    + " /sdcard/Traces/" + appPackageName + ".trace");

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getScreenSize(String androidSDKPath) {
        return getScreenSize(androidSDKPath, null);
    }

    public static String getScreenSize(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String result = "";
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[] { "/bin/sh", "-c", androidToolsPath + "adb " + device
                    + "shell dumpsys window | grep \"mUnrestrictedScreen\"|  awk '{ print $2 }'" });
            proc.waitFor();
            return getStringFromInputStream(proc.getInputStream());
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static String getAppVersionAdb(String androidSDKPath, String appPackage) {
        String result = "";
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools" + File.separator;
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[] { "/bin/sh", "-c",
                    androidToolsPath + "adb shell dumpsys package " + appPackage + " | grep versionName" });
            proc.waitFor();
            String version = getStringFromInputStream(proc.getInputStream());
            return version.substring(version.indexOf("=") + 1, version.length()).trim();
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static void stopProfiler(String androidSDKPath, String appPackageName) {
        try {
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
            Runtime rt = Runtime.getRuntime();

            System.out.println("-- Stopping profiler");
            rt.exec(androidToolsPath + File.separator + "adb shell am profile stop " + appPackageName).waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void pullTraceAndExtactAppMethods(String androidSDKPath, String appPackageName, String outputFolder,
            String label) {
        try {
            Runtime rt = Runtime.getRuntime();
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            System.out.println("-- Copying profiler trace from device");
            rt.exec(androidToolsPath + File.separator + "adb pull /sdcard/Traces/" + appPackageName + ".trace "
                    + outputFolder + File.separator + "ptrace_" + label).waitFor();

            System.out.println("-- Translating profiler trace");
            DataExtractor.extractDumpFromTrace(outputFolder + File.separator + "ptrace_" + label, androidSDKPath,
                    outputFolder, "trace_dump_" + label);

            System.out.println("-- Extracting  project specific methods from the dump");
            DataExtractor.filterMethodsFromDump(appPackageName, outputFolder + File.separator + "trace_dump_" + label,
                    outputFolder, "method_calls_" + label);
        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Set<String> pullTraceAndGetAppMethods(String androidSDKPath, String appPackageName,
            String outputFolder, String label) {

        Set<String> methods = new HashSet<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

            System.out.println("-- Copying profiler trace from device");
            rt.exec(androidToolsPath + File.separator + "adb pull /sdcard/Traces/" + appPackageName + ".trace "
                    + outputFolder + File.separator + "ptrace_" + label).waitFor();

            System.out.println("-- Translating profiler trace");

            Process proc = rt.exec(new String[] { "/bin/sh", "-c",
                    "grep -E \"^0x[a-z0-9]{8}\\s+\" " + outputFolder + File.separator + "ptrace_" + label
                            + "| awk '{print $2,$3,$4 }' | sed 's/ /./g' | sed 's/$[0-9]*//g' | grep -E '"
                            + appPackageName + "'" });
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            System.out.println("ERROR: " + getStringFromInputStream(proc.getErrorStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                System.out.println("LINE: " + line);
                methods.add(line);
            }
            proc.waitFor();

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return methods;

    }

    /**
     * Transforms input stream in an string
     * 
     * @param is input stream to be transformed
     * @return string transformed
     */

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    // /**
    // *
    // * @return
    // */
    // public static ArrayList<Integer> getStatusBarDimensions() {
    // HierarchyViewerConnection.getStatusBarTreeInfo();
    // DrawableViewNode node = HierarchyViewerConnection.getStatusBarTreeInfo();
    // ArrayList<Integer> list = new ArrayList<Integer>();
    // list.add(node.viewNode.width);
    // list.add(node.viewNode.height);
    // return list;
    // }

    /**
     * 
     * @param transitions   .entrySet()
     * @param transitionKey
     * @return list of transitions to be executed in order to reach a view
     */
    public static List<Transition> getTransitions(HashMap<String, Transition> transitions, String windowTransition,
            List<String> ids) {
        ArrayList<Transition> result = new ArrayList<Transition>();
        String[] split = windowTransition.split("@");
        String source = split[0];
        String target = split[1];

        GraphFindPath<String> graph = new GraphFindPath<String>();
        // add all nodes to the graph
        for (String id : ids) {
            graph.addNode(id);
        }
        // add all edges
        for (Entry<String, Transition> t : transitions.entrySet()) {
            graph.addEdge(t.getValue().getSourceWindow(), t.getValue().getTargetWindow(), 0);
        }
        // set goal
        FindPath<String> path = new FindPath<String>(graph);
        List<String> resultPath = path.getAllPaths(source, target);
        if (resultPath != null) {
            for (int i = 0; i < resultPath.size() - 1; i++) {
                String key = resultPath.get(i) + "@" + resultPath.get(i + 1);
                result.add(transitions.get(key));
            }
            return result;
        }
        return null;
    }

    public static String getCurrentActivityImproved(String androidSDKPath, String appPackage) {
        return getCurrentActivityImproved(androidSDKPath, appPackage, null, null, null);
    }

    /**
     * @param androidSDKPath
     * @param appPackage
     * @return
     */
    public static String getCurrentActivityImproved(String androidSDKPath, String appPackage, String devicePort,
            String adbPort, TypeDeviceEnum device) {
        // String androidToolsPath = androidSDKPath + File.separator +
        // "platform-tools";
        // String command = androidToolsPath + File.separator +
        // "adb shell dumpsys activity | grep -E 'mFocusedActivity'";
        // String data[] =
        // TerminalHelper.executeCommand(command).trim().split(" ");
        // if (data.length >= 4) {
        // return data[3].trim();
        // }
        // LogHelper.getInstance("logs_dfs/crashes.txt")
        // .addLine(appPackage + ":CRASH:" + Calendar.getInstance().getTime());
        // return "";

        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        // System.out.println(activity);

        String deviceConnect = "";

        if (devicePort != null && !devicePort.isEmpty()) {
            switch (device) {
            case EMULATOR:
                deviceConnect = " -P " + adbPort + " -s emulator:" + devicePort;
                break;
            case AVD:
                deviceConnect = " -P " + adbPort + " -s 127.0.0.1:" + devicePort;
                break;
            case GENY_MOTION:
                deviceConnect = " -P " + adbPort + " -s " + devicePort;
                break;
            case DEVICE:
                String adbPortCheck = "";
                if (adbPort != null) {
                    adbPortCheck = " -P " + adbPort;
                }
                if (devicePort != null) {
                    adbPortCheck += " -s " + devicePort;
                }
                deviceConnect = adbPortCheck;
                break;
            case OLD_AVD:
                deviceConnect = " -P " + adbPort + " -s " + devicePort + ":5555";
                break;
            default:
                break;
            }
        }

        String command = androidToolsPath + File.separator + "adb" + deviceConnect + " shell dumpsys window windows";
        String test = TerminalHelper.executeCommand(command).trim();
        String[] lines = test.split(System.getProperty("line.separator"));
        String activityLine = "";

        for (String activityTest : lines) {
            if (activityTest.contains("mCurrentFocus")) {
                activityLine = activityTest;
            } else if (activityTest.contains("mFocusedApp")) {
                activityLine = activityLine + "\n" + activityTest;
            }
        }

        // System.out.println(test);
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
            return activity1;
        } else {
            return activity2;
        }
    }

    // ------------------------------------------
    // Coverage measurement
    // ----------------------------------------

    public static CoverageValuesVO getCoverageValues(HashSet<String> coveredMethods, HashSet<String> coveredActivities,
            String apkPath, String astBinariesPath, String appSourceCodePath) throws Exception {
        CoverageValuesVO values = new CoverageValuesVO();

        HashSet<String> activities = APKInfoExtractor.getActivitiesFromAPK(apkPath);
        List<ClassInformationVO> classesInfo = AppSignatureGenerator.getClassesInformation(appSourceCodePath,
                astBinariesPath);

        int totalMethods = 0;
        for (ClassInformationVO classInfo : classesInfo) {
            totalMethods += classInfo.getMethods().size();
        }

        values.setActivitiesInTheAPK(activities.size());
        values.setCoveredMethods(coveredMethods.size());
        values.setCoveredActivities(coveredActivities.size());
        values.setMethodsInTheAPK(totalMethods);
        return values;
    }

    public static int getOrientation(String androidSDKPath) {
        return getOrientation(androidSDKPath, null);
    }

    /**
     * @return
     */
    public static int getOrientation(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String line = null;
        String adb = androidSDKPath + File.separator + "platform-tools" + File.separator + "adb";
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[] { "/bin/sh", "-c",
                    adb + " " + device + "shell dumpsys input | grep 'SurfaceOrientation' |  awk '{ print $2 }'" });
            line = getStringFromInputStream(proc.getInputStream());
            proc.waitFor();
            if (line == null || line.isEmpty()) {
                return 0;
            }

        } catch (Exception ex) {
            Logger.getLogger(StepByStepEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.parseInt(line);
    }

    public static DynGuiComponentVO getHVComponentFromBasicTreeNode(BasicTreeNode node, int widthScreen,
            int heightScreen, String currentActivity, DynGuiComponentVO parent) {
        return getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen, currentActivity, parent, true);
    }

    public static DynGuiComponentVO getHVComponentFromBasicTreeNode(BasicTreeNode node, int widthScreen,
            int heightScreen, String currentActivity, DynGuiComponentVO parent, boolean propagateText) {
        DynGuiComponentVO vo = new DynGuiComponentVO();
        if (parent != null && parent.getName().equals("android.widget.ListView")) {
            vo.setItemList(true);
            vo.setComponentTotalIndex(node.getParent().getChildCount());
        }
        vo.setHeight(node.height);
        vo.setWidth(node.width);
        vo.setPositionX(node.x);
        vo.setPositionY(node.y);
        vo.setName("");

        int centerX = vo.getPositionX() + (vo.getWidth() / 2);
        int centerY = vo.getPositionY() + (vo.getHeight() / 2);

        vo.setRelativeLocation(getNewLocation(centerX, centerY, widthScreen, heightScreen));
        // Random r = new Random();
        // String idDb = System.currentTimeMillis() + "" + r.nextInt(10) +
        // r.nextInt(10);
        // vo.setId(Long.parseLong(idDb));
        vo.setActivity(currentActivity);
        for (Object attribute : node.getAttributesArray()) {
            AttributePair pair = (AttributePair) attribute;
            if (pair.key.equals("index")) {
                vo.setComponentIndex(Integer.parseInt(pair.value));
            } else if (pair.key.equals("text")) {
                vo.setText(pair.value);
            } else if (pair.key.equals("resource-id")) {
                // int index = pair.value.indexOf(":");
                // if (index != -1) {
                // vo.setIdXml(pair.value.substring(index + 1, pair.value.length()));
                // vo.setIdText(pair.value.substring(index + 1, pair.value.length()));
                // }
                vo.setIdXml(pair.value);
                vo.setIdText(pair.value);
            } else if (pair.key.equals("checkable")) {
                vo.setCheckable(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("checked")) {
                vo.setChecked(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("clickable")) {
                vo.setClickable(Boolean.valueOf(pair.value));
            } else if (pair.key.equals("enabled")) {
                vo.setEnabled(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("focusable")) {
                vo.setFocusable(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("focused")) {
                vo.setFocused(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("scrollable")) {
                vo.setScrollable(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("long-clickable")) {
                vo.setLongClickable(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("password")) {
                vo.setPassword(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("selected")) {
                vo.setSelected(Boolean.parseBoolean(pair.value));
            } else if (pair.key.equals("class")) {
                vo.setName(pair.value);
            } else if (pair.key.equals("package")) {
                vo.setPackageName(pair.value);
            } else if (pair.key.equals("content-desc")) {
                vo.setContentDescription(pair.value);
            }
        }
        if (vo.getIdXml() == null) {
            vo.setIdXml("NO_ID");
            vo.setIdText("NO_ID");
        }
        // We don't need this any more
        // if (((vo.getName().equals("android.widget.LinearLayout") ||
        // vo.getName().equals("android.widget.RelativeLayout")
        // || vo.getName().equals("android.widget.FrameLayout") || vo.getName()
        // .equals("android.app.ActionBar$Tab"))) && propagateText) {
        // DynGuiComponentVO temp = null;
        // for (BasicTreeNode child : node.getChildren()) {
        // temp = getHVComponentFromBasicTreeNode(child, widthScreen, heightScreen,
        // currentActivity, parent);
        // // if (temp.getName().endsWith("android.widget.TextView")) {
        // if (temp.getText() != null && !temp.getText().isEmpty()) {
        // break;
        // }
        // }
        // if (temp != null) {
        // if (vo.getText() != null && vo.getText().isEmpty()) {
        // vo.setText(temp.getText());
        // }
        // if (vo.getContentDescription() != null &&
        // vo.getContentDescription().isEmpty()) {
        // vo.setContentDescription(temp.getContentDescription());
        // }
        // // Fixed to get the real id
        // // vo.setIdXml(temp.getIdXml());
        // }
        // }
        return vo;
    }

    /**
     * @deprecated
     * 
     * @param x
     * @param y
     * @param sizeX
     * @param sizeY
     * @return
     */
    @Deprecated
    public static String getLocation(int x, int y, int sizeX, int sizeY) {
        double percentage = 0.25;
        int minX = (int) (sizeX * percentage);
        int minY = (int) (sizeY * percentage);
        int maxX = sizeX - minX;
        int maxY = sizeY - minY;
        String result = "";

        if (y <= maxY) {
            if (y <= minY) {
                result = "Top";
            } else {
                result = "Center";
            }
        } else {
            result = "Bottom";
        }

        if (x <= maxX) {
            if (x <= minX) {
                result += " left";
            } else {
                result += "";
            }
        } else {
            result += "Right";
        }
        return result;
    }

    public static String getNewLocation(int x, int y, int sizeX, int sizeY) {
        double percentageX = 0.05;// 5% * 2 = 10%
        double percentageY = 0.3;
        int minX = (int) (sizeX / 2d - (sizeX * percentageX));
        int minY = (int) (sizeY * percentageY);
        int maxX = (int) (sizeX / 2d + (sizeX * percentageX));
        int maxY = sizeY - minY;
        String result = "";

        if (y <= maxY) {
            if (y <= minY) {
                result = "Top";
            } else {
                result = "Center";
            }
        } else {
            result = "Bottom";
        }

        if (x <= maxX) {
            if (x <= minX) {
                result += " left";
            } else {
                result += "";
            }
        } else {
            result += " right";
        }
        return result;
    }

    public static String getCurrentWindowIdFromComponent(String androidSDKPath, DynGuiComponentVO vo) {
        return vo.getPositionX() + "$" + vo.getPositionY() + "_" + vo.getWidth() + "$" + vo.getHeight();
        // int width = vo.getWidth();
        // int height = vo.getHeight();
        // ArrayList<DynGuiComponent> nodes =
        // UiAutoConnector.getScreenInfo(androidSDKPath, width, height);
        // String windowHash = "";
        //
        // for (DynGuiComponent getText : nodes) {
        // windowHash +=
        // getText.getName().substring(getText.getName().lastIndexOf("."));
        //
        // }
        // System.out.println("Window Hash: " + windowHash);
        // return windowHash;
    }

    public static boolean isKeyboardActive(String androidSDKPath) {
        return isKeyboardActive(androidSDKPath, null);
    }

    public static boolean isKeyboardActive(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb " + device
                + "shell dumpsys input_method|grep mInputShown| awk '{print $4 }'";
        Runtime rt = Runtime.getRuntime();
        Process proc;
        try {
            proc = rt.exec(new String[] { "/bin/sh", "-c", command });
            String line = getStringFromInputStream(proc.getInputStream());
            proc.waitFor();
            if (line != null && !line.isEmpty() && line.contains("true")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void disposeKeyboard(String androidSDKPath) {
        disposeKeyboard(androidSDKPath, null);
    }

    public static void disposeKeyboard(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb " + device + "shell input keyevent 111";
        Runtime rt = Runtime.getRuntime();
        Process proc;
        try {
            proc = rt.exec(new String[] { "/bin/sh", "-c", command });
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getDeviceVersion(String androidSDKPath) {
        return getDeviceVersion(androidSDKPath, null);
    }

    /**
     * @param androidSDKPath
     * @return
     */
    public static String getDeviceVersion(String androidSDKPath, String device) {
        if (device == null || (device != null && device.isEmpty())) {
            device = "";
        } else {
            device = "-s " + device + " ";
        }
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb " + device + "shell getprop ro.product.model";
        return TerminalHelper.executeCommand(command).trim();
    }

    public static String getErrorsFromLogcat(String androidSDKPath, String appPackage, String outputFolder) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "./logcat_error_helper.sh " + appPackage + " " + androidToolsPath + File.separator + "adb";
        String error = TerminalHelper.executeCommand(command);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String clear_logcat = androidToolsPath + File.separator + "adb logcat -c";
        TerminalHelper.executeCommand(clear_logcat);
        if (error != null && !error.isEmpty()) {
            System.out.println("Logcat Errors: " + error);
            return (error);
        }
        System.out.println("No exceptions");
        return (null);

    }

    public static String getAndroidVersion(String androidSDKPath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        String command = androidToolsPath + File.separator + "adb shell getprop ro.build.version.release";
        String result = TerminalHelper.executeCommand(command);

        return result;

    }

    /**
     * 
     * @param androidSDKPath
     * @param value          landscape = 1, portrait = 0
     */
    private static void setOrientation(String androidSDKPath, String value) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = "adb shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:";

        // disable accelerometer controlling rotation
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command + "0");
        String command2 = "adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:";
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command2 + value);
        // enable accelerometer controlling rotation
        TerminalHelper.executeCommand(androidToolsPath + File.separator + command + "1");

    }

    public static void setOrientationLandscape(String androidSDKPath) {
        setOrientation(androidSDKPath, "1");
    }

    public static void setOrientationPortrait(String androidSDKPath) {
        setOrientation(androidSDKPath, "0");
    }

    public static String getCurrentFragment(String androidSDKPath, String currentActivity, TypeDeviceEnum device,
            String devicePort, String adbPort) {

        String result = "";
        String deviceConnect = "";

        if (devicePort != null && !devicePort.isEmpty()) {
            switch (device) {
            case EMULATOR:
                deviceConnect = " -P " + adbPort + " -s emulator:" + devicePort;
                break;
            case AVD:
                deviceConnect = " -P " + adbPort + " -s 127.0.0.1:" + devicePort;
                break;
            case GENY_MOTION:
                deviceConnect = " -P " + adbPort + " -s " + devicePort;
                break;
            case DEVICE:
                String adbPortCheck = "";
                if (adbPort != null) {
                    adbPortCheck = " -P " + adbPort;
                }
                deviceConnect = adbPortCheck;
                break;
            case OLD_AVD:
                deviceConnect = " -P " + adbPort + " -s " + devicePort + ":5555";
            default:
                break;
            }
        }
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";

        // System.out.println(activity);
        String command = androidToolsPath + File.separator + "adb" + deviceConnect + " shell dumpsys activity "
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
        return result;
    }

    // for contextual menu and bottom menu
    private static final String MENU_BOTTOM_AND_CONTEXTUAL = "AtchDlg";
    private static final String POPUP = "PopupWindow";

    // private static final String MENU_BOTTOM = "PopupWindow";

    public static WindowVO detectTypeofWindow(String androidSDKPath, int widthScreen, int heightScreen,
            TypeDeviceEnum device, String devicePort, String adbPort) {
        String appTransitionState = null;
        do {
            System.out.println("-App State not Idle, waiting...");
            appTransitionState = StepByStepEngine.getAppTransitionState(androidSDKPath);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Catch Thread Interrupted Exception
                e.printStackTrace();
            }

        } while (!appTransitionState.equals("APP_STATE_IDLE"));
        String deviceConnect = "";
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        if (devicePort != null && !devicePort.isEmpty()) {
            switch (device) {
            case EMULATOR:
                deviceConnect = " -P " + adbPort + " -s emulator-" + devicePort;
                break;
            case AVD:
                deviceConnect = " -P " + adbPort + " -s 127.0.0.1:" + devicePort;
                break;
            case GENY_MOTION:
                deviceConnect = " -P " + adbPort + " -s " + devicePort;
                break;
            case DEVICE:
                String adbPortCheck = "";
                if (adbPort != null) {
                    adbPortCheck = " -P " + adbPort;
                }
                if (devicePort != null) {
                    adbPortCheck += " -s " + devicePort;
                }
                deviceConnect = adbPortCheck;
                break;
            case OLD_AVD:
                deviceConnect = " -P " + adbPort + " -s " + devicePort + ":5555";
            default:
                break;
            }
        }
        WindowVO vo = new WindowVO();
        // System.out.println(activity);
        String command = androidToolsPath + File.separator + "adb" + deviceConnect
                + " shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp' | awk '{print $3 }'";

        String result = TerminalHelper.executeCommand(command).split("\n")[0];
        result = result.substring(0, result.length() - 1);
        String activity = getCurrentActivityImproved(androidSDKPath, "", devicePort, adbPort, device);
        String window = "";
        if (result.contains(MENU_BOTTOM_AND_CONTEXTUAL)) {
            // It's contextual menu or it's in the bottom
            window += "MENU:";
        } else if (result.contains(POPUP)) {
            // It's option menu in the top, popup windows from spinners and
            // other android components
            window += "POPUP:";
        }

        boolean isAlert = false;
        // It's a normal activity or an AlertDialog

        DynGuiComponentVO root = new DynGuiComponentVO();

        if (devicePort != null && !devicePort.isEmpty()) {
            switch (device) {
            case EMULATOR:
                root = UiAutoConnector.getScreenInfoHierarchyEmulator(androidSDKPath, new StringBuilder(), widthScreen,
                        heightScreen, false, devicePort, adbPort, "dump.xml").getChildren().get(0);
                break;
            case AVD:
                root = UiAutoConnector.getScreenInfoHierarchyAVD(androidSDKPath, new StringBuilder(), widthScreen,
                        heightScreen, false, devicePort, adbPort, "dump.xml").getChildren().get(0);
                break;
            case GENY_MOTION:
                root = UiAutoConnector.getScreenInfoHierarchyEmulatorGeny(androidSDKPath, new StringBuilder(),
                        widthScreen, heightScreen, false, devicePort, adbPort, "dump.xml").getChildren().get(0);
                break;
            case DEVICE:
                root = UiAutoConnector.getScreenInfoHierarchy(androidSDKPath, new StringBuilder(), widthScreen,
                        heightScreen, false, devicePort).getChildren().get(0);
                break;
            case OLD_AVD:
                root = UiAutoConnector.getScreenInfoHierarchyAVDOld(androidSDKPath, new StringBuilder(), widthScreen,
                        heightScreen, false, devicePort, adbPort, "dump.xml").getChildren().get(0);
            default:
                break;
            }
        } else {
            try {
                root = UiAutoConnector
                        .getScreenInfoHierarchy(androidSDKPath, new StringBuilder(), widthScreen, heightScreen, false)
                        .getChildren().get(0);
            } catch (NullPointerException e) {
                return detectTypeofWindow(androidSDKPath, widthScreen, heightScreen, device, devicePort, adbPort);
            }
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

        String fragment = getCurrentFragment(androidSDKPath, activity, device, devicePort, adbPort);
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

    /**
     * Pull all the files from device using a prefix
     * 
     * @param androidSDKPath
     * @param devicePath     example: adb shell ls '/mnt/sdcard/coverage_*' | tr
     *                       '\r' ' ' | xargs -n1 -I file adb pull file /path/
     * @param targetPath
     */
    public static void pullFilesFromDeviceFolder(String androidSDKPath, String devicePath, String targetPath) {
        String androidToolsPath = androidSDKPath + File.separator + "platform-tools";
        String command = androidToolsPath + File.separator + "adb pull " + devicePath + " " + targetPath;
        // +
        // "adb shell ls '/mnt/sdcard/coverage_org.gnucash.android_chimp_random*' | tr
        // '\r' ' ' | xargs -n1 -I file adb pull file
        // /Users/charlyb07/Documents/workspace/semeru/Data-collector/icse16/output/";
        System.out.println(command);
        System.out.println(TerminalHelper.executeCommand(command));
    }

    /**
     * @param sdkFolder
     * @param prefix    +"*"
     * @param string
     */
    public static void mergeCoverageFiles(String sdkFolder, int numCovFiles, String outputFolder, String prefix) {
        String filesToMerge = "";
        String emmaPath = sdkFolder + File.separator + "tools" + File.separator + "lib" + File.separator + "emma.jar";
        System.out.println("--- Merging Emma Coverage files into " + outputFolder);
        String command = "ls -1 " + outputFolder + File.separator + prefix + "*";
        System.out.println(command);
        String[] split = TerminalHelper.executeCommand(command).split("\n");
        for (int i = 1; i <= split.length; i++) {
            if (i == split.length) {
                filesToMerge = filesToMerge + split[i - 1];
            } else {
                filesToMerge = filesToMerge + split[i - 1] + ",";
            }
        }
        String command_emma = "java -cp " + emmaPath + " emma merge -in " + filesToMerge + " -out " + outputFolder
                + File.separator + "coverage.ec";
        String output_emma = TerminalHelper.executeCommand(command_emma);
        System.out.println(output_emma);
    }
}
