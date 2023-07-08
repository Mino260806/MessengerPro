package tn.amin.mpro2.preference;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapSharedPreferences implements SharedPreferences {
    private final HashMap<String, Object> mMap;

    public final SharedPreferences.Editor fileEditor;

    public MapSharedPreferences(Map<String, ?> map, SharedPreferences.Editor fileSpEditor) {
        mMap = new HashMap<>(map);
        fileEditor = fileSpEditor;
    }

    public MapSharedPreferences(SharedPreferences sharedPreferences) {
        this(sharedPreferences.getAll(), sharedPreferences.edit());
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
        return new Editor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    public class Editor implements SharedPreferences.Editor {

        @Override
        public SharedPreferences.Editor putString(String s, @Nullable String s1) {
            mMap.put(s, s1);
            if (fileEditor != null)
                fileEditor.putString(s, s1);
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String s, @Nullable Set<String> set) {
            mMap.put(s, set);
            if (fileEditor != null)
                fileEditor.putStringSet(s, set);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String s, int i) {
            mMap.put(s, i);
            if (fileEditor != null)
                fileEditor.putInt(s, i);
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String s, long l) {
            mMap.put(s, l);
            if (fileEditor != null)
                fileEditor.putLong(s, l);
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String s, float v) {
            mMap.put(s, v);
            if (fileEditor != null)
                fileEditor.putFloat(s, v);
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String s, boolean b) {
            mMap.put(s, b);
            if (fileEditor != null)
                fileEditor.putBoolean(s, b);
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String s) {
            mMap.remove(s);
            if (fileEditor != null)
                fileEditor.remove(s);
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mMap.clear();
            if (fileEditor != null)
                fileEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            if (fileEditor != null)
                return fileEditor.commit();
            return true;
        }

        @Override
        public void apply() {
            if (fileEditor != null)
                fileEditor.apply();
        }
    }

    @SuppressWarnings("unchecked")
    public static void assignMapToSharedPreferences(SharedPreferences sharedPreferences, Map<String, ?> prefMap) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                editor.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                editor.putInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                editor.putLong(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                editor.putFloat(entry.getKey(), (Float) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Set) {
                editor.putStringSet(entry.getKey(), (Set<String>) entry.getValue());
            }
        }
        editor.apply();
    }
}
