package tn.amin.mpro2.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.file.FileHelper;

public class BitmapUtil {
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getBitmapFromUrl(String url) {
        InputStream inputStream = FileHelper.getInputStreamFromUrl(url);
        if (inputStream == null) return null;

        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap convertTransparentToWhiteBackground(Bitmap source, int padding) {
        // Create a new Bitmap with the same dimensions and a white background
        Bitmap whiteBackgroundBitmap = Bitmap.createBitmap(
                source.getWidth() + padding * 2,
                source.getHeight() + padding * 2,
                Bitmap.Config.ARGB_8888
        );

        // Create a Canvas and draw the white background
        Canvas canvas = new Canvas(whiteBackgroundBitmap);
        canvas.drawColor(Color.WHITE);

        // Draw the original transparent Bitmap on top of the white background
        canvas.drawBitmap(source, padding, padding, null);

        return whiteBackgroundBitmap;
    }

    public static boolean saveBitmapAsJPEG(Bitmap bitmap, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }

        return true;
    }
}
