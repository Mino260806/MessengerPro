package tn.amin.mpro2.features.util.theme.supplier;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import tn.amin.mpro2.features.util.theme.ColorAttribute;

public class CustomThemeColorSupplier extends ThemeColorSupplier {
    private StaticThemeColorSupplier mChild = null;

    public void setSeedColor(@ColorInt Integer color) {
        if (color == null) {
            mChild = null;
            return;
        }

        mChild = new StaticThemeColorSupplier(color);
    }

    @Override
    public Integer getColor(ColorAttribute colorAttr) {
        if (mChild == null) return null;
        return mChild.getColor(colorAttr);
    }

    @Nullable
    @Override
    public Integer getSeedColor() {
        if (mChild == null) return null;
        return mChild.getSeedColor();
    }
}
