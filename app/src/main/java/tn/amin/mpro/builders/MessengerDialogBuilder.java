package tn.amin.mpro.builders;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.internal.ui.DialogUtil;

public class MessengerDialogBuilder extends ObjectBuilder {
    private Object mBuilder;
    private Object mWrapper;

    public MessengerDialogBuilder(Context context) {
        mBuilder = XposedHelpers.newInstance(MProMain.getReflectedClasses().X_FancyDialogBuilder,
                MProMain.getContext(), getTheme());
        mWrapper = XposedHelpers.getObjectField(mBuilder, "A00");
    }

    public MessengerDialogBuilder() {
        this(MProMain.getContext());
    }

    public MessengerDialogBuilder setMessage(CharSequence message) {
        setFieldInternal("A0F", message);
        return this;
    }

    public MessengerDialogBuilder setTitle(CharSequence message) {
        setFieldInternal("A0J", message);
        return this;
    }

    public MessengerDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener ocl) {
        setFieldInternal("A07", ocl);
        setFieldInternal("A0I", text);
        return this;
    }

    public MessengerDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener ocl) {
        setFieldInternal("A04", ocl);
        setFieldInternal("A0G", text);
        return this;
    }

    public MessengerDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener ocl) {
        setFieldInternal("A05", ocl);
        setFieldInternal("A0H", text);
        return this;
    }

    public MessengerDialogBuilder setOnCancelListener(DialogInterface.OnCancelListener ocl) {
        setFieldInternal("A03", ocl);
        return this;
    }

    public MessengerDialogBuilder setOnDismissListener(DialogInterface.OnDismissListener odl) {
        setFieldInternal("A08", odl);
        return this;
    }

    public MessengerDialogBuilder setIcon(Drawable d) {
        setFieldInternal("A0B", d);
        return this;
    }

    public MessengerDialogBuilder setIcon(@DrawableRes int res) {
        return setIcon(ResourcesCompat.getDrawable(MProMain.getMProResources(), res, null));
    }

    public Dialog build() {
        return (Dialog) XposedHelpers.callMethod(mBuilder, "A00");
    }

    private int getTheme() {
        return ((Number) XposedHelpers.callMethod(
                DialogUtil.getMigColorScheme(),
                "CBp",
                XposedHelpers.getStaticObjectField(MProMain.getReflectedClasses().X_FancyDialogColorApplier, "A02")))
                .intValue();
    }

    @Override
    protected Object getWrapper() {
        return mWrapper;
    }
}
