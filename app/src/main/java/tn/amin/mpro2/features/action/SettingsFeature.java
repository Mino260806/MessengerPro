package tn.amin.mpro2.features.action;

import android.content.ComponentName;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.Serializable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class SettingsFeature extends Feature {
    public SettingsFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.SETTINGS_LAUNCH;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.ACTION;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[0];
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_settings";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.SETTINGS;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_settings;
    }

    @Override
    public void executeAction() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("tn.amin.mpro2", "tn.amin.mpro2.settings.SettingsActivity"));
        intent.putExtra(StorageConstants.prefName, (Serializable) gateway.pref.sp.getAll());
        intent.putExtra(StorageConstants.unobfPrefName, (Serializable) gateway.unobfuscator.getPreferences().getAll());
        intent.putExtra(StorageConstants.statePrefName, (Serializable) gateway.state.sp.getAll());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        gateway.getContext().startActivity(intent);
    }
}
