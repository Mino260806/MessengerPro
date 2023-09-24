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
//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("com.facebook.messaging.customthreads.model.ThreadThemeInfo", classLoader), new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.logST();
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.facebook.msys.mci.CQLResultSetImpl", classLoader), "getNullableLong", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                int attrId = (int) param.args[1];
//                if (attrId == 0x57) {
//                    param.setResult(System.currentTimeMillis());
//                    Logger.logST();
//                }
//            }
//        });

//        XposedBridge.hookAllMethods(ContentValues.class, "put", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("ContentValues.put(" + param.args[0] + ", " + param.args[1] + ")");
//                if ("unsent_timestamp_ms".equals(param.args[0])) {
//                    Logger.verbose("Throwing exception to prevent unsending message");
//                    throw new RuntimeException();
//                }
//            }
//        });

//        XposedBridge.hookAllConstructors(XposedHelpers.findClass(OrcaClassNames.MESSAGE, classLoader), new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                XposedHelpers.setLongField(param.thisObject, "A05", 0);
//            }
//
//            private void log(Object thisObject, String field) {
//                Logger.verbose("| " + field + ": " + XposedHelpers.getBooleanField(thisObject, field));
//            }
//        });

//        XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                File file = (File) param.thisObject;
//                Logger.verbose("new File: " + file.getAbsolutePath());
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("X.5CK", classLoader), "A00", new XC_MethodHook() {
//            Object originalResultSet = null;
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                originalResultSet = XposedHelpers.getObjectField(param.args[1], "mResultSet");
//                XposedHelpers.setObjectField(param.args[1], "mResultSet",
//                        CQLResultSetDelegateFactory.getDelegate(
//                                originalResultSet,
//                                XposedHelpers.findClass(OrcaClassNames.CQL_RESULT_SET, classLoader),
//                                (i) -> i));
//            }
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                XposedHelpers.setObjectField(param.args[1], "mResultSet",
//                        originalResultSet);
//            }
//        });

        Random random = new Random();

//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("com.facebook.messaging.composer.ComposerActionButton", classLoader), new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                ImageView view = (ImageView) param.thisObject;
//                view.setRotation(90);
//            }
//        });

//        XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                View view = (View) param.thisObject;
//
//                Logger.verbose("Clicked on view " + view.getClass().getName());
//
//                if (view.getClass().getName().endsWith("ComponentHost")) {
//                    Logger.verbose("A0T: " + XposedHelpers.callMethod(view, "A0T", 26, 2006));
//                }
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("X.1fD", classLoader), "setColorFilter", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
//            }
//        });
//        XposedBridge.hookAllMethods(XposedHelpers.findClass("X.3px", classLoader), "setColorFilter", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (param.args[0] == null) return;
//                Logger.verbose("ColorFilter: " + param.args[0].getClass().getName());
//                param.args[0] = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
////                Drawable d = (Drawable) XposedHelpers.getObjectField(param.thisObject, "A00");
////                if (d!= null)
////                    Logger.verbose("Drawable: " + d.getClass().getName());
//            }
//        });

//        XposedBridge.hookAllConstructors(BlendModeColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = Color.YELLOW;
//            }
//        });
//        XposedBridge.hookAllMethods(PorterDuffColorFilter.class, "createNativeInstance", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                XposedHelpers.setIntField(param.thisObject, "mColor", Color.MAGENTA);
//            }
//        });

//        XposedBridge.hookAllConstructors(LightingColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = Color.YELLOW;
//            }
//        });
//        XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//
//                int r = random.nextInt(256);
//                int g = random.nextInt(256);
//                int b = random.nextInt(256);
//                int randomColor = Color.rgb(r, g, b);
//
//                String hexColor = String.format("#%06X", (0xFFFFFF & randomColor));
//                Logger.verbose("Color(" + hexColor + ") replaced! " + getCallingMethod(5));
//
//                param.args[0] = randomColor;
//            }
//        });


//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("X.418", classLoader), new XC_MethodHook() {
//            boolean logged = false;
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (!logged) {
//                    logged = true;
//                    Logger.logST();
//                }
//            }
//        });

//        XposedBridge.hookAllConstructors(ColorDrawable.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (param.args.length == 0) Logger.verbose("No initial color");
//                if (param.args.length == 1) Logger.verbose("Initial color: " + formatColor((Integer) param.args[0]));
//            }
//        });

//        XposedBridge.hookAllMethods(Drawable.class, "invalidateSelf", new XC_MethodHook() {
//            boolean allowHook = true;
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (!allowHook) return;
//                allowHook = false;
//
//                int r = random.nextInt(256);
//                int g = random.nextInt(256);
//                int b = random.nextInt(256);
//                int randomColor = Color.rgb(r, g, b);
//
//                Drawable drawable = (Drawable) param.thisObject;
//                drawable.setColorFilter(new PorterDuffColorFilter(randomColor, PorterDuff.Mode.SRC_IN));
//
//                allowHook = true;
//
//                String hexColor = String.format("#%06X", (0xFFFFFF & randomColor));
//                Logger.verbose("Color(" + hexColor + ") replaced! " + getCallingMethod(5));
//            }
//        });

//        XC_MethodHook getColorMH = new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("getColor(): " + param.getResult());
//                param.setResult(Color.RED);
//            }
//        };
//
//        XposedBridge.hookAllMethods(Resources.class, "getColor", getColorMH);
//        XposedBridge.hookAllMethods(ContextWrapper.class, "getColor", getColorMH);
////
//        XposedBridge.hookAllMethods(View.class, "setBackgroundColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("View.setBackgroundColor called ! " + param.args[0]);
//                param.args[0] = Color.YELLOW;
//            }
//        });
//
//        XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("View " + param.thisObject.getClass().getName() + " was clicked");
//            }
//        });
//
//
//        XposedBridge.hookAllMethods(Paint.class, "getNativeInstance", new XC_MethodHook() {
//            Random random = new Random();
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Paint paint = (Paint) param.thisObject;
//                int r = random.nextInt(256);
//                int g = random.nextInt(256);
//                int b = random.nextInt(256);
//                int randomColor = Color.rgb(r, g, b);
//                paint.setColor(randomColor);
//
//                String hexColor = String.format("#%06X", (0xFFFFFF & randomColor));
//
//                Logger.verbose("Color(" + hexColor + ") replaced! " + getCallingMethod(5));
//            }
//        });
//

//        XposedHelpers.findAndHookMethod("android.graphics.BaseRecordingCanvas", classLoader, "drawRect", "float", "float", "float", "float", Paint.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Paint paint = (Paint) param.args[4];
//                paint.setColor(Color.GREEN);
//            }
//        });

//        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.graphics.BaseRecordingCanvas", classLoader), "drawRect", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.verbose("Inside drawRect");
//            }
//        });

        //
//        XposedHelpers.findAndHookMethod("android.graphics.BaseRecordingCanvas", classLoader, "drawRect", RectF.class, Paint.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Paint paint = (Paint) param.args[1];
//                paint.setColor(Color.GREEN);
//            }
//        });


//        XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = Color.GREEN;
//            }
//        });

//        XposedBridge.hookAllConstructors(Paint.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Paint paint = (Paint) param.thisObject;
//                paint.setColor(0);
//            }
//        });

//        XposedBridge.hookAllMethods(Paint.class, "setColorFilter", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                if (param.args[0] == null) return;
//                if (param.args[0] instanceof PorterDuffColorFilter) {
//                    PorterDuffColorFilter colorFilter = (PorterDuffColorFilter) param.args[0];
//                }
//
//                param.args[0] = new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
//            }
//        });
//
//        XposedBridge.hookAllConstructors(PorterDuffColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = Color.YELLOW;
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
