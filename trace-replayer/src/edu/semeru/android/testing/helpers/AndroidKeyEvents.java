package edu.semeru.android.testing.helpers;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Android key and button events
 *
 * Taken from:
 * https://stackoverflow.com/questions/7789826/adb-shell-input-events
 *
 * More events defined at:
 * https://developer.android.com/reference/android/view/KeyEvent.html
 *
 * More events can be added on demand.
 *
 * Check @KeyCode for the key codes
 */
public class AndroidKeyEvents {

    private static HashMap<KeyCode,Integer> keyEvents = new LinkedHashMap<>();
    static{
        keyEvents.put(KeyCode.UNKNOWN, 0);
        //keyEvents.put(KeyCode.MENU, 1);
        keyEvents.put(KeyCode.SOFT_RIGHT, 2);
        keyEvents.put(KeyCode.HOME, 3);
        keyEvents.put(KeyCode.BACK, 4);
        keyEvents.put(KeyCode.CALL, 5);
        keyEvents.put(KeyCode.ENDCALL, 6);
        keyEvents.put(KeyCode.ZERO, 7);
        keyEvents.put(KeyCode.ONE, 8);
        keyEvents.put(KeyCode.TWO, 9);
        keyEvents.put(KeyCode.THREE, 10);
        keyEvents.put(KeyCode.FOUR, 11);
        keyEvents.put(KeyCode.FIVE, 12);
        keyEvents.put(KeyCode.SIX, 13);
        keyEvents.put(KeyCode.SEVEN, 14);
        keyEvents.put(KeyCode.EIGHT, 15);
        keyEvents.put(KeyCode.NINE, 16);
        keyEvents.put(KeyCode.STAR, 17);
        keyEvents.put(KeyCode.POUND, 18);
        keyEvents.put(KeyCode.DPAD_UP, 19);
        keyEvents.put(KeyCode.DPAD_DOWN, 20);
        keyEvents.put(KeyCode.DPAD_LEFT, 21);
        keyEvents.put(KeyCode.DPAD_RIGHT, 22);
        keyEvents.put(KeyCode.DPAD_CENTER, 23);
        keyEvents.put(KeyCode.VOLUME_UP, 24);
        keyEvents.put(KeyCode.VOLUME_DOWN, 25);
        keyEvents.put(KeyCode.POWER, 26);
        keyEvents.put(KeyCode.CAMERA, 27);
        keyEvents.put(KeyCode.CLEAR, 28);
        keyEvents.put(KeyCode.A, 29);
        keyEvents.put(KeyCode.B, 30);
        keyEvents.put(KeyCode.C, 31);
        keyEvents.put(KeyCode.D, 32);
        keyEvents.put(KeyCode.E, 33);
        keyEvents.put(KeyCode.F, 34);
        keyEvents.put(KeyCode.G, 35);
        keyEvents.put(KeyCode.H, 36);
        keyEvents.put(KeyCode.I, 37);
        keyEvents.put(KeyCode.J, 38);
        keyEvents.put(KeyCode.K, 39);
        keyEvents.put(KeyCode.L, 40);
        keyEvents.put(KeyCode.M, 41);
        keyEvents.put(KeyCode.N, 42);
        keyEvents.put(KeyCode.O, 43);
        keyEvents.put(KeyCode.P, 44);
        keyEvents.put(KeyCode.Q, 45);
        keyEvents.put(KeyCode.R, 46);
        keyEvents.put(KeyCode.S, 47);
        keyEvents.put(KeyCode.T, 48);
        keyEvents.put(KeyCode.U, 49);
        keyEvents.put(KeyCode.V, 50);
        keyEvents.put(KeyCode.W, 51);
        keyEvents.put(KeyCode.X, 52);
        keyEvents.put(KeyCode.Y, 53);
        keyEvents.put(KeyCode.Z, 54);
        keyEvents.put(KeyCode.COMMA, 55);
        keyEvents.put(KeyCode.PERIOD, 56);
        keyEvents.put(KeyCode.ALT_LEFT, 57);
        keyEvents.put(KeyCode.ALT_RIGHT, 58);
        keyEvents.put(KeyCode.SHIFT_LEFT, 59);
        keyEvents.put(KeyCode.SHIFT_RIGHT, 60);
        keyEvents.put(KeyCode.TAB, 61);
        keyEvents.put(KeyCode.SPACE, 62);
        keyEvents.put(KeyCode.SYM, 63);
        keyEvents.put(KeyCode.EXPLORER, 64);
        keyEvents.put(KeyCode.ENVELOPE, 65);
        keyEvents.put(KeyCode.ENTER, 66);
        keyEvents.put(KeyCode.DEL, 67);
        keyEvents.put(KeyCode.GRAVE, 68);
        keyEvents.put(KeyCode.MINUS, 69);
        keyEvents.put(KeyCode.EQUALS, 70);
        keyEvents.put(KeyCode.LEFT_BRACKET, 71);
        keyEvents.put(KeyCode.RIGHT_BRACKET, 72);
        keyEvents.put(KeyCode.BACKSLASH, 73);
        keyEvents.put(KeyCode.SEMICOLON, 74);
        keyEvents.put(KeyCode.APOSTROPHE, 75);
        keyEvents.put(KeyCode.SLASH, 76);
        keyEvents.put(KeyCode.AT, 77);
        keyEvents.put(KeyCode.NUM, 78);
        keyEvents.put(KeyCode.HEADSETHOOK, 79);
        keyEvents.put(KeyCode.FOCUS, 80);
        keyEvents.put(KeyCode.PLUS, 81);
        keyEvents.put(KeyCode.MENU, 82);
        keyEvents.put(KeyCode.NOTIFICATION, 83);
        keyEvents.put(KeyCode.SEARCH, 84);
        keyEvents.put(KeyCode.TAG_LAST, 85);
        keyEvents.put(KeyCode.MOVE_END, 123);
    }

    public static Integer getKeyEvent(KeyCode code){
        if (code==null) return null;
        Integer keyEvent = keyEvents.get(code);
        if (keyEvent==null) throw new RuntimeException("Could not find the key event for: " + code.toString());
        return keyEvent;
    }

    public static String getStringKeyEvent(KeyCode code){
        Integer keyEvent = getKeyEvent(code);
        if (keyEvent == null) return null;
        return keyEvent.toString();
    }
}

