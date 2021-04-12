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
 * Constants.java
 * 
 * Created on Jun 20, 2014, 12:52:02 AM
 * 
 */
package edu.semeru.android.testing.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.semeru.android.core.entity.model.*;
import edu.semeru.android.core.entity.model.Class;
import edu.semeru.android.core.model.XmlFile;



/**
 * {Insert class description here}
 * 
 * @author Carlos Bernal
 * @since Jun 20, 2014
 */
public class Constants {
    public static final String DB_SCHEMA = "APK-analyzer";

    public static Boolean hasPopup = false;
    public static Boolean hasDialog = false;
    public static ArrayList<String> ACTIONS = new ArrayList<String>();
    
    // Properties
    public static final String PROPERTIES_FILE = "config.properties";
    public static final String PROPERTIES_PATH_TOOLS = "path_tools";
    public static final String PROPERTIES_PATH_OUTPUT = "path_output";
    public static final String PROPERTIES_PATH_JAVA = "path_java";
    // Paths
    public static final String PATH_RES = "res/";
    public static final String PATH_SRC_MAIN = "src_main/";
    public static final String PATH_SRC_TPL = "src_tpl/";
    public static final String MANIFEST = "AndroidManifest.xml";
    public static final String PATH_TEMP = "temp/";
    // Classes and Methods
    public static List<Class> CLASSES = new ArrayList<Class>();
    public static List<Method> METHODS = new ArrayList<Method>();
    public static ArrayList<String> METHOD_NAMES = new ArrayList<String>();
    public static ArrayList<String> GUI_INTS = new ArrayList<String>();
    // Action_type
    public static ActionType CLICK = new ActionType(1l, "CLICK");
    public static ActionType LONG_CLICK = new ActionType(2l, "LONG_CLICK");
    public static ActionType TYPE = new ActionType(3l, "TYPE");
    public static ActionType SWIPE = new ActionType(4l, "SWIPE");
    public static ActionType ITEM_CLICK = new ActionType(5l, "CLICK");
    public static ActionType ITEM_LONG_CLICK = new ActionType(6l, "LONG_CLICK");
    public static ActionType SCROLL_SWIPE = new ActionType(7l, "SWIPE");
    public static ActionType MENU_ITEM_CLICK = new ActionType(8l, "CLICK");
    public static ActionType NONE = new ActionType(8l, "NONE");
    
    // Event Keys
    public static long EV_SYN = 0;
    public static long EV_NAV = 2;
    public static long EV_KEY = 1;
    public static long EV_ABS = 3;
    public static long ABS_X = 0;
    public static long ABS_Y = 1;
    public static long ABS_MT_POSITION_X = 53;
    public static long ABS_MT_POSITION_Y = 54;
    public static long ABS_MT_TRACKING_ID = 57;
    public static long ABS_MT_PRESSURE = 58;
    public static long SYN_REPORT = 0;
    public static long BACK = 6;
    public static long BTN_TOUCH = 330;
    
    //Gui Instances Temp
    
    public static List<GuiComponent> GUIS = new ArrayList<GuiComponent>();
    public static List<GuiComponent> ALL_GUIS = new ArrayList<GuiComponent>();
    public static List<XmlFile> XMLS = new ArrayList<XmlFile>();
    public static ArrayList<Class> Clazzez = new ArrayList<Class>();
    public static ArrayList<String> Activitiez = new ArrayList<String>();
    public static ArrayList<String> filez = new ArrayList<String>();
    public static ArrayList<Class> clazzez = new ArrayList<Class>();
    public static ArrayList<LineNums> linez = new ArrayList<LineNums>();
    public static ArrayList<Integer> ll = new ArrayList<Integer>();
    /**
     * @param methods
     * @param myClass
     */
    private static void setMethods(List<Method> methods, Class myClass) {
	for (Method method : methods) {
	    method.setClazz(myClass);
	}
    }

