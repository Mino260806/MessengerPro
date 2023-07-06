package tn.amin.mpro2.orca.wrapper;

import java.util.ArrayList;

import de.robv.android.xposed.XposedHelpers;

public class MSGOpenPathRenderedNotification {
    private Object mObj;

    public MSGOpenPathRenderedNotification(Object object) {
        mObj = object;
    }

    public final Integer getAttachmentType() {
        return (Integer) XposedHelpers.callMethod(mObj, "getAttachmentType");
    }

    public final String getChannelId() {
        return (String) XposedHelpers.callMethod(mObj, "getChannelId");
    }

    public final Integer getChannelType() {
        return (Integer) XposedHelpers.callMethod(mObj, "getChannelType");
    }

    public final Long getClientThreadKey() {
        return (Long) XposedHelpers.callMethod(mObj, "getClientThreadKey");
    }

    public final String getEngineMessage() {
        return (String) XposedHelpers.callMethod(mObj, "getEngineMessage");
    }

    public final String getExperimentMask() {
        return (String) XposedHelpers.callMethod(mObj, "getExperimentMask");
    }

    public final String getGroupingID() {
        return (String) XposedHelpers.callMethod(mObj, "getGroupingID");
    }

    public final String getIntendedRecipientUserId() {
        return (String) XposedHelpers.callMethod(mObj, "getIntendedRecipientUserId");
    }

    public final boolean getIsGroupThread() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsGroupThread");
    }

    public final boolean getIsRenderedByEngine() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsRenderedByEngine");
    }

    public final boolean getIsSecureMessage() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsSecureMessage");
    }

    public final boolean getIsShowPreviewsEnabled() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsShowPreviewsEnabled");
    }

    public final boolean getIsSilentPush() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsSilentPush");
    }

    public final boolean getIsUnsent() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsUnsent");
    }

    public final boolean getIsVanishModeEnabled() {
        return (boolean) XposedHelpers.callMethod(mObj, "getIsVanishModeEnabled");
    }

    public final Long getLastReadWatermarkTimestampMs() {
        return (Long) XposedHelpers.callMethod(mObj, "getLastReadWatermarkTimestampMs");
    }

    public final Integer getMaximumUnreadMessagesAllowed() {
        return (Integer) XposedHelpers.callMethod(mObj, "getMaximumUnreadMessagesAllowed");
    }

    public final String getMessageClientContext() {
        return (String) XposedHelpers.callMethod(mObj, "getMessageClientContext");
    }

    public final String getMessageId() {
        return (String) XposedHelpers.callMethod(mObj, "getMessageId");
    }

    public final Long getNotifType() {
        return (Long) XposedHelpers.callMethod(mObj, "getNotifType");
    }

    public final String getNotificationId() {
        return (String) XposedHelpers.callMethod(mObj, "getNotificationId");
    }

    public final String getOrcaThreadKey() {
        return (String) XposedHelpers.callMethod(mObj, "getOrcaThreadKey");
    }

    public final String getRenderedEngineMessage() {
        return (String) XposedHelpers.callMethod(mObj, "getRenderedEngineMessage");
    }

    public final String getSenderAvatarUrl() {
        return (String) XposedHelpers.callMethod(mObj, "getSenderAvatarUrl");
    }

    public final Long getSenderId() {
        return (Long) XposedHelpers.callMethod(mObj, "getSenderId");
    }

    public final String getSenderPK() {
        return (String) XposedHelpers.callMethod(mObj, "getSenderPK");
    }

    public final String getShortcutId() {
        return (String) XposedHelpers.callMethod(mObj, "getShortcutId");
    }

    public final String getSound() {
        return (String) XposedHelpers.callMethod(mObj, "getSound");
    }

    public final Long getSoundNameInteger() {
        return (Long) XposedHelpers.callMethod(mObj, "getSoundNameInteger");
    }

    public final String getSubtitle() {
        return (String) XposedHelpers.callMethod(mObj, "getSubtitle");
    }

    public final String getThreadId() {
        return (String) XposedHelpers.callMethod(mObj, "getThreadId");
    }

    public final Long getThreadKey() {
        return (Long) XposedHelpers.callMethod(mObj, "getThreadKey");
    }

    public final String getThreadName() {
        return (String) XposedHelpers.callMethod(mObj, "getThreadName");
    }

    public final String getThreadPictureUrl() {
        return (String) XposedHelpers.callMethod(mObj, "getThreadPictureUrl");
    }

    public final Long getThreadType() {
        return (Long) XposedHelpers.callMethod(mObj, "getThreadType");
    }

    public final long getTimestampMs() {
        return (long) XposedHelpers.callMethod(mObj, "getTimestampMs");
    }

    public final String getTitle() {
        return (String) XposedHelpers.callMethod(mObj, "getTitle");
    }

    @SuppressWarnings("unchecked")
    public final ArrayList<Object> getUnreadMessages() {
        return (ArrayList<Object>) XposedHelpers.callMethod(mObj, "getUnreadMessages");
    }

    public final String getUnreadMessagesSummary() {
        return (String) XposedHelpers.callMethod(mObj, "getUnreadMessagesSummary");
    }

    public final Long getUnsentTimestampMs() {
        return (Long) XposedHelpers.callMethod(mObj, "getUnsentTimestampMs");
    }
}
