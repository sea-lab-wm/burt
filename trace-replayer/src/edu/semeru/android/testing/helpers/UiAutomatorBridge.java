package edu.semeru.android.testing.helpers;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import com.android.uiautomator.tree.UiNode;

import edu.semeru.android.testing.helpers.DeviceHelper;
import edu.semeru.android.testing.helpers.DeviceInfo;
import edu.semeru.android.testing.helpers.Utilities;
import edu.semeru.android.core.model.DynGuiComponentVO;
import edu.semeru.android.core.model.GUIEventVO;
import edu.semeru.android.testing.helpers.ClonerHelper;
import edu.semeru.android.testing.helpers.TerminalHelper;


/**
 * 
 * UiAutomatorBridge serves as a means for obtaining hierarchy and screenshot
 * data from an Android device or emulator. An instance of UiAutomatorBridge 
 * should be created for each device/emulator using a DeviceHelper class as input.
 * 
 * Before hierarchy or screenshot information can be obtained, a UiAutomator-Server 
 * needs to be started on the device. This class provides the method startUiAutomatorServer
 * to that effect, but if this does not work the server can be  manually started. 
 * 
 * To gather or save a complete hierarchy or screenshot, use the applicable named 
 * functions. To instead gather the parsed hierarchy as DynGuiComponentVO's, first
 * call updateTree() to refresh the tree stored by this class, then call getScreenInfo[...]()
 * 
 * @author Carlos Bernal
 * @since Sep 28, 2016
 */
public class UiAutomatorBridge {

    private static final int THRESHOLD = 0;
    private static final int UIAUTOMATOR_REMOTE_PORT = 9008;

    private DeviceHelper device;
    private BasicTreeNode tree = null;
    private int uiAutomatorServerPort = UIAUTOMATOR_REMOTE_PORT; 
    private String uiAutomatorServerUrl = "http://localhost";

    /**
     * @param fileName
     * @param device
     */
    public UiAutomatorBridge(DeviceHelper device) {
        super();
        this.device = device;
    }
    
    public UiAutomatorBridge(DeviceHelper device, int serverPort) {
        super();
        this.device = device;
        this.uiAutomatorServerPort = serverPort;
    }

    public void updateTree() {
        tree = getTreeFromXmlServer();
    }
    
    public void updateTreeFromFile(String xmlFile) {
        tree = getTreeFromXmlFile(xmlFile);
    }

