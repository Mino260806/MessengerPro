package tn.amin.mpro2.orca.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.math.NumberUtils;

import tn.amin.mpro2.debug.Logger;

public class ThreadKeyWrapper {
    private final String type;
    private final Long facebookThreadKey;
    private final String[] toStringSplit;

    public ThreadKeyWrapper(Parcelable threadKey) {
        toStringSplit = threadKey.toString().split(":");
        type = toStringSplit[0];
        switch (type) {
            case "GROUP":
                facebookThreadKey = getGroupThreadKey();
                break;
            case "ONE_TO_ONE":
                facebookThreadKey = getOneToOneThreadKey1();
                break;
            default:
                facebookThreadKey = null;
                break;
        }
    }

    public String getType() {
        return type;
    }

    public Long getGroupThreadKey() {
        requireType("GROUP");
        return Long.parseLong(toStringSplit[1]);
    }

    public Long getOneToOneThreadKey1() {
        requireType("ONE_TO_ONE");
        return Long.parseLong(toStringSplit[1]);
    }

    public Long getOneToOneThreadKey2() {
        requireType("ONE_TO_ONE");
        return Long.parseLong(toStringSplit[2]);
    }

    public Long getFacebookThreadKey() {
        return facebookThreadKey;
    }

    private void requireType(String other) {
        if (!type.equals(other)) throw new RuntimeException("ThreadKey must be of type " + other + " (actual " + type + ")");
    }
}
