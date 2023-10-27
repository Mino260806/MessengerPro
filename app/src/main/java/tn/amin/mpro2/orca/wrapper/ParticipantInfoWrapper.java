package tn.amin.mpro2.orca.wrapper;

import android.os.Parcelable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.WrapperHelper;

public class ParticipantInfoWrapper {
    private final WeakReference<Object> mObject;

    private final Field mUserKeyField;

    public ParticipantInfoWrapper(OrcaGateway gateway, Object participantInfo) {
        mObject = new WeakReference<>(participantInfo);

        mUserKeyField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_PARTICIPANT_INFO_USER_KEY);
    }

    public UserKeyWrapper getUserKey() {
        Object userKey = WrapperHelper.fieldGet(mUserKeyField, mObject.get());
        if (userKey == null) return null;

        return new UserKeyWrapper((Parcelable) userKey);
    }
}
