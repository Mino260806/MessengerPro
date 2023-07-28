package tn.amin.mpro2.features.util.theme.supplier;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import tn.amin.mpro2.features.util.theme.ColorAttribute;
import tn.amin.mpro2.features.util.theme.ColorNature;
import tn.amin.mpro2.features.util.theme.ColorTone;
import tn.amin.mpro2.features.util.theme.monet.ColorScheme;
import tn.amin.mpro2.features.util.theme.monet.TonalPalette;

public class StaticThemeColorSupplier extends ThemeColorSupplier {
    private final Map<ColorAttribute, Integer> mColorSchemeIndex = new HashMap<>();

    private final @ColorInt int mSeedColor;

    public StaticThemeColorSupplier(@ColorInt int seedColor) {
        mSeedColor = seedColor;
        ColorScheme colorScheme = new ColorScheme(seedColor, false);

        indexPalette(mColorSchemeIndex, ColorNature.PRIMARY, colorScheme.getAccent1());
        indexPalette(mColorSchemeIndex, ColorNature.SECONDARY, colorScheme.getAccent2());
        indexPalette(mColorSchemeIndex, ColorNature.TERTIARY, colorScheme.getAccent3());
        indexPalette(mColorSchemeIndex, ColorNature.NEUTRAL, colorScheme.getNeutral1());
        indexPalette(mColorSchemeIndex, ColorNature.NEUTRAL_VARIANT, colorScheme.getNeutral2());
    }

    private void indexPalette(Map<ColorAttribute, Integer> colorSchemeIndex, ColorNature nature, TonalPalette palette) {
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_0), Color.WHITE);
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_10), palette.getS10());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_50), palette.getS50());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_100), palette.getS100());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_200), palette.getS200());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_300), palette.getS300());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_400), palette.getS400());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_500), palette.getS500());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_600), palette.getS600());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_700), palette.getS700());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_800), palette.getS800());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_900), palette.getS900());
        colorSchemeIndex.put(new ColorAttribute(nature, ColorTone.TONE_1000), palette.getS1000());
    }

    @Override
    public Integer getColor(ColorAttribute colorAttr) {
        return mColorSchemeIndex.getOrDefault(colorAttr, null);
    }

    @Override
    public @Nullable @ColorInt Integer getSeedColor() {
        return mSeedColor;
    }
}
