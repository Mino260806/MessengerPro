package tn.amin.mpro2.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.util.biometric.ConversationLock;
import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.orca.OrcaBridge;
import tn.amin.mpro2.preference.MapSharedPreferences;
import tn.amin.mpro2.settings.hookstate.HookStateFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (!getIntent().hasExtra("mpro_pref")) {
            Toast.makeText(this, "Please launch settings from messenger toolbar", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> normalPrefMap = (Map<String, Object>) getIntent().getSerializableExtra(StorageConstants.prefName);
        @SuppressWarnings("unchecked")
        Map<String, Object> unobfPrefMap = (Map<String, Object>) getIntent().getSerializableExtra(StorageConstants.unobfPrefName);
        @SuppressWarnings("unchecked")
        Map<String, Object> statePrefMap = (Map<String, Object>) getIntent().getSerializableExtra(StorageConstants.statePrefName);

        SharedPreferences normalSharedPreferences = new MapSharedPreferences(normalPrefMap, null);
        SharedPreferences unobfuscatorSharedPreferences = new MapSharedPreferences(unobfPrefMap, null);
        SharedPreferences stateSharedPreferences = new MapSharedPreferences(statePrefMap, null);

        Map<String, SharedPreferences> sharedPreferences = new HashMap<>();
        sharedPreferences.put(StorageConstants.prefName, normalSharedPreferences);
        sharedPreferences.put(StorageConstants.unobfPrefName, unobfuscatorSharedPreferences);
        sharedPreferences.put(StorageConstants.statePrefName, stateSharedPreferences);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(sharedPreferences, SettingsType.ROOT))
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(o -> {
            Map<String, Map<String, ?>> allPreferences = new HashMap<>();
            for (Map.Entry<String, SharedPreferences> entry: sharedPreferences.entrySet()) {
                allPreferences.put(entry.getKey(), entry.getValue().getAll());
            }

            OrcaBridge.reloadPreferences(this, allPreferences);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                else
                    finish();
                break;
        }
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final SettingsType mSettingsType;
        private final Map<String, SharedPreferences> mSharedPreferences;
        private final String mActiveSharedPreferencesName;

        public SettingsFragment(Map<String, SharedPreferences> sharedPreferences, SettingsType settingsType) {
            mSharedPreferences = sharedPreferences;
            mSettingsType = settingsType;

            switch (settingsType) {
                case UNOBFUSCATOR:
                case API_CODES:
                    mActiveSharedPreferencesName = StorageConstants.unobfPrefName;
                    break;
                case HOOK_STATE:
                    mActiveSharedPreferencesName = StorageConstants.statePrefName;
                    break;
                default:
                    mActiveSharedPreferencesName = StorageConstants.prefName;
                    break;
            }
        }

        private void assignSharedPreferences(PreferenceManager manager) {
            try {
                Field sharedPreferencesField = PreferenceManager.class.getDeclaredField("mSharedPreferences");
                sharedPreferencesField.setAccessible(true);
                sharedPreferencesField.set(manager, getActiveSharedPreferences());
            } catch (Throwable t) {
                Logger.error(t);
            }
        }

        private SharedPreferences getActiveSharedPreferences() {
            return mSharedPreferences.get(mActiveSharedPreferencesName);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager manager = getPreferenceManager();
            assignSharedPreferences(manager);

            switch (mSettingsType) {
                case ROOT:
                    displayRootSettings();
                    break;

                case FEATURES:
                    displayFeaturesSettings();
                    break;

                case AI_CONFIG:
                    displayAiConfigSettings();
                    break;

                case TOOLBAR:
                    displayToolbarSettings();
                    break;

                case ADVANCED:
                    displayAdvancedSettings();
                    break;

                case UNOBFUSCATOR:
                    displayUnobfuscatorSettings();
                    break;

                case API_CODES:
                    displayApiCodesSettings();
                    break;

                case HOOK_STATE:
                    // another Fragment is used
                    break;
            }
        }

        private void displayApiCodesSettings() {
            addPreferencesFromResource(R.xml.preferences_apicodes);
        }

        private void displayUnobfuscatorSettings() {
            addPreferencesFromResource(R.xml.preferences_unobfuscator);
        }

        private void displayAdvancedSettings() {
            addPreferencesFromResource(R.xml.preferences_advanced);

            linkPreferenceToFragment("mpro_advanced_unobfuscator", SettingsType.UNOBFUSCATOR, "fragUnobfuscator");
            linkPreferenceToFragment("mpro_advanced_apicodes", SettingsType.API_CODES, "fragApiCodes");
            linkPreferenceToFragment("mpro_advanced_hookstate", new HookStateFragment(mSharedPreferences.get(StorageConstants.statePrefName)), "fragApiCodes");
        }

        private void displayToolbarSettings() {
            addPreferencesFromResource(R.xml.preferences_toolbar);

            DropDownPreference preferenceFingers = Objects.requireNonNull(findPreference("mpro_toolbar_summon_fingers"));
            SwitchPreference preferenceFromEdge = Objects.requireNonNull(findPreference("mpro_toolbar_summon_edge"));
            Preference.OnPreferenceChangeListener preferenceFingersListener = (preference, newValue) -> {
                if ("1".equals(newValue)) {
                    preferenceFromEdge.setChecked(true);
                    preferenceFromEdge.setEnabled(false);
                    preferenceFromEdge.setSummary(R.string.use_multi_finger_to_disable);
                } else {
                    preferenceFromEdge.setEnabled(true);
                    preferenceFromEdge.setSummary(null);
                }

                return true;
            };
            preferenceFingers.setOnPreferenceChangeListener(preferenceFingersListener);
            preferenceFingersListener.onPreferenceChange(preferenceFingers, preferenceFingers.getValue());
        }

        public void displayFeaturesSettings() {
            addPreferencesFromResource(R.xml.preferences_features);

            SwitchPreference enableConversationLockPreference = Objects.requireNonNull(findPreference("mpro_conversation_lock"));
            enableConversationLockPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                        ConversationLock lock = new ConversationLock();
                        lock.promptAuthentication(getContext(), () -> {
                            enableConversationLockPreference.setChecked((Boolean) newValue);
                        }, ()->{}, true);

                        return false;
                    });

            SwitchPreference customThemePreference = Objects.requireNonNull(findPreference("mpro_ui_color_theme_enable"));
            customThemePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                        if (!(Boolean) newValue) {
                            getActiveSharedPreferences().edit()
                                    .putInt("mpro_ui_color_theme", 0)
                                    .apply();
                        }
                        return true;
                    });

            linkPreferenceToFragment("mpro_commands_ai_config", SettingsType.AI_CONFIG, "fragAiConfig");
        }

        private void displayAiConfigSettings() {
            addPreferencesFromResource(R.xml.preferences_aiconfig);
        }

        private void displayRootSettings() {
            addPreferencesFromResource(R.xml.preferences_root);

            linkPreferenceToFragment("mpro_root_features", SettingsType.FEATURES, "fragPreferences");
            linkPreferenceToFragment("mpro_root_toolbar", SettingsType.TOOLBAR, "fragToolbar");
            linkPreferenceToFragment("mpro_root_advanced", SettingsType.ADVANCED, "fragAdvanced");
            linkPreferenceToActivity("mpro_root_about", new ComponentName("tn.amin.mpro2", "tn.amin.mpro2.AboutActivity"));
        }

        private void linkPreferenceToFragment(String key, Fragment fragment, String backStackName) {
            Preference targetPreference = findPreference(key);
            Objects.requireNonNull(targetPreference)
                    .setOnPreferenceClickListener(preference -> {
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.settings, fragment)
                                .addToBackStack(backStackName)
                                .commit();
                        return true;
                    });
        }

        private void linkPreferenceToFragment(String key, SettingsType type, String backStackName) {
            linkPreferenceToFragment(key, new SettingsFragment(mSharedPreferences, type), backStackName);
        }

        private void linkPreferenceToActivity(String key, ComponentName componentName) {
            Preference targetPreference = findPreference(key);
            Objects.requireNonNull(targetPreference)
                    .setOnPreferenceClickListener(preference -> {
                        Intent intent = new Intent();
                        intent.setComponent(componentName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        requireContext().startActivity(intent);
                        return true;
                    });
        }
    }
}