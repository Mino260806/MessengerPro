package tn.amin.mpro.builders;


import android.os.Message;
import android.os.Parcel;

import java.lang.reflect.Constructor;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.utils.XposedHilfer;

public class MessageBuilder extends ObjectBuilder {
    Object mWrapper;

    public MessageBuilder() throws ClassNotFoundException {
        Class X_Message = MProMain.getReflectedClasses().X_Message;
        Class X_MessageBuilder = null;
        for (Constructor c: X_Message.getDeclaredConstructors()) {
            if (c.getParameterTypes()[0] != Parcel.class) {
                X_MessageBuilder = c.getDeclaringClass();
            }
        }
        if (X_MessageBuilder == null) throw new ClassNotFoundException("Could not find X_MessageBuilder");
        mWrapper = XposedHelpers.newInstance(X_MessageBuilder);
    }

    public MessageBuilder setText(String s) {
        boolean isSMS = (Boolean) XposedHelpers.callStaticMethod(
                MProMain.getReflectedClasses().X_ThreadKey,
                "A0d", null); //MProMain.get);
        return this;
    }

    public void send() {
//        Class<Enum> X_MessageSource = (Class<Enum>) XposedHelpers.findClass("X.4ix", XposedHilfer.getClassLoader());
//        XposedHelpers.callStaticMethod(O_threadViewMessagesFragment.get().getClass(),
//                "A0G",
//                Enum.valueOf(X_MessageSource, "COMPOSER_TEXT_TAB"),
//                null,
//                O_threadViewMessagesFragment.get());
    }

    @Override
    protected Object getWrapper() {
        return null;
    }

    @Override
    public Object build() {
        return null;
    }
}
