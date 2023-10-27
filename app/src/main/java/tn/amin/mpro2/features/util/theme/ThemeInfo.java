package tn.amin.mpro2.features.util.theme;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import tn.amin.mpro2.features.util.theme.supplier.ThemeColorSupplier;

public class ThemeInfo {
    public final String name;
    public ThemeColorSupplier colorSupplier;

    public ThemeInfo(String name, ThemeColorSupplier colorSupplier) {
        this.name = name;
        this.colorSupplier = colorSupplier;
    }

    public @Nullable @ColorInt Integer getSeedColor() {
        return colorSupplier.getSeedColor();
    }
}
