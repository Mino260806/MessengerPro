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

import tn.amin.mpro.R;

public class SettingsActivity extends AppCompatActivity {
    private static SharedPreferences sp;

    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("tn.amin.mpro_preferences", Context.MODE_WORLD_READABLE);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings_fragment, new SettingsFragment())
                .commit();

        setTitle("Messenger Pro settings");
    }
}
