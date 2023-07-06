package tn.amin.mpro2.debug;

import android.content.Intent;
import android.util.Log;

import androidx.core.util.Supplier;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

import de.robv.android.xposed.XposedBridge;

public class Logger {
    public static Supplier<Boolean> verbosePermissionSupplier = null;

    public static final String TAG_MPRO2 = "MPro2";

    public static void warn(String message) {
        XposedBridge.log("(" + TAG_MPRO2 + ") [W] " + message);
    }

    public static void info(String message) {
        XposedBridge.log("(" + TAG_MPRO2 + ") [I] " + message);
    }

    public static void logNoXposed(String message) {
        Log.d("LSPosed-Bridge", "(" + TAG_MPRO2 + ") [I] " + message);
    }

    public static void verbose(String message) {
        if (verbosePermissionSupplier == null || verbosePermissionSupplier.get()) {
            XposedBridge.log("(" + TAG_MPRO2 + ") [V] " + message);
        }
    }

    public static void error(Throwable t) {
        XposedBridge.log(t);
    }

    public static void error(String message) {
        XposedBridge.log("(" + TAG_MPRO2 + ") [E] " + message);
    }

    public static void logST() {
        verbose(Log.getStackTraceString(new Throwable()));
    }

    public static void logObject(Object o) {
        try {
            info(ReflectionToStringBuilder.toString(o, new StandardToStringStyle()));
        } catch (Throwable t) {
            info("Oh no! " + Log.getStackTraceString(t));
        }
    }

    public static void logObjectRecursive(Object o) {
        try {
            info(ReflectionToStringBuilder.toString(o, new RecursiveToStringStyle()));
        } catch (Throwable t) {
            info("Oh no! " + Log.getStackTraceString(t));
        }
    }

    public static void logExtras(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            for (String key: intent.getExtras().keySet()) {
                Object object = intent.getExtras().get(key);
                String type = "null";
                if (object != null)
                    type = object.getClass().getName();
                Logger.verbose("[" + key + "](" + type + ") = " + object);
            }
        }
    }
}
