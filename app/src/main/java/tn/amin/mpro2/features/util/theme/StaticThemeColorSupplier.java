package tn.amin.mpro2.features.util.theme;

import android.graphics.Color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticThemeColorSupplier extends ThemeColorSupplier {
    private final HashMap<ColorType, Integer> mColorMap;

    StaticThemeColorSupplier(HashMap<ColorType, Integer> colorMap) {
        mColorMap = colorMap;
    }

    @Override
    public Integer getColor(ColorType colorType) {
        return mColorMap.getOrDefault(colorType, null);
    }

    public Set<String> serialize() {
        Set<String> stringSet = new HashSet<>();
        for (Map.Entry<ColorType, Integer> colorEntry: mColorMap.entrySet()) {
            stringSet.add(colorEntry.getKey().name() + "," + colorEntry.getValue());
        }

        return stringSet;
    }

    public static StaticThemeColorSupplier deserialize(Set<String> stringSet) {
        Builder builder = new Builder();
        for (String rawColorEntry: stringSet) {
            String[] split = rawColorEntry.split(",");
            builder.addColor(ColorType.valueOf(ColorType.class, split[0]),
                    Integer.valueOf(split[1]));
        }

        return builder.build();
    }

    public static class Builder {
        private final HashMap<ColorType, Integer> colorMap = new HashMap<>();

        public Builder addColor(ColorType type, Integer color) {
            colorMap.put(type, color);
            return this;
        }

        public StaticThemeColorSupplier build() {
            return new StaticThemeColorSupplier(colorMap);
        }
    }
}
