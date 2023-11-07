package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.AdBlockHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;

public class AdBlockFeature extends Feature implements AdBlockHook.AdBlockListener {

    public AdBlockFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_adblocker";
    }
    @Override
    public FeatureId getId() {
        return FeatureId.AD_BLOCKER;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] {HookId.AD_BLOCKER};
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
    @Override
    public HookListenerResult<Boolean> onAdBlocked() {
        Logger.verbose("isEnabled: " + isEnabled());
        return HookListenerResult.consume(true);
    }
}
