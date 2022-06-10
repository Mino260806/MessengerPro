package tn.amin.mpro.internal;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MapSharedPreferences implements SharedPreferences {
    private final Map<String, ?> mMap;

    public final static MapSharedPreferences emptySharedPreferences =
            new MapSharedPreferences(Collections.emptyMap());

    public MapSharedPreferences(Map<String, ?> map) {
        mMap = map;
    }

    @Override
    public Map<String, ?> getAll() {
        return mMap;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defaultValue) {
        return getObject(key, defaultValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defaultValue) {
        return getObject(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getObject(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getObject(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getObject(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getObject(key, defaultValue);
    }

    private <T> T getObject(String key, T defaultValue) {
        Object o = mMap.get(key);
        if (o != null)
            return (T) o;
        else
            return defaultValue;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }
}
