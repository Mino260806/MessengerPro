package tn.amin.mpro2.features.util.theme;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tn.amin.mpro2.features.util.theme.supplier.DynamicThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.supplier.KeepThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.supplier.StaticThemeColorSupplier;

public class Themes {
    public static final ArrayList<ThemeInfo> themes = new ArrayList<>(Arrays.asList(
            new ThemeInfo("None", new KeepThemeColorSupplier()),

            new ThemeInfo("Orange", new StaticThemeColorSupplier.Builder()
                    .addColor(ColorType.SURFACE_LIGHT, Color.parseColor("#fff7e1"))
                    .addColor(ColorType.PRIMARY_LIGHT, Color.parseColor("#ffae05"))
                    .addColor(ColorType.SECONDARY_LIGHT, Color.parseColor("#ff3305"))
                    .addColor(ColorType.SURFACE_DARK, Color.parseColor("#000000"))
                    .addColor(ColorType.PRIMARY_DARK, Color.parseColor("#fe6c08"))
                    .addColor(ColorType.SECONDARY_DARK, Color.parseColor("#cc0000"))
                    .build()),

            new ThemeInfo("Purple", new StaticThemeColorSupplier.Builder()
                    .addColor(ColorType.SURFACE_LIGHT, Color.parseColor("#f6e3ff"))
                    .addColor(ColorType.PRIMARY_LIGHT, Color.parseColor("#b805ff"))
                    .addColor(ColorType.SECONDARY_LIGHT, Color.parseColor("#fb00bd"))
                    .addColor(ColorType.SURFACE_DARK, Color.parseColor("#000000"))
                    .addColor(ColorType.PRIMARY_DARK, Color.parseColor("#7300f3"))
                    .addColor(ColorType.SECONDARY_DARK, Color.parseColor("#dd00ac"))
                    .build()),

            new ThemeInfo("Teal", new StaticThemeColorSupplier.Builder()
                    .addColor(ColorType.SURFACE_LIGHT, Color.parseColor("#dcf4f0"))
//                    .addColor(ColorType.SURFACE_VARIANT_LIGHT, Color.parseColor("#a9e3d7"))
                    .addColor(ColorType.PRIMARY_LIGHT, Color.parseColor("#00bfa3"))
//                    .addColor(ColorType.PRIMARY_VARIANT_LIGHT, Color.parseColor("#00b18f"))
                    .addColor(ColorType.SECONDARY_LIGHT, Color.parseColor("#fc3838"))
                    .addColor(ColorType.SURFACE_DARK, Color.parseColor("#000000"))
                    .addColor(ColorType.PRIMARY_DARK, Color.parseColor("#008360"))
                    .addColor(ColorType.SECONDARY_DARK, Color.parseColor("#bf001d"))
                    .build()),

            new ThemeInfo("Red", new StaticThemeColorSupplier.Builder()
                    .addColor(ColorType.SURFACE_LIGHT, Color.parseColor("#ffeaed"))
                    .addColor(ColorType.PRIMARY_LIGHT, Color.parseColor("#fa3b1e"))
                    .addColor(ColorType.SECONDARY_LIGHT, Color.parseColor("#00a29e"))
                    .addColor(ColorType.SURFACE_DARK, Color.parseColor("#000000"))
                    .addColor(ColorType.PRIMARY_DARK, Color.parseColor("#bf0000"))
                    .addColor(ColorType.SECONDARY_DARK, Color.parseColor("#00574e"))
                    .build()),

            new ThemeInfo("Custom", null)));

    public static void addMonetThemeIfSupported(Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            themes.add(1, new ThemeInfo("Follow System",
                    new DynamicThemeColorSupplier(resources)));
        }
    }

    public static List<String> getThemeNames() {
        return themes.stream()
                .map(themeInfo -> themeInfo.name)
                .collect(Collectors.toList());
    }
}
