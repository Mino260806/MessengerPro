package tn.amin.mpro2.orca;

import android.graphics.Color;
import android.util.Pair;

import androidx.annotation.ColorInt;

public enum OrcaColors {
    SURFACE(Color.WHITE, Color.BLACK),
    SURFACE_VARIANT_1(Color.WHITE, Color.parseColor("#222222")),
    SURFACE_VARIANT_2(Color.WHITE, Color.parseColor("#262626")),
    SURFACE_VARIANT_3(Color.parseColor("#F1F1F1"), Color.parseColor("#303030")),
    PRIMARY(Color.parseColor("#0A7CFF"), Color.parseColor("#429AFF")),
    PRIMARY_VARIANT_1(Color.parseColor("#0A7CFF"), Color.parseColor("#0A7CFF")),
    PRIMARY_VARIANT_2(Color.parseColor("#0028EF"), Color.parseColor("#0E32E5")),
    PRIMARY_DIMMED(Color.parseColor("#1B83ED"), Color.parseColor("#0F3C8A")),
    SECONDARY(Color.parseColor("#A033FF"), Color.parseColor("#A947FF")),
    TERTIARY(Color.parseColor("#FF3A33"), Color.parseColor("#FF4942")),

    EDITTEXT_INPUT_BACKGROUND(Color.parseColor("#F5F5F5"), Color.parseColor("#303030")),
    EDITTEXT_INPUT_PLACEHOLDER_COLOR(Color.parseColor("#7A7A7A"), Color.parseColor("#898989")),
    ;

    public final @ColorInt int colorLight;
    public final @ColorInt int colorDark;

    OrcaColors(int colorLight, int colorDark) {
        this.colorLight = colorLight;
        this.colorDark = colorDark;
    }

    public Pair<Integer, Integer> toIntPair() {
        return new Pair<>(colorLight, colorDark);
    }

    public Pair<String, String> toStringPair() {
        return new Pair<>(String.valueOf(colorLight), String.valueOf(colorDark));
    }
}
