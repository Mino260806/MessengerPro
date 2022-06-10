package tn.amin.mpro.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;

public class RetrieveSharedPreferencesActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = new Intent();
        intent.putExtra("prefPath", getPreferencesPath(prefs));
        intent.putExtra("prefMap", new HashMap(prefs.getAll()));
        setResult(RESULT_OK, intent);
        finish();
    }

    private static String getPreferencesPath(SharedPreferences sp) {
        Field f; //NoSuchFieldException
        try {
            f = sp.getClass().getDeclaredField("mFile");
            f.setAccessible(true);
            File mFile = (File) f.get(sp); //IllegalAccessException
            Log.d("LSPosed-Bridge", "Returning preferences with path: " + mFile.getAbsolutePath());
            return mFile.getAbsolutePath();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
