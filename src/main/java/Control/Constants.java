package Control;

public class Constants
{

    public static int MOTOR_FR = 0;
    public static int MOTOR_FL = 1;
    public static int MOTOR_BL = 2;
    public static int MOTOR_BR = 3;

    public static final int CONTROLLER_COMPONENT_TYPE_BUTTON = 0;
    public static final int CONTROLLER_COMPONENT_TYPE_AXIS = 1;
    public static final int CONTROLLER_COMPONENT_TYPE_POV = 2;

    public static final double CONTROLLER_REMAP_DEADZONE = 0.2;

    public static final String CONTROLLER_BTN_A = "A";
    public static final String CONTROLLER_BTN_B = "B";
    public static final String CONTROLLER_BTN_X = "X";
    public static final String CONTROLLER_BTN_Y = "Y";
    public static final String CONTROLLER_BTN_BL = "Bumper Left";
    public static final String CONTROLLER_BTN_BR = "Bumper Right";
    public static final String CONTROLLER_BTN_TL = "Trigger Left";
    public static final String CONTROLLER_BTN_TR = "Trigger Right";
    public static final String CONTROLLER_BTN_START = "Start";
    public static final String CONTROLLER_BTN_SELECT = "Select";
    public static final String CONTROLLER_BTN_UP = "DPad Up";
    public static final String CONTROLLER_BTN_DOWN = "DPad Down";
    public static final String CONTROLLER_BTN_LEFT = "DPad Left";
    public static final String CONTROLLER_BTN_RIGHT = "DPad Right";
    public static final String CONTROLLER_BTN_JOYL = "Joystick Left Click";
    public static final String CONTROLLER_BTN_JOYR = "Joystick Right Click";
    public static final String CONTROLLER_AXIS_RX = "Right Joystick Horizontal";
    public static final String CONTROLLER_AXIS_RY = "Right Joystick Vertical";
    public static final String CONTROLLER_AXIS_LX = "Left Joystick Horizontal";
    public static final String CONTROLLER_AXIS_LY = "Left Joystick Vertical";
    public static final String[] CONTROLLER_INPUTS = {
            CONTROLLER_BTN_A, CONTROLLER_BTN_B, CONTROLLER_BTN_X, CONTROLLER_BTN_Y,
            CONTROLLER_BTN_BL, CONTROLLER_BTN_BR, CONTROLLER_BTN_TL, CONTROLLER_BTN_TR,
            CONTROLLER_BTN_START, CONTROLLER_BTN_SELECT,
            CONTROLLER_BTN_UP, CONTROLLER_BTN_DOWN, CONTROLLER_BTN_LEFT, CONTROLLER_BTN_RIGHT,
            CONTROLLER_BTN_JOYL, CONTROLLER_BTN_JOYR,
            CONTROLLER_AXIS_RX, CONTROLLER_AXIS_RY, CONTROLLER_AXIS_LX, CONTROLLER_AXIS_LY
    };
}
