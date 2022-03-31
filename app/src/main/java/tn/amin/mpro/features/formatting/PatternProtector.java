package tn.amin.mpro.features.formatting;

import android.text.Editable;
import android.util.Pair;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternProtector {
    private static Pattern mPattern = null;
    private static ArrayList<String> protectedIndexes = new ArrayList<>();

    static public void setPattern(Pattern pattern) {
        mPattern = pattern;
    }

    public void protect(Editable text) {
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            text.delete(start, end);
            text.insert(start,
                    // We replace the url with line breaks with same length, to prevent
                    // the url from getting formatted.
                    new String(new char[end - start]).replace("\0", "\r"));
            // Storing indexes to be able to insert the url back afterwards
            protectedIndexes.add(matcher.group());
        }
    }

    public void unprotect(Editable text) {
        for (String url: protectedIndexes) {
            // Text length may change after formatting so storing position
            // of url won't be efficient. indexOf is our only way to do it
            int start = text.toString().indexOf("\r");

            text.delete(start, start + url.length());
            text.insert(start, url);
        }
        protectedIndexes.clear();
    }
}
