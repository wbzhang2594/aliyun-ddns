package fun.lnex.tools.http;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    static Pattern ipValidationPattern = Pattern.compile("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");

    public static String getPublicIP() {
        String myPublicIP;
        try {
            myPublicIP = OneShotHttp.execute("http://ip.xdty.org");
            Matcher matcher = ipValidationPattern.matcher(myPublicIP);
            if (matcher.find()) {
                myPublicIP = matcher.group(0).toString();
            }
        } catch (IOException e) {
            System.out.println("Failed to get public ip");
            e.printStackTrace();
            myPublicIP = null;
        }

        System.out.println("public ip: " + myPublicIP);
        return myPublicIP;
    }

    public static boolean validateIP(String ip) {
        Matcher matcher = ipValidationPattern.matcher(ip);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}

