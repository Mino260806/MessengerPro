package tn.amin.mpro2.features.util.theme;

public class ThemeInfo {
    public final String name;
    public ThemeColorSupplier colorSupplier;

    public ThemeInfo(String name, ThemeColorSupplier colorSupplier) {
        this.name = name;
        this.colorSupplier = colorSupplier;
    }
}
