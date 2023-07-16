package tn.amin.mpro2.features.util.translate;

import androidx.annotation.NonNull;

public class TranslationInfo {
    public final String source;
    public final String target;

    public TranslationInfo(String source, String target) {
        this.source = source;
        this.target = target;
    }

    @NonNull
    @Override
    public String toString() {
        return source + "," + target;
    }

    public static TranslationInfo fromString(String s) {
        String[] split = s.split(",");
        return new TranslationInfo(split[0], split[1]);
    }
}
