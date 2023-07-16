package tn.amin.mpro2.features.action;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

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
    private boolean isProgressDialogShown = false;

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
        if (!gateway.requireThreadKey()) return;

        TranslateConfigurationFrame configurationFrame = new TranslateConfigurationFrame(gateway.getActivityWithModuleResources(), gateway, gateway.currentThreadKey);
        AlertDialog dialog = new AlertDialog.Builder(gateway.getActivity())
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
        return HookListenerResult.ignore();
    }

    @Override
    public void onMessageDisplay(@Nullable MessageWrapper message, int index, int count, MessagesCollectionWrapper messagesCollection) {
        if (!gateway.requireThreadKey(false)) return;

        TranslationInfo translationInfo = gateway.pref.getTranslatedConversationReceived(gateway.currentThreadKey);
        Logger.verbose("TranslationInfo : " + translationInfo);
        if (translationInfo == null) return;

        if (message != null &&
                message.getText() != null && !StringUtils.isBlank(message.getText()) &&
                !message.getText().contains("\n----------\n") &&
                !Objects.equals(message.getUserKey().getUserKeyLong(), gateway.authData.getFacebookUserKey())) {
            if (!mMessageTranslations.containsKey(message.getId())) {
                Logger.verbose("[" + index + "] Translating message \"" + message.getText() + "\"");
                updateProgressDialog(index);
                mMessageTranslations.put(message.getId(), translate(message.getText(), translationInfo));
            }

            message.setText(message.getText() + "\n----------\n" + mMessageTranslations.get(message.getId()));
        }

        Logger.verbose("Message " + index + " out of " + count + " processed");
        if (index == count - 1 && isProgressDialogShown) {
            dismissProgressDialog();
        }
    }

    private String translate(String message, TranslationInfo translationInfo) {
        try {
            return MeiTranslate.translate(message, translationInfo.source, translationInfo.target);
        } catch (Throwable t) {
            Logger.error(t);
            return "translation error";
        }
    }

    private void updateProgressDialog(int messageIndex) {
        String message =  "[" + messageIndex + "] Translating messages...";
        isProgressDialogShown = true;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mProgressDialog == null)
                mProgressDialog = ProgressDialog.show(gateway.getActivity(), "Loading", message);
            else
                mProgressDialog.setMessage(message);
        });
    }

    private void dismissProgressDialog() {
        isProgressDialogShown = false;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            mProgressDialog = null;
        });
    }
}
