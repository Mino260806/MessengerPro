package tn.amin.mpro2.features.util.translate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import tn.amin.mpro2.R;
import tn.amin.mpro2.orca.OrcaGateway;

public class TranslateConfigurationFrame extends FrameLayout {
    private OnSaveListener mListener = null;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public TranslateConfigurationFrame(@NonNull Context context, OrcaGateway gateway, Long threadKey) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.layout_translate_config, this);

        Button buttonSave = findViewById(R.id.button_save);
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        Switch receivedMessagesSwitch = findViewById(R.id.switch_received);
        Switch sentMessagesSwitch = findViewById(R.id.switch_sent);
        Switch keepOriginReceivedMessagesSwitch = findViewById(R.id.switch_keep_original_received);
        Switch keepOriginSentMessagesSwitch = findViewById(R.id.switch_keep_original_sent);

        EditText sourceLanguageReceivedMessagesEdit = findViewById(R.id.edit_source_language_received);
        EditText targetLanguageReceivedMessagesEdit = findViewById(R.id.edit_target_language_received);
        EditText sourceLanguageSentMessagesEdit = findViewById(R.id.edit_source_language_sent);
        EditText targetLanguageSentMessagesEdit = findViewById(R.id.edit_target_language_sent);

        TextView sourceLanguageReceivedMessagesText = findViewById(R.id.text_source_language_received);
        TextView targetLanguageReceivedMessagesText = findViewById(R.id.text_target_language_received);
        TextView sourceLanguageSentMessagesText = findViewById(R.id.text_source_language_sent);
        TextView targetLanguageSentMessagesText = findViewById(R.id.text_target_language_sent);

        receivedMessagesSwitch.setText(gateway.res.getString(R.string.received_messages));
        sentMessagesSwitch.setText(gateway.res.getString(R.string.sent_messages));
        buttonSave.setText(gateway.res.getString(R.string.save));
        sourceLanguageReceivedMessagesText.setText(gateway.res.getString(R.string.source_language));
        targetLanguageReceivedMessagesText.setText(gateway.res.getString(R.string.target_language));
        sourceLanguageSentMessagesText.setText(gateway.res.getString(R.string.source_language));
        targetLanguageSentMessagesText.setText(gateway.res.getString(R.string.target_language));
        keepOriginReceivedMessagesSwitch.setText(gateway.res.getString(R.string.keep_original_message));
        keepOriginSentMessagesSwitch.setText(gateway.res.getString(R.string.keep_original_message));

        receivedMessagesSwitch.setOnCheckedChangeListener((s, checked) -> {
            sourceLanguageReceivedMessagesEdit.setEnabled(checked);
            targetLanguageReceivedMessagesEdit.setEnabled(checked);
            keepOriginReceivedMessagesSwitch.setEnabled(checked);
        });
        sentMessagesSwitch.setOnCheckedChangeListener((s, checked) -> {
            sourceLanguageSentMessagesEdit.setEnabled(checked);
            targetLanguageSentMessagesEdit.setEnabled(checked);
            keepOriginSentMessagesSwitch.setEnabled(checked);
        });

        buttonSave.setOnClickListener((b) -> {
            if (receivedMessagesSwitch.isChecked()) {
                if (StringUtils.isBlank(targetLanguageReceivedMessagesEdit.getText().toString())) {
                    targetLanguageReceivedMessagesEdit.setError("Mandatory field");
                    return;
                }

                if (!StringUtils.isBlank(sourceLanguageReceivedMessagesEdit.getText()) &&
                        !TranslationSupportedLanguages.isSupported(sourceLanguageReceivedMessagesEdit.getText().toString())) {
                    sourceLanguageReceivedMessagesEdit.setError("Invalid language code");
                    return;
                }

                if (!TranslationSupportedLanguages.isSupported(targetLanguageReceivedMessagesEdit.getText().toString())) {
                    targetLanguageReceivedMessagesEdit.setError("Invalid language code");
                    return;
                }
            }

            if (sentMessagesSwitch.isChecked()) {
                if (StringUtils.isBlank(targetLanguageSentMessagesEdit.getText().toString())) {
                    targetLanguageSentMessagesEdit.setError("Mandatory field");
                    return;
                }

                if (!StringUtils.isBlank(sourceLanguageSentMessagesEdit.getText()) &&
                        !TranslationSupportedLanguages.isSupported(sourceLanguageSentMessagesEdit.getText().toString())) {
                    sourceLanguageSentMessagesEdit.setError("Invalid language code");
                    return;
                }

                if (!TranslationSupportedLanguages.isSupported(targetLanguageSentMessagesEdit.getText().toString())) {
                    targetLanguageSentMessagesEdit.setError("Invalid language code");
                    return;
                }
            }

            if (receivedMessagesSwitch.isChecked()) {
                String source = sourceLanguageReceivedMessagesEdit.getText().toString().trim();
                if (StringUtils.isBlank(source)) source = "auto";
                String target = targetLanguageReceivedMessagesEdit.getText().toString().trim();
                boolean keepOriginal = keepOriginReceivedMessagesSwitch.isChecked();

                gateway.pref.addTranslatedConversationReceived(threadKey, new TranslationInfo(source, target, keepOriginal));
            } else {
                gateway.pref.removeTranslatedConversationReceived(threadKey);
            }

            if (sentMessagesSwitch.isChecked()) {
                String source = sourceLanguageSentMessagesEdit.getText().toString().trim();
                if (StringUtils.isBlank(source)) source = "auto";
                String target = targetLanguageSentMessagesEdit.getText().toString().trim();
                boolean keepOriginal = keepOriginSentMessagesSwitch.isChecked();

                gateway.pref.addTranslatedConversationSent(threadKey, new TranslationInfo(source, target, keepOriginal));
            } else {
                gateway.pref.removeTranslatedConversationSent(threadKey);
            }

            if (mListener != null) mListener.onSave();
        });

        TranslationInfo translationInfoReceived = gateway.pref.getTranslatedConversationReceived(threadKey);
        TranslationInfo translationInfoSent = gateway.pref.getTranslatedConversationSent(threadKey);

        if (translationInfoReceived == null) {
            receivedMessagesSwitch.setChecked(false);
            sourceLanguageReceivedMessagesEdit.setEnabled(false);
            targetLanguageReceivedMessagesEdit.setEnabled(false);
            keepOriginReceivedMessagesSwitch.setEnabled(false);
        } else {
            receivedMessagesSwitch.setChecked(true);
            if (!translationInfoReceived.source.equals("auto")) sourceLanguageReceivedMessagesEdit.setText(translationInfoReceived.source);
            targetLanguageReceivedMessagesEdit.setText(translationInfoReceived.target);
            keepOriginReceivedMessagesSwitch.setChecked(translationInfoReceived.keepOriginal);
        }

        if (translationInfoSent == null) {
            sentMessagesSwitch.setChecked(false);
            sourceLanguageSentMessagesEdit.setEnabled(false);
            targetLanguageSentMessagesEdit.setEnabled(false);
            keepOriginSentMessagesSwitch.setEnabled(false);
        } else {
            sentMessagesSwitch.setChecked(true);
            if (!translationInfoSent.source.equals("auto")) sourceLanguageSentMessagesEdit.setText(translationInfoSent.source);
            targetLanguageSentMessagesEdit.setText(translationInfoSent.target);
            keepOriginSentMessagesSwitch.setChecked(translationInfoSent.keepOriginal);
        }
    }

    public void setOnSaveListener(OnSaveListener listener) {
        mListener = listener;
    }

    public interface OnSaveListener {
        void onSave();
    }
}
