package tn.amin.mpro2.hook.helper;

import android.content.ContextWrapper;
import android.content.Intent;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class ContextHookHelper {
    public static Set<XC_MethodHook.Unhook> interceptBroadcast(String targetAction, Predicate<Intent> predicate, Function<XC_MethodHook, XC_MethodHook> methodHookWrapper) {
        return XposedBridge.hookAllMethods(ContextWrapper.class, "sendBroadcast", methodHookWrapper.apply(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                String action = intent.getAction();

                if (targetAction.equals(action)) {
                    if (predicate.test(intent)) {
                        param.setResult(null);
                    }
                }
            }
        }));
    }

    public static Set<XC_MethodHook.Unhook> interceptBroadcast(String targetAction, Predicate<Intent> predicate) {
        return interceptBroadcast(targetAction, predicate, methodHook -> methodHook);
    }
}
