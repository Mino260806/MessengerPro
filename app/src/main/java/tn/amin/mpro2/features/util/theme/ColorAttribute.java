package tn.amin.mpro2.features.util.theme;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ColorAttribute {
    public final ColorNature nature;
    public final ColorTone tone;

    public ColorAttribute(ColorNature nature, ColorTone tone) {
        this.nature = nature;
        this.tone = tone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nature, tone);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ColorAttribute) {
            ColorAttribute other = (ColorAttribute) obj;
            return nature == other.nature && tone == other.tone;
        }
        return false;
    }
}
