package tn.amin.mpro2.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class XposedHilfer {
    public static Set<Method> findAllMethods(Class<?> clazz, String methodName) {
        HashSet<Method> result = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods())
            if (method.getName().equals(methodName))
                result.add(method);
        return result;
    }
}
