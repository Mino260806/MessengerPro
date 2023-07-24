package tn.amin.mpro2.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.util.theme.StaticThemeColorSupplier;
import tn.amin.mpro2.features.util.theme.ThemeInfo;
import tn.amin.mpro2.features.util.theme.Themes;
import tn.amin.mpro2.features.util.translate.TranslationInfo;
import tn.amin.mpro2.file.StorageConstants;
import tn.amin.mpro2.ui.touch.SwipeDirection;


public class ModulePreferences {
    public final SharedPreferences sp;
    public final SharedPreferences spTranslate;

    public ModulePreferences(Context context) {

        sp = context.getSharedPreferences(StorageConstants.prefName, Context.MODE_PRIVATE);
        spTranslate = context.getSharedPreferences(StorageConstants.translatePrefName, Context.MODE_PRIVATE);
        Logger.info("SharedPreferences setup !");
    }

    public boolean isCommandsEnabled() {
        return sp.getBoolean("mpro_commands", true);
    }

    public boolean isCommandsInputEnabled() {
        return sp.getBoolean("mpro_commands_send_input", true);
    }

    public boolean isFormattingEnabled() {
        return sp.getBoolean("mpro_text_format", true);
    }

    public boolean isPreventSeenEnabled() {
        return sp.getBoolean("mpro_conversation_seen", false);
    }

    public boolean isPreventTypingIndicatorEnabled() {
        return sp.getBoolean("mpro_conversation_typing_indicator", false);
    }

    public boolean isConversationLockEnabled() {
        return sp.getBoolean("mpro_conversation_lock", true);
    }

    public boolean isDefaultCameraEnabled() {
        return sp.getBoolean("mpro_image_default_camera", false);
    }

    public boolean isVerboseLoggingEnabled() {
        return sp.getBoolean("mpro_other_allow_logs", false);
    }

    public int getToolbarX() {
        return sp.getInt("mpro_toolbar_x", 10);
    }

    public int getToolbarY() {
        return sp.getInt("mpro_toolbar_y", 100);
    }

    public void setToolbarPosition(int x, int y) {
        sp.edit()
                .putInt("mpro_toolbar_x", x)
                .putInt("mpro_toolbar_y", y)
                .apply();
    }

    public boolean isConversationLocked(long threadKey) {
        return getLockedConversations().contains(String.valueOf(threadKey));
    }

    public Set<String> getLockedConversations() {
        return sp.getStringSet("mpro_locked_conversations", Collections.emptySet());
    }

     public void addLockedConversation(long threadKey) {
        Set<String> lockedConversations = new HashSet<>(getLockedConversations());

        lockedConversations.add(String.valueOf(threadKey));
        sp.edit()
            .putStringSet("mpro_locked_conversations", lockedConversations)
            .apply();
     }

    public void removeLockedConversation(long threadKey) {
        Set<String> lockedConversations = new HashSet<>(getLockedConversations());

        lockedConversations.remove(String.valueOf(threadKey));
        sp.edit()
                .putStringSet("mpro_locked_conversations", lockedConversations)
                .apply();
    }

    public void addTranslatedConversationReceived(long threadKey, TranslationInfo translationInfo) {
        spTranslate.edit()
                .putString("r:" + threadKey, translationInfo.toString())
                .apply();
    }

    public void removeTranslatedConversationReceived(long threadKey) {
        spTranslate.edit()
                .remove("r:" + threadKey)
                .apply();
    }

    public void addTranslatedConversationSent(long threadKey, TranslationInfo translationInfo) {
        spTranslate.edit()
                .putString("s:" + threadKey, translationInfo.toString())
                .apply();
    }

    public void removeTranslatedConversationSent(long threadKey) {
        spTranslate.edit()
                .remove("s:" + threadKey)
                .apply();
    }

    public TranslationInfo getTranslatedConversationReceived(long threadKey) {
        String raw = spTranslate.getString("r:" + threadKey, null);
        if (raw == null) return null;
        return TranslationInfo.fromString(raw);
    }

    public TranslationInfo getTranslatedConversationSent(long threadKey) {
        String raw = spTranslate.getString("s:" + threadKey, null);
        if (raw == null) return null;
        return TranslationInfo.fromString(raw);
    }

    public String getAiConfigModel() {
        return sp.getString("mpro_aiconfig_model", "gpt-4");
    }

    public String getAiConfigProvider() {
        return sp.getString("mpro_aiconfig_provider", "ChatgptAi");
    }

    public String getAiConfigAuthData() {
        return sp.getString("mpro_aiconfig_authdata", "");
    }

    public int getColorTheme() {
        int themeIndex = sp.getInt("mpro_ui_color_theme", 0);
        ThemeInfo themeInfo = Themes.themes[themeIndex];
        if (themeInfo.name.equals("Custom")) {
            Set<String> rawSupplier = sp.getStringSet("mpro_ui_color_theme_custom", null);
            if (rawSupplier == null) throw new RuntimeException("mpro_ui_color_theme_custom holds a null value");

            themeInfo.colorSupplier = StaticThemeColorSupplier.deserialize(rawSupplier);
        }

        return themeIndex;
    }

    public void setColorTheme(int themeIndex) {
        ThemeInfo themeInfo = Themes.themes[themeIndex];
        SharedPreferences.Editor edit = sp.edit()
                .putInt("mpro_ui_color_theme", themeIndex);
        if (themeInfo.name.equals("Custom")) {
            Set<String> rawSupplier = ((StaticThemeColorSupplier) themeInfo.colorSupplier).serialize();
            edit.putStringSet("mpro_ui_color_theme_custom", rawSupplier);
        }
        edit.apply();
    }

    public boolean isToolbarButtonVisible(String key) {
        return sp.getBoolean(key + "_toolbar", true);
    }

    public int getToolbarSummonFingersCount() {
        return Integer.parseInt(sp.getString("mpro_toolbar_summon_fingers", "1"));
    }

    public SwipeDirection getToolbarSummonSwipeDirection() {
        return SwipeDirection.valueOf(sp.getString("mpro_toolbar_summon_direction", "LEFT"));
    }

    public boolean getToolbarSummonFromEdge() {
        return sp.getBoolean("mpro_toolbar_summon_edge", true);
    }

    public boolean isLongPressOnTopSettingsEnabled() {
        return sp.getBoolean("mpro_other_settings_longpress_top", true);
    }

    public boolean getDoNotDisplayPatreon() {
        return sp.getBoolean("mpro_patreon_popup", false);
    }

    public void setDoNotDisplayPatreon(boolean enabled) {
        sp.edit()
                .putBoolean("mpro_patreon_popup", enabled)
                .apply();
    }
}
