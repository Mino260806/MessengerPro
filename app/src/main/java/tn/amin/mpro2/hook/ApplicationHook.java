package tn.amin.mpro2.hook;

import android.app.Activity;
import android.app.Application;
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

public class ApplicationHook {
    private ApplicationEventListener mListener;
    public WeakReference<Application> currentApplication;


    public ApplicationHook(String applicationName, ClassLoader classLoader, ApplicationEventListener listener) {
        mListener = listener;

        final Class<?> TargetApplication = XposedHelpers.findClass(applicationName, classLoader);

        XposedBridge.hookAllMethods(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (TargetApplication.isInstance(param.thisObject)) {
                    Application application = (Application) param.thisObject;
                    currentApplication = new WeakReference<>(application);

                    listener.onApplicationCreate(application);
                }
            }
        });
    }

    public interface ApplicationEventListener {
        void onApplicationCreate(Application application);
    }
}
