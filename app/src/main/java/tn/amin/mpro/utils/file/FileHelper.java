package tn.amin.mpro.utils.file;

import android.net.Uri;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class FileHelper {
    public static long lastFileSize = -1;

    public static boolean copy(InputStream src, OutputStream dst) {
        try {
            byte[] buf = new byte[1024];
            int length = 0;
            int chunkLength;
            while ((chunkLength = src.read(buf)) > 0) {
                dst.write(buf, 0, chunkLength);
                length += chunkLength;
            }
            lastFileSize = length;
            src.close();
            dst.close();
        } catch (IOException e) {
            XposedBridge.log(e);
            return false;
        }
        return true;
    }

    public static Uri copyToCache(String dstName, InputStream src, File cacheDir) {
        File dst = copyToCacheInternal(dstName, src, cacheDir);
        if (dst == null)
            return null;
        return Uri.parse("file://" + dst.getAbsolutePath()); // Strangely Messenger doesn't accept Uris created using Uri.fromFile
    }

    private static File copyToCacheInternal(String dstName, InputStream src, File cacheDir) {
        File dstFile = new File(cacheDir, dstName);
        OutputStream dst;
        try {
            dst = new FileOutputStream(dstFile);
        } catch (IOException e) {
            return null;
        }
        boolean succeed = copy(src, dst);
        if (!succeed)
            return null;
        return dstFile;
    }

    public static long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            List values = conn.getHeaderFields().get("content-Length");
            if (values != null && !values.isEmpty()) {
                String sLength = (String) values.get(0);
                if (sLength != null) {
                    return Long.parseLong(sLength);
                }
            }
            return -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
