package tn.amin.mpro2.orca.wrapper;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.WrapperHelper;

public class MessageWrapper {
    private OrcaGateway gateway;
    private WeakReference<Object> mObject;

    private final Method mGetTextMethod;
    private final Field mTextField;
    private final Field mThreadKeyField;
    private final Field mIdField;
    private final Field mParticipantInfoField;

    public MessageWrapper(OrcaGateway gateway, Object message) {
        this.gateway = gateway;
        mObject = new WeakReference<>(message);

        mGetTextMethod = gateway.unobfuscator.getMethod(OrcaUnobfuscator.METHOD_MESSAGE_GETTEXT);
        mTextField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_TEXT);
        mThreadKeyField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_THREADKEY);
        mIdField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_ID);
        mParticipantInfoField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_PARTICIPANT_INFO);
    }

    public String getId() {
        return (String) WrapperHelper.fieldGet(mIdField, mObject.get());
    }

    public String getText() {
        Object secretString = WrapperHelper.methodGet(mGetTextMethod, mObject.get());
        if (secretString == null) return null;

        return new SecretStringWrapper(gateway, secretString).getContent();
    }

    public boolean setText(String text) {
        Object secretString = WrapperHelper.methodGet(mGetTextMethod, mObject.get());
        if (secretString == null) return false;

        return new SecretStringWrapper(gateway, secretString).setContent(text);
    }

    public ThreadKeyWrapper getThreadKey() {
        Object threadKey = WrapperHelper.fieldGet(mThreadKeyField, mObject.get());
        if (threadKey == null) return null;
        return new ThreadKeyWrapper((Parcelable) threadKey);
    }

    public UserKeyWrapper getUserKey() {
        Object participantInfo = WrapperHelper.fieldGet(mParticipantInfoField, mObject.get());
        if (participantInfo == null) return null;

        return new ParticipantInfoWrapper(gateway, participantInfo).getUserKey();
    }

    @NonNull
    public String toString() {
        return "Message{content=\"" + getText() + "\",id=\""+getId()+"\",threadKey=\"" + getThreadKey() + "\"}";
    }
}
