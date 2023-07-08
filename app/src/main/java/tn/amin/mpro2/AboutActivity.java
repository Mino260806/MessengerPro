package tn.amin.mpro2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tn.amin.mpro2.activity.ActivityResultListener;
import tn.amin.mpro2.constants.ModuleInfo;
import tn.amin.mpro2.file.AssetsTransfer;
import tn.amin.mpro2.file.FileHelper;
import tn.amin.mpro2.file.StorageAccessGranter;
import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.file.StorageRequestResultListener;

public class AboutActivity extends AppCompatActivity {
    private final Map<Integer, ActivityResultListener> mListeners = new HashMap<>();

    private boolean mIsRequestingStorage = false;

    private SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        String descriptionText;
        try {
            descriptionText = FileHelper.readInputStream(
                    getAssets().open("about.html"));
        } catch (IOException e) {
            descriptionText = "Could not load description";
        }

        TextView descriptionTextView = findViewById(R.id.text_description);
        descriptionTextView.setText(HtmlCompat.fromHtml("" +
                descriptionText, HtmlCompat.FROM_HTML_MODE_COMPACT));

        linkCardToURL(R.id.card_patreon, ModuleInfo.LINK_PATREON_PAGE);
        linkCardToURL(R.id.card_github, ModuleInfo.LINK_GITHUB_PAGE);
        linkCardToURL(R.id.card_telegram, ModuleInfo.LINK_TELEGRAM_SUPPORT);

        mSharedPreferences = getSharedPreferences("mpro2_internal", MODE_PRIVATE);
    }

    private void linkCardToURL(@IdRes int id, String url) {
        CardView targetCard = findViewById(id);
        targetCard.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsRequestingStorage) {
            if (!hasAccessToOrcaStorage()) {
                mIsRequestingStorage = true;

                StorageAccessGranter granter = new StorageAccessGranter(StorageConstants.orcaFilesRelSdcard);
                registerRequestCode(StorageAccessGranter.REQUEST_CODE, granter);
                granter.setListener(new OrcaStorageListener());

                new AlertDialog.Builder(this)
                        .setTitle("Storage Access")
                        .setMessage("For the module to access Messenger's storage, " +
                                "you must grant permissions to use its folder")
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            granter.requestAccess(this);

                            mIsRequestingStorage = false;
                        })
                        .setCancelable(false)
                        .show();
            }
            else if (needToUpdateAssets()) {
                transferAssets(getAccessToOrcaStorage());
            }
        }
    }

    private boolean hasAccessToOrcaStorage() {
        return mSharedPreferences.contains("orca_uri");
    }

    private boolean needToUpdateAssets() {
        return BuildConfig.VERSION_CODE > mSharedPreferences.getInt("assets_version", -1);
    }

    private void saveAccessToOrcaStorage(Uri grantedUri) {
        mSharedPreferences.edit()
                .putString("orca_uri", grantedUri.toString())
                .putInt("assets_version", BuildConfig.VERSION_CODE)
                .apply();

        getContentResolver().takePersistableUriPermission(grantedUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );
    }

    private Uri getAccessToOrcaStorage() {
        return Uri.parse(mSharedPreferences.getString("orca_uri", null));
    }

    private void removeAccessToOrcaStorage() {
        mSharedPreferences.edit()
                .remove("orca_uri")
                .apply();
    }

    private void registerRequestCode(int requestCode, ActivityResultListener listener) {
        mListeners.put(requestCode, listener);
    }

    private void transferAssets(Uri targetFolder) {
        ProgressDialog dialog =
                ProgressDialog.show(this, "Please Wait", "Transferring assets");

        new Thread(() -> {
            boolean success = new AssetsTransfer(AboutActivity.this, targetFolder).transferAllAssets();

            new Handler(Looper.getMainLooper()).post(() -> {
                dialog.dismiss();

                if (!success) {
                    removeAccessToOrcaStorage();
                    toastAndLeave("Unable to transfer assets");
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (mListeners.containsKey(requestCode)) {
                mListeners.get(requestCode).onActivityResult(data);
            }
        }
    }

    private class OrcaStorageListener implements StorageRequestResultListener {

        @Override
        public void onSuccess(Uri grantedUri) {
            saveAccessToOrcaStorage(grantedUri);

            if (needToUpdateAssets()) {
                transferAssets(grantedUri);
            }
        }

        @Override
        public void onIncorrectPath() {
            toastAndLeave("Incorrect path");
        }
        @Override
        public void onCancel() {
            toastAndLeave("Operation cancelled");
        }

    }

    private void toastAndLeave(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}