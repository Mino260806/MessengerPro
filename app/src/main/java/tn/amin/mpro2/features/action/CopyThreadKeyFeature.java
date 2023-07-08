package tn.amin.mpro2.features.action;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class CopyThreadKeyFeature extends Feature {
    public CopyThreadKeyFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.THREADKEY_COPY;
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
        return "mpro_conversation_copy_threadkey";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.QUICK_ACTION;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_copy_threadkey;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_clipboard;
    }

    @Override
    public void executeAction() {
        if (!gateway.requireThreadKey()) return;

        final String threadKeyString = String.valueOf(gateway.currentThreadKey);

        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) gateway.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        // Creates a new text clip to put your string in.
        ClipData clip = ClipData.newPlainText("OrcaThreadKey", threadKeyString);

        // Sets the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);

        Toast.makeText(gateway.getContext(), gateway.res.getText(R.string.threadkey_copied), Toast.LENGTH_SHORT).show();
    }
}
