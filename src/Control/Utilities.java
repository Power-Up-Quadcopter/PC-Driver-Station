package Control;/*
This class is intended for functions that are not specific to any section but
are useful and can be used in different areas of the code
 */

import java.util.Date;
import java.sql.Timestamp;

public class Utilities {

    public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static Timestamp getTimestamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }

    public static String padSpaces(String s, int totalSize, boolean leftAlign) {
        String spaces = "";
        for(int i = 0; i < totalSize - s.length(); i++) {
            spaces += " ";
        }
        if(!leftAlign) return spaces + s;
        else return s + spaces;
    }

}
