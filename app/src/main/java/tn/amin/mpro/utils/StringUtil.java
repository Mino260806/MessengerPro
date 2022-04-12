package tn.amin.mpro.utils;

public class StringUtil {
    public static String multiply(String s, int count) {
        StringBuilder sb = new StringBuilder();
        while (count-- != 0) {
            sb.append(s);
        }
        return sb.toString();
    }
}
