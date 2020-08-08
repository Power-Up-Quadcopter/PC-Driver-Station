/*
GUI Implementation Level
 */

package Implementation;

import Control.*;
import javax.swing.*;

public class GUI_I
{
    private static MainWindow window;

    public static void initialize() {
        //  set the app theme to match Windows 10 instead of the ugly Java theme
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            window = new MainWindow();
        });
    }

    public static boolean isWindowInitialized() {
        return window != null;
    }

    public static int getMotorSlider(int motor) {
        return window.getMotorSlider(motor);
    }

    public static void setMotorSliderText(int motor, int value) {
        window.setMotorSliderText(motor, value);
    }

}
