package sealab.burt.qualitychecker.graph.db;

import sealab.burt.nlparser.euler.actions.DeviceActions;
import seers.appcore.utils.JavaUtils;

import java.util.Set;

public class DeviceUtils {

    public static boolean isSwipe(Integer event) {
        return event == DeviceActions.SWIPE_DOWN || event == DeviceActions.SWIPE_LEFT || event == DeviceActions.SWIPE_RIGHT
                || event == DeviceActions.SWIPE_UP;
    }

    public static boolean isClick(Integer event) {
        return DeviceActions.LONG_CLICK == event || DeviceActions.CLICK == event;
    }

    public static boolean isType(Integer event) {
        return DeviceActions.TYPE == event;
    }
    
    public static boolean isAnyInputType(Integer event) {
        return DeviceActions.TYPE == event || DeviceActions.CLICK_TYPE == event;
    }

    public static boolean isDeleteText(Integer event) {
        return DeviceActions.DELETE_TEXT == event;
    }

    public static boolean isAnyType(Integer event) {
        return isType(event) || isDeleteText(event);
    }

    public static boolean isOpenApp(Integer event) {
        return DeviceActions.OPEN_APP == event;
    }

    public static boolean isCloseApp(Integer event) {
        return DeviceActions.CLOSE_APP == event;
    }

    public static boolean isNothing(Integer event) {
        return DeviceActions.NOTHING == event;
    }

    public static boolean isClickBackButton(Integer event) {
        return DeviceActions.BACK == event;
    }

    public static boolean isClickMenuButton(Integer event) {
        return DeviceActions.MENU_BTN == event;
    }

    public static boolean isKeyEvent(Integer event) {
        return DeviceActions.KEYEVENT == event;
    }

    public static boolean isChangeRotation(Integer event) {
        return DeviceActions.ROTATION == event;
    }

    public static Set<String> SPECIAL_CHARS = JavaUtils.getSet( "%", "'", "(", ")", "&", "<", ">", ";", "*", "|",
            "~", "¬", "`", "$");

    public static String encodeText(String text) {
        if (text == null) return null;
        StringBuilder builder = new StringBuilder(text.replace(" ", "%s").replace("\\", "\\\\").replace("\"",
                "\\\""));
        SPECIAL_CHARS.stream().forEach(c -> {
            int i = builder.indexOf(c);
            if (i != -1)
                builder.insert(i, '\\');
        });
        return builder.toString();
    }

}
