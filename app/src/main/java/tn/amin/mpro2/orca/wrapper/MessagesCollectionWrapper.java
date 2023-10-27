package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.WrapperHelper;

public class MessagesCollectionWrapper {
    private final WeakReference<Object> mObject;

    private final Field mMessagesListField;

    ThreadKeyWrapper mThreadKey;
    boolean mIncludesFirstMessageInThread;
    boolean mIncludesLastMessageInThread;

    public MessagesCollectionWrapper(OrcaGateway gateway, Object messagesCollection) {
        mObject = new WeakReference<>(messagesCollection);

        mMessagesListField = gateway.unobfuscator.getField(OrcaUnobfuscator.FIELD_MESSAGES_COLLECTION_MESSAGES);

        Parcel parcel = Parcel.obtain();

        ((Parcelable) messagesCollection).writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        mThreadKey = new ThreadKeyWrapper(parcel.readParcelable(gateway.classLoader));
        parcel.setDataPosition(parcel.dataSize() - 4 * 2);
        mIncludesFirstMessageInThread = parcel.readInt() == 1;
        mIncludesLastMessageInThread = parcel.readInt() == 1;

        parcel.recycle();
    }

    public List<?> getList() {
        return (List<?>) WrapperHelper.fieldGet(mMessagesListField, mObject.get());
    }

    public ThreadKeyWrapper getThreadKey() {
        return mThreadKey;
    }

    public boolean includesFirstMessageInThread() {
        return mIncludesFirstMessageInThread;
    }

    public boolean includesLastMessageInThread() {
        return mIncludesLastMessageInThread;
    }
}
