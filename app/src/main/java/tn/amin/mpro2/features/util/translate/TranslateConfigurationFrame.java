package tn.amin.mpro2.features.util.translate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import tn.amin.mpro2.R;
import tn.amin.mpro2.orca.OrcaGateway;

public class TranslateConfigurationFrame extends FrameLayout {
    private OnSaveListener mListener = null;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public TranslateConfigurationFrame(@NonNull Context context, OrcaGateway gateway, Long threadKey) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.layout_translate_config, this);

        Button buttonSave = findViewById(R.id.button_save);
        Switch receivedMessagesSwitch = findViewById(R.id.switch_received);
        Switch sentMessagesSwitch = findViewById(R.id.switch_sent);
        Switch keepOriginReceivedMessagesSwitch = findViewById(R.id.switch_keep_original_received);
        Switch keepOriginSentMessagesSwitch = findViewById(R.id.switch_keep_original_sent);
        Spinner sourceLanguageReceivedMessagesSpinner = findViewById(R.id.spinner_source_language_received);
        Spinner targetLanguageReceivedMessagesSpinner = findViewById(R.id.spinner_target_language_received);
        Spinner sourceLanguageSentMessagesSpinner = findViewById(R.id.spinner_source_language_sent);
        Spinner targetLanguageSentMessagesSpinner = findViewById(R.id.spinner_target_language_sent);

        TextView sourceLanguageReceivedMessagesText = findViewById(R.id.text_source_language_received);
        TextView targetLanguageReceivedMessagesText = findViewById(R.id.text_target_language_received);
        TextView sourceLanguageSentMessagesText = findViewById(R.id.text_source_language_sent);
        TextView targetLanguageSentMessagesText = findViewById(R.id.text_target_language_sent);
        Animation shake = AnimationUtils.loadAnimation(this.getContext(), R.anim.shake_animation);
        //Filling up the Source/Received spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, TranslationSupportedLanguages.languages);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageReceivedMessagesSpinner.setAdapter(adapter1);

        //Filling up the Target/Received spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, TranslationSupportedLanguages.languages);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetLanguageReceivedMessagesSpinner.setAdapter(adapter2);

        //Filling up the Source/Sent spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, TranslationSupportedLanguages.languages);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageSentMessagesSpinner.setAdapter(adapter3);

        //Filling up the Target/Sent spinner
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, TranslationSupportedLanguages.languages);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetLanguageSentMessagesSpinner.setAdapter(adapter4);


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
            sourceLanguageReceivedMessagesSpinner.setEnabled(checked);
            targetLanguageReceivedMessagesSpinner.setEnabled(checked);
            keepOriginReceivedMessagesSwitch.setEnabled(checked);
        });
        sentMessagesSwitch.setOnCheckedChangeListener((s, checked) -> {
            sourceLanguageSentMessagesSpinner.setEnabled(checked);
            targetLanguageSentMessagesSpinner.setEnabled(checked);
            keepOriginSentMessagesSwitch.setEnabled(checked);
        });

        buttonSave.setOnClickListener((b) -> {
            if (receivedMessagesSwitch.isChecked()) {
                if (targetLanguageReceivedMessagesSpinner.getSelectedItemPosition() < 1) {
                    targetLanguageReceivedMessagesSpinner.startAnimation(shake);
                    return;
                }
            }

            if (sentMessagesSwitch.isChecked()) {
                if (targetLanguageSentMessagesSpinner.getSelectedItemPosition() < 1) {
                    targetLanguageSentMessagesSpinner.startAnimation(shake);
                    return;
                }
            }

            if (receivedMessagesSwitch.isChecked()) {
                String source = TranslationSupportedLanguages.languageCodes.get(sourceLanguageReceivedMessagesSpinner.getSelectedItemPosition());
                String target = TranslationSupportedLanguages.languageCodes.get(targetLanguageReceivedMessagesSpinner.getSelectedItemPosition());
                boolean keepOriginal = keepOriginReceivedMessagesSwitch.isChecked();

                gateway.pref.addTranslatedConversationReceived(threadKey, new TranslationInfo(source, target, keepOriginal));
            } else {
                gateway.pref.removeTranslatedConversationReceived(threadKey);
            }

            if (sentMessagesSwitch.isChecked()) {
                String source = TranslationSupportedLanguages.languageCodes.get(sourceLanguageSentMessagesSpinner.getSelectedItemPosition());
                String target = TranslationSupportedLanguages.languageCodes.get(targetLanguageSentMessagesSpinner.getSelectedItemPosition());
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
            sourceLanguageReceivedMessagesSpinner.setEnabled(false);
            targetLanguageReceivedMessagesSpinner.setEnabled(false);
            keepOriginReceivedMessagesSwitch.setEnabled(false);
        } else {
            receivedMessagesSwitch.setChecked(true);
            if (!translationInfoReceived.source.equals("auto")) sourceLanguageReceivedMessagesSpinner.setSelection(getIndexFromList(TranslationSupportedLanguages.languageCodes, translationInfoReceived.source));
            targetLanguageReceivedMessagesSpinner.setSelection(getIndexFromList(TranslationSupportedLanguages.languageCodes, translationInfoReceived.target));
            keepOriginReceivedMessagesSwitch.setChecked(translationInfoReceived.keepOriginal);
        }

        if (translationInfoSent == null) {
            sentMessagesSwitch.setChecked(false);
            sourceLanguageSentMessagesSpinner.setEnabled(false);
            targetLanguageSentMessagesSpinner.setEnabled(false);
            keepOriginSentMessagesSwitch.setEnabled(false);
        } else {
            sentMessagesSwitch.setChecked(true);
            if (!translationInfoSent.source.equals("auto")) sourceLanguageSentMessagesSpinner.setSelection(getIndexFromList(TranslationSupportedLanguages.languageCodes, translationInfoSent.source));
            targetLanguageSentMessagesSpinner.setSelection(getIndexFromList(TranslationSupportedLanguages.languageCodes, translationInfoSent.target));
            keepOriginSentMessagesSwitch.setChecked(translationInfoSent.keepOriginal);
        }
    }
    public static int getIndexFromList(List<String> list, String target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(target)) {
                return i; // Found a match, return the index
            }
        }
        return -1; // Not found
    }


    public void setOnSaveListener(OnSaveListener listener) {
        mListener = listener;
    }

    public interface OnSaveListener {
        void onSave();
    }
}
