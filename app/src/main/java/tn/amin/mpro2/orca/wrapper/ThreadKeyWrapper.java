package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

public class ThreadKeyWrapper {
    private final String type;
    private final Long key1;
    private final Long key2;
    private final Long key3;
    private final Long key4;
    private final Long key5;

    public ThreadKeyWrapper(Parcelable threadKey) {
        Parcel parcel = Parcel.obtain();

        threadKey.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        type = parcel.readString();
        key1 = parcel.readLong();
        key2 = parcel.readLong();
        key3 = parcel.readLong();
        key4 = parcel.readLong();
        key5 = parcel.readLong();

        parcel.recycle();
    }

    public String getType() {
        return type;
    }

    public Long getGroupThreadKey() {
        return key1;
    }

    public Long getOneToOneThreadKey1() {
        return key2;
    }

    public Long getOneToOneThreadKey2() {
        return key3;
    }
}
