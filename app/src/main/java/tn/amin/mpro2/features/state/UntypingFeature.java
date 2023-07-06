package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.TypingIndicatorSentHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class UntypingFeature extends Feature
        implements TypingIndicatorSentHook.TypingIndicatorSentListener {
    public UntypingFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.UNTYPING;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.TYPING_INDICATOR_SEND };
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_conversation_typing_indicator";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.TOGGLABLE;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_typing;
    }

    @Override
    public HookListenerResult<Boolean> onTypingIndicatorSent() {
        return HookListenerResult.consume(!isEnabled());
    }
}
