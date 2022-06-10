package tn.amin.mpro.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kotlin.NotImplementedError;

public class XposedHilfer {

    private static ClassLoader mClassLoader;
    public static void setClassLoader(ClassLoader cl) { mClassLoader = cl; }
    public static ClassLoader getClassLoader() { return mClassLoader; }
    /*
    * Hooks and unhooks given method after first execution
    * */
    public static void findAndHookMethodOnce(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        List<Object> parameterTypesAndCallbackList = Arrays.asList(parameterTypesAndCallback);
        int callbackIndex = parameterTypesAndCallback.length-1;
        XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallbackList.get(callbackIndex);
        XC_MethodHook newCallback = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(callback, "beforeHookedMethod", param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(callback, "afterHookedMethod", param);
                XposedBridge.unhookMethod(param.method, this);
            }
        };
        parameterTypesAndCallbackList.set(callbackIndex, newCallback);
        XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallbackList.toArray());
    }

    public static void findAndHookMethodOnce(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        findAndHookMethodOnce(XposedHelpers.findClass(className, classLoader), methodName, parameterTypesAndCallback.length);
    }

    public static void hookAgain(XC_MethodHook.Unhook unhook) {
        XposedBridge.hookMethod(unhook.getHookedMethod(), unhook.getCallback());
    }
    public static Set<XC_MethodHook.Unhook> hookAllMethods(String className, String methodName, XC_MethodHook callback) {
        return XposedBridge.hookAllMethods(XposedHelpers.findClass(className, mClassLoader), methodName, callback);
    }
    public static Set<XC_MethodHook.Unhook> hookAllConstructors(String className, XC_MethodHook callback) {
        return XposedBridge.hookAllConstructors(XposedHelpers.findClass(className, mClassLoader), callback);
    }
    public static Object invokeOriginalMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
    }
    public static Class<?> findClass(String className) {
        return XposedHelpers.findClass(className, mClassLoader);
    }

    public static Object getObjectFieldRecursive(Object object, String ... fields) {
        Object field = object;
        for (String fieldName: fields) {
            field = XposedHelpers.getObjectField(field, fieldName);
        }
        return field;
    }

    public static <T> T getFirstObjectFieldByType(Object object, Class<?> type) throws IllegalAccessException {
        Class<?> clz = object.getClass();
        do {
            for (Field field : clz.getDeclaredFields()) {
                if (type.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return (T) field.get(object);
                }
            }
        } while ((clz = clz.getSuperclass()) != null);
        throw new NoSuchFieldError("Field of type " + type.getName() + " in class " + object.getClass().getName());
    }

    public static Object newInstanceNoConstructor(Class<?> clazz) {
        // Not sure if this library is safe
//        Objenesis objenesis = new ObjenesisStd();
//        ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(clazz);
//        return instantiator.newInstance();
        throw new NotImplementedError();
    }
}
