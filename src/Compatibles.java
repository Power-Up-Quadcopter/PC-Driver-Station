/*
The purpose of this class is to put miscellaneous functions in here that doesn't fit well into
any other section of the code. These functions are used by higher level code that stays the same
between the PC app and Android app. Inside this class is where the functions will be different
because of low-level implementation on different systems.
 */

import javax.swing.*;

public class Compatibles
{

    public static void setupGUI() {
        //  set the app theme to match Windows 10 instead of the ugly Java theme
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }


    }

}
