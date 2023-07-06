package tn.amin.mpro2.features.action;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.hook.ActivityHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.builder.AttachmentBuilder;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class AttachFileFeature extends Feature {
    public AttachFileFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.FILE_ATTACH;
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
        return "mpro_conversation_attach";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.QUICK_ACTION;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_attach;
    }

    @Override
    public void executeAction() {
        if (!gateway.requireThreadKey()) return;

        final long threadKey = gateway.currentThreadKey;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        gateway.activityHook.startIntent(intent, ActivityHook.REQUESTCODE_PICKFILE, (data) -> {
            try {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    String fileName = FileHelper.getFileName(gateway.getActivity(), fileUri);
                    InputStream inputStream = gateway.getActivity().getContentResolver().openInputStream(fileUri);
                    if (inputStream != null) {
                        File outputFile = FileHelper.createTempFile(FilenameUtils.getExtension(fileName));
                        OutputStream outputStream = new FileOutputStream(outputFile);
                        FileHelper.copyFile(inputStream, outputStream);

                        gateway.mailboxConnector.sendAttachment(new MediaAttachment(outputFile, fileName, AttachmentBuilder.FILETYPE_UNKNOWN),
                                threadKey, 500);
                    }
                }
            } catch (Throwable t) {
                Logger.error(t);
            }
        });
    }
}
