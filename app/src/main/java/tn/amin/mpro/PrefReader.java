package tn.amin.mpro;

import java.io.File;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class PrefReader {
    private static final File prefFile = new File("/data/data/tn.amin.mpro/shared_prefs/tn.amin.mpro_preferences.xml");
    private XSharedPreferences mPrefs;

    public PrefReader() {
        if (XposedBridge.getXposedVersion() < 93)
            mPrefs = new XSharedPreferences(prefFile);
        else
            mPrefs = new XSharedPreferences("tn.amin.mpro");
    }

    public void reload() {
        mPrefs.reload();
    }

    public boolean isMProEnabled() { return mPrefs.getBoolean("mpro_enabled", true); }
    public boolean isTextFormattingEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_text_format", true); }
    public boolean isCommandsEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_commands", true); }
    public boolean isDontSendCommandEnabled() { return isMProEnabled() && !mPrefs.getBoolean("mpro_commands_dont_send", true); }
    public boolean isWatermarkEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_image_watermark", false); }
    public boolean isCallConfirmationEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_other_call_confirmation", true); }
    public boolean isDoubleTapEmojiEnabled() { return isMProEnabled() && mPrefs.getBoolean("mpro_other_double_tap_emoji", false); }

    public String getWatermarkText() { return mPrefs.getString("mpro_image_watermark_text", "Messenger Pro"); }
    public int getWatermarkTextSize() { return mPrefs.getInt("mpro_image_watermark_text_size", 50); }
}
