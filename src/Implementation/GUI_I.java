/*
GUI Implementation Level
 */

package Implementation;

import Control.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GUI_I
{
    private static MainWindow window;
    private static JDialog controllerMappingDialog;

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

    public static void setAvailableControllers(ArrayList<String> names) {
        window.controllerComboBox.removeAllItems();
        window.controllerComboBox.addItem("-");
        for(String s : names) window.controllerComboBox.addItem(s);
    }

    public static String getSelectedController() {
        return (String) window.controllerComboBox.getSelectedItem();
    }

    public static void setSelectedController(int index) {
        window.controllerComboBox.setSelectedIndex(index);
    }

    public static void setControllerProperties(int axes, int buttons) {
        String toDisplay = String.format("   Axes: %d\nButtons: %d", axes, buttons);
        window.controllerPropertiesTextArea.setText(toDisplay);
    }

    public static void startControllerMappingDialog(String componentName) {
        if(window.controllerComboBox.getSelectedIndex() == 0) return;

        //  Don't start if dialog hasn't been fully disposed and destroyed.
        //  This means that a remap is currently still happening.
        if(controllerMappingDialog != null) return;

        controllerMappingDialog = new JDialog(window);
        JLabel componentLabel = new JLabel("Press: " + componentName);

        componentLabel.setHorizontalAlignment(JLabel.CENTER);   //  center text on label
        controllerMappingDialog.setContentPane(componentLabel);  //  put label into dialog
        controllerMappingDialog.setMinimumSize(new Dimension(150, 70));  //  force size of dialog window
        controllerMappingDialog.setMaximumSize(new Dimension(150, 70));
        controllerMappingDialog.setLocationRelativeTo(window);   //  put dialog in middle of screen
        controllerMappingDialog.setVisible(true);

        //  dialog should cancel mapping operation on closing dialog window
        controllerMappingDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controllerMappingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ControllerHandler_I.cancelRemapControllerComponent();
            }
        });

        ControllerHandler_I.remapControllerComponent(componentName);
    }

    public static void stopControllerMappingDialog() {
        if(controllerMappingDialog != null) controllerMappingDialog.dispose();
        controllerMappingDialog = null;
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
