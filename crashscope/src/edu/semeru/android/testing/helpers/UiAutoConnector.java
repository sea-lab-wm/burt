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

import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import edu.semeru.android.core.dao.StepDao;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;

import org.apache.commons.lang3.SystemUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * UiAutoConnector.java
 * 
 * Created on Sep 25, 2014, 5:25:19 PM
 */

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Sep 25, 2014
 */
public class UiAutoConnector {

    // private static final int THRESHOLD = 3;
    private static final int THRESHOLD = 0;
    public static final int GENERIC_STRATEGY = 0;
    public static final int RANDOM_STRATEGY = 1;
    public static final int LAYOUT_STRATEGY = 2;
    public static final int ACTIONABLE_COMPONENTS_STRATEGY = 3;
    public static final int PROPERTIES_COMPONENTS_STRATEGY = 4;
    public static final int PROPERTIES_COMPONENTS_DIMENSIONS_STRATEGY = 5;
    private String fileName = null;
    private Logger logger = null;
    private static int sequenceHierarchy = 0;

    public enum TypeDeviceEnum {
        DEVICE, EMULATOR, AVD, GENY_MOTION, OLD_AVD, FILE
    }

    /**
     * @param fileName
     */
    public UiAutoConnector(String fileName) {
        super();
        this.fileName = fileName;
    }

