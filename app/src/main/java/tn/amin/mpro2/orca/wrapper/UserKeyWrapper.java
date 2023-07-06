package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

public class UserKeyWrapper {
    private final String type;
    private final String id;

    public UserKeyWrapper(Parcelable userKey) {
        Parcel parcel = Parcel.obtain();

        userKey.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        type = parcel.readString();
        id = parcel.readString();

        parcel.recycle();
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Long getUserKey() {
        if ("FACEBOOK".equals(type)) {
            return Long.parseLong(id);
        }
        return -1L;
    }
}
