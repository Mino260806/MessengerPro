package tn.amin.mpro.builders;

import android.app.Dialog;

import de.robv.android.xposed.XposedHelpers;
import kotlin.NotImplementedError;
import tn.amin.mpro.MProMain;

public class UniversalDialogBuilder extends ObjectBuilder {
    private Dialog mDialog = null;

    public UniversalDialogBuilder() {
        mDialog = (Dialog) XposedHelpers.newInstance(
                MProMain.getReflectedClasses().X_UniversalDialog,
                MProMain.getContext(),
                2132607571
                );
        throw new NotImplementedError();
    }

    public UniversalDialogBuilder setTitle(CharSequence title) {
        callSetterInternal("setTitle", title);
        return this;
    }

    public void show() {
        mDialog.show();
    }

    @Override
    protected Object getWrapper() {
        return mDialog;
    }

    @Override
    public UniversalDialogBuilder build() {
        return this;
    }
}
