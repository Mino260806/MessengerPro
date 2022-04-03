package tn.amin.mpro.internal;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.net.URI;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.constants.ReflectedClasses;
import tn.amin.mpro.utils.XposedHilfer;

public class Debugger {
    /**
     * Includes various hooks useful for debugging and placing
     * breakpoints
     * */
    public static void initDebugHooks() {
        ReflectedClasses classes = MProMain.getReflectedClasses();

        XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View view = (View) param.thisObject;
                View.OnClickListener defaultOCL = (View.OnClickListener) XposedHelpers
                        .getObjectField(
                                XposedHelpers.callMethod(view, "getListenerInfo"),
                                "mOnClickListener"
                        );
                if (defaultOCL == null)
                    defaultOCL = view1 -> {
                    };
                XposedBridge.log("performClick called: View.OnClickListener: " +
                        defaultOCL.getClass().getName() + " View: " + view.getClass().getName());
            }
        });

        XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StringBuilder path = new StringBuilder();
                boolean log = false;
                if (param.args.length == 2) {
                    if (param.args[0] instanceof String)
                        path.append((String) param.args[0]);
                    else
                        log = false;
                    path.append("/");
                    path.append((String) param.args[1]);
                } else if (param.args[0] instanceof String)
                    path.append((String) param.args[0]);
                else
                    path.append(((URI) param.args[0]).getPath());
                if (log)
                    XposedBridge.log("Accessing File: " + path.toString());
//				if (path.toString().startsWith("/storage/emulated/0/DCIM/Camera/test.mp4")) {
//					XposedBridge.log(new Throwable());
//				}
            }
        });
        XposedHelpers.findAndHookMethod(classes.X_ResourcesImpl, "loadXmlResourceParser",
                String.class, "int", "int", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String file = (String) param.args[0];
                        int id = (Integer) param.args[1];
                        int assetCookie = (Integer) param.args[2];
                        String type = (String) param.args[3];
                        XposedBridge.log("Loading " + type + " from file " + file);

//						if (file.equals("r/el3.xml")) {
//							XposedBridge.log(new Throwable());
//						}
                    }
                });

        XposedBridge.hookAllConstructors(classes.X_MediaResource, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object mediaResource = param.thisObject;
                Uri uri = (Uri) XposedHelpers.getObjectField(mediaResource, "A0E");
//				XposedBridge.log("A new MediaResource was created with path: " +
//						uri.getPath());
            }
        });

        XposedBridge.hookAllMethods(classes.X_ComponentHost, "setContentDescription", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }
        });

        XposedBridge.hookAllMethods(classes.X_ResourcesImpl, "loadDrawableForCookie", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Resources wrapper = (Resources) param.args[0];
                TypedValue value = (TypedValue) param.args[1];
                Integer id = (Integer) param.args[2];
                Integer density = (Integer) param.args[3];

                final String file = value.string.toString();
            }
        });

        XposedBridge.hookAllConstructors(classes.X_Message, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
//				param.setResult(null);
            }
        });

        // OneLineComposerView onActionSent
        XposedBridge.hookAllMethods(classes.X_OneLineComposerView, "A08", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedBridge.hookAllConstructors(classes.X_MediaResource, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.79Z", "A00", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.1jh", "A0F", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        // Any ComponentHost onClickListener is A18 in class param.thisObject
        XposedHilfer.hookAllMethods("X.1Jl", "APb", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.3Aj", "onClick", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.3Ag", "BSJ", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.B7D", "run", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.77b", "A16", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedBridge.hookAllMethods(Dialog.class, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.GPo", "A00", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.2tN", "A00", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        XposedBridge.hookAllMethods(AssetManager.class, "open", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!((String) param.args[0]).equals("strings/default.frsc"))
                    super.beforeHookedMethod(param);
            }
        });

        XposedHilfer.hookAllMethods("X.0wT", "getString", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

        XposedHilfer.hookAllConstructors("com.facebook.messaging.attachments.OtherAttachmentData", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }
}
