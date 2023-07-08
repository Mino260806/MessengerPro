package tn.amin.mpro2.hook.helper;

import android.content.Intent;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;

public class FragmentHookHelper {
    public static Set<XC_MethodHook.Unhook> interceptStartActivityForResult(final int targetRequestCode, Predicate<Intent> predicate, ClassLoader classLoader,
                                                                           Function<XC_MethodHook, XC_MethodHook> methodHookWrapper) {
        return XposedBridge.hookAllMethods(XposedHelpers.findClass("androidx.fragment.app.Fragment", classLoader),
                "startActivityForResult", methodHookWrapper.apply(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        Intent intent = (Intent) param.args[0];
                        int requestCode = (int) param.args[1];

                        Logger.info("Starting activity with request code " + requestCode);

                        if (requestCode == targetRequestCode) {
                            if (predicate.test(intent)) {
                                param.setResult(null);
                            }
                        }
                    }
                }));
    }

    public static Set<XC_MethodHook.Unhook> interceptStartActivityForResult(final int targetRequestCode, Predicate<Intent> predicate, ClassLoader classLoader) {
        return interceptStartActivityForResult(targetRequestCode, predicate, classLoader, methodHook -> methodHook);
    }
}