    public void getComponent(String androidSDKPath, GUIEventVO guiEventVO, int widthScreen, int heightScreen,
            String device) {
        String deviceVersion = Utilities.getDeviceVersion(androidSDKPath, device);
        boolean keyboardActive = Utilities.isKeyboardActive(androidSDKPath, device);
        DynGuiComponentVO result = null;
        int area = Integer.MAX_VALUE;
        int orientation = Utilities.getOrientation(androidSDKPath, device);
        if (deviceVersion.equals(DeviceInfo.NEXUS_7) && (orientation == 0 || orientation == 2)) {
            ArrayList<DynGuiComponentVO> screenInfo = null;
            // at least one component covering the keyboard
            boolean isHover = false;
            // check for the biggest component on the screen
            boolean noHoverBigScreen = false;
            if (keyboardActive) {
                // Done in this way in order to speed up the process a little
                // bit and avoid unnecessary calls to UIAutomator
                // screenInfo = getScreenInfoCache(androidSDKPath, widthScreen,
                // heightScreen, false, false, device);
    
                // getScreenInfo(String androidSDKPath, int widthScreen, int
                // heightScreen,
                // boolean all, boolean cache, boolean isLists) {
                screenInfo = getScreenInfo(widthScreen, heightScreen, true, false);
                if (screenInfo.size() > 0) {
                    for (DynGuiComponentVO dynGuiComponent : screenInfo.subList(1, screenInfo.size())) {
                        if (dynGuiComponent.getPositionY()
                                + dynGuiComponent.getHeight() > DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD
                                && dynGuiComponent.getPositionY()
                                        + dynGuiComponent.getHeight() < DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT
                                && dynGuiComponent.getPositionX()
                                        + dynGuiComponent.getWidth() != DeviceInfo.NEXUS_7_WIDTH) {
                            isHover = true;
                            break;
                        }
                        if (dynGuiComponent.getWidth() == DeviceInfo.NEXUS_7_WIDTH
                                && dynGuiComponent.getHeight() == DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT) {
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
            if (keyboardActive && guiEventVO.getRealInitialY() > DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD && !isHover
                    && guiEventVO.getRealInitialY() < DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT) {
                try {
                    result = ClonerHelper.deepClone(DeviceInfo.NEXUS5X_KEYBOARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (screenInfo == null) {
                    // Keyboard is not active
                    screenInfo = getScreenInfo(widthScreen, heightScreen, true, false);
                }
                if (guiEventVO.getRealInitialY() > DeviceInfo.NEXUS_7_HEIGHT_PORTRAIT) {
                    try {
                        result = ClonerHelper.deepClone(getNavBarComponent(guiEventVO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
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
                            result = component;
                        }
                    }
                }
            }
        } else {
            System.out.println("Device or orientation not supported");
        }
        if (result != null && result.getText() == null) {
            result.setText("");
        } else if (result == null) {
            result = new DynGuiComponentVO("BACK_MODAL", guiEventVO.getRealInitialX(), guiEventVO.getRealInitialY(), 0,
                    0, "com.android.systemui.statusbar.policy.KeyButtonView", "Bottom left");
        }
        guiEventVO.setHvInfoComponent(result);
    }

    public ArrayList<DynGuiComponentVO> getScreenInfo(int widthScreen, int heightScreen, boolean all, boolean isLists) {
        ArrayList<DynGuiComponentVO> list = new ArrayList<DynGuiComponentVO>();

        String currentActivity = device.getCurrentActivityImproved();

        visitNodes(currentActivity, tree, list, widthScreen, heightScreen, all, null, isLists, 0);
        boolean isOffset = false;
        boolean isCalendarWindow = false;
        int offset = 0;
        for (DynGuiComponentVO vo : list) {
            if (vo.getOffset() != 0) {
                offset = vo.getOffset();
                isOffset = true;
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
    
    
    public ArrayList<DynGuiComponentVO> getScreenInfoNoDevice(int widthScreen, int heightScreen, boolean all, boolean isLists) {
        ArrayList<DynGuiComponentVO> list = new ArrayList<DynGuiComponentVO>();

        String currentActivity = "";

        visitNodes(currentActivity, tree, list, widthScreen, heightScreen, all, null, isLists, 0);
        boolean isOffset = false;
        boolean isCalendarWindow = false;
        int offset = 0;
        for (DynGuiComponentVO vo : list) {
            if (vo.getOffset() != 0) {
                offset = vo.getOffset();
                isOffset = true;
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
    
    public ArrayList<DynGuiComponentVO> getLeafComponentsNoDevice(int widthScreen, int heightScreen) {
        ArrayList<DynGuiComponentVO> list = new ArrayList<DynGuiComponentVO>();
        ArrayList<BasicTreeNode> basicComponents = tree.getLeafNodes();
        DynGuiComponentVO tempDynComponent = new DynGuiComponentVO();
        
        for(BasicTreeNode currBasicComponent: basicComponents){
            
            tempDynComponent = Utilities.getHVComponentFromBasicTreeNode(currBasicComponent, widthScreen, heightScreen, "", null);
            list.add(tempDynComponent);
            
        }
        
        boolean isOffset = false;
        boolean isCalendarWindow = false;
        int offset = 0;
        for (DynGuiComponentVO vo : list) {
            if (vo.getOffset() != 0) {
                offset = vo.getOffset();
                isOffset = true;
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
    
    public DynGuiComponentVO getScreenInfoHierarchyNoDevice(StringBuilder builder, int width, int height){
        String currentActivity = "";
        DynGuiComponentVO parent = null;
        return visitNodesHierarchy(currentActivity, tree, width, height, parent, builder, false);
    }

    public DynGuiComponentVO getScreenInfoHierarchy(String androidSDKPath, StringBuilder builder,
            int widthScreen, int heightScreen) {
        DynGuiComponentVO parent = null;
        String currentActivity = device.getCurrentActivityImproved();
        return visitNodesHierarchy(currentActivity, tree, widthScreen, heightScreen, parent, builder);
    
    }

    public DynGuiComponentVO getScreenInfoHierarchyRandom(String androidSDKPath, StringBuilder builder, int widthScreen,
            int heightScreen) {
        DynGuiComponentVO parent = null;
        String currentActivity = device.getCurrentActivityImproved();
    
        return visitNodesHierarchyRandom(currentActivity, tree, widthScreen, heightScreen, parent, builder);
    
    }

    private static void visitNodes(String currentActivity, BasicTreeNode node, ArrayList<DynGuiComponentVO> list,
            int widthScreen, int heightScreen, boolean all, DynGuiComponentVO parent, boolean includeLists,
            int offset) {
        DynGuiComponentVO vo = Utilities.getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen,
                currentActivity, parent);
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
                && vo.getPositionY() < heightScreen
                && (!vo.getName().equals("android.widget.ListView") || includeLists)) {
            list.add(vo);
        }
        for (BasicTreeNode child : node.getChildren()) {
            visitNodes(currentActivity, child, list, widthScreen, heightScreen, all, vo, includeLists, offset);
        }
    }

    private DynGuiComponentVO visitNodesHierarchy(String currentActivity, BasicTreeNode node, int widthScreen, int heightScreen,
            DynGuiComponentVO parent, StringBuilder builder ) {
        return visitNodesHierarchy(currentActivity, node, widthScreen, heightScreen, parent, builder, true);
    }
    
    private DynGuiComponentVO visitNodesHierarchy(String currentActivity, BasicTreeNode node, int widthScreen, int heightScreen,
            DynGuiComponentVO parent, StringBuilder builder, boolean propagateText) {
        DynGuiComponentVO vo = Utilities.getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen,
                currentActivity, parent, propagateText);
        builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.getText() + ">");
        vo.setParent(parent);
        for (BasicTreeNode child : node.getChildren()) {
            DynGuiComponentVO component = visitNodesHierarchy(currentActivity, child, widthScreen, heightScreen, vo, builder, propagateText);
            vo.addChild(component);
        }
        builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.getText() + ">");
        return vo;
    }

    private static DynGuiComponentVO visitNodesHierarchyRandom(String currentActivity, BasicTreeNode node, int widthScreen,
            int heightScreen, DynGuiComponentVO parent, StringBuilder builder) {
        DynGuiComponentVO vo = Utilities.getHVComponentFromBasicTreeNode(node, widthScreen, heightScreen,
                currentActivity, parent);
        // System.out.println("------------");
        builder.append("<" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.isFocused() + ">");
        vo.setParent(parent);
        for (BasicTreeNode child : node.getChildren()) {
            DynGuiComponentVO component = visitNodesHierarchyRandom(currentActivity, child, widthScreen, heightScreen, vo,
                    builder);
            vo.addChild(component);
        }
        builder.append("</" + vo.getName() + "_" + vo.getIdXml().replace("id/", "") + "_" + vo.isFocused() + ">");
        return vo;
    }

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
    
    /**
     * Send command to UiAutomator-Server. 
     * Effectively visits url given by: loc:port/cmd?filepath
     * ex. http://localhost:45555/dumpuihierarchy?filepath=sdcard/ui_dump.xml
     * @param host 
     * @param port 
     * @param cmd as of 3/17/2017 one of dumpuihierarchy, screenshot, status, stop
     * @param filepath path on device to store hierarchy/screenshot
     * @return
     */
    public String sendCommandToUiAutomator(String host, int port, String cmd, String filepath) {
        if (host == null) {
            host = uiAutomatorServerUrl;
        }
        if (port < 0) {
            port = uiAutomatorServerPort;
        }
        filepath = (filepath == null ? "" : "?filepath=" + filepath);
        try {
            URL url = new URL(host + ":" + port + "/" + cmd + filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns device hierarchy as a string using UiAutomator-Server
     * @return
     */
    public String getScreenHierarchyString() {
        String hierarchy = sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "dumpuihierarchy", null);
        if (hierarchy == null || !hierarchy.startsWith("<")) {
            return null;
        }
        return hierarchy;
    }
    
    /**
     * Saves device hierarchy on the device at filepath
     * @param filepath location on the device to store the hierarchy file
     * @return true if success, false if failure
     */
    public boolean saveScreenHierarchyToDeviceFile(String filepath) {
        return sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "dumpuihierarchy", filepath).indexOf("Success") != -1;
    }
    
    /**
     * Saves device hierarchy on the local machine 
     * @param filepath location on the local machine to store the hierarchy file
     * @return true if success, false if failure
     */
    public boolean saveScreenHierarchyToLocalFile(String filepath) {
        String remotePath = "/sdcard/ui_dump.xml";
        if (sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "dumpuihierarchy", remotePath).indexOf("Success") == -1) {
            return false;
        }
        device.pullFilesFromDeviceFolder(remotePath, filepath);
        return new File(filepath).exists();
    }
    
    /**
     * Returns the device hierarchy as a tree of BasicTreeNode(s)
     * @return root BasicTreeNode of the parsed device hierarchy
     */
    private BasicTreeNode getTreeFromXmlServer() {
        String hierarchy = null;
        hierarchy = getScreenHierarchyString();
        int i = 0;
        while(StringUtils.isEmpty(hierarchy) && i <= 10){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Unable to Retrieve Screen Hierarchy for emulator on port " + device.getDevicePort() + "... Trying Again");
            hierarchy = getScreenHierarchyString();
            i++;
        }
        
        UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
        InputStream stream = new ByteArrayInputStream(hierarchy.getBytes());
        BasicTreeNode treeFromXml = loader.parseXml(stream);
        return treeFromXml;
    }
    
    /**
     * Returns the device hierarchy as a tree of BasicTreeNode(s)
     * @return root BasicTreeNode of the parsed device hierarchy
     */
    private BasicTreeNode getTreeFromXmlFile(String xmlFile) {
        
        UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
        BasicTreeNode treeFromXml = loader.parseXml(xmlFile);
        return treeFromXml;
        
    }
    
    /**
     * Returns a screenshot of the device as a base64 encoded string
     * @return base64 encoded screenshot if success, null if failure
     */
    public String getScreenshotString() {
        String screen = sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "screenshot", null);
        if (screen == null || screen.startsWith("Invalid") || screen.startsWith("Error")) {
            return null;
        }
        return screen;
    }
    
    /**
     * Saves a screenshot of the device to a file on the device
     * @param filepath location on the device to store the screenshot file
     * @return true if success, false if failure
     */
    public boolean saveScreenshotToDeviceFile(String filepath) {
        return sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "screenshot", filepath).indexOf("Success") != -1;
    }
    
    /**
     * Saves a screenshot of the device to a file on the local machine
     * @param filepath location on the local machine to store the screenshot file
     * @return true if success, false if failure
     */
    public boolean saveScreenshotToLocalFile(String filepath) {
        String remotePath = "/sdcard/screen.png";
        if (sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "screenshot", remotePath).indexOf("Success") == -1) {
            return false;
        }
        device.pullFilesFromDeviceFolder(remotePath, filepath);
        return new File(filepath).exists();
    }
    
    /** 
     * Returns true if UiAutomator-Server is connected (accessible at host:port/status)
     * @return true if connected to server, false otherwise
     */
    public boolean connectedToUiAutomatorServer() {
        String res = sendCommandToUiAutomator(uiAutomatorServerUrl, uiAutomatorServerPort, "status", null);
        return res != null && res.startsWith("ALIVE");
    }
    
    /**
     * Returns true if server successfully started, false otherwise
     * @param url optional, hostname to look for the UiAutomator-Server
     * @param port optional, local port to run UiAutomator-Server on. Uses next available port to device port if not given.
     * @param apkPath optional, path to UiAutomator-Server apk; if given will reinstall apk
     * @param testApkPath optional, path to UiAutomator-Server testApk; if given will reinstall apk
     * @return true if server successfully started, false otherwise
     */
    public boolean startUiAutomatorServer(String url, int port, String apkPath, String testApkPath) {
        if (connectedToUiAutomatorServer()) {
            stopUiAutomatorServer();
        }
        String adb = device.getAndroidSDKPath() + File.separator + "platform-tools" + File.separator + "adb";
        String terminal = "";
        String deviceCommand = device.getDeviceCommand();
        uiAutomatorServerPort = port < 0 ? Integer.valueOf(device.getDevicePort()) + 1 : port;
        uiAutomatorServerUrl = url == null ? "http://localhost" : url;

        // Install apks
        if (apkPath != null) {
            device.installApp(apkPath, null);
        }
        if (testApkPath != null) {
            device.installApp(testApkPath, null);
        }
        
        // Forward next available port
        --uiAutomatorServerPort;
        do {
            terminal = TerminalHelper.executeCommand(adb + " -P " + deviceCommand + " forward --no-rebind tcp:" + (++uiAutomatorServerPort) + " tcp:" + UIAUTOMATOR_REMOTE_PORT);
            System.out.println(terminal);
        } while(terminal != null && terminal.startsWith("error"));
        if (uiAutomatorServerPort != port) {
            System.err.println("Starting UiAutomatorServer on port " + uiAutomatorServerPort);
        }
        
        // kill old process: can only connect to one instance of UiAutomation at a time
        terminal = TerminalHelper.executeCommand(adb + " -P " + deviceCommand + " shell \"su -c killall com.uiautomator.lite\"");
        
        // start server 
        terminal = TerminalHelper.executeCommand(adb + " -P " + deviceCommand + " shell am instrument -w -r com.uiautomator.lite.test/android.support.test.runner.AndroidJUnitRunner &");
        try { 
            Thread.sleep(200);
        } catch (Exception e) {}
        return connectedToUiAutomatorServer();
    }
    
    /**
     * Convenience function for default values. 
     * Starts UiAutomator-Server at http://localhost:defaultPort 
     * -- where defaultPort is the next available port to the device port
     * @return true if server successfully started, false otherwise
     */
    public boolean startUiAutomatorServer() {
        return startUiAutomatorServer(null, -1, null, null);
    }
    
    /**
     * Convenience function for default values. 
     * Starts UiAutomator-Server at http://localhost:portNumber
     * @param portNumber
     * @return true if server successfully started, false otherwise
     */
    public boolean startUiAutomatorServer(int portNumber) {
        return startUiAutomatorServer(null, portNumber, null, null);
    }
    
    /**
     * Stops the UiAutomator-Server. Equivalent to accessing host:port/stop
     */
    public void stopUiAutomatorServer() {
        sendCommandToUiAutomator(null, -1, "stop", null);
    }
    
    public DeviceHelper getDevice() {
        return device;
    }

    public void setDevice(DeviceHelper device) {
        this.device = device;
    }

}

