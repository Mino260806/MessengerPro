package tn.amin.mpro2.orca.wrapper;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;

public class AuthDataWrapper {
    private final WeakReference<Object> mObject;

    public AuthDataWrapper(Object authData) {
        mObject = new WeakReference<>(authData);
    }

    public Object getAccessToken() {
        return (Object) XposedHelpers.callMethod(mObject.get(), "getAccessToken");
    }

    public String getAnalyticsClaim() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getAnalyticsClaim");
    }

    public Map<?, ?> getAssetIds() {
        return (Map<?, ?>) XposedHelpers.callMethod(mObject.get(), "getAssetIds");
    }

    public int getAuthType() {
        return (int) XposedHelpers.callMethod(mObject.get(), "getAuthType");
    }

    public String getDeviceID() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getDeviceID");
    }

    public String getFacebookUserID() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getFacebookUserID");
    }

    public Long getFacebookUserKey() {
        return Long.parseLong(getFacebookUserID());
    }

    public String getFamilyDeviceID() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getFamilyDeviceID");
    }

    public String getFirstName() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getFirstName");
    }

    public String getFullName() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getFullName");
    }

    public String getMachineID() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getMachineID");
    }

    public List<?> getSessionCookies() {
        return (List<?>) XposedHelpers.callMethod(mObject.get(), "getSessionCookies");
    }

    public String getUnderlyingAdminUserID() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getUnderlyingAdminUserID");
    }

    public String getUsername() {
        return (String) XposedHelpers.callMethod(mObject.get(), "getUsername");
    }
}
