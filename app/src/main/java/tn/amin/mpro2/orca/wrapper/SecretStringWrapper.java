package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

public class SecretStringWrapper {
    private final String content;
    private final String stars;

    public SecretStringWrapper(Parcelable secretString) {
        Parcel parcel = Parcel.obtain();

        secretString.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        content = parcel.readString();
        stars = parcel.readString();

        parcel.recycle();
    }

    public String getContent() {
        return content;
    }

    public String getStars() {
        return stars;
    }
}
