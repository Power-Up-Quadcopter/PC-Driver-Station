/*
GUI Implementation Level
 */

package Implementation;

import Control.*;
import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if(motor == Constants.MOTOR_FR) {
            return window.sliderFR.getValue();
        } else if(motor == Constants.MOTOR_FL) {
            return window.sliderFL.getValue();
        } else if(motor == Constants.MOTOR_BL) {
            return window.sliderBL.getValue();
        } else if(motor == Constants.MOTOR_BR) {
            return window.sliderBR.getValue();
        }
        return -1;
    }

    public static void setMotorSliderText(int motor, int value) {
        String valueStr = Utilities.padSpaces(value+"", 3, false);

        if(motor == Constants.MOTOR_FR) {
            window.labelSpeedFR.setText(valueStr);
        } else if(motor == Constants.MOTOR_FL) {
            window.labelSpeedFL.setText(valueStr);
        } else if(motor == Constants.MOTOR_BL) {
            window.labelSpeedBL.setText(valueStr);
        } else if(motor == Constants.MOTOR_BR) {
            window.labelSpeedBR.setText(valueStr);
        }
    }

    public static void printTCPConsole(String s) {
        String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String formatted = String.format("[%s] %s\n", timeStamp, s);

        window.textAreaTCP.append(formatted);
    }

    public static void printUDPConsole(String s) {
        String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String formatted = String.format("[%s] %s\n", timeStamp, s);

        window.textAreaUDP.append(formatted);
    }

}
