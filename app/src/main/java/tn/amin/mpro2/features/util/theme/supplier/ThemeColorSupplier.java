package tn.amin.mpro2.features.util.theme.supplier;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import tn.amin.mpro2.features.util.theme.ColorAttribute;
import tn.amin.mpro2.features.util.theme.ColorType;

abstract public class ThemeColorSupplier {
    abstract public Integer getColor(ColorAttribute colorAttr);

    public abstract @Nullable @ColorInt Integer getSeedColor();
}
