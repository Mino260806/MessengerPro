package tn.amin.mpro2.features.state;
import android.net.Uri;

import androidx.annotation.Nullable;
import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.image.DefaultCameraMaster;
import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.CameraLaunchHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.MediaAttachment;
import android.content.ContentResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DefaultCameraFeature extends Feature
        implements CameraLaunchHook.CameraLaunchListener {
    public DefaultCameraFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.DEFAULT_CAMERA;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.CAMERA_LAUNCH };
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_image_default_camera";
    }

    @Override
    public HookListenerResult<Boolean> onCameraLaunch() {
        if (!isEnabled()) return HookListenerResult.ignore();

        final long threadKey = gateway.currentThreadKey;

        Logger.info("Starting default camera...");
        boolean success = DefaultCameraMaster.launchCamera(gateway.activityHook, FileHelper.generateUniqueFilename("jpg"), (imageUri) -> {
            sendImageFromUri(imageUri, threadKey);
        });

        if (success) return HookListenerResult.consume(true);
        else {
            gateway.getToaster().toast(R.string.camera_need_permission, true);
            return HookListenerResult.ignore();
        }
    }
    private void sendImageFromUri(Uri imageUri, long threadKey) {
        // Use contentResolver to get InputStream from Uri
        ContentResolver contentResolver = gateway.activityHook.currentActivity.get().getContentResolver();
        FileHelper fileHelper = new FileHelper();
        try (InputStream inputStream = contentResolver.openInputStream(imageUri)) {
            File tempFile = fileHelper.convertStreamToFile(inputStream);

            gateway.mailboxConnector.sendAttachment(new MediaAttachment(tempFile, FileHelper.getFileName(this.gateway.getActivity(), imageUri)), threadKey, 0);
        } catch (IOException e) {
            Logger.error("Failed to read image from Uri");
        }
    }
}
