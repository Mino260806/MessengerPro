package tn.amin.mpro2.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
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
}
