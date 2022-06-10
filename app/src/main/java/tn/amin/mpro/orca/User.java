package tn.amin.mpro.orca;


import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;


public class User {
    private final Object thisObject;

    public User() {
        thisObject = XposedHelpers.callStaticMethod(
                MProMain.getReflectedClasses().X_UserRetriever,
                "A04",
                MProMain.classInjector);
    }

    public String getFbId() {
        return getFieldInternal("A0v");
    }

    private <T> T getFieldInternal(String fieldName) {
        return (T) XposedHelpers.getObjectField(thisObject, fieldName);
    }
}
