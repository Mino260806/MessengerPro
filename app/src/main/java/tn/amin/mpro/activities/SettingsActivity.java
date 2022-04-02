package tn.amin.mpro.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.prefs.Preferences;

import tn.amin.mpro.MProMain;
import tn.amin.mpro.R;

public class SettingsActivity extends AppCompatActivity {
    private static SharedPreferences sp;

    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstance) {
        if (MProMain.isDarkMode(this))
            setTheme(R.style.Theme_AppCompat_DayNight);
        else
            setTheme(R.style.Theme_AppCompat_Light);

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);

        boolean fromMessenger = getIntent().getBooleanExtra("fromMessenger", false);
        int sharedPrefMode;
        if (fromMessenger)
            sharedPrefMode = Context.MODE_WORLD_READABLE;
        else
            sharedPrefMode = Context.MODE_PRIVATE;
        sp = getSharedPreferences("tn.amin.mpro_preferences", sharedPrefMode);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings_fragment, new SettingsFragment())
                .commit();

        setTitle("Messenger Pro settings");
    }
}
