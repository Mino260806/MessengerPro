package tn.amin.mpro2.features.util.image;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.util.Consumer;

import java.io.File;

import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.hook.ActivityHook;

public class DefaultCameraMaster {
    public static boolean launchCamera(ActivityHook activityHook, String imageFileName, Consumer<Uri> onCapture) {
        Uri imageUri = FileHelper.insertImage(activityHook.currentActivity.get().getContentResolver(), imageFileName, "image/jpeg");
        if (imageUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            // Check if there is a camera app on the device
            if (cameraIntent.resolveActivity(activityHook.currentActivity.get().getPackageManager()) != null) {
                try {
                    activityHook.startIntent(cameraIntent, 2608, (intent) -> {
                        onCapture.accept(imageUri);
                    });
                } catch (SecurityException e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
