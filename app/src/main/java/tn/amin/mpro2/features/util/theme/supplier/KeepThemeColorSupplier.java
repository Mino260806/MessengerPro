package tn.amin.mpro2.features.util.theme.supplier;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import tn.amin.mpro2.features.util.theme.ColorAttribute;
import tn.amin.mpro2.features.util.theme.ColorType;

public class KeepThemeColorSupplier extends ThemeColorSupplier {
    @Override
    public Integer getColor(ColorAttribute colorAttr) {
        return null;
    }

    @Override
    public @Nullable @ColorInt Integer getSeedColor() {
        return null;
    }
}
