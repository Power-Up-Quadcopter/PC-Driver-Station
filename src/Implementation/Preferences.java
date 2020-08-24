package Implementation;

import java.io.*;
import java.util.HashMap;

public class Preferences {

    static final String FILENAME = "saved_data.dat";
    static final String FILENAME_TEMPCOPY = "temp";
    static final String HEADER_TEXT = "// Keys and data are formatted like so: \"KEY:DATA\"\n" +
                                    "// Lines can be commented out using double backslashes\n\n";

    static final String KEY_REGEX = "[a-zA-Z0-9 _.,+-_'\"/();]+";
    static final String VALUE_REGEX = "[a-zA-Z0-9 .,+-_'\"/();]+";

    static HashMap<String, String> map = new HashMap<>();  //  data is saved in key, value format

    public static void intialize() {
        try {
            File f = new File(FILENAME);

            if (!f.exists()) {
                //  create blank file if it doesn't exist
                PrintWriter pw = new PrintWriter(f);
                pw.print(HEADER_TEXT);
                pw.close();
            }

            //  read all data
            BufferedReader br = new BufferedReader(new FileReader(f));
            while(br.ready()) {
                String line = br.readLine();
                line = line.trim();  // trim whitespace
                if(line.startsWith("//") || line.length() == 0) continue;  //  check if comment/blank
                if(!line.matches("^(" + KEY_REGEX + ":" + VALUE_REGEX + ")")) {
                    //  checks for alphanumeric, value can be float
                    //  allows underscore in key, spaces in value
                    //  checks for colon in between
                    continue;
                }

                //  get key and value
                String[] pair = line.split(":");
                String key = pair[0].toUpperCase();
                String value = pair[1];

                //  check if value has key that contains period but isn't actually float
//                if(value.contains(".") && (!value.matches("^[0-9]*.[0-9]*") || value.length() == 1)) continue;

                //  all checks passed at this point, record down data. duplicates will be overwritten
                map.put(key, value);
            }
            br.close();
            f.delete();
            rewriteFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rewriteFile() {
        try {
            //  write to copy file, rewrite everything to only keep valid data
            File copy = new File(FILENAME_TEMPCOPY);
            PrintWriter copy_pw = new PrintWriter(copy);

            copy_pw.print(HEADER_TEXT);

            for (String key : map.keySet()) {
                copy_pw.print(String.format("%s:%s\n", key, map.get(key) + ""));
            }

            copy_pw.close();

            File original = new File(FILENAME);
            if(original.exists()) original.delete();

            copy.renameTo(original);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save(String key, int value) {
        save(key, value+"");
    }

    public static void save(String key, double value) {
        save(key, value+"");
    }

    public static void save(String key, String value) {
        key = key.toUpperCase();
        if(!key.matches(KEY_REGEX)) {
            Compat.log("Preferences ERROR", "Key invalid: " + key);
            return;
        }
        if(!value.matches(VALUE_REGEX)) {
            Compat.log("Preferences ERROR", "Value invalid: " + value);
            return;
        }

        //  checks passed, add to map and file
        map.put(key, value);
        rewriteFile();
    }

    public static int get(String key, int backup) {
        return Integer.parseInt(get(key, backup+""));
    }

    public double get(String key, double backup) {
        return Double.parseDouble(get(key, backup+""));
    }

    public static String get(String key, String backup) {
        key = key.toUpperCase();
        if(!key.matches(KEY_REGEX)) {
            Compat.log("Preferences error", "get(): Invalid key: \"" + key + "\"");
        }

        if(map.containsKey(key)) {
            return map.get(key);
        }
        else {
            //  entry doesn't exist yet, add to map and return backup
            map.put(key, backup);
            rewriteFile();
            return backup;
        }
    }

}
