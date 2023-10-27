package tn.amin.mpro2.debug;

import android.app.Activity;
import android.content.Context;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.methodhook.MethodHookLogParams;
import tn.amin.mpro2.orca.OrcaGateway;

public class OrcaExplorer {
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
//        hookMethodAndLogParams("com.facebook.messenger.notification.engine.MSGOpenPathRenderedNotification", "getIsUnsent", classLoader);
//        hookAllDispatch("Community", classLoader);
//        hookConstructorAndLogParams("com.facebook.msys.mci.Attachment", classLoader);
//        hookConstructorAndLogST("com.facebook.attachments.mca.MailboxAttachmentsJNI", classLoader);
//        hookMethodAndLogParams("com.facebook.msys.mci.Attachment", "getAttachmentType", classLoader);


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

                Logger.verbose(className + "." + method + " called !");;
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
        for (Method method: cls.getDeclaredMethods()) {
            if (method.getName().startsWith("dispatch")) {
                XposedBridge.hookMethod(method, new MethodHookLogParams());
            }
        }

    }
}
