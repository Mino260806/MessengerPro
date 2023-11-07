package tn.amin.mpro2.debug;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.BuildConfig;
import tn.amin.mpro2.constants.OrcaClassNames;
import tn.amin.mpro2.debug.methodhook.MethodHookLogParams;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.delegate.CQLResultSetDelegateFactory;

public class OrcaExplorer {
    public static void exploreEarly(ClassLoader classLoader) {

    }

    public static void explore(final OrcaGateway gateway, Context context) {
        ClassLoader classLoader = gateway.classLoader;
        hookAllDispatch("Core", classLoader);
        hookAllDispatch("SDK", classLoader);
        hookAllDispatch("Copresence", classLoader);
        hookAllDispatch("Composer", classLoader);
        hookAllDispatch("Events", classLoader);
        hookAllDispatch("Presence", classLoader);
        hookAllDispatch("Orca", classLoader);
        hookAllDispatch("OrcaSlim", classLoader);
        hookAllDispatch("QP", classLoader);
        hookAllDispatch("Status", classLoader);
        hookAllDispatch("SyncStates", classLoader);
        hookAllDispatch("Cowatch", classLoader);
        hookAllDispatch("Copresence", classLoader);
        hookAllDispatch("BroadcastFlow", classLoader);

        // hookMethodAndLogParams("com.facebook.messenger.notification.engine.MSGOpenPathRenderedNotification",
        // "getIsUnsent", classLoader);
        // hookAllDispatch("Community", classLoader);
        // hookConstructorAndLogParams("com.facebook.msys.mci.Attachment", classLoader);
        // hookConstructorAndLogST("com.facebook.attachments.mca.MailboxAttachmentsJNI",
        // classLoader);
        // hookMethodAndLogParams("com.facebook.msys.mci.Attachment",
        // "getAttachmentType", classLoader);

//        hookMethodAndLogParams("com.facebook.core.mca.MailboxCoreJNI", "dispatchVIIIJJOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", classLoader);
//        hookMethodAndLogParams("com.facebook.core.mca.MailboxCoreJNI", "dispatchVIJOOOOOOOOOOOOOOOOOOOO", classLoader);
//        hookMethodAndLogST("com.facebook.msys.mci.Execution", "nativeScheduleTask", classLoader);
//        hookMethodAndLogST("com.facebook.msys.mci.NotificationCenter", "dispatchNotificationToCallbacks", classLoader);
//        hookMethodAndLogST("android.app.NotificationManager", "notify", classLoader);
//        hookConstructorAndLogST("com.facebook.messaging.notify.type.NewMessageNotification", classLoader);
//        hookConstructorAndLogParams("com.facebook.secure.secrettypes.SecretString", classLoader);
//        hookConstructorAndLogST("com.facebook.messenger.notification.engine.MSGOpenPathRenderedNotification", classLoader);
//        hookConstructorAndLogParams("com.facebook.msys.mci.Attachment", classLoader);
    }

    public static void exploreUI(OrcaGateway gateway, Activity activity) {
    }

    private static void hookConstructorAndLogParams(final String className, ClassLoader classLoader) {
        final Class<?> Cls = XposedHelpers.findClass(className, classLoader);
        XposedBridge.hookAllConstructors(Cls, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                Logger.verbose("New " + className + " instance !");
                Logger.logObject(param.args);
            }
        });
    }

    private static void hookConstructorAndLogST(final String className, ClassLoader classLoader) {
        final Class<?> Cls = XposedHelpers.findClass(className, classLoader);
        XposedBridge.hookAllConstructors(Cls, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                Logger.verbose("New " + className + " instance !");
                Logger.logST();
            }
        });
    }

    private static void hookMethodAndLogST(final String className, final String method, ClassLoader classLoader) {
        final Class<?> Cls = XposedHelpers.findClass(className, classLoader);
        XposedBridge.hookAllMethods(Cls, method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                Logger.verbose(className + "." + method + " called !");
                ;
                Logger.logST();
            }
        });
    }

    private static void hookMethodAndLogParams(final String className, final String method, ClassLoader classLoader) {
        final Class<?> Cls = XposedHelpers.findClass(className, classLoader);
        XposedBridge.hookAllMethods(Cls, method, new MethodHookLogParams());
    }

    private static void hookAllDispatch(final String feature, ClassLoader classLoader) {
        Class<?> cls = XposedHelpers.findClass(
                "com.facebook." + feature.toLowerCase() + ".mca.Mailbox" + feature + "JNI", classLoader);
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().startsWith("dispatch")) {
                XposedBridge.hookMethod(method, new MethodHookLogParams());
            }
        }

    }

    private static String getCallingMethod(int index) {
        Throwable t = new Throwable();
        return t.getStackTrace()[index].getClassName() + "." +
        t.getStackTrace()[index].getMethodName() + " (" +
        t.getStackTrace()[index].getLineNumber() + ")";
    }

    private static String formatColor(@ColorInt int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
