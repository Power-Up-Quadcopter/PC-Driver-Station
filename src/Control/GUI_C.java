/*
GUI Control Level
 */

package Control;

import Implementation.Compat;
import Implementation.GUI_I;
import Implementation.MainWindow;
import sun.awt.windows.WPrinterJob;

import javax.swing.*;

public class GUI_C
{
    static int sliderPrevFR = -1;
    static int sliderPrevFL = -1;
    static int sliderPrevBL = -1;
    static int sliderPrevBR = -1;

    public static void initialize() {
        GUI_I.initialize();

        //  start GUI Control thread loop
        Runnable GUILoop = GUI_C::loop;
        Compat.startThread(GUILoop, 10);    //  100hz
    }

    private static void loop() {
        //  Wait until window has been initialized to avoid null pointer error
        System.out.println(GUI_I.isWindowInitialized());
        if(GUI_I.isWindowInitialized() == false) return;

        //  Handle sliders
        int sliderFR = GUI_I.getMotorSlider(Constants.MOTOR_FR);
        int sliderFL = GUI_I.getMotorSlider(Constants.MOTOR_FL);
        int sliderBL = GUI_I.getMotorSlider(Constants.MOTOR_BL);
        int sliderBR = GUI_I.getMotorSlider(Constants.MOTOR_BR);
        if(sliderFR != sliderPrevFR) {
            GUI_I.setMotorSliderText(Constants.MOTOR_FR, sliderFR);
            //  TODO send motor network command
        }
        if(sliderFL != sliderPrevFL) {
            GUI_I.setMotorSliderText(Constants.MOTOR_FL, sliderFL);
            //  TODO send motor network command
        }
        if(sliderBL != sliderPrevBL) {
            GUI_I.setMotorSliderText(Constants.MOTOR_BL, sliderBL);
            //  TODO send motor network command
        }
        if(sliderBR != sliderPrevBR) {
            GUI_I.setMotorSliderText(Constants.MOTOR_BR, sliderBR);
            //  TODO send motor network command
        }
    }

}
