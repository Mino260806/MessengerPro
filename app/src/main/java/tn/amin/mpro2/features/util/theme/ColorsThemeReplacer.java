package tn.amin.mpro2.features.util.theme;

import android.graphics.Color;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ColorsThemeReplacer {
    private final Map<Integer, ColorType> mColorTypeMapLight = initColorTypeMapLight();
    private final Map<Integer, ColorType> mColorTypeMapDark = initColorTypeMapDark();
    private ThemeColorSupplier mColorSupplier;
    private boolean mIsLightMode = true;

    public ColorsThemeReplacer() {
    }

    public void setTheme(ThemeInfo themeInfo) {
        mColorSupplier = themeInfo.colorSupplier;
    }

    private static Map<Integer, ColorType> initColorTypeMapLight() {
        HashMap<Integer, ColorType> map = new HashMap<>();
        map.put(Color.WHITE, ColorType.SURFACE_LIGHT);
        map.put(Color.parseColor("#0A7CFF"), ColorType.PRIMARY_LIGHT);
        map.put(Color.parseColor("#FF3A33"), ColorType.SECONDARY_LIGHT);
        return map;
    }

    private static Map<Integer, ColorType> initColorTypeMapDark() {
        HashMap<Integer, ColorType> map = new HashMap<>();
        map.put(Color.BLACK, ColorType.SURFACE_DARK);
        map.put(Color.parseColor("#429AFF"), ColorType.PRIMARY_DARK);
        map.put(Color.parseColor("#0A7CFF"), ColorType.PRIMARY_DARK);
        map.put(Color.parseColor("#FF4942"), ColorType.SECONDARY_DARK);
        return map;
    }

    public @Nullable Integer replaceColor(Integer color) {
        Map<Integer, ColorType> colorTypeMap = mIsLightMode? mColorTypeMapLight: mColorTypeMapDark;

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
