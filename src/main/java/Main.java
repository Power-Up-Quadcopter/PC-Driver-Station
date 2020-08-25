import Control.CommandHandler;
import Control.ControllerHandler;
import Control.GUI_C;
import Implementation.Preferences;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Preferences.intialize();

        GUI_C.initialize();
        ControllerHandler.initialize();
        CommandHandler.initialize();
    }

}
