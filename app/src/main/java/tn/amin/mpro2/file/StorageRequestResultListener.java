package tn.amin.mpro2.file;

import android.net.Uri;

public interface StorageRequestResultListener {
    void onSuccess(Uri grantedUri);
    void onIncorrectPath();
    void onCancel();
}
