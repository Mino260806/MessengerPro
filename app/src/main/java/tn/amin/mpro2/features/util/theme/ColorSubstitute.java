package tn.amin.mpro2.features.util.theme;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import tn.amin.mpro2.features.util.theme.supplier.ThemeColorSupplier;
import tn.amin.mpro2.orca.OrcaColors;

public class ColorSubstitute {
    private static final Map<Integer, ColorAttribute> mColorTypeMapLight = new HashMap<>();
    private static final Map<Integer, ColorAttribute> mColorTypeMapDark = new HashMap<>();
    private ThemeColorSupplier mColorSupplier;
    private boolean mIsLightMode = true;

    static {
        mColorTypeMapLight.put(OrcaColors.SURFACE_VARIANT_3.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_50));
        mColorTypeMapLight.put(OrcaColors.SURFACE_VARIANT_2.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_50));
        mColorTypeMapLight.put(OrcaColors.SURFACE_VARIANT_1.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_50));
        mColorTypeMapLight.put(OrcaColors.SURFACE.colorLight, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_50));
        mColorTypeMapLight.put(OrcaColors.PRIMARY_DIMMED.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_200));
        mColorTypeMapLight.put(OrcaColors.PRIMARY_VARIANT_1.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_400));
        mColorTypeMapLight.put(OrcaColors.PRIMARY_VARIANT_2.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_600));
        mColorTypeMapLight.put(OrcaColors.PRIMARY.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_400));
        mColorTypeMapLight.put(OrcaColors.SECONDARY.colorLight, new ColorAttribute(ColorNature.SECONDARY, ColorTone.TONE_500));
        mColorTypeMapLight.put(OrcaColors.TERTIARY.colorLight, new ColorAttribute(ColorNature.SECONDARY, ColorTone.TONE_500));
        mColorTypeMapLight.put(OrcaColors.EDITTEXT_INPUT_BACKGROUND.colorLight, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_50));

        mColorTypeMapDark.put(OrcaColors.SURFACE_VARIANT_3.colorDark, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_800));
        mColorTypeMapDark.put(OrcaColors.SURFACE_VARIANT_2.colorDark, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_800));
        mColorTypeMapDark.put(OrcaColors.SURFACE_VARIANT_1.colorDark, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_800));
        mColorTypeMapDark.put(OrcaColors.SURFACE.colorDark, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_900));
        mColorTypeMapDark.put(OrcaColors.PRIMARY_DIMMED.colorDark, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_800));
        mColorTypeMapDark.put(OrcaColors.PRIMARY_VARIANT_1.colorDark, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_600));
        mColorTypeMapDark.put(OrcaColors.PRIMARY_VARIANT_2.colorDark, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_400));
        mColorTypeMapDark.put(OrcaColors.PRIMARY.colorDark, new ColorAttribute(ColorNature.PRIMARY, ColorTone.TONE_500));
        mColorTypeMapDark.put(OrcaColors.SECONDARY.colorDark, new ColorAttribute(ColorNature.SECONDARY, ColorTone.TONE_500));
        mColorTypeMapDark.put(OrcaColors.TERTIARY.colorDark, new ColorAttribute(ColorNature.SECONDARY, ColorTone.TONE_500));
        mColorTypeMapDark.put(OrcaColors.EDITTEXT_INPUT_BACKGROUND.colorDark, new ColorAttribute(ColorNature.NEUTRAL, ColorTone.TONE_800));
    }

    public ColorSubstitute() {
    }

    public void setTheme(ThemeInfo themeInfo) {
        mColorSupplier = themeInfo.colorSupplier;
    }

    public @Nullable Integer substitute(Integer color) {
        Map<Integer, ColorAttribute> colorTypeMap = mIsLightMode? mColorTypeMapLight: mColorTypeMapDark;

        if (colorTypeMap.containsKey(color)) {
            ColorAttribute colorAttr = colorTypeMap.get(color);
            return mColorSupplier.getColor(colorAttr);
        }

        return null;
    }

    public void setLightMode(boolean isLightMode) {
        mIsLightMode = isLightMode;
    }

    public boolean getLightMode() {
        return mIsLightMode;
    }
}
