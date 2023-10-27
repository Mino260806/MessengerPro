package tn.amin.mpro2.features.util.theme.supplier;

import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import tn.amin.mpro2.features.util.theme.ColorAttribute;
import tn.amin.mpro2.features.util.theme.ColorType;

@RequiresApi(api= Build.VERSION_CODES.S)
public class DynamicThemeColorSupplier extends ThemeColorSupplier {
    Resources mResources;

    public DynamicThemeColorSupplier(Resources resources) {
        this.mResources = resources;
    }

    @Override
    public Integer getColor(ColorAttribute colorAttr) {
        switch (colorAttr.nature) {
            case PRIMARY:
                switch (colorAttr.tone) {
                    case TONE_1000:
                        return mResources.getColor(android.R.color.system_accent1_1000, null);
                    case TONE_900:
                        return mResources.getColor(android.R.color.system_accent1_900, null);
                    case TONE_800:
                        return mResources.getColor(android.R.color.system_accent1_800, null);
                    case TONE_700:
                        return mResources.getColor(android.R.color.system_accent1_700, null);
                    case TONE_600:
                        return mResources.getColor(android.R.color.system_accent1_600, null);
                    case TONE_500:
                        return mResources.getColor(android.R.color.system_accent1_500, null);
                    case TONE_400:
                        return mResources.getColor(android.R.color.system_accent1_400, null);
                    case TONE_300:
                        return mResources.getColor(android.R.color.system_accent1_300, null);
                    case TONE_200:
                        return mResources.getColor(android.R.color.system_accent1_200, null);
                    case TONE_100:
                        return mResources.getColor(android.R.color.system_accent1_100, null);
                    case TONE_50:
                        return mResources.getColor(android.R.color.system_accent1_50, null);
                    case TONE_10:
                        return mResources.getColor(android.R.color.system_accent1_10, null);
                    case TONE_0:
                        return mResources.getColor(android.R.color.system_accent1_0, null);
                }

            case SECONDARY:
                switch (colorAttr.tone) {
                    case TONE_1000:
                        return mResources.getColor(android.R.color.system_accent2_1000, null);
                    case TONE_900:
                        return mResources.getColor(android.R.color.system_accent2_900, null);
                    case TONE_800:
                        return mResources.getColor(android.R.color.system_accent2_800, null);
                    case TONE_700:
                        return mResources.getColor(android.R.color.system_accent2_700, null);
                    case TONE_600:
                        return mResources.getColor(android.R.color.system_accent2_600, null);
                    case TONE_500:
                        return mResources.getColor(android.R.color.system_accent2_500, null);
                    case TONE_400:
                        return mResources.getColor(android.R.color.system_accent2_400, null);
                    case TONE_300:
                        return mResources.getColor(android.R.color.system_accent2_300, null);
                    case TONE_200:
                        return mResources.getColor(android.R.color.system_accent2_200, null);
                    case TONE_100:
                        return mResources.getColor(android.R.color.system_accent2_100, null);
                    case TONE_50:
                        return mResources.getColor(android.R.color.system_accent2_50, null);
                    case TONE_10:
                        return mResources.getColor(android.R.color.system_accent2_10, null);
                    case TONE_0:
                        return mResources.getColor(android.R.color.system_accent2_0, null);
                }

            case TERTIARY:
                switch (colorAttr.tone) {
                    case TONE_1000:
                        return mResources.getColor(android.R.color.system_accent3_1000, null);
                    case TONE_900:
                        return mResources.getColor(android.R.color.system_accent3_900, null);
                    case TONE_800:
                        return mResources.getColor(android.R.color.system_accent3_800, null);
                    case TONE_700:
                        return mResources.getColor(android.R.color.system_accent3_700, null);
                    case TONE_600:
                        return mResources.getColor(android.R.color.system_accent3_600, null);
                    case TONE_500:
                        return mResources.getColor(android.R.color.system_accent3_500, null);
                    case TONE_400:
                        return mResources.getColor(android.R.color.system_accent3_400, null);
                    case TONE_300:
                        return mResources.getColor(android.R.color.system_accent3_300, null);
                    case TONE_200:
                        return mResources.getColor(android.R.color.system_accent3_200, null);
                    case TONE_100:
                        return mResources.getColor(android.R.color.system_accent3_100, null);
                    case TONE_50:
                        return mResources.getColor(android.R.color.system_accent3_50, null);
                    case TONE_10:
                        return mResources.getColor(android.R.color.system_accent3_10, null);
                    case TONE_0:
                        return mResources.getColor(android.R.color.system_accent3_0, null);
                }

            case NEUTRAL:
                switch (colorAttr.tone) {
                    case TONE_1000:
                        return mResources.getColor(android.R.color.system_neutral1_1000, null);
                    case TONE_900:
                        return mResources.getColor(android.R.color.system_neutral1_900, null);
                    case TONE_800:
                        return mResources.getColor(android.R.color.system_neutral1_800, null);
                    case TONE_700:
                        return mResources.getColor(android.R.color.system_neutral1_700, null);
                    case TONE_600:
                        return mResources.getColor(android.R.color.system_neutral1_600, null);
                    case TONE_500:
                        return mResources.getColor(android.R.color.system_neutral1_500, null);
                    case TONE_400:
                        return mResources.getColor(android.R.color.system_neutral1_400, null);
                    case TONE_300:
                        return mResources.getColor(android.R.color.system_neutral1_300, null);
                    case TONE_200:
                        return mResources.getColor(android.R.color.system_neutral1_200, null);
                    case TONE_100:
                        return mResources.getColor(android.R.color.system_neutral1_100, null);
                    case TONE_50:
                        return mResources.getColor(android.R.color.system_neutral1_50, null);
                    case TONE_10:
                        return mResources.getColor(android.R.color.system_neutral1_10, null);
                    case TONE_0:
                        return mResources.getColor(android.R.color.system_neutral1_0, null);
                }

            case NEUTRAL_VARIANT:
                switch (colorAttr.tone) {
                    case TONE_1000:
                        return mResources.getColor(android.R.color.system_neutral2_1000, null);
                    case TONE_900:
                        return mResources.getColor(android.R.color.system_neutral2_900, null);
                    case TONE_800:
                        return mResources.getColor(android.R.color.system_neutral2_800, null);
                    case TONE_700:
                        return mResources.getColor(android.R.color.system_neutral2_700, null);
                    case TONE_600:
                        return mResources.getColor(android.R.color.system_neutral2_600, null);
                    case TONE_500:
                        return mResources.getColor(android.R.color.system_neutral2_500, null);
                    case TONE_400:
                        return mResources.getColor(android.R.color.system_neutral2_400, null);
                    case TONE_300:
                        return mResources.getColor(android.R.color.system_neutral2_300, null);
                    case TONE_200:
                        return mResources.getColor(android.R.color.system_neutral2_200, null);
                    case TONE_100:
                        return mResources.getColor(android.R.color.system_neutral2_100, null);
                    case TONE_50:
                        return mResources.getColor(android.R.color.system_neutral2_50, null);
                    case TONE_10:
                        return mResources.getColor(android.R.color.system_neutral2_10, null);
                    case TONE_0:
                        return mResources.getColor(android.R.color.system_neutral2_0, null);
                }

            default:
                return null;
        }
    }

    @Override
    public @Nullable @ColorInt Integer getSeedColor() {
        return mResources.getColor(android.R.color.system_accent1_500);
    }
}
