package tn.amin.mpro2.features;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public abstract class Feature {
    protected OrcaGateway gateway;

    public Feature(OrcaGateway gateway) {
        this.gateway = gateway;
    }

    public abstract FeatureId getId();
    public abstract FeatureType getType();
    public abstract HookId[] getHookIds();

    public boolean isEnabledByDefault() {
        return true;
    }
    @Nullable
    public String getPreferenceKey() {
        return null;
    }

    @Nullable
    public ToolbarButtonCategory getToolbarCategory() {
        return null;
    }

    @Nullable
    public @DrawableRes Integer getDrawableResource() {
        return null;
    }

    public void executeAction() {  }

    public final boolean isEnabled() {
        if (getPreferenceKey() != null)
            return gateway.pref.sp.getBoolean(getPreferenceKey(), isEnabledByDefault());
        else
            return true;
    }

    public final void setEnabled(boolean activated) {
        if (getPreferenceKey() != null) {
            gateway.pref.sp.edit()
                    .putBoolean(getPreferenceKey(), activated)
                    .apply();
        }
    }
}
