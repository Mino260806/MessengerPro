package tn.amin.mpro.builders;

import android.app.Dialog;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.internal.ui.DialogUtil;

public class LoadingDialogBuilder {
    String mText = "";
    Object mInstance = null; // LoadingDialogController (guessed name)

    public LoadingDialogBuilder setText(String text) {
        mText = text;
        return this;
    }

    public void show() {
        mInstance = XposedHelpers.newInstance(
                MProMain.getReflectedClasses().X_LoadingDialog,
                MProMain.getContext(),
                mText
        );
        XposedHelpers.setObjectField(mInstance, "A00", getTheme());

        XposedHelpers.callMethod(
                mInstance,
                "AFz"
        );
    }

    public void dismiss() {
        XposedHelpers.callMethod(
                mInstance,
                "CSM"
        );
    }

    public static int getTheme() {
        return (Integer) XposedHelpers.callMethod(
                DialogUtil.getMigColorScheme(),
                "Aeu");
//      return Integer.valueOf(((MigColorScheme) C16830rQ.A0T(r3.A00, 9318)).Aeu());
    }
}
