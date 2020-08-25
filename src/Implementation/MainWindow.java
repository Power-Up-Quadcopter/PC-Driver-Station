package Implementation;

import Control.Constants;
import Control.GUI_C;
import Imports.SmartScroller;
import org.lwjgl.input.Mouse;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static Control.Constants.CONTROLLER_COMPONENT_TYPE_AXIS;
import static Control.Constants.CONTROLLER_COMPONENT_TYPE_BUTTON;

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
    public JButton sendUdpBtn;
    public JTextField tcpSendField;
    public JTextField udpSendField;
    public JButton tcpConnectButton;
    public JComboBox controllerComboBox;
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
    public JTextField textField1;
    public JTextArea controllerPropertiesTextArea;
    public JProgressBar lx_progressBar;
    public JProgressBar rx_progressBar;
    public JProgressBar ly_progressBar;
    public JProgressBar ry_progressBar;
    public JButton controllerReloadButton;
    public JLabel ctrlBtn_X;
    public JLabel ctrlBtn_Y;

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

        //  progress bar range configuration
        lx_progressBar.setMinimum(-100);
        lx_progressBar.setMaximum(100);
        ly_progressBar.setMinimum(-100);
        ly_progressBar.setMaximum(100);
        rx_progressBar.setMinimum(-100);
        rx_progressBar.setMaximum(100);
        ry_progressBar.setMinimum(-100);
        ry_progressBar.setMaximum(100);

        //  add listeners
        tcpConnectButton.addActionListener(e -> GUI_C.tcpConnect_btnHandler());
        controllerReloadButton.addActionListener(e -> ControllerHandler_I.controllerReloadButtonListener());
        controllerComboBox.addItemListener(e -> ControllerHandler_I.controllerComboBoxListener());
        ctrlBtn_A.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_A, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_B.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_B, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_X.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_X, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_Y.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_Y, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_BL.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_BL, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_BR.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_BR, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_TL.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_TL, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_TR.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_TR, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_START.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_START, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_SELECT.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_SELECT, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_UP.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_UP, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_DOWN.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_DOWN, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_LEFT.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_LEFT, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_RIGHT.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_RIGHT, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_JOYL.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_JOYL, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        ctrlBtn_JOYR.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_BTN_JOYR, CONTROLLER_COMPONENT_TYPE_BUTTON); } });
        rx_progressBar.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_AXIS_RX, CONTROLLER_COMPONENT_TYPE_AXIS); } });
        ry_progressBar.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_AXIS_RY, CONTROLLER_COMPONENT_TYPE_AXIS); } });
        lx_progressBar.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_AXIS_LX, CONTROLLER_COMPONENT_TYPE_AXIS); } });
        ly_progressBar.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            GUI_I.startControllerMappingDialog(Constants.CONTROLLER_AXIS_LY, CONTROLLER_COMPONENT_TYPE_AXIS); } });

        setVisible(true);
    }

}
