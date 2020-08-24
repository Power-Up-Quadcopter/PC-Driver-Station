package Implementation;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ControllerHandler_I {

    static Controller controller;
    static ArrayList<Controller> controllers;
    static ArrayList<String> controllerNames;   //  parallel to controllers array

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

    public static void controllerReloadButtonListener() {
        initialize();
    }

}
