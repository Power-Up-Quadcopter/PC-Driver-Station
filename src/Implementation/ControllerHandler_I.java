package Implementation;

import Control.Constants;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ControllerHandler_I {

    static Controller controller;
    static ArrayList<Controller> controllers;
    static ArrayList<String> controllerNames;   //  parallel to controllers array

    static Thread remappingThread;
    static volatile boolean stopRemappingFlag;

    public static void initialize() {
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
    }

    public static void remapControllerComponent(String componentName) {
        stopRemappingFlag = false;

        remappingThread = new Thread(() -> {
            try {

                //  keep track of initial button inputs on controller
                controller.poll();
                Component[] components = controller.getComponents();
                float[] originalInputs = new float[components.length];
                for(int i = 0; i < components.length; i++) {
                    originalInputs[i] = components[i].getPollData();
                }

                while(true) {
                    //  check if remapping is cancelled
                    if(stopRemappingFlag) break;

                    //  check for change in component value (button press)
                    controller.poll();
                    int componentIndex;
                    for(componentIndex = 0; componentIndex < components.length; componentIndex++) {
                        float pollData = components[componentIndex].getPollData();

                        //  if the button was pressed initially, ignore falling edge of button release
                        if(Math.abs(pollData) < Math.abs(originalInputs[componentIndex])) originalInputs[componentIndex] = pollData;

                        //  joysticks are processed differently
                        else if(components[componentIndex].isAnalog()) {
                            //  check both +/- on axes and deadzone
                            if(Math.abs(pollData - originalInputs[componentIndex])
                                    >= Constants.CONTROLLER_REMAP_DEADZONE) break;
                        }

                        //  leave loop on rising edge
                        else if(pollData > originalInputs[componentIndex]) break;
                    }

                    //  if there has been a button press detected, save this data into preferences and stop remap
                    if(componentIndex < components.length) {
                        String key = componentName;
                        String value = componentIndex+"";
                        if(components[componentIndex].getIdentifier().getName().equals("pov")) {
                            //  if the component is a pov stick, add pov to back of index. ex: "12pov0.25"
                            value += "pov" + components[componentIndex].getPollData();
                        } else if(components[componentIndex].isAnalog()) {
                            //  mark component as joystick in preferences
                            value += "axis";

                            //  for joysticks, check the direction of change. Mark with - or + in key
                            if(originalInputs[componentIndex] > components[componentIndex].getPollData()) value += "-";
                            else value += "+";
                        }
                        Preferences.save(key, value);
                        System.out.println(componentName + ":" + value);
                        break;
                    }

                    Thread.sleep(50);
                }
                GUI_I.stopControllerMappingDialog();
            } catch (Exception e) { e.printStackTrace(); }
        });
        remappingThread.start();
    }

    public static void cancelRemapControllerComponent() {
        stopRemappingFlag = true;
    }

    public static void controllerReloadButtonListener() {
        initialize();
    }

}
