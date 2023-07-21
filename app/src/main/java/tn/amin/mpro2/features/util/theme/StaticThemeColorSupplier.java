package tn.amin.mpro2.features.util.theme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticThemeColorSupplier extends ThemeColorSupplier {
    private final HashMap<ColorType, Long> mColorMap;

    StaticThemeColorSupplier(HashMap<ColorType, Long> colorMap) {
        mColorMap = colorMap;
    }

    @Override
    public Long getColor(ColorType colorType) {
        return mColorMap.getOrDefault(colorType, null);
    }

    public Set<String> serialize() {
        Set<String> stringSet = new HashSet<>();
        for (Map.Entry<ColorType, Long> colorEntry: mColorMap.entrySet()) {
            stringSet.add(colorEntry.getKey().name() + "," + colorEntry.getValue());
        }

        return stringSet;
    }

    public static StaticThemeColorSupplier deserialize(Set<String> stringSet) {
        Builder builder = new Builder();
        for (String rawColorEntry: stringSet) {
            String[] split = rawColorEntry.split(",");
            builder.addColor(ColorType.valueOf(ColorType.class, split[0]),
                    Long.valueOf(split[1]));
        }

        return builder.build();
    }

    public static class Builder {
        private final HashMap<ColorType, Long> colorMap = new HashMap<>();

        public Builder addColor(ColorType type, Integer color) {
            colorMap.put(type, color.longValue());
            return this;
        }

        public Builder addColor(ColorType type, Long color) {
            colorMap.put(type, color);
            return this;
        }

        public StaticThemeColorSupplier build() {
            return new StaticThemeColorSupplier(colorMap);
        }
    }
}
