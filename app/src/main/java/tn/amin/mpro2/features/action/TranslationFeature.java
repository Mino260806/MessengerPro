package tn.amin.mpro2.features.action;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.TruongMio.MeiTranslate;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.translate.TranslateConfigurationFrame;
import tn.amin.mpro2.features.util.translate.TranslationInfo;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.all.MessagesDisplayHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.TextMessage;
import tn.amin.mpro2.orca.wrapper.MessageWrapper;
import tn.amin.mpro2.orca.wrapper.MessagesCollectionWrapper;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class TranslationFeature extends Feature
        implements MessagesDisplayHook.MessageDisplayHookListener, MessageSentHook.MessageSentListener, ConversationEnterHook.ConversationEnterListener {

    private final Map<String, String> mMessageTranslations = new HashMap<>();
    private ProgressDialog mProgressDialog = null;
    private boolean mIsProgressDialogShown = false;
    private int mProgressDialogOwner = -1;

    private static final int PROGRESS_OWNER_MSG_SENT = 0;
    private static final int PROGRESS_OWNER_MSG_RECEIVED = 1;

    public TranslationFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.TRANSLATE;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.ACTION;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MESSAGES_DISPLAY, HookId.MESSAGE_SEND, HookId.CONVERSATION_ENTER };
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_conversation_translate";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.QUICK_ACTION;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_conv_translate;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_translate;
    }

    @Override
    public void executeAction() {
        if (!isEnabled()) {
            Toast.makeText(gateway.getActivity(), gateway.res.getString(R.string.please_enable_translate), Toast.LENGTH_SHORT).show();
            return;
        }
        if (gateway.requireThreadKey()) return;

        TranslateConfigurationFrame configurationFrame = new TranslateConfigurationFrame(
                gateway.getActivityWithModuleResources(), gateway, gateway.currentThreadKey);
        AlertDialog dialog = new AlertDialog.Builder(gateway.getActivity())
                .setTitle(gateway.res.getString(R.string.translate))
                .setView(configurationFrame)
                .show();
        configurationFrame.setOnSaveListener(dialog::dismiss);
    }


    @Override
    public void onConversationEnter(Long threadKey) {
        mMessageTranslations.clear();
    }

    @Override
    public HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey) {
        // User must be inside a conversation
        if (!gateway.requireThreadKey(false)) return HookListenerResult.ignore();

        // The message to be sent must be present in current conversation
        if (!Objects.equals(threadKey, gateway.currentThreadKey)) return HookListenerResult.ignore();

        // User must have saved sent messages translation configuration
        TranslationInfo translationInfo = gateway.pref.getTranslatedConversationSent(gateway.currentThreadKey);
        if (translationInfo == null) return HookListenerResult.ignore();

        updateProgressDialog("sent", PROGRESS_OWNER_MSG_SENT);

        String translation = translate(message.content, translationInfo);
        message.content = appendTranslation(message.content, translation, translationInfo.keepOriginal);

        dismissProgressDialog(PROGRESS_OWNER_MSG_SENT);

        // Will disable formatting and commands
        return HookListenerResult.consume(message);
    }

    @Override
    public void onMessageDisplay(@Nullable MessageWrapper message, int index, int count, MessagesCollectionWrapper messagesCollection) {
        // User must be inside a conversation
        if (!gateway.requireThreadKey(false)) return;

        // The message to be displayed must be present in current conversation
        if (message != null && !Objects.equals(message.getThreadKey().getFacebookThreadKey(), gateway.currentThreadKey)) return;

        // User must have saved received messages translation configuration
        TranslationInfo translationInfo = gateway.pref.getTranslatedConversationReceived(gateway.currentThreadKey);
        if (translationInfo == null) return;

        if (message != null &&
                // message content must not be blank
                message.getText() != null && !StringUtils.isBlank(message.getText()) &&
                // message must not come from the user of the device
                !Objects.equals(message.getUserKey().getUserKeyLong(), gateway.authData.getFacebookUserKey())) {
            if (!mMessageTranslations.containsKey(message.getId())) {
                Logger.verbose("[" + index + "] Translating message \"" + message.getText() + "\"");
                updateProgressDialog("received [" + index + "]", PROGRESS_OWNER_MSG_RECEIVED);
                mMessageTranslations.put(message.getId(), translate(message.getText(), translationInfo));
            }

            message.setText(appendTranslation(message.getText(), mMessageTranslations.get(message.getId()), translationInfo.keepOriginal));
        }

        Logger.verbose("Message " + index + " out of " + count + " processed");
        if (index == count - 1) {
            dismissProgressDialog(PROGRESS_OWNER_MSG_RECEIVED);
        }
    }

    private String translate(String message, TranslationInfo translationInfo) {
        try {
            return MeiTranslate.translate(message, translationInfo.source, translationInfo.target);
        } catch (Throwable t) {
            Logger.verbose("Could not translate message due to " + t.getClass().getName());
            return null;
        }
    }

    private String appendTranslation(String original, String translation, boolean keepOriginal) {
        if (keepOriginal) {
            if (translation == null) {
                translation = gateway.res.getString(R.string.translation_error);
            }
            return original + "\n----------\n" + translation;
        } else {
            if (translation == null) {
                translation = gateway.res.getString(R.string.translation_error);
                return original + "\n(" + translation + ")";
            } else {
                return translation;
            }
        }
    }

    private void updateProgressDialog(String messageDetails, int ownerId) {
        mProgressDialogOwner = ownerId;

        String message =  "Translating message " + messageDetails + " ...";
        mIsProgressDialogShown = true;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mProgressDialog == null)
                mProgressDialog = ProgressDialog.show(gateway.getActivity(), "Loading", message);
            else
                mProgressDialog.setMessage(message);
        });
    }

    private void dismissProgressDialog(int ownerId) {
        if (mProgressDialogOwner != ownerId) return;

        mIsProgressDialogShown = false;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            mProgressDialog = null;
        });
    }
}
