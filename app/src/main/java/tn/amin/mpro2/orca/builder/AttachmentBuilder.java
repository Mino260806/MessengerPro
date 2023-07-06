package tn.amin.mpro2.orca.builder;

import android.media.MediaMetadataRetriever;
import android.webkit.MimeTypeMap;

import androidx.exifinterface.media.ExifInterface;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Constructor;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;

public class AttachmentBuilder {
    private final Constructor<?> mAttachmentConstructor;

    private String mAbsolutePath;
    private String mFileName;
    private String mMimeType;
    private Long mFileSize;
    private Long mTime;
    private long mWidth = 0L;
    private long mHeight = 0L;
    private long mDuration = 0L;
    private long mFileType = FILETYPE_UNKNOWN;

    public static final long FILETYPE_UNKNOWN = -1L;
    public static final long FILETYPE_IMAGE = 2L;
    public static final long FILETYPE_AUDIO = 5L;
    public static final long FILETYPE_OTHER = 6L;

    public AttachmentBuilder(ClassLoader classLoader) {
        Class<?> Attachment = XposedHelpers.findClass("com.facebook.msys.mci.Attachment", classLoader);
        mAttachmentConstructor = XposedHelpers.findConstructorExact(Attachment, java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.Long.class, boolean.class, boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, byte[].class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Long.class);

        mTime = System.currentTimeMillis() * 1000;
    }

    public AttachmentBuilder setType(long type) {
        mFileType = type;
        return this;
    }

    public AttachmentBuilder setFile(File file) {
        mAbsolutePath = file.getAbsolutePath();
        mFileName = file.getName();

        mFileSize = file.length();
        mMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                FilenameUtils.getExtension(file.getName()));

        updateFileType();
        return this;
    }

    public AttachmentBuilder setFileName(String fileName) {
        mFileName = fileName;
        return this;
    }

    public AttachmentBuilder setResolution(long width, long height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public Object build() {
        updateMetadata();

        try {
            return mAttachmentConstructor.newInstance(
                    mTime.toString(), mTime.toString(), mFileType,
                    mFileName, mFileSize, null, 0L, true, false,
                    mAbsolutePath, mMimeType, mDuration, // only in video / audio
                    mAbsolutePath, mMimeType, // preview path
                    null,
                    mWidth, mHeight, // only in video / image
                    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
                    mAbsolutePath,
                    null,null,null,null,null, 0L);
        } catch (Throwable t) {
            Logger.error(t);
            return null;
        }
    }

    private void updateMetadata() {
        if (mFileType == FILETYPE_IMAGE) {
            if (mWidth == 0 || mHeight == 0) {
                try {
                    ExifInterface exif = new ExifInterface(mAbsolutePath);

                    mWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                    mHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

                } catch (Throwable t) {
                    Logger.error(t);
                }
            }
        } else if (mFileType == FILETYPE_AUDIO) {
            if (mDuration == 0) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(mAbsolutePath);
                    String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    mDuration = Integer.parseInt(durationString);
                    retriever.release();
                } catch (Throwable t) {
                    Logger.error(t);
                }
            }
        }

        Logger.info("Width: " + mWidth + ", Height: " + mHeight + ", Duration: " + mDuration);
    }

    private void updateFileType() {
        if (mFileType == FILETYPE_UNKNOWN) {
            if (mMimeType == null) {
                mFileType = FILETYPE_OTHER;
            } else if (mMimeType.startsWith("image")) {
                mFileType = FILETYPE_IMAGE;
            } else if (mMimeType.startsWith("audio")) {
                mFileType = FILETYPE_AUDIO;
            } else {
                mFileType = FILETYPE_OTHER;
            }
        }

        Logger.info("File type: " + mFileType);
    }
}