    public static void main(String[] args) {
        // /data/local/tmp
        // shell", "screencap", "-p", "/sdcard/screenshot.png
        // shell", "/system/bin/uiautomator", "dump", "/sdcard/uidump.xml

        String androidSdk = "/Users/junayed/Library/Android/sdk";

        ArrayList<Integer> screenDimensions = Utilities.getScreenDimensions(androidSdk);
        StringBuilder builder = new StringBuilder();
        getScreenInfoHierarchy(androidSdk, builder, screenDimensions.get(0), screenDimensions.get(1), false);
        System.out.println(builder.toString());
        long time = System.currentTimeMillis();
        //
        // for (DynGuiComponent dynGuiComponent : screenInfo) {
        // System.out.println(dynGuiComponent);
        // }
        // --
        // System.out.println(Utilities.detectTypeofWindow(androidSdk,
        // screenDimensions.get(0), screenDimensions.get(1)));
        ArrayList<DynGuiComponentVO> screenInfo = getScreenInfo(androidSdk, screenDimensions.get(0),
                screenDimensions.get(1), false, false, false);
        // for (DynGuiComponent dynGuiComponent : screenInfo) {
        // if(dynGuiComponent.isCalendarWindow()){
        // System.out.println("TRUE");
        // }
        // }
        // 723 439
        GUIEventVO guiEventVO = new GUIEventVO(600, 693, StepByStepEngine.CLICK);
        //getComponent(androidSdk, guiEventVO, screenDimensions.get(0), screenDimensions.get(1));
        // setComponentAreas(guiEventVO, androidSdk, screenDimensions.get(0),
        // screenDimensions.get(1));
        System.out.println(guiEventVO.getHvInfoComponent());
        System.out.println(guiEventVO.getHvInfoComponent().isCalendarWindow());

        // --back
        // guiEventVO = new GUIEventVO(220, 1830, StepByStepEngine.CLICK);
        // getComponent(androidSdk, guiEventVO, screenDimensions.get(0),
        // screenDimensions.get(1));
        // System.out.println(guiEventVO.getHvInfoComponent());
        // // --up a level
        // guiEventVO = new GUIEventVO(256, 215, StepByStepEngine.CLICK);
        // getComponent(androidSdk, guiEventVO, screenDimensions.get(0),
        // screenDimensions.get(1));
        // System.out.println(guiEventVO.getHvInfoComponent());
        // // --down a level
        // guiEventVO = new GUIEventVO(291, 486, StepByStepEngine.CLICK);
        // getComponent(androidSdk, guiEventVO, screenDimensions.get(0),
        // screenDimensions.get(1));
        // System.out.println(guiEventVO.getHvInfoComponent());
        // // --
        // guiEventVO = new GUIEventVO(900, 204, StepByStepEngine.CLICK);
        // getComponent(androidSdk, guiEventVO, screenDimensions.get(0),
        // screenDimensions.get(1));
        // System.out.println(guiEventVO.getHvInfoComponent());
        // // --
        // guiEventVO = new GUIEventVO(886, 489, StepByStepEngine.CLICK);
        // getComponent(androidSdk, guiEventVO, screenDimensions.get(0),
        // screenDimensions.get(1));
        // System.out.println(guiEventVO.getHvInfoComponent());

        // ArrayList<HVComponentVO> list = new ArrayList<HVComponentVO>();
        // DynGuiComponent root = getScreenInfoHierarchy(androidSdk,
        // screenDimensions.get(0), screenDimensions.get(1),
        // false);
        // System.out.println(root);
        // DynGuiComponent component = getComponentByIdAndType("id/alertTitle",
        // "TextView", root);
        // System.out.println(component);
        // component = getComponentByIdAndType("id/action_bar_title",
        // "TextView", root);
        // component = getComponentByIdAndType("id/title", "TextView", root);
        // System.out.println(component);
        // System.out.println("Time: " + (System.currentTimeMillis() - time));

        // DynGuiComponent a = new DynGuiComponent();
        // DynGuiComponent b = new DynGuiComponent();
        // a.setId(0l);
        // a.setActivity("activity");
        // a.setCheckable(true);
        // b.setId(0l);
        // b.setActivity("activity");
        // b.setCheckable(true);
        // Stack<DynGuiComponent> stack = new Stack<DynGuiComponent>();
        // stack.add(a);
        // if (stack.contains(b)) {
        // System.out.println(":(");
        // } else {
        // System.out.println("? :S");
        // }

    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoNoCache(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean isLists, String device) {
        return getScreenInfo(androidSDKPath, widthScreen, heightScreen, all, false, isLists, device);
    }

    /**
     * 
     * @param androidSDKPath
     * @param widthScreen
     * @param heightScreen
     * @param all
     * @return
     */
    public static ArrayList<DynGuiComponentVO> getScreenInfoNoCache(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean isLists) {
        return getScreenInfo(androidSDKPath, widthScreen, heightScreen, all, false, isLists, null);
    }

    /**
     * @param androidSDKPath
     * @param widthScreen
     * @param heightScreen
     * @param all
     * @param isLists
     * @return
     */
    public static ArrayList<DynGuiComponentVO> getScreenInfoCache(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean isLists) {
        return getScreenInfo(androidSDKPath, widthScreen, heightScreen, all, true, isLists, null);
    }

    /**
     * 
     * @param androidSDKPath
     * @param widthScreen
     * @param heightScreen
     * @param all
     * @return
     */
    public static ArrayList<DynGuiComponentVO> getScreenInfoCache(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean isLists, String device) {
        return getScreenInfo(androidSDKPath, widthScreen, heightScreen, all, false, isLists, device);
    }

    private static ArrayList<DynGuiComponentVO> getScreenInfo(String androidSDKPath, int widthScreen, int heightScreen,
            boolean all, boolean cache, boolean isLists) {
        return getScreenInfo(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, null);
    }

    /**
     * 
     * @param androidSDKPath
     * @param widthScreen
     * @param heightScreen
     * @param all
     * @param cache
     * @return
     */
    private static ArrayList<DynGuiComponentVO> getScreenInfo(String androidSDKPath, int widthScreen, int heightScreen,
            boolean all, boolean cache, boolean isLists, String devicePort) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, devicePort, null,
                null, TypeDeviceEnum.DEVICE);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoDevice(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, null, null, null,
                TypeDeviceEnum.DEVICE);
    }

    private static ArrayList<DynGuiComponentVO> getScreenInfoEmulatorGeny(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, null, null, null,
                TypeDeviceEnum.GENY_MOTION);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoAVD(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort,
            String name) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, emuPort, adbPort,
                name, TypeDeviceEnum.AVD);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoEmulator(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort,
            String name) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, emuPort, adbPort,
                name, TypeDeviceEnum.EMULATOR);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoAvdOld(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort,
            String name) {
        return getScreenInfoGeneric(androidSDKPath, widthScreen, heightScreen, all, cache, isLists, emuPort, adbPort,
                name, TypeDeviceEnum.OLD_AVD);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoGeneric(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, null, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.GENERIC_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoLayout(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, null, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.LAYOUT_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoPropertiesComponents(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, null, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.PROPERTIES_COMPONENTS_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoActionableComponents(String androidSDKPath, int widthScreen,
            int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, null, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.ACTIONABLE_COMPONENTS_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoGeneric(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort,
            String adbPort, String name, TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, builder, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.GENERIC_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoLayout(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort,
            String adbPort, String name, TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, builder, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.LAYOUT_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoPropertiesComponents(String androidSDKPath,
            StringBuilder builder, int widthScreen, int heightScreen, boolean all, boolean cache, boolean isLists,
            String emuPort, String adbPort, String name, TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, builder, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.PROPERTIES_COMPONENTS_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoActionableComponents(String androidSDKPath,
            StringBuilder builder, int widthScreen, int heightScreen, boolean all, boolean cache, boolean isLists,
            String emuPort, String adbPort, String name, TypeDeviceEnum device) {
        return getScreenInfoGeneric(androidSDKPath, builder, widthScreen, heightScreen, all, cache, isLists, emuPort,
                adbPort, name, device, UiAutoConnector.ACTIONABLE_COMPONENTS_STRATEGY);
    }

    public static ArrayList<DynGuiComponentVO> getScreenInfoGeneric(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean all, boolean cache, boolean isLists, String emuPort,
            String adbPort, String name, TypeDeviceEnum device, int typeStrategy) {
        ArrayList<DynGuiComponentVO> list = new ArrayList<DynGuiComponentVO>();

        BasicTreeNode tree = null;
        String currentActivity = null;
        switch (device) {
        case AVD:
            tree = getTreeFromXmlAVD(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.AVD);
            break;
        case EMULATOR:
            tree = getTreeFromXmlEmulator(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.EMULATOR);
            break;
        case GENY_MOTION:
            tree = getTreeFromXmlEmulatorGeny(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.GENY_MOTION);
            break;
        case DEVICE:
            if (name != null && !name.isEmpty()) {
                tree = getTreeFromXmlDevice(androidSDKPath, cache, emuPort, adbPort, name);
            } else {
                tree = getTreeFromXml(androidSDKPath, cache, emuPort);
            }
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.DEVICE);
            break;
        case OLD_AVD:
            tree = getTreeFromXmlAvdOld(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.OLD_AVD);
        case FILE:
            tree = getTreeFromXmlGeneric(androidSDKPath, false, emuPort, adbPort, name, TypeDeviceEnum.FILE);
            currentActivity = "";
        default:
            break;
        }

        sequenceHierarchy = 0;
        visitNodes(currentActivity, tree, list, widthScreen, heightScreen, all, null, isLists, 0, builder, typeStrategy);
        boolean isOffset = false;
        boolean isCalendarWindow = false;
        int offset = 0;
        for (DynGuiComponentVO vo : list) {
            if (vo.getOffset() != 0) {
                offset = vo.getOffset();
                isOffset = true;
                // break;
            }
            if (vo.isCalendarWindow()) {
                isCalendarWindow = true;
            }
        }
        // // Remove hidden component on date picker
        // if (index != -1) {
        // list.remove(index);
        // }
        if (isOffset || isCalendarWindow) {
            for (DynGuiComponentVO vo : list) {
                vo.setPositionY(vo.getPositionY() + offset);
                vo.setCalendarWindow(isCalendarWindow);
            }
        }

        return list;
    }

    public static DynGuiComponentVO getScreenInfoHierarchy(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache) {
        return getScreenInfoHierarchy(androidSDKPath, builder, widthScreen, heightScreen, cache, null);
    }

    public static DynGuiComponentVO getScreenInfoHierarchy(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String devicePort) {
        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, devicePort,
                null, null, TypeDeviceEnum.DEVICE, GENERIC_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyEmulator(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name) {
        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, TypeDeviceEnum.EMULATOR, GENERIC_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyEmulatorGeny(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name) {
        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, TypeDeviceEnum.GENY_MOTION, GENERIC_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyAVD(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name) {
        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, TypeDeviceEnum.AVD, GENERIC_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyAVDOld(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name) {
        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, TypeDeviceEnum.OLD_AVD, GENERIC_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyGeneric(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name,
            TypeDeviceEnum device, int typeStrategy) {
        DynGuiComponentVO parent = null;
        BasicTreeNode tree = null;
        String currentActivity = null;
        switch (device) {
        case AVD:
            tree = getTreeFromXmlAVD(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.AVD);
            break;
        case EMULATOR:
            tree = getTreeFromXmlEmulator(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.EMULATOR);
            break;
        case GENY_MOTION:
            tree = getTreeFromXmlEmulatorGeny(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.GENY_MOTION);
            break;
        case DEVICE:
            tree = getTreeFromXml(androidSDKPath, cache, emuPort);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.DEVICE);
            break;
        case OLD_AVD:
            tree = getTreeFromXmlAvdOld(androidSDKPath, cache, emuPort, adbPort, name);
            currentActivity = Utilities.getCurrentActivityImproved(androidSDKPath, null, emuPort, adbPort,
                    TypeDeviceEnum.OLD_AVD);
            break;
        default:
            break;
        }
        if (tree == null) {
            return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                    adbPort, name, device, typeStrategy);
        }
        sequenceHierarchy = 0;
        return visitNodes(currentActivity, tree, null, widthScreen, heightScreen, false, parent, false, 0, builder,
                typeStrategy);

    }

    public static DynGuiComponentVO getComponentByIdAndType(String id, String type, DynGuiComponentVO root) {
        DynGuiComponentVO result = null;
        for (DynGuiComponentVO child : root.getChildren()) {
            if (child != null && child.getIdXml().endsWith(id) && child.getName().endsWith(type)) {
                return child;
            }
            result = getComponentByIdAndType(id, type, child);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    private static BasicTreeNode getTreeFromXml(String androidSDKPath, boolean cache, String device) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, device, null, "ui_dump", TypeDeviceEnum.DEVICE);
    }

    /**
     * @param androidSDKPath
     * @param cache
     * @return
     */
    private static BasicTreeNode getTreeFromXml(String androidSDKPath, boolean cache) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, null, null, "ui_dump", TypeDeviceEnum.DEVICE);
    }

    private static BasicTreeNode getTreeFromXmlEmulator(String androidSDKPath, boolean cache, String emuPort,
            String adbPort, String name) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, emuPort, adbPort, name, TypeDeviceEnum.EMULATOR);
    }

    private static BasicTreeNode getTreeFromXmlEmulatorGeny(String androidSDKPath, boolean cache, String emuPort,
            String adbPort, String name) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, emuPort, adbPort, name, TypeDeviceEnum.GENY_MOTION);
    }

    private static BasicTreeNode getTreeFromXmlAVD(String androidSDKPath, boolean cache, String emuPort, String adbPort,
            String name) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, emuPort, adbPort, name, TypeDeviceEnum.AVD);
    }

    private static BasicTreeNode getTreeFromXmlDevice(String androidSDKPath, boolean cache, String emuPort,
            String adbPort, String name) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, emuPort, adbPort, name, TypeDeviceEnum.DEVICE);
    }

    private static BasicTreeNode getTreeFromXmlAvdOld(String androidSDKPath, boolean cache, String emuPort,
            String adbPort, String name) {
        return getTreeFromXmlGeneric(androidSDKPath, cache, emuPort, adbPort, name, TypeDeviceEnum.OLD_AVD);
    }

    private static BasicTreeNode getTreeFromXmlGeneric(String androidSDKPath, boolean cache, String devicePort,
            String adbPort, String name, TypeDeviceEnum device) {
        if (!cache) {
            String adb = androidSDKPath + File.separator + "platform-tools" + File.separator + "adb";
            String terminal = "";
            String deviceCommand = "";
//            System.out.println("Name: " + name);

            switch (device) {
            case EMULATOR:
                deviceCommand = adb + " -P " + adbPort + " -s emulator-" + devicePort;
                break;
            case AVD:
                if (SystemUtils.IS_OS_MAC) {
                    deviceCommand = adb + " -P " + adbPort + " -s 127.0.0.1:" + devicePort;
                } else {
                    deviceCommand = adb + " -P " + adbPort + " -s localhost:" + devicePort;
                }
                break;
            case GENY_MOTION:
                deviceCommand = adb + " -P " + adbPort + " -s " + devicePort;
                break;
            case DEVICE:
                String adbPortCheck = "";
                if (adbPort != null) {
                    adbPortCheck = " -P " + adbPort;
                }
                if (devicePort != null) {
                    adbPortCheck += " -s " + devicePort;
                }
                deviceCommand = adb + adbPortCheck;
                System.out.println(deviceCommand);
                break;
            case OLD_AVD:
                deviceCommand = adb + " -P " + adbPort + " -s " + devicePort + ":5555";
                break;
            default:
                break;
            }
            terminal = TerminalHelper.executeCommand(deviceCommand + " shell mkdir /sdcard/uimonkeyautomator");
            terminal = TerminalHelper.executeCommand(
                    deviceCommand + " shell /system/bin/uiautomator dump /sdcard/uimonkeyautomator/ui_dump.xml");
            // Logger.getLogger("error").log(Level.INFO, terminal);
            terminal = TerminalHelper
                    .executeCommand(deviceCommand + " pull /sdcard/uimonkeyautomator/ui_dump.xml " + name + ".xml");
            // Logger.getLogger("error").log(Level.INFO, terminal);
        }
        UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
        String xmlPath = name + ".xml";
        BasicTreeNode tree = loader.parseXml(xmlPath);
        return tree;
    }

    public static boolean checkForCrash(String appPackage, String mainActivity, String androidSDKPath, int widthScreen,
            int heightScreen) {
        // Check new state and update the list of components
        boolean crash = false;
        System.out.println("Checking for Crash...");
        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoNoCache(androidSDKPath, widthScreen,
                heightScreen, true, false);
        for (DynGuiComponentVO dynGuiComponent : nodes) {
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("has stopped.")) {
                System.out.println("CRASH");
                crash = true;
            }
        }
        return crash;
    }

    public static boolean checkForCrashEmulator(String appPackage, String mainActivity, String androidSDKPath,
            int widthScreen, int heightScreen, String emuPort, String adbPort, String name) {
        // Check new state and update the list of components
        boolean crash = false;
        System.out.println("Checking for Crash...");
        ArrayList<DynGuiComponentVO> nodes = UiAutoConnector.getScreenInfoEmulator(androidSDKPath, widthScreen,
                heightScreen, true, false, false, emuPort, adbPort, name);
        for (DynGuiComponentVO dynGuiComponent : nodes) {
            if (dynGuiComponent.getText() != null && dynGuiComponent.getText().contains("has stopped.")) {
                System.out.println("CRASH");
                crash = true;
            }
        }
        return crash;
    }

    /**
     * @param node
     * @param list
     * @param heightScreen
     * @param widthScreen
     */
    public static DynGuiComponentVO visitNodes(String currentActivity, BasicTreeNode node,
            ArrayList<DynGuiComponentVO> list, int widthScreen, int heightScreen, boolean all, DynGuiComponentVO parent,
            boolean includeLists, int offset, StringBuilder builder, int typeStrategy) {
        DynGuiComponentVO vo = Utilities.getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen,
                currentActivity, parent);
        // Unique sequence to identify components
        if (sequenceHierarchy == 0) {
            UiAutoConnector.sequenceHierarchy = 0;
            if(builder != null) {
                BasicTreeNode firstComponent = node.getChildren()[0];
                builder.append("<w>" + firstComponent.width + "</w><h>" + firstComponent.height + "</h>");
            }
        }
        vo.setSequenceHierarchy(UiAutoConnector.sequenceHierarchy);
        if (vo.getName().contains("android.widget.DatePicker") && vo.getIdXml().contains("id/datePicker")) {
            vo.setCalendarWindow(true);
        }
        if (parent != null && parent.isCalendarWindow()) {
            vo.setCalendarWindow(true);
        }
        // Fixing bug of UIAutomator temporally for Nexus 7
        if ((vo.getName().contains("android.widget.DatePicker") && vo.getIdXml().contains("id/datePicker"))
                || (vo.getName().contains("android.widget.TimePicker") && vo.getIdXml().contains("id/timePicker"))) {
            if ((vo.getPositionX() == 75 && vo.getPositionY() == 225)
                    || (vo.getPositionX() == 152 && vo.getPositionY() == 323)) {
                // Android 5.x
                offset = 347;
            } else if ((vo.getPositionX() == 52 && vo.getPositionY() == 322)
                    || (vo.getPositionX() == 52 && vo.getPositionY() == 390)) {
                // Android 4.x
                offset = 346;
            } else if ((vo.getPositionX() == 52 && vo.getPositionY() == 366)
                    || (vo.getPositionX() == 52 && vo.getPositionY() == 434)) {
                // Android 4.x
                offset = 302;
            } else if ((vo.getPositionX() == 68 && vo.getPositionY() == 308)
                    || (vo.getPositionX() == 68 && vo.getPositionY() == 376)) {
                // Android 2.x
                offset = 302;
            }
        }
        vo.setOffset(offset);
        if ((vo.isClickable() || vo.isLongClickable() || vo.isCheckable()
        // seek bar is not clickable nor longclickable nor checkeable
                || (vo.getName().equals("android.widget.FrameLayout") || all)
                || vo.getName().equals("android.widget.SeekBar"))
                // discards the list view but includes children
                && list != null && vo.getPositionY() < heightScreen
                && (!vo.getName().equals("android.widget.ListView") || includeLists)) {
            list.add(vo);
        }
        // System.out.println("------------");
        switch (typeStrategy) {
        case GENERIC_STRATEGY:
            if (builder != null) {
                builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
                vo.setParent(parent);
            }
            for (BasicTreeNode child : node.getChildren()) {
                UiAutoConnector.sequenceHierarchy++;

                DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen, all,
                        vo, includeLists, offset, builder, typeStrategy);
                vo.addChild(component);
            }
            if (builder != null) {
                builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
            }
            break;
        case RANDOM_STRATEGY:
            if (builder != null) {
                builder.append(
                        "<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.isFocused() + ">");
                vo.setParent(parent);
            }
            for (BasicTreeNode child : node.getChildren()) {
                UiAutoConnector.sequenceHierarchy++;
                DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen, all,
                        vo, includeLists, offset, builder, typeStrategy);
                vo.addChild(component);
            }
            if (builder != null)
                builder.append(
                        "</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.isFocused() + ">");
            break;
        case LAYOUT_STRATEGY:
            if (builder != null) {
                if (vo.getName() != null && vo.getName().contains("Layout"))
                    builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
                vo.setParent(parent);
            }
            for (BasicTreeNode child : node.getChildren()) {
                UiAutoConnector.sequenceHierarchy++;
                DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen, all,
                        vo, includeLists, offset, builder, typeStrategy);
                vo.addChild(component);
            }
            if (builder != null)
                if (vo.getName() != null && vo.getName().contains("Layout"))
                    builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
            break;
        case ACTIONABLE_COMPONENTS_STRATEGY:
            if (builder != null) {
                if (vo.isClickable() || vo.isLongClickable() || vo.isCheckable()
                        || vo.getName().equals("android.widget.SeekBar") || vo.isScrollable())
                    builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
                vo.setParent(parent);
            }
            for (BasicTreeNode child : node.getChildren()) {
                UiAutoConnector.sequenceHierarchy++;
                DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen, all,
                        vo, includeLists, offset, builder, typeStrategy);
                vo.addChild(component);
            }
            if (builder != null)
                if (vo.isClickable() || vo.isLongClickable() || vo.isCheckable()
                        || vo.getName().equals("android.widget.SeekBar") || vo.isScrollable())
                    builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + ">");
            break;
        case PROPERTIES_COMPONENTS_STRATEGY:
            // We are using the same approach as
            // http://dl.acm.org/citation.cfm?id=2970313
            // use text + content description
            if (builder != null) {
//                if (vo.isClickable() || vo.isLongClickable() || vo.isCheckable() || vo.isScrollable()
//                        || vo.getName().equals("android.widget.SeekBar")) {
                    builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.getText()
                            + "_" + vo.getContentDescription() + ">");
