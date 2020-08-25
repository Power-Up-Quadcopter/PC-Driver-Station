package Implementation;

import Control.Constants;
import Control.ControllerHandler;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ControllerHandler_I {

    static Controller controller;
    static ArrayList<Controller> controllers;
    static ArrayList<String> controllerNames;   //  parallel to controllers array

    static volatile HashMap<InputComponent, String> mapping;
    static volatile HashMap<Integer, InputComponent> mappingLookup;

    static Thread remappingThread;
    static Thread monitorThread;
    static volatile boolean stopRemappingFlag;

    public static void initialize() {
        findController();
        startControllerDisplayThread();
        startMonitorControllerThread();
    }

    public static void findController() {
        controllers = new ArrayList<>();
        Controller[] controllerArray;

        try {
            //  this code allows controller hotplugging. otherwise, the controller changes aren't reflected
            Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                    Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            constructor.newInstance();
            controllerArray = constructor.newInstance().getControllers();

            for(Controller controller : controllerArray) {
                if(controller.getType() == Controller.Type.GAMEPAD) controllers.add(controller);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        controllerNames = new ArrayList<>();
        for(Controller c : controllers) controllerNames.add(c.getName().trim());
        GUI_I.setAvailableControllers(controllerNames);

        //  check if previous controller is available to set
        String lastController = Preferences.get("LAST_CONTROLLER", "-");
        if(!lastController.equals("-")) {
            int controllerIndex = controllerNames.indexOf(lastController);
            if(controllerIndex > -1) GUI_I.setSelectedController(controllerIndex+1);
        }
    }

    public static void controllerComboBoxListener() {
        //  state of combo box changed, update preferences and start controller
        String controllerName = GUI_I.getSelectedController();

        int controllerIndex = controllerNames.indexOf(controllerName);

        if(controllerIndex < 0) {
            GUI_I.setControllerProperties(-1, -1);
            return;
        }
        Preferences.save("LAST_CONTROLLER", controllerName);    //  don't save if no controller selected
        controller = controllers.get(controllerIndex);

        int numButtons = 0;
        int numAxes = 0;

        controller.poll();
        for(Component comp : controller.getComponents()) {
            if(comp.isAnalog()) numAxes++;  //  is axis
            else if(comp.getIdentifier().getName().equals("pov")) numButtons += 4;     //  is hat
            else numButtons++;
        }
        GUI_I.setControllerProperties(numAxes, numButtons);
        reloadMappingPreferences();
    }

    public static void remapControllerComponent(String componentName, int componentType) {
        stopRemappingFlag = false;

        remappingThread = new Thread(() -> {
            try {
                //  mapping process of buttons and axes are different
                if(componentType == Constants.CONTROLLER_COMPONENT_TYPE_BUTTON) {
                    //  keep track of initial button inputs on controller
                    controller.poll();
                    Component[] components = controller.getComponents();
                    float[] originalInputs = new float[components.length];
                    for (int i = 0; i < components.length; i++) {
                        originalInputs[i] = components[i].getPollData();
                    }

                    while (true) {
                        //  check if remapping is cancelled
                        if (stopRemappingFlag) break;

                        //  check for change in component value (button press)
                        controller.poll();
                        int componentIndex;
                        for (componentIndex = 0; componentIndex < components.length; componentIndex++) {
                            float pollData = components[componentIndex].getPollData();

                            //  if the button was pressed initially, ignore falling edge of button release
                            if (Math.abs(pollData) < Math.abs(originalInputs[componentIndex]))
                                originalInputs[componentIndex] = pollData;

                            //  joysticks are processed differently
                            else if (components[componentIndex].isAnalog()) {
                                //  check both +/- on axes and deadzone
                                if (Math.abs(pollData - originalInputs[componentIndex])
                                        >= Constants.CONTROLLER_REMAP_DEADZONE) break;
                            }

                            //  leave loop on rising edge
                            else if (pollData > originalInputs[componentIndex]) break;
                        }

                        //  if there has been a button press detected, save this data into preferences and stop remap
                        if (componentIndex < components.length) {
                            String key = componentName;
                            String value = componentIndex + "";
                            if (components[componentIndex].getIdentifier().getName().equals("pov")) {
                                //  if the component is a pov stick, add pov to back of index. ex: "12pov0.25"
                                value += "pov" + components[componentIndex].getPollData();
                            } else if (components[componentIndex].isAnalog()) {
                                //  mark component as axis in preferences.  ex: "12axis-"
                                value += "axis";

                                //  for joysticks, check the direction of change. Mark with - or + in key.
                                if (originalInputs[componentIndex] > components[componentIndex].getPollData())
                                    value += "-";
                                else value += "+";
                            } else {
                                //  component is just a button, add 'btn' to end. ex: "12btn"
                                value += "btn";
                            }
                            Preferences.save(key, value);
                            break;
                        }

                        Thread.sleep(50);
                    }
                }
                else if(componentType == Constants.CONTROLLER_COMPONENT_TYPE_AXIS) {
                    //  keep track of initial joystick inputs
                    controller.poll();
                    Component[] components = controller.getComponents();
                    float[] originalInputs = new float[components.length];
                    for (int i = 0; i < components.length; i++) {
                        originalInputs[i] = components[i].getPollData();
                    }

                    int detectedAxis = -1;
                    float previousValue = Integer.MIN_VALUE;
                    float previousPreviousValue = Integer.MIN_VALUE;

                    while(true) {
                        //  check if remapping is cancelled
                        if (stopRemappingFlag) break;
                        controller.poll();

                        //  first step: detect axis
                        if(detectedAxis == -1) {
                            //  poll all components, then compare it to original inputs to detect axis
                            int componentIndex;
                            for (componentIndex = 0; componentIndex < components.length; componentIndex++) {

                                //  skip if button
                                if(!components[componentIndex].isAnalog()) continue;

                                if(Math.abs(components[componentIndex].getPollData() - originalInputs[componentIndex])
                                        > Constants.CONTROLLER_REMAP_DEADZONE)
                                    break;  //  detected!
                            }

                            //  update detectedAxis if axis was detected in loop
                            if(componentIndex < components.length) detectedAxis = componentIndex;
                        }
                        //  second step: detect rising or falling edge
                        if(detectedAxis != -1){
                            float value = components[detectedAxis].getPollData();

                            //  get previous 2 values
                            if(previousValue == Integer.MIN_VALUE) previousValue = value;
                            else if(previousPreviousValue == Integer.MIN_VALUE) {
                                //  only update previous previous if current is different from previous
                                if(value != previousValue) {
                                    previousPreviousValue = previousValue;
                                    previousValue = value;
                                }
                            }
                            else {
                                //  check for edge
                                if(previousPreviousValue < previousValue && previousValue > value) {
                                    //  falling edge - non inverted
                                    Preferences.save(componentName, detectedAxis + "axis+");
                                    break;
                                } else if(previousPreviousValue > previousValue && previousValue < value) {
                                    //  rising edge - inverted
                                    Preferences.save(componentName, detectedAxis + "axis-");
                                    break;
                                }
                                else {
                                    //  no edge detected, update previous value if new value's different
                                    if(value != previousValue) {
                                        previousPreviousValue = previousValue;
                                        previousValue = value;
                                    }
                                }
                            }
                        }
                        Thread.sleep(20);
                    }
                }
                reloadMappingPreferences();
                GUI_I.stopControllerMappingDialog();
            } catch (Exception e) { e.printStackTrace(); }
        });
        remappingThread.start();
    }

    //  this function should only be called once at startup. the thread will continuously
    //  monitor the controller and display them onto the GUI
    public static void startMonitorControllerThread() {
        if(monitorThread != null) return;
        monitorThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(20);

                    //  wait for mapping to initialize
                    if (mapping == null) continue;

                    //  check out controller
                    controller.poll();
                    Component[] components = controller.getComponents();
                    Set<Integer> lookupKeys = mappingLookup.keySet();
                    for (int i = 0; i < components.length; i++) {
                        if(lookupKeys.contains(i)) {
                            //  if this index is in lookupkeys, it has been previously mapped.
                            //  get the inputcomponent and name for this index
                            InputComponent component = mappingLookup.get(i);
                            String inputName = mapping.get(component);

                            switch (component.componentType) {
                                case Constants.CONTROLLER_COMPONENT_TYPE_BUTTON:
                                    boolean pressed = components[i].getPollData() != 0;
                                    switch(inputName) {
                                        case Constants.CONTROLLER_BTN_A: ControllerHandler.BTN_A = pressed; break;
                                        case Constants.CONTROLLER_BTN_B: ControllerHandler.BTN_B = pressed; break;
                                        case Constants.CONTROLLER_BTN_X: ControllerHandler.BTN_X = pressed; break;
                                        case Constants.CONTROLLER_BTN_Y: ControllerHandler.BTN_Y = pressed; break;
                                        case Constants.CONTROLLER_BTN_BL: ControllerHandler.BTN_BL = pressed; break;
                                        case Constants.CONTROLLER_BTN_BR: ControllerHandler.BTN_BR = pressed; break;
                                        case Constants.CONTROLLER_BTN_TL: ControllerHandler.BTN_TL = pressed; break;
                                        case Constants.CONTROLLER_BTN_TR: ControllerHandler.BTN_TR = pressed; break;
                                        case Constants.CONTROLLER_BTN_START: ControllerHandler.BTN_START = pressed; break;
                                        case Constants.CONTROLLER_BTN_SELECT: ControllerHandler.BTN_SELECT = pressed; break;
                                        case Constants.CONTROLLER_BTN_JOYL: ControllerHandler.BTN_JOYL = pressed; break;
                                        case Constants.CONTROLLER_BTN_JOYR: ControllerHandler.BTN_JOYR = pressed; break;
                                    }
                                    break;
                                case Constants.CONTROLLER_COMPONENT_TYPE_AXIS:
                                    float value = components[i].getPollData();
                                    if(component.axisInverted) value = -value;

                                    boolean pressedValue = value > Constants.CONTROLLER_REMAP_DEADZONE;
                                    System.out.println(value);

                                    switch(inputName) {
                                        case Constants.CONTROLLER_AXIS_LX: ControllerHandler.AXIS_LX = value; break;
                                        case Constants.CONTROLLER_AXIS_LY: ControllerHandler.AXIS_LY = value; break;
                                        case Constants.CONTROLLER_AXIS_RX: ControllerHandler.AXIS_RX = value; break;
                                        case Constants.CONTROLLER_AXIS_RY: ControllerHandler.AXIS_RY = value; break;

                                        case Constants.CONTROLLER_BTN_A: ControllerHandler.BTN_A = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_B: ControllerHandler.BTN_B = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_X: ControllerHandler.BTN_X = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_Y: ControllerHandler.BTN_Y = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_BL: ControllerHandler.BTN_BL = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_BR: ControllerHandler.BTN_BR = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_TL: ControllerHandler.BTN_TL = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_TR: ControllerHandler.BTN_TR = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_START: ControllerHandler.BTN_START = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_SELECT: ControllerHandler.BTN_SELECT = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_JOYL: ControllerHandler.BTN_JOYL = pressedValue; break;
                                        case Constants.CONTROLLER_BTN_JOYR: ControllerHandler.BTN_JOYR = pressedValue; break;
                                    }

                                    break;
                                case Constants.CONTROLLER_COMPONENT_TYPE_POV:
                                    //  buffer final outputs
                                    int up = -1;
                                    int down = -1;
                                    int left = -1;
                                    int right = -1;
                                    int a = -1;
                                    int b = -1;
                                    int x = -1;
                                    int y = -1;
                                    int bl = -1;
                                    int br = -1;
                                    int tl = -1;
                                    int tr = -1;
                                    int start = -1;
                                    int select = -1;
                                    int joyl = -1;
                                    int joyr = -1;

                                    //  get the values for the keys, compare with expected values to find which btn is being pressed
                                    String key_up = Preferences.get(Constants.CONTROLLER_BTN_UP, "-");
                                    String key_down = Preferences.get(Constants.CONTROLLER_BTN_DOWN, "-");
                                    String key_left = Preferences.get(Constants.CONTROLLER_BTN_LEFT, "-");
                                    String key_right = Preferences.get(Constants.CONTROLLER_BTN_RIGHT, "-");
                                    String key_A = Preferences.get(Constants.CONTROLLER_BTN_A, "-");
                                    String key_B = Preferences.get(Constants.CONTROLLER_BTN_B, "-");
                                    String key_X = Preferences.get(Constants.CONTROLLER_BTN_X, "-");
                                    String key_Y = Preferences.get(Constants.CONTROLLER_BTN_Y, "-");
                                    String key_BL = Preferences.get(Constants.CONTROLLER_BTN_BL, "-");
                                    String key_BR = Preferences.get(Constants.CONTROLLER_BTN_BR, "-");
                                    String key_TL = Preferences.get(Constants.CONTROLLER_BTN_TL, "-");
                                    String key_TR = Preferences.get(Constants.CONTROLLER_BTN_TR, "-");
                                    String key_START = Preferences.get(Constants.CONTROLLER_BTN_START, "-");
                                    String key_SELECT = Preferences.get(Constants.CONTROLLER_BTN_SELECT, "-");
                                    String key_JOYL = Preferences.get(Constants.CONTROLLER_BTN_JOYL, "-");
                                    String key_JOYR = Preferences.get(Constants.CONTROLLER_BTN_JOYR, "-");

                                    float povValue = components[i].getPollData();

                                    String[] valueCheck = {"", ""};

                                    if(povValue == 0) {
                                        //  no dpad buttons pressed, just use the buffered values
                                    } else if(povValue % 0.25 == 0) {
                                        //  single button pressed

                                        valueCheck[0] = i + "pov" + povValue;
                                    } else {
                                        //  2 buttons pressed
                                        float first = povValue - 0.125f;
                                        float second = povValue + 0.125f;

                                        valueCheck[0] = i + "pov" + first;
                                        valueCheck[1] = i + "pov" + second;
                                    }

                                    for(String valueSearch : valueCheck) {
                                        if(valueSearch.equals(key_up)) up = true;
                                        else if(valueSearch.equals(key_down)) down = true;
                                        else if(valueSearch.equals(key_left)) left = true;
                                        else if(valueSearch.equals(key_right)) right = true;
                                        else if(valueSearch.equals(key_A)) a = true;
                                        else if(valueSearch.equals(key_B)) b = true;
                                        else if(valueSearch.equals(key_X)) x = true;
                                        else if(valueSearch.equals(key_Y)) y = true;
                                        else if(valueSearch.equals(key_BL)) bl = true;
                                        else if(valueSearch.equals(key_BR)) br = true;
                                        else if(valueSearch.equals(key_TL)) tl = true;
                                        else if(valueSearch.equals(key_TR)) tr = true;
                                        else if(valueSearch.equals(key_START)) start = true;
                                        else if(valueSearch.equals(key_SELECT)) select = true;
                                        else if(valueSearch.equals(key_JOYL)) joyl = true;
                                        else if(valueSearch.equals(key_JOYR)) joyr = true;
                                    }

                                    ControllerHandler.BTN_UP = up;
                                    ControllerHandler.BTN_DOWN = down;
                                    ControllerHandler.BTN_LEFT = left;
                                    ControllerHandler.BTN_RIGHT = right;
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        });

        monitorThread.start();
    }

    public static void startControllerDisplayThread() {
        new Thread( () -> {
            try {
                while(true) {
                    ControllerHandler.displayInputs();
                    Thread.sleep(50);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public static void reloadMappingPreferences() {
        if(mapping != null) mapping.clear();
        if(mappingLookup != null) mappingLookup.clear();
        mapping = new HashMap<>();
        mappingLookup = new HashMap<>();

        for(String inputName : Constants.CONTROLLER_INPUTS) {
            String newKey = Preferences.get(inputName, "-");
            if(newKey.equals("-")) continue;

            InputComponent inputComponent = new InputComponent(newKey);
            mapping.put(inputComponent, inputName);
            mappingLookup.put(inputComponent.componentIndex, inputComponent);
        }
    }

    public static void cancelRemapControllerComponent() {
        stopRemappingFlag = true;
    }

    public static void controllerReloadButtonListener() {
        findController();
    }

}

//  this object is just an easy way of parsing and storing input components
class InputComponent {

    int componentIndex = -1;
    int componentType = -1;
    float povValue = -1;
    boolean axisInverted = false;

    public InputComponent(String savedValue) {
        if(savedValue.contains("btn")) {
            componentType = Constants.CONTROLLER_COMPONENT_TYPE_BUTTON;
            String[] delimited = savedValue.split("btn");
            componentIndex = Integer.parseInt(delimited[0]);

        } else if(savedValue.contains("axis")) {
            componentType = Constants.CONTROLLER_COMPONENT_TYPE_AXIS;
            String[] delimited = savedValue.split("axis");
            componentIndex = Integer.parseInt(delimited[0]);
            axisInverted = delimited[1].equals("-");

        } else if(savedValue.contains("pov")) {
            componentType = Constants.CONTROLLER_COMPONENT_TYPE_POV;
            String[] delimited = savedValue.split("pov");
            componentIndex = Integer.parseInt(delimited[0]);
            povValue = Float.parseFloat(delimited[1]);
        }
    }

}