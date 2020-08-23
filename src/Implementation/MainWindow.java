package Implementation;

import Control.Constants;
import Control.GUI_C;
import Control.Utilities;
import Imports.SmartScroller;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame
{
    public JTabbedPane tabs;
    public JPanel rootPanel;
    public JPanel tabControl;
    public JPanel tabCamera;
    public JPanel tabGPS;
    public JPanel tabTuning;
    public JPanel tabNetwork;
    public JPanel tabSettings;
    public JPanel tabDebug;
    public JSlider sliderFR;
    public JSlider sliderFL;
    public JSlider sliderBL;
    public JSlider sliderBR;
    public JLabel labelSpeedFR;
    public JLabel labelSpeedFL;
    public JLabel labelSpeedBL;
    public JLabel labelSpeedBR;
    public JButton sendTcpBtn;
    public JTextPane tcpTextPane;
    public JTextPane udpTextPane;
    public JButton sendUdpBtn;
    public JTextField tcpSendField;
    public JTextField udpSendField;
    public JButton tcpConnectButton;
    public JComboBox comboBox1;
    public JProgressBar progressBar1;
    public JProgressBar progressBar2;
    public JLabel ctrlBtn_A;
    public JLabel ctrlBtn_B;
    public JLabel ctrlBtn_START;
    public JLabel ctrlBtn_SELECT;
    public JLabel ctrlBtn_JOYL;
    public JLabel ctrlBtn_JOYR;
    public JLabel ctrlBtn_LEFT;
    public JLabel ctrlBtn_RIGHT;
    public JLabel ctrlBtn_DOWN;
    public JLabel ctrlBtn_UP;
    public JLabel ctrlBtn_BR;
    public JLabel ctrlBtn_BL;
    public JLabel ctrlBtn_TR;
    public JLabel ctrlBtn_TL;
    public JTextArea textAreaTCP;
    public JTextArea textAreaUDP;
    public JScrollPane scrollPaneTCP;
    public JScrollPane scrollPaneUDP;
    private JTextField textField1;
    private JButton a4Button;

    public MainWindow() {
        add(rootPanel);     //  needed or else nothing will be on screen

        setTitle("Power Up Quadcopter | Driver Station");   //  set window title
        setSize(1600,1000);     //  set window size
        setLocationRelativeTo(null);    //  center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //  set what clicking 'X' does

        //  make stuff unselectable
        sliderFR.setFocusable(false);
        sliderFL.setFocusable(false);
        sliderBL.setFocusable(false);
        sliderBR.setFocusable(false);

        tabs.setFocusable(false);
        tcpConnectButton.setFocusable(false);
        sendTcpBtn.setFocusable(false);
        sendUdpBtn.setFocusable(false);

        //  tcp/udp text area
        textAreaTCP.setLineWrap(true);
        textAreaUDP.setLineWrap(true);
        new SmartScroller(scrollPaneTCP, SmartScroller.VERTICAL, SmartScroller.END);

        //  add listeners
        tcpConnectButton.addActionListener(e -> GUI_C.tcpConnect_btnHandler());

        setVisible(true);
    }

}
