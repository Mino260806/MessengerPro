package tn.amin.mpro2.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import tn.amin.mpro2.debug.Logger;

public class FileHelper {
    public static File downloadFromUrl(String url) {
        URLConnection urlConnection;
        try {
            urlConnection = new URL(url).openConnection();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int fileLength = httpsURLConnection.getContentLength();
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);

                InputStream inputStream = httpsURLConnection.getInputStream();
                File downloadedFile = createTempFile(fileExtension);
                FileOutputStream outputStream = new FileOutputStream(downloadedFile);

                copyFile(inputStream, outputStream);

                inputStream.close();
                outputStream.flush();
                outputStream.close();

                return downloadedFile;
            }

            throw new IOException("responseCode: " + responseCode);
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }

    public static File createTempFile(String extension) {
        return createTempFile(extension, StorageConstants.moduleInternalCache);
    }

    public static File createTempFile(String extension, File directory) {
        directory.mkdirs();
        try {
            return File.createTempFile("tmp",
                    "." + extension,
                    directory);
        } catch (Throwable t) {
            Logger.error(t);
            return null;
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    @SuppressLint("Range")
    public static String getFileName(Activity activity, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }
    public static Uri insertImage(ContentResolver contentResolver, String name, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, type);
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    public File convertStreamToFile(InputStream stream) throws IOException {
        File tempFile = null;
        OutputStream outStream = null;

        try {
            tempFile = this.createTempFile("jpg");

            outStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096]; // 4K buffer
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } finally {
            // Ensure streams are closed, even if there's an exception
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                Logger.error("Failed to close output stream");
            }

            try {
                stream.close();
            } catch (IOException e) {
                Logger.error("Failed to close input stream");
            }
        }

        return tempFile;
    }
    public static String generateUniqueFilename(String extension) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "IMG_" + timeStamp + "." + extension;
    }

}
