package tn.amin.mpro2.preference;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StubSharedPreference implements SharedPreferences {
    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>();
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        return s1;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        return set;
    }

    @Override
    public int getInt(String s, int i) {
        return i;
    }

    @Override
    public long getLong(String s, long l) {
        return l;
    }

    @Override
    public float getFloat(String s, float v) {
        return v;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return b;
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
