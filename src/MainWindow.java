import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame
{
    private JPanel rootPanel;
    private JTabbedPane tabs;
    private JPanel Tab1;
    private JPanel Tab2;

    public MainWindow() {
        add(rootPanel);     //  needed or else nothing will be on screen

        setTitle("Power Up Quadcopter | Driver Station");   //  set window title
        setSize(1600,1000);     //  set window size
        setLocationRelativeTo(null);    //  center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  //  set what clicking 'X' does

        //  make tabs larger

        setVisible(true);
    }

}
