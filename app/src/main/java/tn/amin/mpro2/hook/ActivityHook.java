package tn.amin.mpro2.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.util.Consumer;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;

public class ActivityHook {
    private ActivityEventListener mListener;
    public WeakReference<Activity> currentActivity;

    private Map<Integer, Consumer<Intent>> registeredOnActivityResults = new HashMap<>();

    public static final int REQUESTCODE_PICKFILE = 2608;

    public interface ActivityEventListener {
        void onActivityCreate(Activity activity);
        void onActivityResume(Activity activity);
        void onCreateContextMenu(Activity activity, ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo);

        boolean onDispatchTouchEvent(MotionEvent event);
    }

    public ActivityHook(String activityName, ClassLoader classLoader, ActivityEventListener listener) {
        mListener = listener;

        Class<?> TargetActivity = XposedHelpers.findClass(activityName, classLoader);
        Method onCreate = XposedHelpers.findMethodBestMatch(TargetActivity, "onCreate", Bundle.class);
        Method onResume = XposedHelpers.findMethodBestMatch(TargetActivity, "onResume");
        Method onCreateContextMenu = XposedHelpers.findMethodBestMatch(TargetActivity, "onCreateContextMenu",
                ContextMenu.class, View.class, ContextMenu.ContextMenuInfo.class);
        Method onActivityResult = XposedHelpers.findMethodBestMatch(TargetActivity, "onActivityResult",
                int.class, int.class, Intent.class);
        Method dispatchTouchEvent = XposedHelpers.findMethodBestMatch(TargetActivity, "dispatchTouchEvent",
                MotionEvent.class);
        Method[] startActivityMethods = new Method[] {
                XposedHelpers.findMethodBestMatch(TargetActivity, "startActivity", Intent.class),
                XposedHelpers.findMethodBestMatch(TargetActivity, "startActivity", Intent.class, Bundle.class),
                XposedHelpers.findMethodBestMatch(TargetActivity, "startActivityForResult", Intent.class, int.class),
                XposedHelpers.findMethodBestMatch(TargetActivity, "startActivityForResult", Intent.class, int.class, Bundle.class),
        };

        XposedBridge.hookMethod(onCreate, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetActivity.isInstance(param.thisObject)) {
                    Activity activity = (Activity) param.thisObject;
                    currentActivity = new WeakReference<>(activity);

                    listener.onActivityCreate(activity);
                }
            }
        });

        XposedBridge.hookMethod(onResume, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetActivity.isInstance(param.thisObject)) {
                    Activity activity = (Activity) param.thisObject;
                    currentActivity = new WeakReference<>(activity);

                    listener.onActivityResume(activity);
                }
            }
        });

        XposedBridge.hookMethod(onCreateContextMenu, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetActivity.isInstance(param.thisObject)) {
                    Activity activity = (Activity) param.thisObject;
                    ContextMenu contextMenu = (ContextMenu) param.args[0];
                    View view = (View) param.args[1];
                    ContextMenu.ContextMenuInfo menuInfo = (ContextMenu.ContextMenuInfo) param.args[2];

                    listener.onCreateContextMenu(activity, contextMenu, view, menuInfo);
                }
            }
        });

        XposedBridge.hookMethod(dispatchTouchEvent, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetActivity.isInstance(param.thisObject)) {
                    MotionEvent event = (MotionEvent) param.args[0];

                    if (listener.onDispatchTouchEvent(event)) {
                        param.setResult(true);
                    }
                }
            }
        });

        XposedBridge.hookMethod(onActivityResult, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetActivity.isInstance(param.thisObject)) {
                    Activity activity = (Activity) param.thisObject;
                    int requestCode = (Integer) param.args[0];
                    int resultCode = (Integer) param.args[1];
                    Intent data = (Intent) param.args[2];

                    if (resultCode == Activity.RESULT_OK && registeredOnActivityResults.containsKey(requestCode)) {
                        registeredOnActivityResults.get(requestCode).accept(data);
                    }
                }
            }
        });

        for (Method startActivity: startActivityMethods) {
            XposedBridge.hookMethod(startActivity, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (TargetActivity.isInstance(param.thisObject)) {
                        Intent intent = (Intent) param.args[0];
                        int requestCode = -1;
                        if (param.args.length > 1 && param.args[1] instanceof Integer) {
                            requestCode = (int) param.args[1];
                        }
                        Logger.verbose(param.method.getName() + "(" + requestCode + "): " + intent);
                        Logger.logExtras(intent);
                    }
                }
            });
        }
    }

    public void startIntent(Intent intent, int requestCode, Consumer<Intent> dataConsumer) {
        registeredOnActivityResults.put(requestCode, dataConsumer);

        currentActivity.get().startActivityForResult(intent, requestCode);
    };
}