    // GuiComponentTypes
    public static HashMap<String, GuiComponentType> getGuiComponentTypes() {
	HashMap<String, GuiComponentType> list = new HashMap<String, GuiComponentType>();

	list.put("AbsListView", new GuiComponentType(1l, "AbsListView", ""));
	list.put("AbsoluteLayout", new GuiComponentType(2l, "AbsoluteLayout", ""));
	list.put("AbsSeekBar", new GuiComponentType(3l, "AbsSeekBar", ""));
	list.put("AbsSpinner", new GuiComponentType(4l, "AbsSpinner", ""));
	list.put("AdapterView", new GuiComponentType(5l, "AdapterView", ""));
	list.put("AdapterViewAnimator", new GuiComponentType(6l, "AdapterViewAnimator", ""));
	list.put("AdapterViewFlipper", new GuiComponentType(7l, "AdapterViewFlipper", ""));
	list.put("AnalogClock", new GuiComponentType(8l, "AnalogClock", ""));
	list.put("AppWidgetHostView", new GuiComponentType(9l, "AppWidgetHostView", ""));
	list.put("AutoCompleteTextView", new GuiComponentType(10l, "AutoCompleteTextView", ""));
	list.put("Button", new GuiComponentType(11l, "Button", ""));
	list.put("CalendarView", new GuiComponentType(12l, "CalendarView", ""));
	list.put("CheckBox", new GuiComponentType(13l, "CheckBox", ""));
	list.put("CheckedTextView", new GuiComponentType(14l, "CheckedTextView", ""));
	list.put("Chronometer", new GuiComponentType(15l, "Chronometer", ""));
	list.put("CompoundButton", new GuiComponentType(16l, "CompoundButton", ""));
	list.put("ContentLoadingProgressBar", new GuiComponentType(17l, "ContentLoadingProgressBar", ""));
	list.put("DatePicker", new GuiComponentType(18l, "DatePicker", ""));
	list.put("DialerFilter", new GuiComponentType(19l, "DialerFilter", ""));
	list.put("DigitalClock", new GuiComponentType(20l, "DigitalClock", ""));
	list.put("DrawerLayout", new GuiComponentType(21l, "DrawerLayout", ""));
	list.put("EditText", new GuiComponentType(22l, "EditText", ""));
	list.put("ExpandableListView", new GuiComponentType(23l, "ExpandableListView", ""));
	list.put("ExtractEditText", new GuiComponentType(24l, "ExtractEditText", ""));
	list.put("FragmentBreadCrumbs", new GuiComponentType(25l, "FragmentBreadCrumbs", ""));
	list.put("FragmentTabHost", new GuiComponentType(26l, "FragmentTabHost", ""));
	list.put("FrameLayout", new GuiComponentType(27l, "FrameLayout", ""));
	list.put("Gallery", new GuiComponentType(28l, "Gallery", ""));
	list.put("GestureOverlayView", new GuiComponentType(29l, "GestureOverlayView", ""));
	list.put("GLSurfaceView", new GuiComponentType(30l, "GLSurfaceView", ""));
	list.put("GridLayout", new GuiComponentType(31l, "GridLayout", ""));
	list.put("GridView", new GuiComponentType(32l, "GridView", ""));
	list.put("HorizontalScrollView", new GuiComponentType(33l, "HorizontalScrollView", ""));
	list.put("ImageButton", new GuiComponentType(34l, "ImageButton", ""));
	list.put("ImageSwitcher", new GuiComponentType(35l, "ImageSwitcher", ""));
	list.put("ImageView", new GuiComponentType(36l, "ImageView", ""));
	list.put("KeyboardView", new GuiComponentType(37l, "KeyboardView", ""));
	list.put("LinearLayout", new GuiComponentType(38l, "LinearLayout", ""));
	list.put("ListView", new GuiComponentType(39l, "ListView", ""));
	list.put("MediaController", new GuiComponentType(40l, "MediaController", ""));
	list.put("MediaRouteButton", new GuiComponentType(41l, "MediaRouteButton", ""));
	list.put("MultiAutoCompleteTextView", new GuiComponentType(42l, "MultiAutoCompleteTextView", ""));
	list.put("NumberPicker", new GuiComponentType(43l, "NumberPicker", ""));
	list.put("PagerTabStrip", new GuiComponentType(44l, "PagerTabStrip", ""));
	list.put("PagerTitleStrip", new GuiComponentType(45l, "PagerTitleStrip", ""));
	list.put("ProgressBar", new GuiComponentType(46l, "ProgressBar", ""));
	list.put("QuickContactBadge", new GuiComponentType(47l, "QuickContactBadge", ""));
	list.put("RadioButton", new GuiComponentType(48l, "RadioButton", ""));
	list.put("RadioGroup", new GuiComponentType(49l, "RadioGroup", ""));
	list.put("RatingBar", new GuiComponentType(50l, "RatingBar", ""));
	list.put("RelativeLayout", new GuiComponentType(51l, "RelativeLayout", ""));
	list.put("ScrollView", new GuiComponentType(52l, "ScrollView", ""));
	list.put("SearchView", new GuiComponentType(53l, "SearchView", ""));
	list.put("SeekBar", new GuiComponentType(54l, "SeekBar", ""));
	list.put("SlidingDrawer", new GuiComponentType(55l, "SlidingDrawer", ""));
	list.put("SlidingPaneLayout", new GuiComponentType(56l, "SlidingPaneLayout", ""));
	list.put("Space", new GuiComponentType(57l, "Space", ""));
	list.put("Spinner", new GuiComponentType(58l, "Spinner", ""));
	list.put("StackView", new GuiComponentType(59l, "StackView", ""));
	list.put("SurfaceView", new GuiComponentType(60l, "SurfaceView", ""));
	list.put("SwipeRefreshLayout", new GuiComponentType(61l, "SwipeRefreshLayout", ""));
	list.put("Switch", new GuiComponentType(62l, "Switch", ""));
	list.put("TabHost", new GuiComponentType(63l, "TabHost", ""));
	list.put("TableRow", new GuiComponentType(64l, "TableRow", ""));
	list.put("TabWidget", new GuiComponentType(65l, "TabWidget", ""));
	list.put("TextClock", new GuiComponentType(66l, "TextClock", ""));
	list.put("TextSwitcher", new GuiComponentType(67l, "TextSwitcher", ""));
	list.put("TextureView", new GuiComponentType(68l, "TextureView", ""));
	list.put("TextView", new GuiComponentType(69l, "TextView", ""));
	list.put("TimePicker", new GuiComponentType(70l, "TimePicker", ""));
	list.put("ToggleButton", new GuiComponentType(71l, "ToggleButton", ""));
	list.put("TwoLineListItem", new GuiComponentType(72l, "TwoLineListItem", ""));
	list.put("VideoView", new GuiComponentType(73l, "VideoView", ""));
	list.put("ViewAnimator", new GuiComponentType(74l, "ViewAnimator", ""));
	list.put("ViewFlipper", new GuiComponentType(75l, "ViewFlipper", ""));
	list.put("ViewGroup", new GuiComponentType(76l, "ViewGroup", ""));
	list.put("ViewPager", new GuiComponentType(77l, "ViewPager", ""));
	list.put("ViewStub", new GuiComponentType(78l, "ViewStub", ""));
	list.put("ViewSwitcher", new GuiComponentType(79l, "ViewSwitcher", ""));
	list.put("WebView", new GuiComponentType(80l, "WebView", ""));
	list.put("ZoomButton", new GuiComponentType(81l, "ZoomButton", ""));
	list.put("ZoomControls", new GuiComponentType(82l, "ZoomControls", ""));
	return list;
    }

}
