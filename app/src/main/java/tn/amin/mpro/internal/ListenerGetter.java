package tn.amin.mpro.internal;

import android.view.View;

import de.robv.android.xposed.XposedHelpers;

/**
 * ListenerGetter is used to retrieve a listener from a View because listeners
 * are private. (ex OnClickListener).
 */
public class ListenerGetter {
    private Object mListenerInfo = null;

    private ListenerGetter(View view) {
        mListenerInfo = XposedHelpers.callMethod(view, "getListenerInfo");
    }

    private Object getListener(String fieldName) {
        return XposedHelpers.getObjectField(mListenerInfo, fieldName);
    }

    public static ListenerGetter from(View view) {
        return new ListenerGetter(view);
    }

    public View.OnClickListener getOnClickListener() {
        return (View.OnClickListener) getListener("mOnClickListener");
    }
    public View.OnTouchListener getOnTouchListener() {
        return (View.OnTouchListener) getListener("mOnTouchListener");
    }
}
