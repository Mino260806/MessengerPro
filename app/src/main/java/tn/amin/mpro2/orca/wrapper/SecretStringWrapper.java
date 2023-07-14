package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.WrapperHelper;
import tn.amin.mpro2.util.StringUtil;

public class SecretStringWrapper {
    private final OrcaGateway gateway;
    private final WeakReference<Object> mObject;

    private final Field mContentField;
    private final Field mStarsField;

    public SecretStringWrapper(OrcaGateway gateway, Object secretString) {
        this.gateway = gateway;
        mObject = new WeakReference<>(secretString);

        mContentField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_SECRET_STRING_CONTENT);
        mStarsField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_SECRET_STRING_STARS);
    }

    public String getContent() {
        return (String) WrapperHelper.fieldGet(mContentField, mObject.get());
    }

    public String getStars() {
        return (String) WrapperHelper.fieldGet(mStarsField, mObject.get());
    }

    public boolean setContent(String content) {
        return WrapperHelper.fieldSet(mContentField, mObject.get(), content) &&
                WrapperHelper.fieldSet(mStarsField, mObject.get(), StringUtil.multiply("*", content.length()));
    }
}
