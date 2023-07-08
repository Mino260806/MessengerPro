package tn.amin.mpro2.features.action;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.biometric.ConversationLock;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.BlackOverlay;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class ConversationLockFeature extends Feature
        implements ConversationEnterHook.ConversationEnterListener {
    private final ConversationLock mConversationLock = new ConversationLock();
    private WeakReference<View> mBlackOverlay = new WeakReference<>(null);

    public ConversationLockFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.CONV_LOCK;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.ACTION;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.CONVERSATION_ENTER };
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_conversation_lock";
    }

    @Override
    public void onConversationEnter(Long threadKey) {
        if (!isEnabled()) return;
        if (!gateway.pref.isConversationLocked(threadKey)) return;


        boolean canSkip = mConversationLock.promptAuthentication(gateway.getContext(), () -> {
            if (mBlackOverlay.get() != null) {
                BlackOverlay.remove(mBlackOverlay.get());
            }
        }, () -> {
            gateway.getActivity().finishAffinity();
        }, false);

        if (!canSkip) {
            mBlackOverlay = new WeakReference<>(BlackOverlay.on(gateway.getActivity()));
        }
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.QUICK_ACTION;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_conv_lock;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_lock;
    }

    @Override
    public void executeAction() {
        if (!isEnabled()) {
            Toast.makeText(gateway.getContext(), gateway.res.getString(R.string.please_enable_lock), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!gateway.requireThreadKey()) return;

        final long threadKey = gateway.currentThreadKey;

        mConversationLock.promptAuthentication(gateway.getActivity(), () -> {
            if (gateway.pref.isConversationLocked(threadKey)) {
                gateway.pref.removeLockedConversation(threadKey);
            } else {
                gateway.pref.addLockedConversation(threadKey);
            }
        }, () -> {}, true);
    }
}
