package tn.amin.mpro2.debug;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
////        XC_MethodHook getColorMH = new XC_MethodHook() {
////            @Override
////            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                Logger.verbose("getColor(): " + param.getResult());
////                param.setResult(Color.RED);
////            }
////        };
////
////        XposedBridge.hookAllMethods(Resources.class, "getColor", getColorMH);
////        XposedBridge.hookAllMethods(ContextWrapper.class, "getColor", getColorMH);
////
////        XposedBridge.hookAllMethods(View.class, "setBackgroundColor", new XC_MethodHook() {
////            @Override
////            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                Logger.verbose("View.setBackgroundColor called ! " + param.args[0]);
////                param.args[0] = Color.YELLOW;
////            }
////        });
//
//        XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("View " + param.thisObject.getClass().getName() + " was clicked");
//            }
//        });
//
//
//        XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = replaceColor((Integer) param.args[0]);
//            }
//        });
//
////        XposedBridge.hookAllMethods(Paint.class, "setColorFilter", new XC_MethodHook() {
////            @Override
////            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                if (param.args[0] == null) return;
////                if (param.args[0] instanceof PorterDuffColorFilter) {
////                    PorterDuffColorFilter colorFilter = (PorterDuffColorFilter) param.args[0];
////                }
////            }
////        });
//
//        XposedBridge.hookAllConstructors(PorterDuffColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = replaceColor((Integer) param.args[0]);
//            }
//        });
//
////        XposedBridge.hookAllMethods(BitmapDrawable.class, "draw", new XC_MethodHook() {
////            @Override
////            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                param.setResult(null);
////            }
////        });
//
//        XposedBridge.hookAllMethods(BitmapDrawable.class, "setBitmap", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("setting Bitmap!");
//            }
//        });
    }

