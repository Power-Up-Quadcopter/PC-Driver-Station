package Control;

import Implementation.Compat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkHandler {

    static String IP = "192.168.137.1";
    static int port = 5414;
    // TCP
    static boolean tcpConnectionInProgress;
    static Socket tcpSocket;
    static BufferedReader tcpInput;
    static PrintWriter tcpOutput;
    // UDP
    static DatagramSocket udpSocket;
    static byte[] buffer = new byte[10000];

    public static final int A1 = 1;
    public static final int A2 = 2;
    public static final int A3 = 3;
    public static final int A4 = 4;

    static boolean sendTCP(char[] buffer) {
        if(tcpSocket == null) return false;
        new Thread(() -> {
            try {
                tcpOutput.write(buffer);
                tcpOutput.flush();
                StringBuilder s = new StringBuilder();
                for (char c : buffer) s.append(c);
                Compat.log("TCP", "Sending: \"" + s + "\"");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }

    static boolean sendUDP(byte[] buffer) {
        if(udpSocket == null) udpNetworkSetup();

        new Thread(() -> {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), port);
                udpSocket.send(packet);
                StringBuilder s = new StringBuilder();
                for (byte b : buffer) s.append((char) b);
                Compat.log("UDPNetworkSetupThread", "UDP send: \"" + s + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }

    static String readTCPLine() {
        if(tcpSocket == null) return null;
        try {
            if(!tcpSocket.isConnected()) {
                Compat.log("TCP", "nope");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = tcpInput.readLine();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    static String readUDPPacket() {
        if(udpSocket == null) udpNetworkSetup();
        try {
            Compat.log("UDP", "Waiting for packet...");
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(IP), port);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            udpSocket.receive(packet);
            String received = buffer.toString();
            Compat.log("UDP RECEIVED: ", received);
            return received;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void tcpNetworkSetup() {
        if(tcpConnectionInProgress || (tcpSocket != null && tcpSocket.isConnected())) return;

        Compat.log("TCPNetworkSetupThread", "tryna do a TCP setup");
        tcpConnectionInProgress = true;
        new Thread(() -> {
            Compat.prepareLooper();
            try {
                tcpSocket = new Socket();
                tcpSocket.connect(new InetSocketAddress(IP, port), 1000);
                tcpInput = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                tcpOutput = new PrintWriter(tcpSocket.getOutputStream());

                Compat.log("TCPNetworkSetupThread", "TCP is gucci");
            } catch (ConnectException e) {
                Compat.log("TCPNetworkSetupThread", "TCP connection timed out");
            } catch (Exception e) {
                e.printStackTrace();
            }
            tcpConnectionInProgress = false;
        }).start();
    }

    static public void udpNetworkSetup() {
        if(udpSocket != null) return;

        try {
            Compat.log("UDPNetworkSetupThread", "tryna do a UDP setup");
            udpSocket = new DatagramSocket(port);
            Compat.log("UDPNetworkSetupThread", "UDP is gucci");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
