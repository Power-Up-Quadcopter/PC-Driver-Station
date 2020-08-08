package Control;

import Implementation.Compat;

public class CommandHandler
{

    static int PING_PERIOD = 300;
    static long lastPingTime = 0;

    public static void initialize() {
        Compat.startThread(CommandHandler::loop, 20);   //  50 hz
    }

    public static void loop() {
        long millis = System.currentTimeMillis();

        //  receive TCP
        char tcpHeader = NetworkHandler.readTCPByte();

        if(tcpHeader != 0) {
            //  ========================================================================
            //  Command and Data Handling goes here

            //  ping!
            if (tcpHeader == 0xF0)
            {
                // pong!
                pongTCP();
            }
        }
    }

    public static void setMotorTest(int motor, int speed) {
        if( (motor != Constants.MOTOR_FR && motor != Constants.MOTOR_FL &&
            motor != Constants.MOTOR_BL && motor != Constants.MOTOR_BR)
            || speed < 0 || speed > 100) {
            Compat.log("setMotorTest Error: ", "Motor: " + motor + "\tSpeed: " + speed);
        }

        speed = Utilities.map(speed, 0, 100, 0, 255);

        byte[] toSend = { (byte) 0xB0, (byte) motor, (byte) speed };
        NetworkHandler.sendTCP(toSend);
    }

    public static void pongTCP() {
        byte[] toSend = { (byte) 0xF1};
        NetworkHandler.sendTCP(toSend);
        Compat.log("CDH", "Pong!");
    }

    public static void pongUDP() {
        byte[] toSend = {(byte) 0xF3};
    }

}
