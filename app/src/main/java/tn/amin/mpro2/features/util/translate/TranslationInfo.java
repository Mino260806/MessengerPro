package tn.amin.mpro2.features.util.translate;

import androidx.annotation.NonNull;

public class TranslationInfo {
    public final String source;
    public final String target;
    public boolean keepOriginal;

    public TranslationInfo(String source, String target, boolean keepOriginal) {
        this.source = source;
        this.target = target;
        this.keepOriginal = keepOriginal;
    }

    @NonNull
    @Override
    public String toString() {
        return source + "," + target + "," + (keepOriginal? "1": "0");
    }

    public static TranslationInfo fromString(String s) {
        String[] split = s.split(",");
        return new TranslationInfo(split[0], split[1], split[2].equals("1"));
    }
}
