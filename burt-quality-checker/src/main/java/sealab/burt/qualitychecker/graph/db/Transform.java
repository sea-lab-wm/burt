package sealab.burt.qualitychecker.graph.db; /**
 *****************************************************************************
 * Copyright (c) 2018, SEMERU
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

import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import sealab.burt.qualitychecker.graph.AppGuiComponent;
import sealab.burt.qualitychecker.graph.Appl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class Transform {
    /**
     * @param pComponent
     * @param pComponents
     * @return
     * @throws Exception
     */
    public static AppGuiComponent getGuiComponent(DynGuiComponent pComponent, HashMap<Long, AppGuiComponent>
            pComponents) {
        if (pComponent == null) {
            return null;
        }

        AppGuiComponent component = new AppGuiComponent();
        component.setDbId(pComponent.getId());
        component.setActivity(pComponent.getActivity());
        component.setCheckable(pComponent.isCheckable());
        component.setChecked(pComponent.isChecked());
        component.setClickable(pComponent.isClickable());
        component.setContentDescription(pComponent.getContentDescription());
        component.setEnabled(pComponent.isEnabled());
        component.setFocusable(pComponent.isFocusable());
        component.setFocused(pComponent.isFocused());
        component.setHeight(pComponent.getHeight());
        component.setIdXml(pComponent.getIdXml());
        component.setIndex(pComponent.getComponentIndex());
        component.setLongClickable(pComponent.isLongClickable());
        if (pComponents != null && pComponent.getParent() != null) {
            AppGuiComponent parent = pComponents.get(pComponent.getParent().getId());
            // Transform the parent
            parent = (parent == null) ? getGuiComponent(pComponent.getParent(), pComponents) : parent;
            // Set relation
            component.setParent(parent);
            parent.getChildren().add(component);
        }
        component.setPassword(pComponent.isPassword());
        component.setRelativeLocation(pComponent.getRelativeLocation());
        component.setScrollable(pComponent.isScrollable());
        component.setSelected(pComponent.isSelected());
        component.setText(pComponent.getText());
        component.setTotalIndex(pComponent.getComponentTotalIndex());
        component.setType(pComponent.getName());
        component.setWidth(pComponent.getWidth());
        component.setX(pComponent.getPositionX());
        component.setY(pComponent.getPositionY());
        component.setCurrentWindow(pComponent.getCurrentWindow());
        if (pComponent.getScreen() != null)
            component.setScreenId(pComponent.getScreen().getId());

        // Add component to the map
        if (pComponents != null) {
            pComponents.put(pComponent.getId(), component);
        }
        return component;
    }

    public static List<AppGuiComponent> getGuiComponents(List<DynGuiComponent> components) {
        List<AppGuiComponent> result = new ArrayList<>();
        HashMap<Long, AppGuiComponent> pComponents = new HashMap<>();
        for (DynGuiComponent dynGuiComponent : components) {
            result.add(getGuiComponent(dynGuiComponent, pComponents));
        }
        return result;
    }

    public static Appl getAppl(App app) {

        if (app == null) return null;

        Appl appl = new Appl();
        appl.setId(app.getId());
        appl.setName(app.getName());
        appl.setApkPath(app.getApkPath());
        appl.setMainActivity(app.getMainActivity());
        appl.setVersion(app.getVersion());
        appl.setPackageName(app.getPackageName());
        return appl;
    }
}
