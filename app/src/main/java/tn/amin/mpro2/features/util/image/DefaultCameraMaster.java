package tn.amin.mpro2.features.util.image;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.util.Consumer;

import java.io.File;

import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.hook.ActivityHook;

public class DefaultCameraMaster {
    public static boolean launchCamera(ActivityHook activityHook, File directory, Consumer<File> onCapture) {
        File imageFile = FileHelper.createTempFile("jpg", directory);
        if (imageFile != null) {
            Uri imageUri = Uri.fromFile(imageFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            // Check if there is a camera app on the device
            if (cameraIntent.resolveActivity(activityHook.currentActivity.get().getPackageManager()) != null) {
                activityHook.startIntent(cameraIntent, 2608, (intent) -> {
                    onCapture.accept(imageFile);
                });
                return true;
            }
        }
        return false;
    }
}
