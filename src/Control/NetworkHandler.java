package Control;

import Implementation.Compat;

import java.io.*;
import java.net.*;

public class NetworkHandler {

//    static String IP = "192.168.1.1";
    static String IP = "localhost";
    static int port = 5414;
    // TCP
    static boolean tcpConnectionInProgress;
    static Socket tcpSocket;
    static InputStream tcpInput;
    static OutputStream tcpOutput;
    // UDP
    static DatagramSocket udpSocket;
    static byte[] buffer = new byte[10000];

    public static final int A1 = 1;
    public static final int A2 = 2;
    public static final int A3 = 3;
    public static final int A4 = 4;

    static boolean sendTCP(byte[] buffer) {
        if(tcpSocket == null || tcpConnectionInProgress) return false;
        new Thread(() -> {
            try {
                tcpOutput.write(buffer);
                tcpOutput.flush();
                StringBuilder s = new StringBuilder();
                for (byte b : buffer) s.append((int)b + " ");
                Compat.log("TCP", "Sending: " + s.toString().trim());
            }
            catch (Exception e) {
                tcpSocket = null;
//                e.printStackTrace();
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

    static char readTCPByte() {
        if(tcpSocket == null || tcpConnectionInProgress) return 0;
        try {
            if(!tcpSocket.isConnected()) {
                Compat.log("TCP", "not actually connected");
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return (char) tcpInput.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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

        Compat.log("TCPNetworkSetupThread", "Attempting TCP connection...");
        tcpConnectionInProgress = true;
        new Thread(() -> {
            Compat.prepareLooper();
            try {
                tcpSocket = new Socket();
                tcpSocket.connect(new InetSocketAddress(IP, port), 1000);
                tcpInput = tcpSocket.getInputStream();
                tcpOutput = tcpSocket.getOutputStream();

                Compat.log("TCPNetworkSetupThread", "TCP is gucci");
            } catch (SocketTimeoutException e) {
                Compat.log("TCPNetworkSetupThread", "TCP connection timed out");
                tcpSocket = null;
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
