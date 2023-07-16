package tn.amin.mpro2.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;

import java.io.File;

public class StorageConstants {

    public static final String prefName = "mpro_pref";
    public static final String unobfPrefName = "mpro_unobfuscator";
    public static final String statePrefName = "mpro_state";
    public static final String translatePrefName = "mpro_translate";

    public static final String orcaExternalSuffix = "Android/data/com.facebook.orca";

    public static final File orcaInternalCache = new File("/data/data/com.facebook.orca/cache");
    public static final File orcaExternalFiles = new File("/storage/emulated/0/Android/data/com.facebook.orca");

    public static final File moduleFiles = new File(orcaExternalFiles, "mpro_files");
    public static final File moduleInternalCache = new File(orcaInternalCache, "mpro_cache");
    public static final File modulePictures = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MessengerPro");

    public static final Uri orcaUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Android/data/com.facebook.orca");
    public static final String orcaFilesRelSdcard = "Android/data/com.facebook.orca";

    static {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        moduleFiles.mkdirs();
        moduleInternalCache.mkdirs();
        modulePictures.mkdirs();
    }

    public static SharedPreferences getHookStatePref(Context context) {
        return context.getSharedPreferences(StorageConstants.statePrefName, Context.MODE_PRIVATE);
    }
}
