/*
GUI Control Level
 */

package Control;

import Implementation.Compat;
import Implementation.GUI_I;
import Implementation.MainWindow;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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

        //  Wait until window has been initialized to avoid null pointer error
        while(!GUI_I.isWindowInitialized()) {
            try { Thread.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //  start GUI Control thread loop
        Runnable GUILoop = GUI_C::loop;
        Compat.startThread(GUILoop, 10);    //  100hz
    }

    private static void loop() {
        //  Handle sliders
        int sliderFR = GUI_I.getMotorSlider(Constants.MOTOR_FR);
        int sliderFL = GUI_I.getMotorSlider(Constants.MOTOR_FL);
        int sliderBL = GUI_I.getMotorSlider(Constants.MOTOR_BL);
        int sliderBR = GUI_I.getMotorSlider(Constants.MOTOR_BR);
        if(sliderFR != sliderPrevFR) {
            sliderPrevFR = sliderFR;
            GUI_I.setMotorSliderText(Constants.MOTOR_FR, sliderFR);
            CommandHandler.setMotorTest(Constants.MOTOR_FR, sliderFR);
        }
        if(sliderFL != sliderPrevFL) {
            sliderPrevFL = sliderFL;
            GUI_I.setMotorSliderText(Constants.MOTOR_FL, sliderFL);
            CommandHandler.setMotorTest(Constants.MOTOR_FL, sliderFL);
        }
        if(sliderBL != sliderPrevBL) {
            sliderPrevBL = sliderBL;
            GUI_I.setMotorSliderText(Constants.MOTOR_BL, sliderBL);
            CommandHandler.setMotorTest(Constants.MOTOR_BL, sliderBL);
        }
        if(sliderBR != sliderPrevBR) {
            sliderPrevBR = sliderBR;
            GUI_I.setMotorSliderText(Constants.MOTOR_BR, sliderBR);
            CommandHandler.setMotorTest(Constants.MOTOR_BR, sliderBR);
        }
    }

    public static void tcpConnect_btnHandler() {
        NetworkHandler.tcpNetworkSetup();
    }

}
