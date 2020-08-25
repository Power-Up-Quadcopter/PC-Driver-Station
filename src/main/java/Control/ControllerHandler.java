package Control;

import Implementation.ControllerHandler_I;

public class ControllerHandler {

    public static volatile boolean BTN_A = false;
    public static volatile boolean BTN_B = false;
    public static volatile boolean BTN_X = false;
    public static volatile boolean BTN_Y = false;
    public static volatile boolean BTN_START = false;
    public static volatile boolean BTN_SELECT = false;
    public static volatile boolean BTN_BL = false;
    public static volatile boolean BTN_BR = false;
    public static volatile boolean BTN_JOYL = false;
    public static volatile boolean BTN_JOYR = false;
    public static volatile boolean BTN_UP = false;
    public static volatile boolean BTN_DOWN = false;
    public static volatile boolean BTN_LEFT = false;
    public static volatile boolean BTN_RIGHT = false;
    public static volatile float AXIS_LX = 0.0f;
    public static volatile float AXIS_LY = 0.0f;
    public static volatile float AXIS_RX = 0.0f;
    public static volatile float AXIS_RY = 0.0f;

    public static void initialize() {
        ControllerHandler_I.initialize();
    }

}
