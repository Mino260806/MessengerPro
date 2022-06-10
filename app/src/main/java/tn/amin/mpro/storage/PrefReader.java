package tn.amin.mpro.storage;

import android.content.SharedPreferences;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.internal.MapSharedPreferences;
import tn.amin.mpro.utils.XposedHilfer;

public class PrefReader {
    private static final File prefFileRoot = new File("/data/data/tn.amin.mpro/shared_prefs/tn.amin.mpro_preferences.xml");
    private static final String dummmyPackageName = "com.facebook.orca";
    private SharedPreferences mPrefs;
    public boolean isPrefMissing = false;

    public PrefReader() {
        XSharedPreferences prefs;
        String prefPath = StorageManager.read("prefPath");
        if (prefPath == null) {
            if (XposedBridge.getXposedVersion() < 93)
                prefs = new XSharedPreferences(prefFileRoot);
            else
                prefs = new XSharedPreferences("tn.amin.mpro", "tn.amin.mpro_preferences");
        } else {
            prefs = new XSharedPreferences(new File(prefPath));
        }

        // Generally occurs with non root
        if (!prefs.getFile().exists() || !prefs.contains("mpro_enabled")) {
            isPrefMissing = true;
            mPrefs = MapSharedPreferences.emptySharedPreferences;
        } else {
            StorageManager.put("prefPath", prefs.getFile().getAbsolutePath());
            mPrefs = prefs;
        }
        reload();
    }

    public void reload() {
        if (mPrefs instanceof XSharedPreferences)
            ((XSharedPreferences) mPrefs).reload();
        else {
            MProMain.startRetrieveSharedPreferences();
        }
    }

    public boolean setSharedPreferences(String prefPath) {
        if (isPrefMissing) {
            StorageManager.put("prefPath", prefPath);
            File prefFile = new File(prefPath);
            XSharedPreferences prefs = new XSharedPreferences(prefFile);
            if (prefs.contains("mpro_enabled")) {
                StorageManager.put("prefPath", prefPath);
                mPrefs = prefs;
                isPrefMissing = false;
            }
        }
        return !isPrefMissing;
    }

    public void setSharedPreferences(Map<String, ?> map) {
//        mPrefs = new MapSharedPreferences(map);
        mPrefs = new RemotePreferences(MProMain.getContext(), "tn.amin.mpro.preferences", "pref_main");
    }

    public boolean isMProEnabled() { return mPrefs.getBoolean("mpro_enabled", true); }
    public boolean isTextFormattingEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_text_format", true); }
    public boolean isCommandsEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_commands", true); }
    public boolean isDontSendCommandEnabled() { return isMProEnabled() && !mPrefs.getBoolean("mpro_commands_dont_send", true); }
    public boolean isWatermarkEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_image_watermark", false); }
    public boolean isCallConfirmationEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_other_call_confirmation", true); }
    public boolean isDoubleTapEmojiEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_other_double_tap_emoji", false); }
    public String getOpenAiApiToken() { return mPrefs.getString("mpro_commands_openai_api_token", ""); }

    public boolean isUserBusy() { return isMProEnabled() && mPrefs.getBoolean("mpro_cc_busy", false); }
    public String getBusyEmoji() { return mPrefs.getString("mpro_cc_busy_emoji", "\uD83D\uDCF4"); }

    public String getWatermarkText() { return mPrefs.getString("mpro_image_watermark_text", "Messenger Pro"); }
    public int getWatermarkTextSize() { return mPrefs.getInt("mpro_image_watermark_text_size", 50); }
}
