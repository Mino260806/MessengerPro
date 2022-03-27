package tn.amin.mpro.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import tn.amin.mpro.R;
import tn.amin.mpro.constants.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {
    private PreferenceScreen mPreferenceScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);

        mPreferenceScreen = getPreferenceScreen();

        SwitchPreference P_Commands = mPreferenceScreen.findPreference("mpro_commands");
        SwitchPreference P_Image = mPreferenceScreen.findPreference("mpro_image_watermark");
        SwitchPreference P_MProEnabled = mPreferenceScreen.findPreference("mpro_enabled");
        Preference P_About = mPreferenceScreen.findPreference("mpro_about");

        assert P_Commands != null;
        assert P_Image != null;
        assert P_MProEnabled != null;
        assert P_About != null;

        P_Commands.setOnPreferenceChangeListener(updateSubPrefsOnChange);
        P_Image.setOnPreferenceChangeListener(updateSubPrefsOnChange);

        P_Commands.callChangeListener(P_Commands.isChecked());
        P_Image.callChangeListener(P_Image.isChecked());
        P_MProEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isChecked = (Boolean) newValue;
            for (int i=0; i < mPreferenceScreen.getPreferenceCount(); i++) {
                Preference pref = mPreferenceScreen.getPreference(i);
                String key = pref.getKey();
                if (key.equals("category_general_key")) continue;
                if (key.equals("mpro_enabled")) continue;

                pref.setEnabled(isChecked);
            }
            return true;
        });
        P_MProEnabled.callChangeListener(P_MProEnabled.isChecked());
        P_About.setOnPreferenceClickListener(preference -> {
            String url = Constants.MPRO_REPO_URL;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        });
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
    }

    private final Preference.OnPreferenceChangeListener updateSubPrefsOnChange =
            (preference, newValue) -> {
                for (int i=0; i < mPreferenceScreen.getPreferenceCount(); i++) {
                    Preference pref = mPreferenceScreen.getPreference(i);
                    String key = pref.getKey();
                    if (pref == preference) continue;
                    if (!key.startsWith(preference.getKey())) continue;

                    pref.setEnabled((Boolean) newValue);
                }
                return true;
            };
}
