package Control;

import Implementation.Compat;

public class CommandHandler
{

    int PING_PERIOD = 50;

    public static void initialize() {
        Compat.startThread(CommandHandler::loop, 20);   //  50 hz
    }

    public static void loop() {
        //  Ping
        
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

    }

}
