package tn.amin.mpro.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.constants.Constants;

/**
 * Checks whether messenger version is compatible with Messenger Pro
 */
public class Compatibility {
    public static boolean isSupported(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(Constants.TARGET_PACKAGE_NAME, 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            return checkVersion(versionName, versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log(e);
            return false;
        }
    }

    private static boolean checkVersion(String version, int versionCode) {
        return getSupportedVersions().contains(version + ":" + versionCode);
    }

    private static List<String> getSupportedVersions() {
        return Collections.singletonList("350.0.0.7.89:300011227");
    }
}
