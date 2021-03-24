import edu.semeru.android.core.helpers.device.DeviceHelper;
import seers.appcore.utils.JavaUtils;

import java.util.Set;

public class DeviceUtils {

    public static boolean isSwipe(Integer event) {
        return event == DeviceHelper.SWIPE_DOWN || event == DeviceHelper.SWIPE_LEFT || event == DeviceHelper.SWIPE_RIGHT
                || event == DeviceHelper.SWIPE_UP;
    }

    public static boolean isClick(Integer event) {
        return DeviceHelper.LONG_CLICK == event || DeviceHelper.CLICK == event;
    }

    public static boolean isType(Integer event) {
        return DeviceHelper.TYPE == event;
    }
    
    public static boolean isAnyInputType(Integer event) {
        return DeviceHelper.TYPE == event || DeviceHelper.CLICK_TYPE == event;
    }

    public static boolean isDeleteText(Integer event) {
        return DeviceHelper.DELETE_TEXT == event;
    }

    public static boolean isAnyType(Integer event) {
        return isType(event) || isDeleteText(event);
    }

    public static boolean isOpenApp(Integer event) {
        return DeviceHelper.OPEN_APP == event;
    }

    public static boolean isNothing(Integer event) {
        return DeviceHelper.NOTHING == event;
    }

    public static boolean isClickBackButton(Integer event) {
        return DeviceHelper.BACK == event;
    }

    public static boolean isClickMenuButton(Integer event) {
        return DeviceHelper.MENU_BTN == event;
    }

    public static boolean isKeyEvent(Integer event) {
        return DeviceHelper.KEYEVENT == event;
    }

    public static boolean isChangeRotation(Integer event) {
        return DeviceHelper.ROTATION == event;
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
