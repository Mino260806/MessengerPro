package tn.amin.mpro2.util;

import androidx.annotation.Nullable;

public class StringUtil {
    public static String multiply(String s, int count) {
        StringBuilder sb = new StringBuilder();
        while (count-- != 0) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String nullToEmpty(@Nullable String s) {
        if (s != null) return s;
        else return "";
    }
}
