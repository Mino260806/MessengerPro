package tn.amin.mpro.builders;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.constants.Constants;
import tn.amin.mpro.utils.file.FileHelper;

public class MediaResourceBuilder extends ObjectBuilder {
    private Object mMediaResourceInitializer;

    public MediaResourceBuilder() {
        mMediaResourceInitializer = XposedHelpers.newInstance(MProMain.getReflectedClasses().X_MediaResourceInitilizer);
    }

    public MediaResourceBuilder setUri(Uri uri) {
        setFieldInternal("A0E", uri);
        return this;
    }

    public MediaResourceBuilder setFileName(String fileName) {
        setFieldInternal("A0e", fileName);
        return this;
    }

    public MediaResourceBuilder setFileSize(long fileSize) {
        setFieldInternal("A07", fileSize);
        return this;
    }

    public MediaResourceBuilder setType(String type) {
        Enum mediaResourceType = Enum.valueOf(MProMain.getReflectedClasses().X_MediaResourceType, type);
        setFieldInternal("A0P", mediaResourceType);
        return this;
    }

    public MediaResourceBuilder setMimeType(String mimeType) {
        setFieldInternal("A0g", mimeType);
        return this;
    }

    public Object build() {
        return XposedHelpers.newInstance(MProMain.getReflectedClasses().X_MediaResource, getWrapper());
    }

    @Override
    protected Object getWrapper() {
        return mMediaResourceInitializer;
    }

    public static MediaResourceBuilder createFromFile(String fileName, InputStream inputStream) {
        File tmpDir = Constants.MPRO_CACHE_DIR;
        Uri uri = FileHelper.copyToCache(fileName, inputStream, tmpDir);
        long fileSize = FileHelper.lastFileSize;

        return new MediaResourceBuilder()
                .setUri(uri)
                .setFileName(fileName)
                .setFileSize(fileSize)
                .setType("OTHER")
                .setMimeType("*/*");
    }

    public static MediaResourceBuilder createFromUrl(String url) {
        Uri uri = null;
        long fileSize = -1;
        String fileName = "";
        try {
            uri = Uri.parse(url);
            fileSize = FileHelper.getFileSize(new URL(url));
            fileName = uri.getLastPathSegment();
        } catch (MalformedURLException ignored) {
        }

        return new MediaResourceBuilder()
                .setUri(uri)
                .setFileName(fileName)
                .setFileSize(fileSize)
                .setType("OTHER")
                .setMimeType("*/*");
    }
}
