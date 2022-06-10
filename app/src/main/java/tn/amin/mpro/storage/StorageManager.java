package tn.amin.mpro.storage;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import tn.amin.mpro.MProMain;

public class StorageManager {
    public static final String PAPER_LOCKED_THREAD_KEYS = "locked_thread_keys";
    public static final HashMap<String, Object> data = new HashMap();

    public static void put(String key, Object object) {
        data.put(key, object);
    }

    public static void save() {
        for (Map.Entry keyValue: data.entrySet()) {
            Paper.book().write((String) keyValue.getKey(), keyValue.getValue());
        }
    }

    public static <T> T read(String key) {
        return Paper.book().read(key, null);
    }

    public static <T> T read(String key, T defaultValue) {
        return Paper.book().read(key, defaultValue);
    }

    public static void init() {
        Paper.init(MProMain.getContext());
    }
}
