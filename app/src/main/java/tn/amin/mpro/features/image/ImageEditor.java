package tn.amin.mpro.features.image;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.constants.Constants;

public class ImageEditor {
    public static Uri onImageLoaded(String path) {
        String watermarkTextString = MProMain.getPrefReader().getWatermarkText();
        int watermarkTextSize = MProMain.getPrefReader().getWatermarkTextSize();

        Context context = AndroidAppHelper.currentApplication();
        long imageID = path.hashCode() + watermarkTextString.hashCode() + watermarkTextSize;
        // This way fileName is unique based on path
        String fileName = FilenameUtils.getBaseName(path) + imageID + FilenameUtils.getExtension(path);
        File output = new File(Constants.MPRO_CACHE_DIR, fileName);
        // This method may get called twice
        if (output.exists())
            return Uri.fromFile(output);

        Bitmap bmp = BitmapFactory.decodeFile(path);
        WatermarkText watermarkText = new WatermarkText(watermarkTextString)
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setTextColor(Color.BLACK)
                //.setTextShadow(0.1f, 5, 5, Color.BLUE)
                .setTextAlpha(50)
                .setRotation(30)
                .setTextSize(watermarkTextSize);

        bmp = WatermarkBuilder
                .create(context, bmp)
                .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                .setTileMode(true)
                .getWatermark()
                .getOutputImage();

        try (FileOutputStream out = new FileOutputStream(output.getAbsolutePath())) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            // PNG is a lossless format, the compression factor (100) is ignored
            out.flush();
        } catch (IOException e) {
            XposedBridge.log(e);
        }

        return Uri.fromFile(output);
    }
}
