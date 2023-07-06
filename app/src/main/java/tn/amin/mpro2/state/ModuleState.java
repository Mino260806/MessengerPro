package tn.amin.mpro2.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import tn.amin.mpro2.BuildConfig;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.file.StorageConstants;

public class ModuleState {
    public SharedPreferences sp = null;

    private Integer mOrcaVersion = null;

    public ModuleState(Context context) {
        sp = context.getSharedPreferences(StorageConstants.statePrefName, Context.MODE_PRIVATE);

        queryOrcaVersion(context);
    }

    public boolean hasOrcaVersionChanged() {
        if (mOrcaVersion == null) return true;

        return sp.getInt("orcaVersion", -1) != mOrcaVersion;
    }

    public boolean hasModuleVersionChanged() {
        return sp.getInt("mproVersion", -1) != BuildConfig.VERSION_CODE;
    }

    public void saveOrcaAndModuleVersion() {
        if (mOrcaVersion == null) return;

        sp.edit()
                .putInt("mproVersion", BuildConfig.VERSION_CODE)
                .apply();
        sp.edit()
                .putInt("orcaVersion", mOrcaVersion)
                .apply();
        sp.edit()
                .putBoolean("firstTime", false)
                .apply();
    }

    public boolean isFirstTime() {
        return sp.getBoolean("firstTime", true);
    }

    public long getTimeElapsed(String key) {
        long timeStart = sp.getLong(key, -1);
        if (timeStart == -1) {
            resetTimeElapsed(key);
            return 0L;
        }

        return System.currentTimeMillis() - timeStart;
    }

    public void resetTimeElapsed(String key) {
        sp.edit()
                .putLong(key, System.currentTimeMillis())
                .apply();
    }

    private boolean queryOrcaVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            mOrcaVersion = pInfo.versionCode;
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.error(e);
        }
        return false;
    }

    public int getOrcaVersion() {
        if (mOrcaVersion == null) return -1;

        return mOrcaVersion;
    }

}
