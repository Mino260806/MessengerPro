package tn.amin.mpro2.hook.helper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;

public class OrcaHookHelper {
    public static Set<XC_MethodHook.Unhook> hookFeature(int featureId, String requiredPrefix, String category, ClassLoader classLoader, XC_MethodHook methodHook) {
        HashSet<XC_MethodHook.Unhook> unhooks = new HashSet<>();
        Class<?> cls = XposedHelpers.findClass(
                "com.facebook." + category.toLowerCase() + ".mca.Mailbox" + category + "JNI", classLoader);
        for (Method method: cls.getDeclaredMethods()) {
            if (method.getName().startsWith("dispatch" + requiredPrefix)) {

                unhooks.add(XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Logger.verbose(Arrays.toString(param.args));
                        if ((int) param.args[0] == featureId) {
                            try {
                                Method beforeHookedMethod = methodHook.getClass().getDeclaredMethod("beforeHookedMethod", MethodHookParam.class);
                                beforeHookedMethod.setAccessible(true);
                                beforeHookedMethod.invoke(methodHook, param);
                            } catch (NoSuchMethodException ignored) {
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if ((int) param.args[0] == featureId) {
                            try {
                                Method afterHookedMethod = methodHook.getClass().getDeclaredMethod("afterHookedMethod", MethodHookParam.class);
                                afterHookedMethod.setAccessible(true);
                                afterHookedMethod.invoke(methodHook, param);
                            } catch (NoSuchMethodException ignored) {
                            }
                        }
                    }
                }));
            }
        }
        return unhooks;
    }
}
