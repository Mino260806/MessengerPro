package tn.amin.mpro2.debug;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.loader.content.CursorLoader;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.methodhook.MethodHookLogParams;
import tn.amin.mpro2.hook.all.UIColorsHook;
import tn.amin.mpro2.orca.OrcaGateway;

public class OrcaExplorer {
    public static void exploreEarly(ClassLoader classLoader) {
        //// XC_MethodHook getColorMH = new XC_MethodHook() {
        //// @Override
        //// protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //// Logger.verbose("getColor(): " + param.getResult());
        //// param.setResult(Color.RED);
        //// }
        //// };
        ////
        //// XposedBridge.hookAllMethods(Resources.class, "getColor", getColorMH);
        //// XposedBridge.hookAllMethods(ContextWrapper.class, "getColor", getColorMH);
        ////
        //// XposedBridge.hookAllMethods(View.class, "setBackgroundColor", new
        //// XC_MethodHook() {
        //// @Override
        //// protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        //// Logger.verbose("View.setBackgroundColor called ! " + param.args[0]);
        //// param.args[0] = Color.YELLOW;
        //// }
        //// });
        //
        // XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
        // @Override
        // protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // Logger.verbose("View " + param.thisObject.getClass().getName() + " was
        //// clicked");
        // }
        // });
        //
        //
        // XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
        // @Override
        // protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // param.args[0] = replaceColor((Integer) param.args[0]);
        // }
        // });
        //
        //// XposedBridge.hookAllMethods(Paint.class, "setColorFilter", new
        //// XC_MethodHook() {
        //// @Override
        //// protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        //// if (param.args[0] == null) return;
        //// if (param.args[0] instanceof PorterDuffColorFilter) {
        //// PorterDuffColorFilter colorFilter = (PorterDuffColorFilter) param.args[0];
        //// }
        //// }
        //// });
        //
        // XposedBridge.hookAllConstructors(PorterDuffColorFilter.class, new
        //// XC_MethodHook() {
        // @Override
        // protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // param.args[0] = replaceColor((Integer) param.args[0]);
        // }
        // });
        //
        //// XposedBridge.hookAllMethods(BitmapDrawable.class, "draw", new
        //// XC_MethodHook() {
        //// @Override
        //// protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        //// param.setResult(null);
        //// }
        //// });
        //
        // XposedBridge.hookAllMethods(BitmapDrawable.class, "setBitmap", new
        //// XC_MethodHook() {
        // @Override
        // protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // Logger.verbose("setting Bitmap!");
        // }
        // });
    }

    // private static int replaceColor(int original) {
    // boolean replaced = true;
    // if (original == Color.WHITE) {
    // return Color.parseColor("#efe5fd");
    // } else if (original == Color.parseColor("#0A7CFF")) {
    // return Color.parseColor("#9965f4");
    // } else if (original == Color.parseColor("#E6DCF3")){
    // return Color.parseColor("#000000"); // 7E3FF2");
    // } else {
    // replaced = false;
    // }
    // return original;
    //// Logger.verbose("Paint.setColor called (replaced: " + replaced + ") ! " +
    // original);
    // }

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
        hookAllDispatch("BroadcastFlow", classLoader);
        // hookMethodAndLogParams("com.facebook.messenger.notification.engine.MSGOpenPathRenderedNotification",
        // "getIsUnsent", classLoader);
        // hookAllDispatch("Community", classLoader);
        // hookConstructorAndLogParams("com.facebook.msys.mci.Attachment", classLoader);
        // hookConstructorAndLogST("com.facebook.attachments.mca.MailboxAttachmentsJNI",
        // classLoader);
        // hookMethodAndLogParams("com.facebook.msys.mci.Attachment",
        // "getAttachmentType", classLoader);

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
}
