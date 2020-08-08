package Implementation;

import Control.Constants;
import Control.Utilities;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import java.awt.*;

public class MainWindow extends JFrame
{
    private JTabbedPane tabs;
    private JPanel rootPanel;
    private JPanel tabControl;
    private JPanel tabCamera;
    private JPanel tabGPS;
    private JPanel tabTuning;
    private JPanel tabNetwork;
    private JPanel tabSettings;
    private JPanel tabDebug;
    private JSlider sliderFR;
    private JSlider sliderFL;
    private JSlider sliderBL;
    private JSlider sliderBR;
    private JLabel labelSpeedFR;
    private JLabel labelSpeedFL;
    private JLabel labelSpeedBL;
    private JLabel labelSpeedBR;

    public MainWindow() {
        add(rootPanel);     //  needed or else nothing will be on screen

        setTitle("Power Up Quadcopter | Driver Station");   //  set window title
        setSize(1600,1000);     //  set window size
        setLocationRelativeTo(null);    //  center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //  set what clicking 'X' does

        //  make sliders unselectable
        sliderFR.setFocusable(false);
        sliderFL.setFocusable(false);
        sliderBL.setFocusable(false);
        sliderBR.setFocusable(false);
        setVisible(true);
    }

    public int getMotorSlider(int motor) {
        if(motor == Constants.MOTOR_FR) {
            return sliderFR.getValue();
        } else if(motor == Constants.MOTOR_FL) {
            return sliderFL.getValue();
        } else if(motor == Constants.MOTOR_BL) {
            return sliderBL.getValue();
        } else if(motor == Constants.MOTOR_BR) {
            return sliderBR.getValue();
        }
        return -1;
    }

    public void setMotorSliderText(int motor, int value) {
        String valueStr = Utilities.padSpaces(value+"", 3, false);
        System.out.println(valueStr);

        if(motor == Constants.MOTOR_FR) {
            labelSpeedFR.setText(valueStr);
        } else if(motor == Constants.MOTOR_FL) {
            labelSpeedFL.setText(valueStr);
        } else if(motor == Constants.MOTOR_BL) {
            labelSpeedBL.setText(valueStr);
        } else if(motor == Constants.MOTOR_BR) {
            labelSpeedBR.setText(valueStr);
        }
    }

}
