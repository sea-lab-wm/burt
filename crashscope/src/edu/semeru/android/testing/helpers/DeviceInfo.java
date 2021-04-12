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
 * DeviceInfo.java
 * 
 * Created on Feb 1, 2015, 12:06:45 PM
 */
package edu.semeru.android.testing.helpers;

import edu.semeru.android.core.model.DynGuiComponentVO;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Feb 1, 2015
 */
public class DeviceInfo {

//    public static final String NEXUS_7 = "HUAWEI CRR-L09";
//    public static final int NEXUS_7_WIDTH = 1080;
//    public static final int NEXUS_7_HEIGHT = 1920;
//    public static final int NEXUS_7_HEIGHT_PORTRAIT = 1812;
//    public static final int NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD = 1164;
//    public static final int NEXUS_7_HEIGHT_LANDSCAPE = 1104;
//    public static final DynGuiComponentVO[] NEXUS_7_NAV_BAR_PORTRAIT = {
//            new DynGuiComponentVO("id/back", 131, 1812, 210, 126, "com.android.systemui.statusbar.policy.KeyButtonView",
//                    "Bottom left"),
//            new DynGuiComponentVO("id/home", 435, 1812, 210, 126, "com.android.systemui.statusbar.policy.KeyButtonView",
//                    "Bottom"),
//            new DynGuiComponentVO("id/recent_apps", 739, 1812, 210, 126,
//                    "com.android.systemui.statusbar.policy.KeyButtonView", "Bottom right"),
//            new DynGuiComponentVO("id/menu", 1100, 1812, 126, 126, "com.android.systemui.statusbar.policy.KeyButtonView",
//                    "Bottom right") };
//    public static final DynGuiComponentVO[] NEXUS_7_NAV_BAR_LANDSCAPE = {};
//    public static final DynGuiComponentVO NEXUS7_KEYBOARD = new DynGuiComponentVO("id/keyboard_view", 0,
//            NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD, NEXUS_7_WIDTH,
//            NEXUS_7_HEIGHT_PORTRAIT - NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD,
//            "com.android.inputmethod.keyboard.MainKeyboardView", "Bottom");
    public static final String NEXUS_7 = "Nexus 7";
    public static final int NEXUS_7_WIDTH = 1200;
    public static final int NEXUS_7_HEIGHT = 1920;
    public static final int NEXUS_7_HEIGHT_PORTRAIT = 1824;
    public static final int NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD = 1220;
    public static final int NEXUS_7_HEIGHT_LANDSCAPE = 1104;
    public static final DynGuiComponentVO[] NEXUS_7_NAV_BAR_PORTRAIT = {
            new DynGuiComponentVO("id/back", 216, 1824, 256, 96, "com.android.systemui.statusbar.policy.KeyButtonView",
                    "Bottom left"),
            new DynGuiComponentVO("id/home", 472, 1824, 256, 96, "com.android.systemui.statusbar.policy.KeyButtonView",
                    "Bottom"),
            new DynGuiComponentVO("id/recent_apps", 728, 1824, 256, 96,
                    "com.android.systemui.statusbar.policy.KeyButtonView", "Bottom right"),
            new DynGuiComponentVO("id/menu", 1100, 1824, 96, 96, "com.android.systemui.statusbar.policy.KeyButtonView",
                    "Bottom right") };
    public static final DynGuiComponentVO[] NEXUS_7_NAV_BAR_LANDSCAPE = {};
    public static final DynGuiComponentVO NEXUS5X_KEYBOARD = new DynGuiComponentVO("id/keyboard_view", 0,
            NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD, NEXUS_7_WIDTH,
            NEXUS_7_HEIGHT_PORTRAIT - NEXUS_7_HEIGHT_PORTRAIT_KEYBOARD,
            "com.android.inputmethod.keyboard.MainKeyboardView", "Bottom");

}
