package tn.amin.mpro2.features.util.theme;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tn.amin.mpro2.features.util.theme.supplier.CustomThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.supplier.DynamicThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.supplier.KeepThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.supplier.StaticThemeColorSupplier;

public class Themes {
    public static final ArrayList<ThemeInfo> themes = new ArrayList<>(Arrays.asList(
            new ThemeInfo("None", new KeepThemeColorSupplier()),

            new ThemeInfo("Green", new StaticThemeColorSupplier(Color.GREEN)),

            new ThemeInfo("Magenta", new StaticThemeColorSupplier(Color.MAGENTA)),

            new ThemeInfo("Teal", new StaticThemeColorSupplier(Color.parseColor("#05998c"))),

            new ThemeInfo("Red", new StaticThemeColorSupplier(Color.RED)),
            new ThemeInfo("Yellow", new StaticThemeColorSupplier(Color.YELLOW)),
            new ThemeInfo("Custom", new CustomThemeColorSupplier())

//            new ThemeInfo("Custom", null)
    ));

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
