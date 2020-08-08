/*
Compatibles class

The purpose of this class is to put miscellaneous functions in here that doesn't fit well into
any other section of the code. These functions are used by higher level code that stays the same
between the PC app and Android app. Inside this class is where the functions will be different
because of low-level implementation on different systems.
 */

package Implementation;

import Control.*;
import javax.swing.*;

public class Compat
{

    //  prints a message with a timestamp
    public static void log(String label, String message) {
        System.out.printf("%s  %s: %s\n", Utilities.getTimestamp(), label, message);
    }

    //  starts repeated thread with a certain period
    //  On Android, this uses the built-in scheduler instead of creating a new thread
    public static void startThread(Runnable r, int periodMillis) {
        new Thread(() -> {
            long lastRun = 0;
            while(true) {
                long sysTime = System.currentTimeMillis();
                if(sysTime - lastRun >= periodMillis) r.run();
                else
                {
                    try { Thread.sleep(1); } catch (InterruptedException e)
                    { e.printStackTrace(); }
                }
            }
        }).start();
    }

    //  specifically for android, does nothing in pc
    public static void prepareLooper() {
//        Looper.prepare();
    }

}