//                }
                vo.setParent(parent);
            }
            for (BasicTreeNode child : node.getChildren()) {
                UiAutoConnector.sequenceHierarchy++;
                DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen, all,
                        vo, includeLists, offset, builder, typeStrategy);
                vo.addChild(component);
            }
            if (builder != null)
//                if (vo.isClickable() || vo.isLongClickable() || vo.isCheckable() || vo.isScrollable()
//                        || vo.getName().equals("android.widget.SeekBar"))
                    builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.getText()
                            + "_" + vo.getContentDescription() + ">");
            break;
        case PROPERTIES_COMPONENTS_DIMENSIONS_STRATEGY:
                String tagName = String.join("_", vo.getName(), vo.getIdXml().replace("id/", ""), getTextcomponent(vo.getName(), vo.getText()),
                        vo.getContentDescription(), String.valueOf(vo.getPositionX()), String.valueOf(vo.getPositionY
                                ()),
                        String.valueOf(vo.getHeight()));

                if (builder != null) {
                    builder.append("<" + tagName + ">");
                    vo.setParent(parent);
                }
                for (BasicTreeNode child : node.getChildren()) {
                    UiAutoConnector.sequenceHierarchy++;
                    DynGuiComponentVO component = visitNodes(currentActivity, child, list, widthScreen, heightScreen,
                            all,
                            vo, includeLists, offset, builder, typeStrategy);
                    vo.addChild(component);
                }
                if (builder != null) {
                    builder.append("</" + tagName + ">");
                }
                break;

        default:
            break;
        }

        return vo;
    }

    /**
     * @param name
     * @param contentDescription
     * @return
     */
    public static String getTextcomponent(String name, String text) {
        String[] notAllowedComponents = { "EditText" };
        boolean anyMatch = Arrays.asList(notAllowedComponents).stream().anyMatch(e -> name.contains(e));
        if (anyMatch) {
            return "";
        }
        return text;
    }

    public static DynGuiComponentVO getScreenInfoHierarchyRandom(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {

        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, device, RANDOM_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyLayout(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort, String name,
            TypeDeviceEnum device) {

        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, device, LAYOUT_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyActionableComponents(String androidSDKPath,
            StringBuilder builder, int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort,
            String name, TypeDeviceEnum device) {

        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, device, ACTIONABLE_COMPONENTS_STRATEGY);
    }

    public static DynGuiComponentVO getScreenInfoHierarchyPropertiesComponents(String androidSDKPath,
            StringBuilder builder, int widthScreen, int heightScreen, boolean cache, String emuPort, String adbPort,
            String name, TypeDeviceEnum device) {

        return getScreenInfoHierarchyGeneric(androidSDKPath, builder, widthScreen, heightScreen, cache, emuPort,
                adbPort, name, device, PROPERTIES_COMPONENTS_STRATEGY);
    }

    public static void visitNodesCrashScope(String currentActivity, BasicTreeNode node,
            ArrayList<DynGuiComponentVO> list, int widthScreen, int heightScreen, boolean all, DynGuiComponentVO parent,
            boolean includeLists) {
        DynGuiComponentVO vo = Utilities.getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen,
                currentActivity, parent);
        if ((vo.isClickable() || vo.isLongClickable() || vo.isCheckable()
        // seek bar is not clickable nor longclickable nor checkeable
                || vo.getName().equals("android.widget.FrameLayout") || vo.getName().equals("android.widget.SeekBar")
                || all)
                // discards the list view but includes children
                && vo.getPositionY() < heightScreen
                && (!vo.getName().equals("android.widget.ListView") || includeLists)) {
            list.add(vo);
        }
        // System.out.println("------------");
        for (BasicTreeNode child : node.getChildren()) {
            // if (vo.getName().equals("android.widget.ListView")) {
            // add every item in the list no matter if it is clickable or
            // not
            // visitNodes(currentActivity, child, list, widthScreen,
            // heightScreen, true, vo, includeLists);
            // } else {
            visitNodesCrashScope(currentActivity, child, list, widthScreen, heightScreen, all, vo, includeLists);
            // }

        }
    }

    public static void getComponent(String androidSDKPath, GUIEventVO guiEventVO, int widthScreen, int heightScreen, String name, String avdPort, String adbPort) {
        getComponent(androidSDKPath, guiEventVO, widthScreen, heightScreen, null, name, avdPort, adbPort);
    }

    /**
     * @param androidSDKPath
     * @param guiEventVO
     */
    public static void getComponent(String androidSDKPath, GUIEventVO guiEventVO, int widthScreen, int heightScreen,
            String device, String name, String avdPort, String adbPort) {
    	ArrayList<DynGuiComponentVO> screenInfo = null;
    	if(guiEventVO.getEventTypeId()==StepByStepEngine.SWIPE) {
        	screenInfo = getScreenInfoEmulator(androidSDKPath, widthScreen,
                    heightScreen, true, false, false, avdPort,
                    adbPort, name);
    		return;
    	}
        String deviceVersion = Utilities.getDeviceVersion(androidSDKPath, device);
        boolean keyboardActive = Utilities.isKeyboardActive(androidSDKPath, device);
        DynGuiComponentVO result = null;
        int area = Integer.MAX_VALUE;
        int orientation = Utilities.getOrientation(androidSDKPath, device);
            

            // at least one component covering the
            boolean isHover = false;
            // check for the biggest component on the screen
            boolean noHoverBigScreen = false;
            if (keyboardActive) {
                // Done in this way in order to speed up the process a little
                // bit and avoid unnecessary calls to UIAutomator
                // screenInfo = getScreenInfoCache(androidSDKPath, widthScreen,
                // heightScreen, false, false, device);
                //screenInfo = getScreenInfoCache(androidSDKPath, widthScreen, heightScreen, true, false, device);
            	
            	screenInfo = getScreenInfoEmulator(androidSDKPath, widthScreen,
                        heightScreen, true, false, false, avdPort,
                        adbPort, name);
            	
                if (screenInfo.size() > 0) {
                    for (DynGuiComponentVO dynGuiComponent : screenInfo.subList(1, screenInfo.size())) {
                        if (dynGuiComponent.getPositionY()
                                + dynGuiComponent.getHeight() > heightScreen
                                && dynGuiComponent.getPositionY()
                                        + dynGuiComponent.getHeight() < heightScreen
                                && dynGuiComponent.getPositionX()
                                        + dynGuiComponent.getWidth() != widthScreen) {
                            isHover = true;
                            break;
                        }
                        if (dynGuiComponent.getWidth() == widthScreen
                                && dynGuiComponent.getHeight() == heightScreen) {
                            noHoverBigScreen = true;
                        }
                    }
                }
            }
            // If there is one "component" covering the screen it means that is
            // not hover
            // (no dialogs cover the entire screen - corner case if this
            // happens)
            if (noHoverBigScreen) {
                isHover = false;
            }
            if (keyboardActive && guiEventVO.getRealInitialY() > 1128 && !isHover
                    && guiEventVO.getRealInitialY() < heightScreen) {
                try {
                    result = ClonerHelper.deepClone(DeviceInfo.NEXUS5X_KEYBOARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (screenInfo == null) {
                    // Keyboard is not active
                    //screenInfo = getScreenInfoCache(androidSDKPath, widthScreen, heightScreen, true, false, device);
                            
                    screenInfo = getScreenInfoEmulator(androidSDKPath, widthScreen,
                            heightScreen, true, false, false, avdPort,
                            adbPort, name);

                }
                if (guiEventVO.getRealInitialY() > heightScreen) {
                    try {
                        result = ClonerHelper.deepClone(getNavBarComponent(guiEventVO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                	DynGuiComponentVO prevComponent = new DynGuiComponentVO(null, 0, 0, 1080, 1920, "test");
                    for (DynGuiComponentVO component : screenInfo) {
                        // System.out.println(component);
                        if (guiEventVO.getRealInitialX() <= component.getPositionX() + component.getWidth() + THRESHOLD
                                && guiEventVO.getRealInitialY() <= component.getPositionY() + component.getHeight()
                                        + THRESHOLD
                                && guiEventVO.getRealInitialX() >= component.getPositionX() - THRESHOLD
                                && guiEventVO.getRealInitialY() >= component.getPositionY() - THRESHOLD
                                && ((area > (component.getWidth() * component.getHeight()))
                                        || (area == (component.getWidth() * component.getHeight())
                                                && !component.getName().endsWith("Layout"))
                                        || (result != null && result.getName().endsWith(".View")
                                                && !component.getName().endsWith(".View")))) {
                            area = component.getWidth() * component.getHeight();
                            
                            if(component.getPositionX()+component.getWidth() <= prevComponent.getPositionX()+prevComponent.getWidth()
                            && component.getPositionY()+component.getHeight() <= prevComponent.getPositionY()+prevComponent.getHeight()) {
                            	result = component;
                            	prevComponent = result;
                            }
                           
                        }
                    }
                }
            }

        if (result != null && result.getText() == null) {
            result.setText("");
        } else if (result == null) {
            result = new DynGuiComponentVO("BACK_MODAL", guiEventVO.getRealInitialX(), guiEventVO.getRealInitialY(), 0,
                    0, "com.android.systemui.statusbar.policy.KeyButtonView", "Bottom left");
        }
        guiEventVO.setHvInfoComponent(result);
    }

    /**
     * @param guiEventVO
     * @return
     */
    private static DynGuiComponentVO getNavBarComponent(GUIEventVO guiEventVO) {
        DynGuiComponentVO[] navBar = DeviceInfo.NEXUS_7_NAV_BAR_PORTRAIT;
        for (int i = 0; i < navBar.length; i++) {
            if (guiEventVO.getRealInitialX() >= navBar[i].getPositionX()
                    && guiEventVO.getRealInitialX() < navBar[i].getPositionX() + navBar[i].getWidth()
                    && guiEventVO.getRealInitialY() >= navBar[i].getPositionY()
                    && guiEventVO.getRealInitialY() < navBar[i].getPositionY() + navBar[i].getHeight()) {
                return navBar[i];
            }
        }
        return null;
    }

    public static String getRandomComponent(String androidSDKPath, int widthScreen, int heightScreen) {
        String result = "";

        ArrayList<DynGuiComponentVO> screenInfo = getScreenInfoNoCache(androidSDKPath, widthScreen, heightScreen, false,
                false);
        Random random = new Random();
        String activity = Utilities.getCurrentActivityImproved(androidSDKPath, null);
        int index = activity.lastIndexOf(".");
        activity = activity.substring(index + 1, activity.length());
        int nextInt = random.nextInt(screenInfo.size() + 1);
        result += activity;

        if (nextInt < screenInfo.size()) {
            result += ".Main.";
            DynGuiComponentVO component = screenInfo.get(nextInt);

            String id = component.getIdXml();
            if (id.trim().isEmpty()) {
                result += "NO_ID#";
            } else {
                result += id + "#";
            }
            result += component.getPositionX() + "_" + component.getPositionY() + "_" + component.getComponentIndex()
                    + ".";

            result += "CLICK." + component.getName().replace(".", "-");
        } else {
            // Keyboard
            if (nextInt == screenInfo.size() && Utilities.isKeyboardActive(androidSDKPath)) {
                result += ".Keyboard." + DeviceInfo.NEXUS5X_KEYBOARD.getIdXml() + ".CLICK."
                        + DeviceInfo.NEXUS5X_KEYBOARD.getName().replace(".", "-");
            } else {
                // Back
                result += ".Main." + DeviceInfo.NEXUS_7_NAV_BAR_PORTRAIT[0].getIdXml() + ".CLICK."
                        + DeviceInfo.NEXUS_7_NAV_BAR_PORTRAIT[0].getName().replace(".", "-");
            }
        }

        return result;
    }

    public static void setComponentAreas(GUIEventVO guiEventVO, String androidSDKPath, int widthScreen,
            int heightScreen) {
        setComponentAreas(guiEventVO, androidSDKPath, widthScreen, heightScreen, null);
    }

    /**
     * @param guiEventVO
     */
    public static void setComponentAreas(GUIEventVO guiEventVO, String androidSDKPath, int widthScreen,
            int heightScreen, String device) {
        ArrayList<DynGuiComponentVO> screenInfo = getScreenInfoCache(androidSDKPath, widthScreen, heightScreen, true,
                true, device);
        int areaEdit = 0;
        int areaList = 0;
        int areaView = 0;
        int areaSelect = 0;
        for (DynGuiComponentVO component : screenInfo) {
            if (component.getName().endsWith("ListView")) {
                areaList += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("EditText")) {
                areaEdit += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("android.widget.TextView")) {
                areaView += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("CheckedTextView")) {
                areaSelect += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("Spinner")) {
                areaEdit += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("CheckBox")) {
                areaEdit += (component.getWidth() * component.getHeight());
            } else if (component.getName().endsWith("SeekBar")) {
                areaEdit += (component.getWidth() * component.getHeight());
            }
        }
        guiEventVO.setAreaEdit(areaEdit);
        guiEventVO.setAreaList(areaList);
        guiEventVO.setAreaView(areaView);
        guiEventVO.setAreaSelect(areaSelect);
    }

    public static String getTypeOfWindow(String uc, String windowName, EntityManager em, Long idExecution) {
        String result = "View ";
        StepDao dao = new StepDao();
        try {
            double areaList = 0;
            double areaEdit = 0;
            double areaView = 0;
            double areaSelect = 0;

            List<Step> steps = dao.findByWindowName(idExecution, windowName, em);
            for (Step step : steps) {
                areaList += step.getAreaList();
                areaEdit += step.getAreaEdit();
                areaView += step.getAreaView();
                areaSelect += step.getAreaSelect();
            }
            areaList = areaList / (steps.size() * 1d);
            areaEdit = areaEdit / (steps.size() * 1d);
            areaView = areaView / (steps.size() * 1d);
            areaSelect = areaSelect / (steps.size() * 1d);
            if (areaSelect > areaEdit && areaSelect > areaView) {
                result = "Select " + uc;
            } else if (areaList > areaEdit && areaList > areaView && areaList > areaSelect) {
                result = "List " + uc;
            } else if (areaEdit > 0) {
                result = "Edit " + uc;
            } else {// if (areaView > areaEdit && areaView > areaList &&
                    // areaView > areaSelect) {
                result = "View " + uc;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param workerLogger
     *            the logger to set
     */
    public void setLogger(Logger workerLogger) {
        this.logger = workerLogger;
    }

    public static GUIEventVO getEventFromComponent(DynGuiComponentVO hvComponentVO, int type) {
        GUIEventVO vo = new GUIEventVO();
        try {
            vo.setHvInfoComponent((DynGuiComponentVO) ClonerHelper.deepClone(hvComponentVO));
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

}
