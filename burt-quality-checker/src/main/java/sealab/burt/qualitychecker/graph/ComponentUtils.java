package sealab.burt.qualitychecker.graph;

import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;

public class ComponentUtils {

    public static boolean equals(DynGuiComponent component1, DynGuiComponent component2) {
        return equalsNoWidth(component1, component2) && component1.getWidth() == component2.getWidth();
    }

    public static boolean equalsNoWidth(DynGuiComponent component1, DynGuiComponent component2) {
        return equalsNoComponentIdx(component1, component2)
                && component1.getComponentIndex() == component2.getComponentIndex();
    }

    public static boolean equalsNoComponentIdx(DynGuiComponent component1, DynGuiComponent component2) {
        return component1.getPositionX() == component2.getPositionX()
                && component1.getPositionY() == component2.getPositionY()
                && component1.getHeight() == component2.getHeight()
                && component1.getName().equals(component2.getName())
                && ((component1.getIdXml() == null ? "" : component1.getIdXml()).equals(
                component2.getIdXml() == null ? "" : component2.getIdXml()));
    }


    public static boolean equalsNoDimensions(DynGuiComponent component1, DynGuiComponent component2) {
        return ((component1.getText() == null ? "" : component1.getText()).equals(
                component2.getText() == null ? "" : component2.getText()))
                && component1.getName().equals(component2.getName())
                && ((component1.getIdXml() == null ? "" : component1.getIdXml()).equals(
                component2.getIdXml() == null ? "" : component2.getIdXml()));
    }


    public static boolean equalsNoDimensions(AppGuiComponent component1, AppGuiComponent component2) {
        return ((component1.getText() == null ? "" : component1.getText()).equals(
                component2.getText() == null ? "" : component2.getText()))
                && component1.getType().equals(component2.getType())
                && ((component1.getIdXml() == null ? "" : component1.getIdXml()).equals(
                component2.getIdXml() == null ? "" : component2.getIdXml()));
    }

}
