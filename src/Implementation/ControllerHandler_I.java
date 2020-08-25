package Implementation;

import Control.Constants;
import Control.ControllerHandler;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

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
    static volatile boolean stopMonitoringFlag;

    public static void initialize() {
        findController();
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
                            System.out.println(componentName + ":" + value);
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
                                    System.out.println("not invert");
                                    break;
                                } else if(previousPreviousValue > previousValue && previousValue < value) {
                                    //  rising edge - inverted
                                    System.out.println("inverted");
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
                                        case Constants.CONTROLLER_BTN_START: ControllerHandler.BTN_START = pressed; break;
                                        case Constants.CONTROLLER_BTN_SELECT: ControllerHandler.BTN_SELECT = pressed; break;
                                        case Constants.CONTROLLER_BTN_JOYL: ControllerHandler.BTN_JOYL = pressed; break;
                                        case Constants.CONTROLLER_BTN_JOYR: ControllerHandler.BTN_JOYR = pressed; break;
                                    }
                                    break;
                                case Constants.CONTROLLER_COMPONENT_TYPE_AXIS:
                                    break;
                                case Constants.CONTROLLER_COMPONENT_TYPE_POV:
                                    //  buffer final outputs
                                    boolean up = false;
                                    boolean down = false;
                                    boolean left = false;
                                    boolean right = false;

                                    //  get the values for the keys, compare with expected values to find which btn is being pressed
                                    String upKey = Preferences.get(Constants.CONTROLLER_BTN_UP, "-");
                                    String downKey = Preferences.get(Constants.CONTROLLER_BTN_DOWN, "-");
                                    String leftKey = Preferences.get(Constants.CONTROLLER_BTN_LEFT, "-");
                                    String rightKey = Preferences.get(Constants.CONTROLLER_BTN_RIGHT, "-");

                                    float povValue = components[i].getPollData();

                                    if(povValue == 0) {
                                        //  no dpad buttons pressed, just use the buffered values
                                    } else if(povValue % 0.25 == 0) {
                                        //  single button pressed

                                        String expectedValue = i + "pov" + povValue;

                                        if(expectedValue.equals(upKey)) up = true;
                                        else if(expectedValue.equals(downKey)) down = true;
                                        else if(expectedValue.equals(leftKey))  left = true;
                                        else if(expectedValue.equals(rightKey))  right = true;
                                    } else {
                                        //  multiple buttons pressed
                                        float first = povValue - 0.125f;
                                        float second = povValue + 0.125f;

                                        String firstExpectedValue = i + "pov" + first;
                                        String secondExpectedValue = i + "pov" + second;

                                        if(firstExpectedValue.equals(upKey)) up = true;
                                        else if(firstExpectedValue.equals(downKey)) down = true;
                                        else if(firstExpectedValue.equals(leftKey))  left = true;
                                        else if(firstExpectedValue.equals(rightKey))  right = true;

                                        if(secondExpectedValue.equals(upKey)) up = true;
                                        else if(secondExpectedValue.equals(downKey)) down = true;
                                        else if(secondExpectedValue.equals(leftKey))  left = true;
                                        else if(secondExpectedValue.equals(rightKey))  right = true;
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
        }
    }

}