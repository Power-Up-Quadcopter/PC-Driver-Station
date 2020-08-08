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

        //  Ping
        if(millis - lastPingTime > PING_PERIOD) {
            lastPingTime = millis;
            pingTCP();
        }
    }

    public static void setMotorTest(int motor, int speed) {
        if( (motor != Constants.MOTOR_FR && motor != Constants.MOTOR_FL &&
            motor != Constants.MOTOR_BL && motor != Constants.MOTOR_BR)
            || speed < 0 || speed > 100) {
            Compat.log("setMotorTest Error: ", "Motor: " + motor + "\tSpeed: " + speed);
        }

        speed = Utilities.map(speed, 0, 100, 0, 255);

        char[] toSend = {0xB0, (char) motor, (char) speed };
        NetworkHandler.sendTCP(toSend);
    }

    public static void pingTCP() {
        char[] toSend = {0xF0};
        NetworkHandler.sendTCP(toSend);
    }

    public static void pingUDP() {
        byte[] toSend = {(byte) 0xF2};
    }

}
