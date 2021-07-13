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

import edu.semeru.android.core.model.GUIEventVO;

public class EventsFormatter {

    public static String format4CollectorFile(GUIEventVO vo) {
        String line = " Activity: " + vo.getActivity() + ", ObjectIndex: "
                + vo.getHvInfoComponent().getComponentIndex() + ", Object: " + vo.getHvInfoComponent().getIdText()
                + "#" + vo.getHvInfoComponent().getPositionX() + "_" + vo.getHvInfoComponent().getPositionY() + "_"
                + vo.getHvInfoComponent().getComponentIndex() + ", ObjectText: " + vo.getHvInfoComponent().getText()
                + ", ObjectPositionX: " + vo.getHvInfoComponent().getPositionX() + ", ObjectPostionY: "
                + vo.getHvInfoComponent().getPositionY() + ", ObjectClass: " + vo.getHvInfoComponent().getName()
                + ", Action: " + vo.getEventLabel()
                + (vo.getEventTypeId() == StepByStepEngine.SWIPE ? "-" + vo.getDirection() : "") + " ";
        if (vo.getEventTypeId() == StepByStepEngine.SWIPE) {
            line += "(" + vo.getRealInitialX() + "," + vo.getRealInitialY() + ")-->";
        }

        line += "(" + vo.getRealFinalX() + "," + vo.getRealFinalY() + ")";

        return line;
    }

    public static String format4Steps(GUIEventVO vo) {
        String result = "";
        String activity = vo.getActivity();
        String id = vo.getHvInfoComponent() != null && vo.getHvInfoComponent().getIdXml() != null ? vo
                .getHvInfoComponent().getIdXml() : "NO_ID";
        String window = (id.equals("id/keyboard_view") ? "Keyboard" : "Main");
        id += "#" + vo.getHvInfoComponent().getPositionX() + "_" + vo.getHvInfoComponent().getPositionY() + "_"
                + vo.getHvInfoComponent().getComponentIndex();
        // activity = activity.substring(activity.lastIndexOf(".") + 1,
        // activity.indexOf("("));
        String action = vo.getEventLabel()
                + (vo.getEventTypeId() == StepByStepEngine.SWIPE ? "-" + vo.getDirection() : "");
        String clazz = vo.getHvInfoComponent().getName();
        if (clazz.contains(".")) {
            clazz = clazz.replace(".", "-");
        }

        if (window.equals("Keyboard")) {
            clazz = vo.getText();
        }
        result = activity + "." + window + "." + id + "." + action + "." + clazz + "."
                + vo.getHvInfoComponent().isItemList();
        return result;
    }
}
