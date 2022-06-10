package tn.amin.mpro.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.R;
import tn.amin.mpro.constants.Constants;

public class SettingsActivity extends AppCompatActivity {
    private SettingsFragment mSettingsFragment = null;
    private boolean mHasToCopyPreferences = false;

    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstance) {
        if (MProMain.isDarkMode(this))
            setTheme(R.style.Theme_AppCompat_DayNight);
        else
            setTheme(R.style.Theme_AppCompat_Light);

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        setResult(RESULT_OK);

        boolean fromMessenger = getIntent().getBooleanExtra("fromMessenger", false);
        int sharedPrefMode;
        if (fromMessenger)
            sharedPrefMode = Context.MODE_WORLD_READABLE;
        else {
            mHasToCopyPreferences = true;
            sharedPrefMode = Context.MODE_PRIVATE;
        }
        try {
            getSharedPreferences("tn.amin.mpro_preferences", sharedPrefMode);
        } catch (SecurityException e) {
            // If SecurityException is raised, then user is using non-root lsposed, which
            // integrates the activity inside facebook messenger, so we can open it in mode private
            getSharedPreferences("tn.amin.mpro_preferences", Context.MODE_PRIVATE);
        }

        String settingsType = getIntent().getStringExtra("settingsType");
        if (settingsType == null || settingsType.isEmpty())
            settingsType = Constants.MPRO_SETTINGS_TYPE_SETTINGS;
        mSettingsFragment = new SettingsFragment(settingsType);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings_fragment, mSettingsFragment)
                .commit();

        switch (settingsType) {
            case Constants.MPRO_SETTINGS_TYPE_POWER_CENTER:
                setTitle("Messenger Pro Power Center");
                break;
            default:
                setTitle("Messenger Pro Settings");
                break;
        }
    }
}
