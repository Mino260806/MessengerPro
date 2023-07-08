package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.SeenIndicatorHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class UnseenFeature extends Feature
        implements SeenIndicatorHook.SeenIndicatorListener {
    public UnseenFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.UNSEEN;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.SEEN_INDICATOR_SEND};
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_conversation_seen";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.TOGGLABLE;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_prevent_seen_description;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_unseen;
    }

    @Override
    public HookListenerResult<Boolean> onSeenIndicator() {
        Logger.verbose("isEnabled: " + isEnabled());
        return HookListenerResult.consume(!isEnabled());
    }
}