//    private static int replaceColor(int original) {
//        boolean replaced = true;
//        if (original == Color.WHITE) {
//            return Color.parseColor("#efe5fd");
//        } else if (original == Color.parseColor("#0A7CFF")) {
//            return Color.parseColor("#9965f4");
//        } else if (original == Color.parseColor("#E6DCF3")){
//            return Color.parseColor("#000000"); // 7E3FF2");
//        } else {
//            replaced = false;
//        }
//        return original;
////                Logger.verbose("Paint.setColor called (replaced: " + replaced + ") ! " + original);
//    }

    public static void explore(final OrcaGateway gateway, Context context) {
        ClassLoader classLoader = gateway.classLoader;

//        hookMethodAndLogParams("com.facebook.core.mca.MailboxCoreJNI", "dispatchVIIIJJOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", classLoader);
//        hookMethodAndLogParams("com.facebook.core.mca.MailboxCoreJNI", "dispatchVIJOOOOOOOOOOOOOOOOOOOO", classLoader);
//        hookMethodAndLogST("com.facebook.msys.mci.Execution", "nativeScheduleTask", classLoader);
//        hookMethodAndLogST("com.facebook.msys.mci.NotificationCenter", "dispatchNotificationToCallbacks", classLoader);
//        hookMethodAndLogST("android.app.NotificationManager", "notify", classLoader);
//        hookConstructorAndLogST("com.facebook.messaging.notify.type.NewMessageNotification", classLoader);
//        hookConstructorAndLogParams("com.facebook.secure.secrettypes.SecretString", classLoader);
//        hookConstructorAndLogST("com.facebook.messenger.notification.engine.MSGOpenPathRenderedNotification", classLoader);

//        hookConstructorAndLogParams("com.facebook.msys.mci.Attachment", classLoader);

//        hookMethodAndLogST("com.facebook.sdk.mca.MailboxSDKJNI", "dispatchVJOOOO", classLoader);
//        hookConstructorAndLogST("X.50e", classLoader);

//        hookAllDispatch("Core", classLoader);
//        hookAllDispatch("SDK", classLoader);
//        hookAllDispatch("Copresence", classLoader);
//        hookAllDispatch("Composer", classLoader);
//        hookAllDispatch("Events", classLoader);
//        hookAllDispatch("Presence", classLoader);
//        hookAllDispatch("Orca", classLoader);
//        hookAllDispatch("OrcaSlim", classLoader);
//        hookAllDispatch("QP", classLoader);
//        hookAllDispatch("Status", classLoader);
//        hookAllDispatch("SyncStates", classLoader);

//        new BroadcastReceiverHook("com.facebook.orca.notify.MessagesNotificationBroadcastReceiver", classLoader,
//        new BroadcastReceiverHook(OrcaInfo.ORCA_SAMPLE_RECEIVER, classLoader,
//                MessengerBridge.PARAM_ACTION,  (receiverContext, intent, action) -> {
//                    Logger.verbose("Received action " + action);
//
//                    EventBus.getDefault().post(new Object());
//        });

//        Unobfuscator unobfuscator = new Unobfuscator(activity, path, classLoader);
//        Class<?> NotificationEngine = unobfuscator.loadNotificationEngine();
//        unobfuscator.save();
//        hookConstructorAndLogParams("android.content.Intent", classLoader);
//        XposedBridge.hookAllConstructors(Intent.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (param.args.length >= 2 && param.args[1] instanceof Class) {
//                    Class<?> cls = (Class<?>) param.args[1];
//                    if (cls.getName().equals("com.facebook.messaging.montage.composer.MontageComposerActivity")) {
//                        Logger.logST();
//                    }
//                }
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("androidx.fragment.app.Fragment", classLoader),
//                "startActivityForResult", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//
//
//                int requestCode = (int) param.args[1];
//                if (requestCode == 7377) {
//                    Logger.verbose("Starting camera...");
//                    param.setResult(null);
//
//
//                }
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("androidx.fragment.app.Fragment", classLoader),
//                "onActivityResult", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//
//                int requestCode = (int) param.args[0];
//                if (requestCode == 7377) {
//                    Logger.verbose("Camera returned !");
//
//                    Intent intent = (Intent) param.args[2];
//                    if (intent != null) {
//                        Bundle extras = intent.getExtras();
//                        for (String key: extras.keySet()) {
//                            Logger.verbose("[" + key + "] " + extras.get(key));
//                        }
//                    }
//                }
//            }
//        });

//        XposedHelpers.findAndHookConstructor("com.facebook.secure.secrettypes.SecretString", classLoader, String.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                if (param.args[0] != null && param.args[0].toString().contains("Access our Travel industry Playlist")) {
//                Logger.verbose("message is \"" + param.args[0] + "\"");
////                Logger.logST();
////                }
//            }
//        });

//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("com.facebook.messaging.model.messages.Message", classLoader),
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//
//                        Logger.verbose("New message object !");
////                        Logger.logObjectRecursive(param.args[0]);
//
////                        try {
////                            String message = (String) XposedHelpers.getObjectField(
////                                    XposedHelpers.getObjectField(param.args[0], "A0a"), "A00");
////                            Logger.verbose("secret message: " + message);
////                            if (message.contains("Welcome ")) {
////                                Logger.logST();
////                            }
////                        } catch (Throwable ignored) {
////
////                        }
//                    }
//                });

//        XposedBridge.hookAllMethods(ArrayList.class, "add", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                if (param.args[0] != null) {
//                    String callerClassName = new Throwable().getStackTrace()[3].getClassName();
//                    if (callerClassName.equals("X.072")) {
//                        Logger.verbose("Added " + param.args[0] + " to mysterious class " + callerClassName);
//                    }
//                }
////                    Logger.verbose("ArrayList.add got called on child " + param.args[0].getClass().getName() + " || " +
////                            new Throwable().getStackTrace()[3].getClassName() + "." +
////                            new Throwable().getStackTrace()[3].getMethodName() + " (" +
////                            new Throwable().getStackTrace()[3].getLineNumber() + ")"
////                            );
//            }
//        });

//        XposedHelpers.findAndHookConstructor("X.0CU", classLoader, android.content.Intent.class, java.util.ArrayList.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//
//                Intent intent = (Intent) param.args[0];
//                Logger.verbose("Intent action: " + intent.getAction());
//                Logger.logExtras(intent);
////                if (intent.hasExtra("extra_user_key")) {
////                    Object userKey = intent.getExtras().get("extra_user_key");
//////                    Logger.logST();
//////                    Logger.verbose("action: " + intent.getAction());
////                }
//            }
//        });
//
//        XposedHelpers.findAndHookMethod(Intent.class, "getAction", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                if ("com.facebook.presence.ACTION_OTHER_USER_TYPING_CHANGED".equals(param.getResult())) {
//                    Logger.logST();
//                }
//            }
//        });

//        XposedBridge.hookAllMethods(ContextWrapper.class, "sendBroadcast", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//
//                Logger.verbose("sendBroadcast: " + ((Intent) param.args[0]).getAction());
//            }
//        });

//        hookMethodAndLogParams("java.lang.Long", "parseLong", classLoader);
//        hookMethodAndLogParams("java.lang.Long", "parseUnsignedLong", classLoader);

//        XposedBridge.hookAllMethods(Long.class, "parseLong", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                if ((long) param.getResult() == 100009037578288L) {
//                    Logger.verbose("parseLong was called on userKey ! " +
//                            new Throwable().getStackTrace()[3].getClassName() + "." +
//                            new Throwable().getStackTrace()[3].getMethodName() + " (" +
//                            new Throwable().getStackTrace()[3].getLineNumber() + ")");
//                }
//            }
//        });

//        XposedBridge.hookAllConstructors(Date.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                if ((long) param.getResult() == 100009037578288L) {
//                    Logger.verbose("Date constructor was called ! " +
//                            new Throwable().getStackTrace()[3].getClassName() + "." +
//                            new Throwable().getStackTrace()[3].getMethodName() + " (" +
//                            new Throwable().getStackTrace()[3].getLineNumber() + ")");
////                }
//            }
//        });
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
