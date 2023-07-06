package tn.amin.mpro2.orca.wrapper;

import android.os.Parcelable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;

public class MessageWrapper {
    private String id;

    private String content;

    private ThreadKeyWrapper threadKey;

    public MessageWrapper(OrcaGateway gateway, Object message) {
        Method getTextMethod = gateway.unobfuscator.getMethod(OrcaUnobfuscator.METHOD_MESSAGE_GETTEXT);
        Field threadKeyField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_THREADKEY);
        Field idField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGE_ID);

        try {
            Object secretContent = getTextMethod.invoke(message);
            Object threadKeyObject = threadKeyField.get(message);
            Object idObject = idField.get(message);

            content = new SecretStringWrapper((Parcelable) secretContent).getContent();
            threadKey = new ThreadKeyWrapper((Parcelable) threadKeyObject);
            id = (String) idObject;
        } catch (Throwable ignored) {
        }
    }
    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public ThreadKeyWrapper getThreadKey() {
        return threadKey;
    }
}
