/*
GUI Implementation Level
 */

package Implementation;

import Control.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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

    public static void startControllerMappingDialog(String componentName, int componentType) {
        if(window.controllerComboBox.getSelectedIndex() == 0) return;

        //  Don't start if dialog hasn't been fully disposed and destroyed.
        //  This means that a remap is currently still happening.
        if(controllerMappingDialog != null) return;

        //  first, start the remapping thread
        ControllerHandler_I.remapControllerComponent(componentName, componentType);

        //  next, start the window dialog
        controllerMappingDialog = new JDialog(window);
        JTextPane componentTextPane = new JTextPane();
        componentTextPane.setEditable(false);
        componentTextPane.setFocusable(false);
        componentTextPane.setOpaque(false);

        //  the following code centers text in the textPane
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        componentTextPane.setParagraphAttributes(attribs, true);

        if(componentType == Constants.CONTROLLER_COMPONENT_TYPE_BUTTON) {
            //  press button message
            componentTextPane.setText("Press: " + componentName);
        } else {
            componentTextPane.setText("Move: " + componentName + "\nMove up first, then down");
        }

        controllerMappingDialog.setContentPane(componentTextPane);  //  put label into dialog
        controllerMappingDialog.setMinimumSize(new Dimension(250, 80));  //  force size of dialog window
        controllerMappingDialog.setMaximumSize(new Dimension(250, 80));
        controllerMappingDialog.setLocationRelativeTo(window);   //  put dialog in middle of screen
        controllerMappingDialog.setVisible(true);

        //  dialog should cancel mapping operation on closing dialog window
        controllerMappingDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controllerMappingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopControllerMappingDialog();
            }
        });
    }

    public static void stopControllerMappingDialog() {
        //  stop remapping thread
        ControllerHandler_I.cancelRemapControllerComponent();

        //  destroy window
        if(controllerMappingDialog != null) controllerMappingDialog.dispose();
        controllerMappingDialog = null;
    }


    public static void displayInputs(boolean A, boolean B, boolean X, boolean Y,
                                     boolean START, boolean SELECT, boolean BL, boolean BR, boolean TL, boolean TR,
                                     boolean JOYL, boolean JOYR, boolean UP, boolean DOWN, boolean LEFT, boolean RIGHT,
                                     float lx, float ly, float rx, float ry) {
        Color on = new Color(27,252,113);
        Color off = new Color(75,75, 75);

        window.ctrlBtn_A.setBackground(A ? on : off);
        window.ctrlBtn_B.setBackground(B ? on : off);
        window.ctrlBtn_X.setBackground(X ? on : off);
        window.ctrlBtn_Y.setBackground(Y ? on : off);
        window.ctrlBtn_START.setBackground(START ? on : off);
        window.ctrlBtn_SELECT.setBackground(SELECT ? on : off);
        window.ctrlBtn_BL.setBackground(BL ? on : off);
        window.ctrlBtn_BR.setBackground(BR ? on : off);
        window.ctrlBtn_TL.setBackground(TL ? on : off);
        window.ctrlBtn_TR.setBackground(TR ? on : off);
        window.ctrlBtn_JOYL.setBackground(JOYL ? on : off);
        window.ctrlBtn_JOYR.setBackground(JOYR ? on : off);
        window.ctrlBtn_UP.setBackground(UP ? on : off);
        window.ctrlBtn_DOWN.setBackground(DOWN ? on : off);
        window.ctrlBtn_LEFT.setBackground(LEFT ? on : off);
        window.ctrlBtn_RIGHT.setBackground(RIGHT ? on : off);

        window.lx_progressBar.setValue(Math.round(lx * 100));
        window.ly_progressBar.setValue(Math.round(ly * 100));
        window.rx_progressBar.setValue(Math.round(rx * 100));
        window.ry_progressBar.setValue(Math.round(ry * 100));
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