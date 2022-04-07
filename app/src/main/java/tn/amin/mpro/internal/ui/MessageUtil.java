package tn.amin.mpro.internal.ui;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;

public class MessageUtil {
    public static void removeAttribute(Object message, String attribute) {
        HashSet<String> newMessageTypes = new HashSet<String>();
        Set<String> originalMessageTypes = (Set<String>)
                XposedHelpers.getObjectField(message, "A1E");
        // Remove "text" from message types to prevent NullPointerException
        for (String messageType: originalMessageTypes) {
            if (messageType.equals(attribute)) continue;
            newMessageTypes.add(messageType);
        }
        XposedHelpers.setObjectField(message, "A1E", newMessageTypes);
    }

    public static void setStickerId(Object message, String stickerId) {
        XposedHelpers.setObjectField(message, "A1C", stickerId);
    }
}
