package tn.amin.mpro2.file;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

import tn.amin.mpro2.activity.ActivityResultListener;

/**
 * A utility class to help request access to a specific folder
 */
public class StorageAccessGranter implements ActivityResultListener {
    public static final int REQUEST_CODE = 315687;
    private final Uri mRequestedUri;
    private final String mPath;
    private StorageRequestResultListener mListener = null;

    /**
     * Constructor
     * @param path relative to /storage/emulated/0/
     */
    public StorageAccessGranter(String path) {
        mPath = path;
        mRequestedUri = DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:" + path);
    }

    public void setListener(StorageRequestResultListener listener) {
        mListener = listener;
    }

    public void requestAccess(Activity activity) {
        Intent intent = buildIntent(mRequestedUri);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    private static Intent buildIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

        return intent;
    }

    @Override
    public void onActivityResult(Intent data) {
        if (mListener == null) return;

        if (data == null) {
            mListener.onCancel();
            return;
        }

        Uri grantedUri = data.getData();
        if (grantedUri == null) {
            mListener.onCancel();
            return;
        }

        if (!grantedUri.getPath().endsWith(mPath)) {
            mListener.onIncorrectPath();
            return;
        }

        mListener.onSuccess(grantedUri);
    }
}
