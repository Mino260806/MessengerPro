package tn.amin.mpro2.features.util.theme;

import android.graphics.Color;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ColorsThemeReplacer {
    private final Map<Long, ColorType> mColorTypeMapLight = initColorTypeMapLight();
    private final Map<Long, ColorType> mColorTypeMapDark = initColorTypeMapDark();
    private ThemeColorSupplier mColorSupplier;
    private boolean mIsLightMode = true;

    public ColorsThemeReplacer() {
    }

    public void setTheme(ThemeInfo themeInfo) {
        mColorSupplier = themeInfo.colorSupplier;
    }

    private static Map<Long, ColorType> initColorTypeMapLight() {
        HashMap<Long, ColorType> map = new HashMap<>();
        map.put((long) Color.WHITE, ColorType.SURFACE_LIGHT);
        map.put((long) Color.parseColor("#0A7CFF"), ColorType.PRIMARY_LIGHT);
        map.put((long) Color.parseColor("#FF3A33"), ColorType.SECONDARY_LIGHT);
        return map;
    }

    private static Map<Long, ColorType> initColorTypeMapDark() {
        HashMap<Long, ColorType> map = new HashMap<>();
        map.put((long) Color.BLACK, ColorType.SURFACE_DARK);
        map.put((long) Color.parseColor("#429AFF"), ColorType.PRIMARY_DARK);
        map.put((long) Color.parseColor("#0A7CFF"), ColorType.PRIMARY_DARK);
        map.put((long) Color.parseColor("#FF4942"), ColorType.SECONDARY_DARK);
        return map;
    }

    public @Nullable Long replaceColor(Long color) {
        Map<Long, ColorType> colorTypeMap = mIsLightMode? mColorTypeMapLight: mColorTypeMapDark;

        if (colorTypeMap.containsKey(color)) {
            ColorType type = colorTypeMap.get(color);
            return mColorSupplier.getColor(type);
        }

        return null;
    }

    public void setLightMode(boolean isLightMode) {
        mIsLightMode = isLightMode;
    }
}
