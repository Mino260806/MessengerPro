package tn.amin.mpro2.ui.toolbar;

import tn.amin.mpro2.preference.ModulePreferences;

public class MProToolbarVisibilityProvider implements MProToolbar.VisibilityProvider {
    private final ModulePreferences mPreferences;

    public MProToolbarVisibilityProvider(ModulePreferences preferences) {
        mPreferences = preferences;
    }

    @Override
    public boolean isButtonVisible(String key) {
        return mPreferences.isToolbarButtonVisible(key);
    }
}
