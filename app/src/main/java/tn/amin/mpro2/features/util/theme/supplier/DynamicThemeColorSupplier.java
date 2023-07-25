package tn.amin.mpro2.features.util.theme.supplier;

import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import tn.amin.mpro2.features.util.theme.ColorType;

@RequiresApi(api= Build.VERSION_CODES.S)
public class DynamicThemeColorSupplier extends ThemeColorSupplier {
    Resources mResources;

    public DynamicThemeColorSupplier(Resources resources) {
        this.mResources = resources;
    }

    @Override
    public Integer getColor(ColorType colorType) {
        switch (colorType) {
            case SURFACE_LIGHT:
                return mResources.getColor(android.R.color.system_accent1_10, null);
            case PRIMARY_LIGHT:
                return mResources.getColor(android.R.color.system_accent1_600, null);
            case SECONDARY_LIGHT:
                return mResources.getColor(android.R.color.system_accent3_600, null);

            case SURFACE_DARK:
                return mResources.getColor(android.R.color.system_accent1_1000, null);
            case PRIMARY_DARK:
                return mResources.getColor(android.R.color.system_accent1_600, null);
            case SECONDARY_DARK:
                return mResources.getColor(android.R.color.system_accent3_600, null);

            default:
                return null;
        }
    }
}
